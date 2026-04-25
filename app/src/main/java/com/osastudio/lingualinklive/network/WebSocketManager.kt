package com.osastudio.lingualinklive.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class WebSocketManager(
    private val serverUrl: String,
    private val userId: String,
    private val onMessage: (JsonObject) -> Unit,
    private val onConnected: () -> Unit,
    private val onDisconnected: () -> Unit
) {
    private val TAG = "WebSocketManager"
    private val gson = Gson()
    private var webSocketClient: WebSocketClient? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun connect() {
        val uri = URI("$serverUrl/$userId")
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "WebSocket Connected")
                scope.launch(Dispatchers.Main) { onConnected() }
            }

            override fun onMessage(message: String?) {
                message?.let {
                    try {
                        val jsonObject = gson.fromJson(it, JsonObject::class.java)
                        scope.launch(Dispatchers.Main) { onMessage(jsonObject) }
                    } catch (e: Exception) {
                        Log.e(TAG, "Parse error: $e")
                    }
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "WebSocket Closed: $reason")
                scope.launch(Dispatchers.Main) { onDisconnected() }
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "WebSocket Error: $ex")
            }
        }
        webSocketClient?.connect()
    }

    fun send(data: Map<String, Any>) {
        val json = gson.toJson(data)
        scope.launch(Dispatchers.IO) {
            webSocketClient?.send(json)
        }
    }

    fun disconnect() {
        webSocketClient?.close()
    }

    fun isConnected() = webSocketClient?.isOpen == true
}