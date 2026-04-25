package com.osastudio.lingualinklive.ui.messaging

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.osastudio.lingualinklive.databinding.FragmentMessagingBinding
import com.osastudio.lingualinklive.model.Message
import com.osastudio.lingualinklive.network.RetrofitClient
import com.osastudio.lingualinklive.translation.TranslationService
import com.osastudio.lingualinklive.utils.Constants
import com.osastudio.lingualinklive.utils.LanguageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessagingFragment : Fragment() {

    private var _binding: FragmentMessagingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MessagingAdapter
    private val messages = mutableListOf<Message>()
    private val scope = CoroutineScope(Dispatchers.Main)

    private var contactId: String = ""
    private var contactName: String = ""
    private var contactLangCode: String = "en"

    companion object {
        fun newInstance(contactId: String, contactName: String, langCode: String): MessagingFragment {
            return MessagingFragment().apply {
                arguments = Bundle().apply {
                    putString("contact_id", contactId)
                    putString("contact_name", contactName)
                    putString("lang_code", langCode)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = arguments?.getString("contact_id") ?: ""
        contactName = arguments?.getString("contact_name") ?: ""
        contactLangCode = arguments?.getString("lang_code") ?: "en"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMessagingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MessagingAdapter(messages)
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.rvMessages.adapter = adapter

        binding.btnSend.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val myLang = prefs.getString(Constants.PREF_MY_LANGUAGE, "Bengali") ?: "Bengali"
        val myLangCode = LanguageUtils.getLanguageCode(myLang)
        val serverUrl = prefs.getString(Constants.PREF_SERVER_URL, Constants.DEFAULT_HTTP_URL) ?: Constants.DEFAULT_HTTP_URL

        val sentMessage = Message(
            contactId = contactId,
            originalText = text,
            translatedText = text,
            isFromMe = true
        )
        messages.add(sentMessage)
        adapter.updateMessages(messages.toList())
        binding.etMessage.text.clear()
        binding.rvMessages.scrollToPosition(messages.size - 1)

        // Translate and send via WebSocket
        scope.launch {
            val apiService = RetrofitClient.getApiService(serverUrl)
            val translationService = TranslationService(apiService)
            val translated = translationService.translateText(text, myLangCode, contactLangCode)
            // In production: send translated text via WebSocket to the contact
            // signalingClient.sendTextMessage(contactId, translated ?: text, myLangCode, contactLangCode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}