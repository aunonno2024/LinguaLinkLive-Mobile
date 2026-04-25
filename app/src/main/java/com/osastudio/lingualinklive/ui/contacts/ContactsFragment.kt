package com.osastudio.lingualinklive.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.osastudio.lingualinklive.databinding.FragmentContactsBinding
import com.osastudio.lingualinklive.model.Contact
import com.osastudio.lingualinklive.ui.call.AudioCallActivity
import com.osastudio.lingualinklive.utils.Constants
import com.osastudio.lingualinklive.utils.LanguageUtils
import com.osastudio.lingualinklive.utils.PermissionUtils

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ContactsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadDemoContacts()
    }

    private fun setupRecyclerView() {
        adapter = ContactsAdapter(
            emptyList(),
            onLanguageClick = { contact -> showLanguageDialog(contact) },
            onCallClick = { contact -> startAudioCall(contact) }
        )
        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContacts.adapter = adapter
    }

    private fun loadDemoContacts() {
        // Demo contacts - in production these come from the database
        val demoContacts = listOf(
            Contact("friend1", "FRIEND 1", "English", "en"),
            Contact("friend2", "FRIEND 2", "Chinese", "zh"),
            Contact("friend3", "FRIEND 3", "Bangla", "bn"),
            Contact("friend4", "FRIEND 4", "Nepali", "ne")
        )
        adapter.updateContacts(demoContacts)
    }

    private fun showLanguageDialog(contact: Contact) {
        LanguageSelectDialog(requireContext()) { selectedLanguage ->
            val updatedContact = contact.copy(
                language = selectedLanguage,
                languageCode = LanguageUtils.getLanguageCode(selectedLanguage)
            )
            val index = adapter.contacts.indexOf(contact)
            // Update in database in production
            adapter.updateContacts(
                adapter.contacts.toMutableList().also { it[index] = updatedContact }
            )
        }.show()
    }

    private fun startAudioCall(contact: Contact) {
        if (!PermissionUtils.hasAudioPermission(requireActivity())) {
            PermissionUtils.requestAudioPermissions(requireActivity())
            return
        }
        val intent = Intent(requireContext(), AudioCallActivity::class.java).apply {
            putExtra(Constants.EXTRA_CONTACT_ID, contact.id)
            putExtra(Constants.EXTRA_CONTACT_NAME, contact.name)
            putExtra(Constants.EXTRA_CONTACT_LANGUAGE, contact.languageCode)
            putExtra(Constants.EXTRA_IS_INCOMING, false)
        }
        startActivity(intent)
    }

    // Make contacts accessible to adapter
    private val ContactsAdapter.contacts: List<Contact>
        get() = this.let {
            val field = it.javaClass.getDeclaredField("contacts")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            field.get(it) as List<Contact>
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}