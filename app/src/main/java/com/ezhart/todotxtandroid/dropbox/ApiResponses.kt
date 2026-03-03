package com.ezhart.todotxtandroid.dropbox

import com.dropbox.core.v2.users.FullAccount

sealed interface GetCurrentAccountResult {
    class Success(val account: FullAccount) : GetCurrentAccountResult
    class Error(val e: Exception) : GetCurrentAccountResult
}