package com.ex.chatappfirebase.data

data class User(val username : String , val fullname : String, val photo_profile : String,val uid : String){
    constructor():this("","","","")
}
