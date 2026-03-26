package com.ezhart.todotxtandroid.dropbox

fun friendlyInterval(minutes: Int): String {
    if(minutes == 0){
        return "Never"
    }

    if(minutes == 60){
        return "1 hour"
    }

    if(minutes > 60){
        return "${minutes/60} hours"
    }

    return "$minutes minutes"
}