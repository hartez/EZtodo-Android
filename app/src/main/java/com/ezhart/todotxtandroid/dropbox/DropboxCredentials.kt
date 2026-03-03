package com.ezhart.todotxtandroid.dropbox

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.dropbox.core.oauth.DbxCredential

class DropboxCredentials(val context: Context) {

    private companion object {
        private val TAG = DropboxCredentials::class.java.simpleName
    }

    fun isAuthenticated(): Boolean {
        return readCredentialLocally() != null
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "todotxtAndroid", // TODO make this a constant that Settings knows about, too
        AppCompatActivity.MODE_PRIVATE
    )

    fun storeCredentialLocally(dbxCredential: DbxCredential) {
        Log.d(TAG, "Storing credential in Shared Preferences")
        sharedPreferences.edit {
            putString("credential", DbxCredential.Writer.writeToString(dbxCredential))
        }
    }

    fun readCredentialLocally(): DbxCredential? {
        val serializedCredentialJson = sharedPreferences.getString("credential", null)
        Log.d(TAG, "Local Credential Value from Shared Preferences: $serializedCredentialJson")
        return try {
            DbxCredential.Reader.readFully(serializedCredentialJson)
        } catch (e: Exception) {
            Log.d(TAG, "Something went wrong parsing the credential, clearing it")
            removeCredentialLocally()
            null
        }
    }

    fun removeCredentialLocally() {
        Log.d(TAG, "Clearing credential from Shared Preferences")
        sharedPreferences.edit {
            remove("credential")
        }
    }
}