package com.osastudio.lingualinklive.ui.video

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osastudio.lingualinklive.databinding.ItemVideoContactBinding
import com.osastudio.lingualinklive.model.Contact

class VideoContactsAdapter(
    private val contacts: List<Contact>,
    private val onVideoClick: (Contact) -> Unit
) : RecyclerView.Adapter<VideoContactsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemVideoContactBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        with(holder.binding) {
            tvFriendName.text = contact.name
            btnVideo.setOnClickListener { onVideoClick(contact) }
        }
    }

    override fun getItemCount() = contacts.size
}