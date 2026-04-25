package com.osastudio.lingualinklive.ui.call

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.osastudio.lingualinklive.databinding.ActivityVideoCallBinding
import com.osastudio.lingualinklive.utils.Constants
import com.osastudio.lingualinklive.webrtc.SignalingClient
import com.osastudio.lingualinklive.webrtc.WebRTCManager
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class VideoCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoCallBinding
    private lateinit var webRTCManager: WebRTCManager
    private var signalingClient: SignalingClient? = null

    private var contactId = ""
    private var isIncoming = false
    private var isMuted = false
    private var isCameraOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactId = intent.getStringExtra(Constants.EXTRA_CONTACT_ID) ?: ""
        isIncoming = intent.getBooleanExtra(Constants.EXTRA_IS_INCOMING, false)

        val prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val wsUrl = prefs.getString(Constants.PREF_SERVER_URL, Constants.DEFAULT_SERVER_URL) ?: Constants.DEFAULT_SERVER_URL
        val userId = prefs.getString(Constants.PREF_USER_ID, "") ?: ""

        webRTCManager = WebRTCManager(this, object : WebRTCManager.WebRTCListener {
            override fun onLocalSdpReady(sdp: SessionDescription) {
                if (sdp.type == SessionDescription.Type.OFFER) {
                    signalingClient?.sendOffer(contactId, sdp.description)
                } else {
                    signalingClient?.sendAnswer(contactId, sdp.description)
                }
            }
            override fun onIceCandidateReady(candidate: IceCandidate) {
                signalingClient?.sendIceCandidate(
                    contactId, candidate.sdp, candidate.sdpMid, candidate.sdpMLineIndex
                )
            }
            override fun onConnected() {}
            override fun onDisconnected() { finish() }
        })

        webRTCManager.initSurfaceViews(binding.svLocalVideo, binding.svRemoteVideo)
        webRTCManager.startLocalStream(withVideo = true)
        webRTCManager.createPeerConnection()

        setupSignaling(wsUrl, userId)
        setupControls()

        if (!isIncoming) {
            webRTCManager.createOffer()
        }
    }

    private fun setupSignaling(wsUrl: String, userId: String) {
        signalingClient = SignalingClient(wsUrl, userId, object : SignalingClient.SignalingListener {
            override fun onConnected() {}
            override fun onDisconnected() {}
            override fun onOfferReceived(offer: String, fromUserId: String) {
                webRTCManager.setRemoteDescription(offer, SessionDescription.Type.OFFER)
                webRTCManager.createAnswer()
            }
            override fun onAnswerReceived(answer: String) {
                webRTCManager.setRemoteDescription(answer, SessionDescription.Type.ANSWER)
            }
            override fun onIceCandidateReceived(candidate: String, sdpMid: String, sdpMLineIndex: Int) {
                webRTCManager.addIceCandidate(candidate, sdpMid, sdpMLineIndex)
            }
            override fun onCallRequest(fromUserId: String, fromName: String, isVideo: Boolean) {}
            override fun onCallAccepted(fromUserId: String) {}
            override fun onCallRejected() { finish() }
            override fun onHangup() { finish() }
            override fun onTranslatedAudioReceived(audioBase64: String) {
                // Show subtitle overlay for video calls
                runOnUiThread {
                    binding.tvTranslationOverlay.visibility = View.VISIBLE
                    binding.tvTranslationOverlay.text = "🔊 Translating…"
                }
            }
            override fun onMessageReceived(text: String, fromUserId: String) {}
        })
        signalingClient?.connect()
    }

    private fun setupControls() {
        binding.btnEndVideoCall.setOnClickListener {
            signalingClient?.sendHangup(contactId)
            finish()
        }
        binding.btnMuteVideo.setOnClickListener {
            isMuted = !isMuted
            webRTCManager.toggleMute(isMuted)
            binding.btnMuteVideo.alpha = if (isMuted) 0.5f else 1.0f
        }
        binding.btnCameraToggle.setOnClickListener {
            isCameraOn = !isCameraOn
            webRTCManager.toggleCamera(isCameraOn)
            binding.btnCameraToggle.alpha = if (isCameraOn) 1.0f else 0.5f
        }
        binding.btnFlipCamera.setOnClickListener {
            webRTCManager.switchCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webRTCManager.dispose()
        signalingClient?.disconnect()
    }
}