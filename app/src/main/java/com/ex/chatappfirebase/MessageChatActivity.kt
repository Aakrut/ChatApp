package com.ex.chatappfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.ex.chatappfirebase.adapters.MessageAdapter
import com.ex.chatappfirebase.data.Message
import com.ex.chatappfirebase.databinding.ActivityMessageChatBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class MessageChatActivity : AppCompatActivity() {

    val TAG = "MessageChatActivity"

    var userId : String = ""
    
    private lateinit var messageChatBinding: ActivityMessageChatBinding
    
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mchatList : List<Message>

    private  var chatsAdaptet : MessageAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageChatBinding = ActivityMessageChatBinding.inflate(layoutInflater)
        val view = messageChatBinding.root
        setContentView(view)

        firebaseAuth = Firebase.auth

        mchatList = ArrayList()



        messageChatBinding.recyclerViewChatMessage.setHasFixedSize(true)
        messageChatBinding.recyclerViewChatMessage.layoutManager =LinearLayoutManager(this)

        
        intent = intent
        userId = intent.getStringExtra("profile_Id").toString()

        messageChatBinding.micorsendFab.setOnClickListener {
            if(messageChatBinding.editTextMessage.text.toString() != ""){
                messageChatBinding.micorsendFab.setImageResource(R.drawable.ic_baseline_send_24)
                sendMessageToUser(firebaseAuth.currentUser!!.uid,userId,messageChatBinding.editTextMessage.text.toString())
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

                retrevieMessages(firebaseAuth.currentUser!!.uid,userId,user.photo_profile)

            } else {
                Log.d(TAG, "Current data: null")
            }
        }


    }

    private fun sendMessageToUser(senderuid: String, receiverVisit: String?, textMessageChat: String) {
        Log.d("MessageChatActivity", "sendMessageToUser: SendMessageToUser")
        val firebase = Firebase.firestore
        val message_firesore_ref = firebase.collection("Chats").document().id



        val messagehashmap = hashMapOf(
            "sender" to senderuid,
            "sender_chat" to textMessageChat,
            "receiver" to receiverVisit,
            "isseen" to false,
            "url" to "",
            "messageId" to message_firesore_ref,
            "time" to Timestamp(
                System.currentTimeMillis(),
                System.currentTimeMillis().toInt()
            )
        )
        firebase.collection("Chats").document(message_firesore_ref).set(messagehashmap).addOnCompleteListener {
                task ->
            Log.d("MessageChatActivity", "sendMessageToUser: MessageHashMap")
            if(task.isSuccessful){

                Log.d("MessageChatActivity", "sendMessageToUser: Successful")

                val chat_list_ref = Firebase.firestore.collection("ChatList").document(firebaseAuth.currentUser!!.uid).collection("UserVisit").document(userId)

                chat_list_ref.addSnapshotListener  { snapshot, error ->
                    if (error != null) {
                        Log.w("MessageChatActivity", "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    if ( !snapshot!!.exists()) {
                        Log.d("MessageChatActivity", "Current data: $snapshot")
                        val chat_list_receiver_ref = Firebase.firestore.collection("ChatList")
                            .document(userId).collection("Logged In User").document(firebaseAuth.currentUser!!.uid)

                        val hashref = hashMapOf(
                            "id" to userId
                        )
                        chat_list_receiver_ref.set(hashref)

                    } else {
                        Log.d("MessageChatActivity", "Current data: null")

                        val hash_list_ref = hashMapOf(
                            "id" to firebaseAuth.currentUser!!.uid
                        )
                        chat_list_ref.set(hash_list_ref)

                    }
                }

                val refrence = Firebase.firestore.collection("Users").document(firebaseAuth.currentUser!!.uid).collection(userId)

            }
        }

    }

    private fun retrevieMessages(senderId: String, receiverId: String?, imageUrl: String) {

        mchatList = ArrayList()
        val firebase = Firebase.firestore
        val ref = firebase.collection("Chats")



        ref.get().addOnSuccessListener {
                result ->
            (mchatList as ArrayList<Message>).clear()
            for(document in result){
                val chat = document.toObject(Message::class.java)

                if(chat.receiver.equals(senderId) && chat.sender.equals(receiverId) ||
                    chat.receiver.equals(receiverId) && chat.sender.equals(senderId)){
                    (mchatList as ArrayList<Message>).add(chat)
                }
                chatsAdaptet = MessageAdapter(this,mchatList,imageUrl)
                messageChatBinding.recyclerViewChatMessage.adapter = chatsAdaptet
            }
        }

        val db = Firebase.firestore

        firebase.collection("Chats")

            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("MessageChatActivity", "Listen failed.", e)
                    return@addSnapshotListener
                }

                mchatList = ArrayList()
                if (snapshot != null) {
                    for (doc in snapshot) {

                            Log.d("MessageChatActivity", "retrevieMessages: retreiving $doc")

                    }
                }

            }

    }

}