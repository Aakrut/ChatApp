package com.ex.chatappfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ex.chatappfirebase.databinding.ActivityMessageChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MessageChatActivity : AppCompatActivity() {

    val TAG = "MessageChatActivity"

    var userId : String = ""
    
    private lateinit var messageChatBinding: ActivityMessageChatBinding
    
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageChatBinding = ActivityMessageChatBinding.inflate(layoutInflater)
        val view = messageChatBinding.root
        setContentView(view)

        firebaseAuth = Firebase.auth
        
        intent = intent
        userId = intent.getStringExtra("profile_Id").toString()

        val db = Firebase.firestore
        db.collection("Users").document(userId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")
                val user : com.ex.chatappfirebase.data.User? = snapshot.toObject(com.ex.chatappfirebase.data.User::class.java)

                messageChatBinding.textUsernameMessageChat.text = user!!.username
                messageChatBinding.textFullnameMessageChat.text = user.fullname
                Picasso.get().load(user.photo_profile).into(messageChatBinding.circleImageChatAppbar)


            } else {
                Log.d(TAG, "Current data: null")
            }
        }


    }
}