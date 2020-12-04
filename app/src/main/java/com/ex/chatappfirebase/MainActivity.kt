package com.ex.chatappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ex.chatappfirebase.databinding.ActivityMainBinding
import com.ex.chatappfirebase.fragments.HomeFragment
import com.ex.chatappfirebase.fragments.ProfileFragment
import com.ex.chatappfirebase.fragments.SearchFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        firebaseAuth = Firebase.auth
        loadFragment(HomeFragment())

        bottomNav()

    }

    //BottomNavigationView
    private fun bottomNav() {
     mainBinding.bottomNavigationBar.setOnNavigationItemSelectedListener {
         when(it.itemId){
             R.id.home_frag -> {
                 loadFragment(HomeFragment())
                 return@setOnNavigationItemSelectedListener true
             }
             R.id.search_frag -> {
                 loadFragment(SearchFragment())
                 return@setOnNavigationItemSelectedListener true
             }
             R.id.profile_frag -> {
                 loadFragment(ProfileFragment())
                 return@setOnNavigationItemSelectedListener true
             }
             else -> return@setOnNavigationItemSelectedListener false
         }
     }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser?.uid == null){
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent(this,LogInActivity::class.java))
            finish()
        }
    }
}