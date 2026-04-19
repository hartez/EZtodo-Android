package com.ezhart.eztodo.dropbox

import android.content.Context
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth

class DropboxAuth(val credentials: DropboxCredentials, val config: DropboxAppConfig) {
    var isAwaitingResult: Boolean = false

    fun startDropboxAuthorization2PKCE(context: Context) {
        val requestConfig = DbxRequestConfig(config.clientIdentifier)

        val scopes = listOf(
            "account_info.read",
            "files.content.write",
            "files.content.read"
        )
        Auth.startOAuth2PKCE(context, config.apiKey, requestConfig, scopes)
        isAwaitingResult = true
    }

    fun onResume() {
        if (isAwaitingResult) {
            val authDbxCredential = Auth.getDbxCredential()
            isAwaitingResult = false
            if (authDbxCredential != null) {
                credentials.storeCredentialLocally(authDbxCredential)
            }
        }
    }
}