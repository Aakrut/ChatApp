package com.ex.chatappfirebase.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ex.chatappfirebase.R
import com.ex.chatappfirebase.adapters.UserAdapter
import com.ex.chatappfirebase.data.Message
import com.ex.chatappfirebase.data.User
import com.ex.chatappfirebase.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    val TAG = "HomeFragment"

    private lateinit var homeBinding: FragmentHomeBinding

    private lateinit var firebase_auth : FirebaseAuth

    private var userList : List<User> ?= null
    private var messageList : List<Message> ?= null
    private var userAdapter : UserAdapter ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater,container,false)
        val view = homeBinding.root

        firebase_auth = Firebase.auth

        homeBinding.recyclerViewHome.setHasFixedSize(true)
        homeBinding.recyclerViewHome.layoutManager = LinearLayoutManager(context)
        userAdapter = context?.let { UserAdapter(it,userList!!) }

        messageList = ArrayList()
        val db = Firebase.firestore

        val ref = db.collection("Chats").document("ChatSenderReciver").addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }

            (messageList as ArrayList<Message>).clear()
            if (value != null && value.exists()) {
                Log.d(TAG, "Current data: ${value.data}")
                val message : Message? = value.toObject(Message::class.java)
                if (message != null) {
                    (messageList as ArrayList<Message>).add(message)
                    Log.d(TAG, "onCreateView: Retrieving 1")
                }
                retreiveChatLists()
            } else {
                Log.d(TAG, "Current data: null")
            }
        }


        return view
    }

    private fun retreiveChatLists() {
        userList = ArrayList()
        messageList = ArrayList()

        val db = Firebase.firestore
        val ref = db.collection("Users").addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            (userList as ArrayList<User>).clear()

            for (doc in value!!) {
                val user: User = doc.toObject(User::class.java)
                for(alllist in messageList!!){
                    if(user.uid.equals(alllist.sender)){
                        (userList as ArrayList<User>).add(user)
                        Log.d(TAG, "retreiveChatLists: Retrieving All Messages")
                    }
                }
            }
            homeBinding.recyclerViewHome.adapter = userAdapter
            Log.d(TAG, "Current ")
        }

    }


}