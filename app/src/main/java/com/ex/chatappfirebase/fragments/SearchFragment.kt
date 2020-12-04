package com.ex.chatappfirebase.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ex.chatappfirebase.R
import com.ex.chatappfirebase.adapters.UserAdapter
import com.ex.chatappfirebase.data.User
import com.ex.chatappfirebase.databinding.FragmentSearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {

    val TAG = "SearchFragment"

    private lateinit var searchBinding: FragmentSearchBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private var mUserList : ArrayList<User> ?= null

    private var searchAdapter : UserAdapter ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        searchBinding = FragmentSearchBinding.inflate(inflater,container,false)
        val view = searchBinding.root

        firebaseAuth = Firebase.auth

        mUserList = ArrayList()

        searchAdapter = context?.let { UserAdapter(it, mUserList!!) }

        searchBinding.userSearchRecyclerView.setHasFixedSize(true)
        searchBinding.userSearchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchBinding.userSearchRecyclerView.adapter = searchAdapter

        searchBinding.searchUserEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(searchBinding.searchUserEditText.text.toString() != ""){
                    searchUser(s.toString().toLowerCase())
                }else{
                    mUserList!!.clear()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        return view
    }

    private fun searchUser(input: String) {
        val firestore = Firebase.firestore
        firestore.collection("Users").orderBy("search").startAt(input).endAt("$input\uf8ff")
            .addSnapshotListener { snapshot, error ->
                mUserList?.clear()
                if(error != null){
                    Log.d(TAG, "searchUser: Error")
                }
               else{
                    mUserList?.clear()
                        for (document in snapshot!!){
                            val user : User = document.toObject(User::class.java)
                            if(user.uid != firebaseAuth.currentUser?.uid ){
                                mUserList?.add(user)
                            }
                        }
                    searchAdapter?.notifyDataSetChanged()
                }
            }
    }


}