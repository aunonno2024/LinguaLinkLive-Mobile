package com.osastudio.lingualinklive.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.osastudio.lingualinklive.databinding.FragmentProfileBinding
import com.osastudio.lingualinklive.ui.contacts.LanguageSelectDialog
import com.osastudio.lingualinklive.utils.Constants
import java.util.UUID

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        setupClickListeners()
    }

    private fun loadProfile() {
        val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        var userId = prefs.getString(Constants.PREF_USER_ID, null)
        if (userId == null) {
            userId = UUID.randomUUID().toString().take(8).uppercase()
            prefs.edit().putString(Constants.PREF_USER_ID, userId).apply()
        }
        binding.tvUserId.text = "User ID: $userId"
        binding.etUsername.setText(prefs.getString(Constants.PREF_USERNAME, ""))
        binding.tvMyLanguage.text = "My Language: ${prefs.getString(Constants.PREF_MY_LANGUAGE, "Bengali")}"
        binding.etServerUrl.setText(prefs.getString(Constants.PREF_SERVER_URL, Constants.DEFAULT_SERVER_URL))
    }

    private fun setupClickListeners() {
        binding.tvMyLanguage.setOnClickListener {
            LanguageSelectDialog(requireContext()) { selectedLanguage ->
                binding.tvMyLanguage.text = "My Language: $selectedLanguage"
            }.show()
        }

        binding.btnSave.setOnClickListener {
            val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString(Constants.PREF_USERNAME, binding.etUsername.text.toString())
                .putString(Constants.PREF_MY_LANGUAGE,
                    binding.tvMyLanguage.text.toString().removePrefix("My Language: "))
                .putString(Constants.PREF_SERVER_URL, binding.etServerUrl.text.toString())
                .apply()
            Toast.makeText(requireContext(), "Profile saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}