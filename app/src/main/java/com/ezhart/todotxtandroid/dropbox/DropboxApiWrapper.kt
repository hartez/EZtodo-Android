package com.ezhart.todotxtandroid.dropbox

import android.content.Context
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

    suspend fun download(
        applicationContext: Context,
        metadata: FileMetadata
    ): DownloadFileTaskResult = withContext(ioDispatcher) {
        try {
            val file = File(applicationContext.filesDir, metadata.name)

            FileOutputStream(file).use { outputStream ->
                dropboxClient.files().download(metadata.pathLower, metadata.rev)
                    .download(outputStream)
            }

            DownloadFileTaskResult.Success(file)
        } catch (e: DbxException) {
            DownloadFileTaskResult.Error(e)
        } catch (e: IOException) {
            DownloadFileTaskResult.Error(e)
        }
    }

    suspend fun getFileMetaData(path: String): GetFileMetaDataTaskResult = withContext(ioDispatcher) {
        try {
            val metadata = dropboxClient.files().getMetadata(path) as FileMetadata
            GetFileMetaDataTaskResult.Success(metadata)
        } catch (e: DbxException) {
            GetFileMetaDataTaskResult.Error(e)
        } catch (e: IOException) {
            GetFileMetaDataTaskResult.Error(e)
        }
    }

    private suspend fun getFilesForFolder(folderPath: String): GetFilesApiResponse =
        withContext(Dispatchers.IO) {
            try {
                val files = dropboxClient.files().listFolder(folderPath)
                GetFilesApiResponse.Success(files)
            } catch (exception: DbxException) {
                GetFilesApiResponse.Failure(exception)
            }
        }
}