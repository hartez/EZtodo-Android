package com.ezhart.todotxtandroid.dropbox

import com.ezhart.todotxtandroid.BuildConfig

class DropboxAppConfig {
    val apiKey: String = BuildConfig.DROPBOX_APP_KEY
    val clientIdentifier: String = "db-${apiKey}"
}