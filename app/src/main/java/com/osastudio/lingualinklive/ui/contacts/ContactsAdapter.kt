package com.osastudio.lingualinklive.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osastudio.lingualinklive.databinding.ItemContactBinding
import com.osastudio.lingualinklive.model.Contact

class ContactsAdapter(
    private var contacts: List<Contact>,
    private val onLanguageClick: (Contact) -> Unit,
    private val onCallClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        with(holder.binding) {
            tvFriendName.text = contact.name
            tvLanguage.text = contact.language
            tvLanguage.setOnClickListener { onLanguageClick(contact) }
            btnCall.setOnClickListener { onCallClick(contact) }
        }
    }

    override fun getItemCount() = contacts.size

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}