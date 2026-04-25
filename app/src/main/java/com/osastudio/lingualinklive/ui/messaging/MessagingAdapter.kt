package com.osastudio.lingualinklive.ui.messaging

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osastudio.lingualinklive.R
import com.osastudio.lingualinklive.databinding.ItemMessageBubbleBinding
import com.osastudio.lingualinklive.model.Message

class MessagingAdapter(private var messages: List<Message>) :
    RecyclerView.Adapter<MessagingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMessageBubbleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBubbleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        with(holder.binding) {
            tvMessage.text = if (message.isFromMe) "You" else "Him"
            val lp = tvMessage.layoutParams as ViewGroup.MarginLayoutParams
            if (message.isFromMe) {
                tvMessage.setBackgroundResource(R.drawable.bg_message_bubble_right)
                (tvMessage.parent as ViewGroup).gravity = Gravity.END
                lp.marginStart = 80
                lp.marginEnd = 0
            } else {
                tvMessage.setBackgroundResource(R.drawable.bg_message_bubble_left)
                (tvMessage.parent as ViewGroup).gravity = Gravity.START
                lp.marginStart = 0
                lp.marginEnd = 80
            }
            tvMessage.layoutParams = lp
            // Show translated text
            tvMessage.text = if (message.isFromMe) message.originalText else message.translatedText
        }
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}