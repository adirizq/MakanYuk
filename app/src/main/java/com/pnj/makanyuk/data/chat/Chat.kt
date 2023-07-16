package com.pnj.makanyuk.data.chat

import com.google.firebase.database.Exclude

data class Chat( val username :String? = null, val message :String? = null, val time :String? = null) {
    @Exclude
    fun getMap(): Map<String, String?> {
        return mapOf(
            "username" to username,
            "message" to message,
            "time" to time

        )
    }
}
