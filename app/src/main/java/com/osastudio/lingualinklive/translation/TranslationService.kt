package com.osastudio.lingualinklive.translation

import android.util.Log
import com.osastudio.lingualinklive.network.ApiService
import com.osastudio.lingualinklive.network.TranslateRequest
import com.osastudio.lingualinklive.network.TtsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class TranslationService(private val apiService: ApiService) {

    private val TAG = "TranslationService"

    suspend fun transcribeAudio(audioFile: File, language: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = audioFile.asRequestBody("audio/wav".toMediaType())
                val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestBody)
                val langBody = language.toRequestBody("text/plain".toMediaType())

                val response = apiService.speechToText(audioPart, langBody)
                if (response.isSuccessful) {
                    response.body()?.text
                } else {
                    Log.e(TAG, "STT error: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "STT exception: $e")
                null
            }
        }
    }

    suspend fun translateText(text: String, sourceCode: String, targetCode: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.translateText(
                    TranslateRequest(text, sourceCode, targetCode)
                )
                if (response.isSuccessful) {
                    response.body()?.translatedText
                } else {
                    Log.e(TAG, "Translate error: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Translate exception: $e")
                null
            }
        }
    }

    suspend fun textToSpeech(text: String, languageCode: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.textToSpeech(TtsRequest(text, languageCode))
                if (response.isSuccessful) {
                    response.body()?.bytes()
                } else {
                    Log.e(TAG, "TTS error: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "TTS exception: $e")
                null
            }
        }
    }
}