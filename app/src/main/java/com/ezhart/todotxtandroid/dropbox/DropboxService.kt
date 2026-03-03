package com.ezhart.todotxtandroid.dropbox

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DropboxService(val applicationContext: Context) {

    val config = DropboxAppConfig()
    val credentials by lazy { DropboxCredentials(applicationContext) }
    val authHandler by lazy { DropboxAuth(credentials, config) }
    val api by lazy {
        DropboxApiWrapper(
            credentials.readCredentialLocally()!!,
            config.clientIdentifier
        )
    }

    private var signedInCallback: () -> Unit = {}

    fun isAuthenticated(): Boolean {
        return credentials.isAuthenticated()
    }

    fun signIn(activityContext: Context, onSignedIn: () -> Unit) {
        signedInCallback = onSignedIn
        authHandler.startDropboxAuthorization2PKCE(activityContext)
    }

    fun signOut() {
        if (credentials.isAuthenticated()) {
            CoroutineScope(Dispatchers.IO).launch {
                api.revokeDropboxAuthorization()
            }
            credentials.removeCredentialLocally()
        }
    }

    fun onResume() {
        authHandler.onResume()
        if(credentials.isAuthenticated()){
            signedInCallback()
        }
    }
}