package com.ex.chatappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ex.chatappfirebase.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class SignUpActivity : AppCompatActivity() {

    val TAG = "SignUpActivity"

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signUpBinding: ActivitySignUpBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpBinding.root
        setContentView(view)

        FirebaseApp.initializeApp(this)

        firebaseAuth = Firebase.auth

        signUpBinding.buttonSignup.setOnClickListener {
            signup()
        }

        signUpBinding.textLogin.setOnClickListener {
            startActivity(Intent(this,LogInActivity::class.java))
            finish()
        }

    }

    private fun signup() {
        val email : String = signUpBinding.signupEditTextEmail.text.toString()
        val password : String = signUpBinding.signupEditTextPassword.text.toString()
        val username : String = signUpBinding.signupEditTextUsername.text.toString()
        val fullname : String = signUpBinding.signupEditTextFullname.text.toString()

        if(email == ""){
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show()
        }else if(password == ""){
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show()
        }else if(username == ""){
            Toast.makeText(this, "Please Enter Your Username", Toast.LENGTH_SHORT).show()
        }else if(fullname == ""){
            Toast.makeText(this, "Please Enter Your Fullname", Toast.LENGTH_SHORT).show()
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    Log.d(TAG, "signup: SuccessFully Signup")
                    val uid = firebaseAuth.currentUser!!.uid
                    val firestore = Firebase.firestore
                    val ref = firestore.collection("Users").document(uid)

                    val hashMap = hashMapOf(
                        "username" to username,
                        "fullname" to fullname,
                        "search" to fullname,
                        "photo_profile" to "https://firebasestorage.googleapis.com/v0/b/messenger-clone-7c509.appspot.com/o/image.png?alt=media&token=63347c50-40e2-4049-ae3c-7e61d43ca6d8",
                        "uid" to uid
                    )

                    ref.set(hashMap).addOnSuccessListener {
                        Toast.makeText(this, "SuccessFully Created", Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }.addOnFailureListener{
                        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this, "SomeThing Went Wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}