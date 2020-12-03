package com.ex.chatappfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ex.chatappfirebase.databinding.ActivityMainBinding
import com.ex.chatappfirebase.fragments.HomeFragment
import com.ex.chatappfirebase.fragments.ProfileFragment
import com.ex.chatappfirebase.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        loadFragment(HomeFragment())

        bottomNav()

    }

    private fun bottomNav() {
     
    }

    private fun loadFragment(fragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}