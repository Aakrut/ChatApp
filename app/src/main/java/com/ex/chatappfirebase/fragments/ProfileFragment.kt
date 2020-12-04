package com.ex.chatappfirebase.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ex.chatappfirebase.LogInActivity
import com.ex.chatappfirebase.R
import com.ex.chatappfirebase.data.User
import com.ex.chatappfirebase.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    val TAG = "ProfileFragment"

    private lateinit var profileBinding: FragmentProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        profileBinding = FragmentProfileBinding.inflate(inflater,container,false)
        val view = profileBinding.root

        firebaseAuth = Firebase.auth

        val db = Firebase.firestore
        val docRef = db.collection("Users").document(firebaseAuth.currentUser!!.uid)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")
                val user : User? = snapshot.toObject(User::class.java)

                profileBinding.textProfileUsername.text = user!!.username
                profileBinding.profileFullname.text = user.fullname
                Picasso.get().load(user.photo_profile).into(profileBinding.circleImageViewProfile)

            } else {
                Log.d(TAG, "Current data: null")
            }
        }


        profileBinding.logout.setOnClickListener {
            firebaseAuth.signOut()
            val inent = Intent()
            inent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent(context,LogInActivity::class.java))
            activity?.onBackPressed()
        }
        return view
    }


}