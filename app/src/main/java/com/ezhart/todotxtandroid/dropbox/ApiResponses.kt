package com.ezhart.todotxtandroid.dropbox

import com.dropbox.core.DbxException
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.ListFolderResult
import com.dropbox.core.v2.users.FullAccount
import java.io.File

sealed interface GetCurrentAccountResult {
    class Success(val account: FullAccount) : GetCurrentAccountResult
    class Error(val e: Exception) : GetCurrentAccountResult
}

sealed interface DownloadFileTaskResult {
    class Success(val result: File) : DownloadFileTaskResult
    class Error(val e: Exception) : DownloadFileTaskResult
}

sealed interface GetFileMetaDataTaskResult {
    class Success(val result: FileMetadata) : GetFileMetaDataTaskResult
    class Error(val e: Exception) : GetFileMetaDataTaskResult
}

sealed class GetFilesApiResponse {
    data class Success(val result: ListFolderResult) : GetFilesApiResponse()
    data class Failure(val exception: DbxException) : GetFilesApiResponse()
}