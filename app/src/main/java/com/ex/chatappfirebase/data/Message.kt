package com.ex.chatappfirebase.data

data class Message(val sender: String, val sender_chat : String, val receiver : String, val isseen : String,val url : String, val messageId : String, val time : String){
    constructor():this("","","","","","","")
}
