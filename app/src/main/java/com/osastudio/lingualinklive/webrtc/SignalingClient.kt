package com.osastudio.lingualinklive.webrtc

import com.google.gson.JsonObject
import com.osastudio.lingualinklive.network.WebSocketManager
import com.osastudio.lingualinklive.utils.Constants

class SignalingClient(
    serverUrl: String,
    userId: String,
    private val listener: SignalingListener
) {
    private val wsManager = WebSocketManager(
        serverUrl, userId,
        onMessage = { handleMessage(it) },
        onConnected = { listener.onConnected() },
        onDisconnected = { listener.onDisconnected() }
    )

    interface SignalingListener {
        fun onConnected()
        fun onDisconnected()
        fun onOfferReceived(offer: String, fromUserId: String)
        fun onAnswerReceived(answer: String)
        fun onIceCandidateReceived(candidate: String, sdpMid: String, sdpMLineIndex: Int)
        fun onCallRequest(fromUserId: String, fromName: String, isVideo: Boolean)
        fun onCallAccepted(fromUserId: String)
        fun onCallRejected()
        fun onHangup()
        fun onTranslatedAudioReceived(audioBase64: String)
        fun onMessageReceived(text: String, fromUserId: String)
    }

    fun connect() = wsManager.connect()
    fun disconnect() = wsManager.disconnect()

    fun sendOffer(targetUserId: String, sdp: String) {
        wsManager.send(mapOf(
            "type" to Constants.SIGNAL_TYPE_OFFER,
            "target" to targetUserId,
            "sdp" to sdp
        ))
    }

    fun sendAnswer(targetUserId: String, sdp: String) {
        wsManager.send(mapOf(
            "type" to Constants.SIGNAL_TYPE_ANSWER,
            "target" to targetUserId,
            "sdp" to sdp
        ))
    }

    fun sendIceCandidate(targetUserId: String, candidate: String, sdpMid: String, sdpMLineIndex: Int) {
        wsManager.send(mapOf(
            "type" to Constants.SIGNAL_TYPE_ICE,
            "target" to targetUserId,
            "candidate" to candidate,
            "sdpMid" to sdpMid,
            "sdpMLineIndex" to sdpMLineIndex
        ))
    }

    fun sendCallRequest(targetUserId: String, callerName: String, isVideo: Boolean) {
        wsManager.send(mapOf(
            "type" to Constants.SIGNAL_TYPE_CALL,
            "target" to targetUserId,
            "callerName" to callerName,
            "isVideo" to isVideo
        ))
    }

    fun sendCallAccept(targetUserId: String) {
        wsManager.send(mapOf(
            "type" to Constants.SIGNAL_TYPE_ACCEPT,
            "target" to targetUserId
        ))
    }

    fun sendCallReject(targetUserId: String) {
        wsManager.send(mapOf(
            "type" to Constants.SIGNAL_TYPE_REJECT,
            "target" to targetUserId
        ))
    }

    fun sendHangup(targetUserId: String) {
        wsManager.send(mapOf(
            "type" to Constants.SIGNAL_TYPE_HANGUP,
            "target" to targetUserId
        ))
    }

    fun sendAudioChunk(targetUserId: String, audioBase64: String, myLang: String, targetLang: String) {
        wsManager.send(mapOf(
            "type" to Constants.SIGNAL_TYPE_AUDIO_CHUNK,
            "target" to targetUserId,
            "audio" to audioBase64,
            "sourceLang" to myLang,
            "targetLang" to targetLang
        ))
    }

    fun sendTextMessage(targetUserId: String, text: String, sourceLang: String, targetLang: String) {
        wsManager.send(mapOf(
            "type" to Constants.SIGNAL_TYPE_MESSAGE,
            "target" to targetUserId,
            "text" to text,
            "sourceLang" to sourceLang,
            "targetLang" to targetLang
        ))
    }

    private fun handleMessage(json: JsonObject) {
        when (json.get("type")?.asString) {
            Constants.SIGNAL_TYPE_OFFER -> {
                val sdp = json.get("sdp").asString
                val from = json.get("from").asString
                listener.onOfferReceived(sdp, from)
            }
            Constants.SIGNAL_TYPE_ANSWER -> {
                listener.onAnswerReceived(json.get("sdp").asString)
            }
            Constants.SIGNAL_TYPE_ICE -> {
                listener.onIceCandidateReceived(
                    json.get("candidate").asString,
                    json.get("sdpMid").asString,
                    json.get("sdpMLineIndex").asInt
                )
            }
            Constants.SIGNAL_TYPE_CALL -> {
                listener.onCallRequest(
                    json.get("from").asString,
                    json.get("callerName")?.asString ?: "Unknown",
                    json.get("isVideo")?.asBoolean ?: false
                )
            }
            Constants.SIGNAL_TYPE_ACCEPT -> listener.onCallAccepted(json.get("from").asString)
            Constants.SIGNAL_TYPE_REJECT -> listener.onCallRejected()
            Constants.SIGNAL_TYPE_HANGUP -> listener.onHangup()
            Constants.SIGNAL_TYPE_TRANSLATED_AUDIO -> {
                listener.onTranslatedAudioReceived(json.get("audio").asString)
            }
            Constants.SIGNAL_TYPE_MESSAGE -> {
                listener.onMessageReceived(
                    json.get("text").asString,
                    json.get("from").asString
                )
            }
        }
    }
}