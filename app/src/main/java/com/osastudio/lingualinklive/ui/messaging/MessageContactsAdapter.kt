package com.osastudio.lingualinklive.ui.messaging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osastudio.lingualinklive.databinding.ItemMessageContactBinding
import com.osastudio.lingualinklive.model.Contact

class MessageContactsAdapter(
    private val contacts: List<Contact>,
    private val onLanguageClick: (Contact) -> Unit,
    private val onChatClick: (Contact) -> Unit
) : RecyclerView.Adapter<MessageContactsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMessageContactBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        with(holder.binding) {
            tvFriendName.text = contact.name
            tvLanguage.text = contact.language
            tvLanguage.setOnClickListener { onLanguageClick(contact) }
            btnChat.setOnClickListener { onChatClick(contact) }
        }
    }

    override fun getItemCount() = contacts.size
}