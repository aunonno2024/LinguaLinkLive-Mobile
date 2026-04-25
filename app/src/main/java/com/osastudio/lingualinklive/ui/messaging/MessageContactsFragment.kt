package com.osastudio.lingualinklive.ui.messaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.osastudio.lingualinklive.databinding.FragmentMessageContactsBinding
import com.osastudio.lingualinklive.model.Contact
import com.osastudio.lingualinklive.ui.contacts.LanguageSelectDialog
import com.osastudio.lingualinklive.utils.LanguageUtils

class MessageContactsFragment : Fragment() {

    private var _binding: FragmentMessageContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MessageContactsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMessageContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val demoContacts = listOf(
            Contact("friend1", "FRIEND 1", "Nepali", "ne"),
            Contact("friend2", "FRIEND 2", "Japanese", "ja"),
            Contact("friend3", "FRIEND 3", "Korean", "ko"),
            Contact("friend4", "FRIEND 4", "Arabic", "ar")
        )

        adapter = MessageContactsAdapter(
            demoContacts,
            onLanguageClick = { contact -> showLanguageDialog(contact) },
            onChatClick = { contact -> openChat(contact) }
        )

        binding.rvMessageContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMessageContacts.adapter = adapter
    }

    private fun showLanguageDialog(contact: Contact) {
        LanguageSelectDialog(requireContext()) { selectedLanguage ->
            // Update language for contact
        }.show()
    }

    private fun openChat(contact: Contact) {
        val fragment = MessagingFragment.newInstance(contact.id, contact.name, contact.languageCode)
        parentFragmentManager.beginTransaction()
            .replace(requireActivity().findViewById<View>(com.osastudio.lingualinklive.R.id.nav_host_fragment).id, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}