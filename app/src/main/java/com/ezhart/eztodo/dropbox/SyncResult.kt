package com.ezhart.eztodo.dropbox

abstract class SyncResult(val message: String) {
    class NotConnected : SyncResult("No network connection")
    class NotAuthenticated : SyncResult("Not authenticated to Dropbox")
    class Success(message: String) : SyncResult(message)
    class Conflict(message: String) : SyncResult(message)
    class Error(message:String, val e: Exception) : SyncResult(message)
}