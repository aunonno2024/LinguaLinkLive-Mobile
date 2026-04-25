package com.osastudio.lingualinklive.ui.video

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.osastudio.lingualinklive.databinding.FragmentVideoContactsBinding
import com.osastudio.lingualinklive.model.Contact
import com.osastudio.lingualinklive.ui.call.VideoCallActivity
import com.osastudio.lingualinklive.utils.Constants
import com.osastudio.lingualinklive.utils.PermissionUtils

class VideoContactsFragment : Fragment() {

    private var _binding: FragmentVideoContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVideoContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contacts = listOf(
            Contact("friend1", "FRIEND 1", "English", "en"),
            Contact("friend2", "FRIEND 2", "Chinese", "zh"),
            Contact("friend3", "FRIEND 3", "Korean", "ko"),
            Contact("friend4", "FRIEND 4", "Arabic", "ar")
        )

        val adapter = VideoContactsAdapter(contacts) { contact ->
            startVideoCall(contact)
        }

        binding.rvVideoContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVideoContacts.adapter = adapter
    }

    private fun startVideoCall(contact: Contact) {
        if (!PermissionUtils.hasCameraPermission(requireActivity())) {
            PermissionUtils.requestVideoPermissions(requireActivity())
            return
        }
        val intent = Intent(requireContext(), VideoCallActivity::class.java).apply {
            putExtra(Constants.EXTRA_CONTACT_ID, contact.id)
            putExtra(Constants.EXTRA_CONTACT_NAME, contact.name)
            putExtra(Constants.EXTRA_CONTACT_LANGUAGE, contact.languageCode)
            putExtra(Constants.EXTRA_IS_INCOMING, false)
            putExtra(Constants.EXTRA_IS_VIDEO, true)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}