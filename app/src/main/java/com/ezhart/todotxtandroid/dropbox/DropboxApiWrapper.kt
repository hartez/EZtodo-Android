package com.ezhart.todotxtandroid.dropbox

import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DropboxApiWrapper(
    dbxCredential: DbxCredential,
    clientIdentifier: String,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {
    val dropboxClient: DbxClientV2 = DbxClientV2(
        DbxRequestConfig(clientIdentifier),
        dbxCredential
    )

    suspend fun revokeDropboxAuthorization() = withContext(ioDispatcher) {
        dropboxClient.auth().tokenRevoke()
    }

    suspend fun getCurrentAccount(): GetCurrentAccountResult = withContext(ioDispatcher) {
        try {
            GetCurrentAccountResult.Success(dropboxClient.users().currentAccount)
        } catch (e: DbxException) {
            GetCurrentAccountResult.Error(e)
        }
    }
}