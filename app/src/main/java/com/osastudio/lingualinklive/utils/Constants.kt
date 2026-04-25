package com.osastudio.lingualinklive.utils

object Constants {
    // Change this to your backend server address
    const val DEFAULT_SERVER_URL = "ws://YOUR_SERVER_IP:8000/ws"
    const val DEFAULT_HTTP_URL = "http://YOUR_SERVER_IP:8000/"

    const val PREFS_NAME = "lingua_link_prefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_USERNAME = "username"
    const val PREF_MY_LANGUAGE = "my_language"
    const val PREF_SERVER_URL = "server_url"

    const val SIGNAL_TYPE_OFFER = "offer"
    const val SIGNAL_TYPE_ANSWER = "answer"
    const val SIGNAL_TYPE_ICE = "ice_candidate"
    const val SIGNAL_TYPE_CALL = "call_request"
    const val SIGNAL_TYPE_ACCEPT = "call_accept"
    const val SIGNAL_TYPE_REJECT = "call_reject"
    const val SIGNAL_TYPE_HANGUP = "hangup"
    const val SIGNAL_TYPE_AUDIO_CHUNK = "audio_chunk"
    const val SIGNAL_TYPE_TRANSLATED_AUDIO = "translated_audio"
    const val SIGNAL_TYPE_MESSAGE = "message"

    const val EXTRA_CONTACT_ID = "contact_id"
    const val EXTRA_CONTACT_NAME = "contact_name"
    const val EXTRA_CONTACT_LANGUAGE = "contact_language"
    const val EXTRA_IS_INCOMING = "is_incoming"
    const val EXTRA_IS_VIDEO = "is_video"
}