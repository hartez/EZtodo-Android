package com.ezhart.todotxtandroid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezhart.todotxtandroid.ui.theme.TodotxtAndroidTheme
import com.ezhart.todotxtandroid.viewmodels.SettingsViewModel


@Composable
fun SettingsScreen() {

    val lifecycleOwner = LocalLifecycleOwner.current

    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
    val context = LocalContext.current

    val isSignedIn = settingsViewModel.isSignedIn
    val accountName by settingsViewModel.accountName.collectAsStateWithLifecycle("")
    val accountEmail by settingsViewModel.accountEmail.collectAsStateWithLifecycle("")

    TodotxtAndroidTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                SectionTitle("Account")

                if (isSignedIn) {
                    Text(accountName)
                    Text(accountEmail)
                } else {
                    Button(onClick = { settingsViewModel.beginSignIn(context) }) {
                        Text("Sign In")
                    }
                }

                // About
                SectionTitle("About")

                SettingItem(
                    title = "TODO.txt Android",
                    value = "0.1.0"
                )
            }
        }
    }
}


@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingItem(
    title: String,
    value: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)
        value?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }
    }
}