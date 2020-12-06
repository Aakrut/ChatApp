package com.ex.chatappfirebase.data

import com.google.firebase.Timestamp


data class Message(val message: String, val message_id: String, val sender: String, val receiver: String, val time: Timestamp){
    constructor():this("","","","", Timestamp.now())
}
