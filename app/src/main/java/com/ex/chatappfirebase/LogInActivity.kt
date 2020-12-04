package com.ex.chatappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ex.chatappfirebase.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {
    val TAG = "LogInActivity"
    private lateinit var logInBinding: ActivityLogInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logInBinding = ActivityLogInBinding.inflate(layoutInflater)
        val view = logInBinding.root
        setContentView(view)

        firebaseAuth = Firebase.auth

        logInBinding.buttonLogin.setOnClickListener {
            login()
        }

        logInBinding.textSignUp.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }
    }

    private fun login() {
        val email : String = logInBinding.loginEditTextEmail.text.toString()
        val password : String = logInBinding.loginEditTextPassword.text.toString()

        if(email == ""){
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show()
        }else if(password == ""){
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show()
        }else{
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    Log.d(TAG, "login: SuccessFully Logged In")
                    val intent = Intent()
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "SomeThing Went Wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}