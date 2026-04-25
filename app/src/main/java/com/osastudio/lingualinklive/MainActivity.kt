package com.osastudio.lingualinklive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.osastudio.lingualinklive.databinding.ActivityMainBinding
import com.osastudio.lingualinklive.ui.contacts.ContactsFragment
import com.osastudio.lingualinklive.ui.messaging.MessageContactsFragment
import com.osastudio.lingualinklive.ui.profile.ProfileFragment
import com.osastudio.lingualinklive.ui.video.VideoContactsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(ContactsFragment())
        }

        setupBottomNav()
    }

    private fun setupBottomNav() {
        binding.navCalls.setOnClickListener { loadFragment(ContactsFragment()) }
        binding.navMessages.setOnClickListener { loadFragment(MessageContactsFragment()) }
        binding.navProfile.setOnClickListener { loadFragment(ProfileFragment()) }
        binding.navVideo.setOnClickListener { loadFragment(VideoContactsFragment()) }
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}