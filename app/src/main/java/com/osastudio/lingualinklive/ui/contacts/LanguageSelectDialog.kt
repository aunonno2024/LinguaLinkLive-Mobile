package com.osastudio.lingualinklive.ui.contacts

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.osastudio.lingualinklive.R
import com.osastudio.lingualinklive.utils.LanguageUtils

class LanguageSelectDialog(
    context: Context,
    private val onLanguageSelected: (String) -> Unit
) {
    private val dialog = Dialog(context, R.style.Theme_LinguaLinkLive)

    fun show() {
        val view = LayoutInflater.from(dialog.context)
            .inflate(R.layout.dialog_language_select, null)

        val rv = view.findViewById<RecyclerView>(R.id.rv_languages)
        rv.layoutManager = LinearLayoutManager(dialog.context)
        rv.adapter = LanguageListAdapter(LanguageUtils.SUPPORTED_LANGUAGES) { language ->
            onLanguageSelected(language)
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private inner class LanguageListAdapter(
        private val languages: List<String>,
        private val onClick: (String) -> Unit
    ) : RecyclerView.Adapter<LanguageListAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tv: TextView = view.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            v.findViewById<TextView>(android.R.id.text1).setTextColor(
                parent.context.getColor(R.color.white)
            )
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.tv.text = languages[position]
            holder.itemView.setOnClickListener { onClick(languages[position]) }
        }

        override fun getItemCount() = languages.size
    }
}