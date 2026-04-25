package com.osastudio.lingualinklive.ui.call

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.osastudio.lingualinklive.databinding.ActivityAudioCallBinding
import com.osastudio.lingualinklive.network.RetrofitClient
import com.osastudio.lingualinklive.translation.SpeechProcessor
import com.osastudio.lingualinklive.translation.TranslationService
import com.osastudio.lingualinklive.utils.Constants
import com.osastudio.lingualinklive.utils.LanguageUtils
import com.osastudio.lingualinklive.webrtc.SignalingClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AudioCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioCallBinding
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var speechProcessor: SpeechProcessor
    private lateinit var translationService: TranslationService
    private var signalingClient: SignalingClient? = null

    private var contactId = ""
    private var contactName = ""
    private var contactLangCode = "en"
    private var isIncoming = false
    private var isMuted = false
    private var isSpeakerOn = true
    private var isTranslationMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityAudioCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactId = intent.getStringExtra(Constants.EXTRA_CONTACT_ID) ?: ""
        contactName = intent.getStringExtra(Constants.EXTRA_CONTACT_NAME) ?: "Unknown"
        contactLangCode = intent.getStringExtra(Constants.EXTRA_CONTACT_LANGUAGE) ?: "en"
        isIncoming = intent.getBooleanExtra(Constants.EXTRA_IS_INCOMING, false)

        binding.tvCallerNumber.text = contactName
        binding.tvCallStatus.text = if (isIncoming) "Incoming call…" else "Calling…"

        val prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val serverUrl = prefs.getString(Constants.PREF_SERVER_URL, Constants.DEFAULT_HTTP_URL) ?: Constants.DEFAULT_HTTP_URL
        val userId = prefs.getString(Constants.PREF_USER_ID, "") ?: ""
        val myLang = prefs.getString(Constants.PREF_MY_LANGUAGE, "Bengali") ?: "Bengali"
        val myLangCode = LanguageUtils.getLanguageCode(myLang)
        val wsUrl = prefs.getString(Constants.PREF_SERVER_URL, Constants.DEFAULT_SERVER_URL) ?: Constants.DEFAULT_SERVER_URL

        speechProcessor = SpeechProcessor()

        val apiService = RetrofitClient.getApiService(serverUrl)
        translationService = TranslationService(apiService)

        setupSignaling(wsUrl, userId, myLangCode)
        setupControls()
        startTranslatedCall(myLangCode)
    }

    private fun setupSignaling(wsUrl: String, userId: String, myLangCode: String) {
        signalingClient = SignalingClient(wsUrl, userId, object : SignalingClient.SignalingListener {
            override fun onConnected() {
                binding.tvCallStatus.text = "Connected"
            }
            override fun onDisconnected() {
                binding.tvCallStatus.text = "Disconnected"
            }
            override fun onOfferReceived(offer: String, fromUserId: String) {}
            override fun onAnswerReceived(answer: String) {}
            override fun onIceCandidateReceived(candidate: String, sdpMid: String, sdpMLineIndex: Int) {}
            override fun onCallRequest(fromUserId: String, fromName: String, isVideo: Boolean) {}
            override fun onCallAccepted(fromUserId: String) {
                binding.tvCallStatus.text = "Connected"
            }
            override fun onCallRejected() {
                binding.tvCallStatus.text = "Call Rejected"
                finish()
            }
            override fun onHangup() { finish() }
            override fun onTranslatedAudioReceived(audioBase64: String) {
                if (!isTranslationMuted) {
                    val audioBytes = Base64.decode(audioBase64, Base64.DEFAULT)
                    speechProcessor.playAudio(audioBytes)
                    binding.tvTranslationStatus.text = "🔊 Translating…"
                }
            }
            override fun onMessageReceived(text: String, fromUserId: String) {}
        })
        signalingClient?.connect()
    }

    private fun startTranslatedCall(myLangCode: String) {
        speechProcessor.startRecording { pcmChunk ->
            if (!isMuted) {
                scope.launch(Dispatchers.IO) {
                    // Save chunk as WAV
                    val tempFile = File(cacheDir, "chunk_${System.currentTimeMillis()}.wav")
                    speechProcessor.saveToWavFile(pcmChunk, tempFile)

                    // STT
                    val transcript = translationService.transcribeAudio(tempFile, myLangCode)
                    tempFile.delete()

                    if (!transcript.isNullOrBlank()) {
                        // Translate
                        val translated = translationService.translateText(transcript, myLangCode, contactLangCode)

                        if (!translated.isNullOrBlank()) {
                            // TTS
                            val audioBytes = translationService.textToSpeech(translated, contactLangCode)
                            if (audioBytes != null) {
                                val audioBase64 = Base64.encodeToString(audioBytes, Base64.DEFAULT)
                                signalingClient?.sendAudioChunk(contactId, audioBase64, myLangCode, contactLangCode)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupControls() {
        binding.btnEndCall.setOnClickListener {
            signalingClient?.sendHangup(contactId)
            finish()
        }
        binding.btnAcceptCall.setOnClickListener {
            signalingClient?.sendCallAccept(contactId)
            binding.tvCallStatus.text = "Connected"
        }
        binding.btnMute.setOnClickListener {
            isMuted = !isMuted
            binding.btnMute.alpha = if (isMuted) 0.5f else 1.0f
        }
        binding.btnMuteTranslation.setOnClickListener {
            isTranslationMuted = !isTranslationMuted
            binding.btnMuteTranslation.alpha = if (isTranslationMuted) 0.5f else 1.0f
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechProcessor.stopRecording()
        signalingClient?.disconnect()
    }
}