package com.ex.chatappfirebase.data

data class Message(val message : String,val message_id : String,val sender : String, val receiver : String, val time : String){
    constructor():this("","","","","")
}
