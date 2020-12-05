package com.ex.chatappfirebase.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ex.chatappfirebase.LogInActivity
import com.ex.chatappfirebase.data.User
import com.ex.chatappfirebase.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage


class ProfileFragment : Fragment() {

    val TAG = "ProfileFragment"

    private lateinit var profileBinding: FragmentProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var photo_URi : Uri
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        profileBinding = FragmentProfileBinding.inflate(inflater, container, false)
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
                
                profileBinding.usernameProfileEditText.setText(user.username)
                profileBinding.fullnameProfileEditText.setText(user.fullname)

            } else {
                Log.d(TAG, "Current data: null")
            }
        }
        
        profileBinding.buttonUpdate.setOnClickListener { 
            updateProfile()
        }

        profileBinding.circleImageViewProfile.setOnClickListener {
            context?.let { it1 ->
                CropImage.activity()
                        .start(it1, this)
            };
        }


        profileBinding.logout.setOnClickListener {
            firebaseAuth.signOut()
            val inent = Intent()
            inent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent(context, LogInActivity::class.java))
            activity?.onBackPressed()
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                val resultUri = result.uri
                profileBinding.circleImageViewProfile.setImageURI(resultUri)
                photo_URi = resultUri
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun updateProfile() {
        when{
            photo_URi == null -> Toast.makeText(context, "Please Select an image", Toast.LENGTH_SHORT).show()
            profileBinding.usernameProfileEditText.text.toString() == "" -> Toast.makeText(context, "Please Enter Your Username", Toast.LENGTH_SHORT).show()
            profileBinding.fullnameProfileEditText.text.toString() == "" -> Toast.makeText(context, "Please Enter Your Fullname", Toast.LENGTH_SHORT).show()
            else -> {
                val storage = Firebase.storage
                val reff = storage.reference.child(System.currentTimeMillis().toString() + ".jpg")
                val uploadTask = photo_URi.let { reff.putFile(it) }

                uploadTask?.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    reff.downloadUrl
                }?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val url = downloadUri

                        val hashMap = hashMapOf(
                                "photo_profile" to url.toString(),
                                "username" to profileBinding.usernameProfileEditText.text.toString(),
                                "fullname" to profileBinding.fullnameProfileEditText.text.toString()
                        )

                        val db = Firebase.firestore
                        db.collection("Users").document(firebaseAuth.currentUser!!.uid).update(hashMap as Map<String, Any>).addOnSuccessListener {
                            Toast.makeText(context, "Profile SuccessFully Updated", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "updateProfile: SuccessFully Updated")
                        }.addOnFailureListener {
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d(TAG, "uploadphoto: Something Went Wrong")
                    }
                }
            }
        }
       
    }

}