package com.ezhart.eztodo.dropbox

import com.ezhart.eztodo.BuildConfig

class DropboxAppConfig {
    val apiKey: String = BuildConfig.DROPBOX_APP_KEY
    val clientIdentifier: String = "db-${apiKey}"
}