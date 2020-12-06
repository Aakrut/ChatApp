package com.ex.chatappfirebase

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.ex.chatappfirebase.adapters.MessageAdapter
import com.ex.chatappfirebase.data.Message
import com.ex.chatappfirebase.databinding.ActivityMessageChatBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

class MessageChatActivity : AppCompatActivity() {

    val TAG = "MessageChatActivity"

    var userId : String = ""
    
    private lateinit var messageChatBinding: ActivityMessageChatBinding
    
    private lateinit var firebaseAuth: FirebaseAuth

    private  var message_list : List<Message> ?= null
    
    private  var messageAdapter: MessageAdapter ?= null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageChatBinding = ActivityMessageChatBinding.inflate(layoutInflater)
        val view = messageChatBinding.root
        setContentView(view)

        firebaseAuth = Firebase.auth
        
        message_list = ArrayList()
        
        

        messageChatBinding.recyclerViewChatMessage.setHasFixedSize(true)
        messageChatBinding.recyclerViewChatMessage.layoutManager =LinearLayoutManager(this)

        
        intent = intent
        userId = intent.getStringExtra("profile_Id").toString()

        messageChatBinding.micorsendFab.setOnClickListener {
            if(messageChatBinding.editTextMessage.text.toString() != ""){
                messageChatBinding.micorsendFab.setImageResource(R.drawable.ic_baseline_send_24)
                SendMessageToUser(firebaseAuth.currentUser!!.uid,userId,messageChatBinding.editTextMessage.text.toString())
                messageChatBinding.editTextMessage.text.clear()
            }else{
                messageChatBinding.micorsendFab.setImageResource(R.drawable.ic_baseline_mic_24)
            }
        }



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

                RetrieveMessages(firebaseAuth.currentUser!!.uid,userId,user.photo_profile)

            } else {
                Log.d(TAG, "Current data: null")
            }
        }

    }

    private fun RetrieveMessages(uid: String, userId: String, photoProfile: String) {
            val db = Firebase.firestore
            db.collection("ChatLists").orderBy("time",Query.Direction.DESCENDING).addSnapshotListener{
                value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                ( message_list as ArrayList<Message>).clear()
                for (doc in value!!) {
                    val messages : Message = doc.toObject(Message::class.java)
                    if(messages.receiver.equals(uid) && messages.sender.equals(userId) ||
                            messages.receiver.equals(userId) && messages.sender.equals(uid)){
                        ( message_list as ArrayList<Message>).add(messages)
                        Log.d(TAG, "RetrieveMessages: Retrieving All Messages")
                    }
                    messageAdapter = MessageAdapter(context = this, message_list as ArrayList<Message>,photoProfile)
                    messageChatBinding.recyclerViewChatMessage.adapter = messageAdapter
                }
               
                Log.d(TAG, "Current ")
    }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun SendMessageToUser(sender: String, receiver: String, editText: String) {
                val db = Firebase.firestore
                val ref = db.collection("Chats").document("ChatSenderReciver")

                val hashMap = hashMapOf(
                        "sender" to sender,
                        "receiver" to receiver
                )

                ref.set(hashMap).addOnSuccessListener {
                    Log.d(TAG, "ChatMessage: SuccessFully Chats")

                    val db_firestore = Firebase.firestore
                    val ref2 = db_firestore.collection("ChatLists")
                    val message_id = ref2.document().id


                    val hasMapChatLists = hashMapOf(
                            "message" to editText,
                            "message_id" to message_id,
                            "sender" to sender,
                            "receiver" to receiver,
                            "time" to Timestamp.now()
                    )

                    ref2.document(message_id).set(hasMapChatLists).addOnSuccessListener {
                        Log.d(TAG, "SendMessageToUser: $editText time is ${Date.from(Instant.now())}")
                    }

                }.addOnFailureListener {
                    Log.d(TAG, "ChatMessage: Chat Failed")
                }
    }


}