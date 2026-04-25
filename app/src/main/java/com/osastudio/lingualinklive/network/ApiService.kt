package com.osastudio.lingualinklive.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("stt")
    suspend fun speechToText(
        @Part audio: MultipartBody.Part,
        @Part("language") language: RequestBody
    ): Response<SttResponse>

    @POST("translate")
    suspend fun translateText(
        @Body request: TranslateRequest
    ): Response<TranslateResponse>

    @POST("tts")
    suspend fun textToSpeech(
        @Body request: TtsRequest
    ): Response<ResponseBody>

    @POST("register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}

data class SttResponse(val text: String, val language: String)
data class TranslateRequest(val text: String, val sourceLang: String, val targetLang: String)
data class TranslateResponse(val translatedText: String)
data class TtsRequest(val text: String, val language: String)
data class RegisterRequest(val userId: String, val username: String, val language: String)
data class RegisterResponse(val success: Boolean, val message: String)