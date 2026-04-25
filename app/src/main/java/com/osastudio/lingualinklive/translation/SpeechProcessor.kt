package com.osastudio.lingualinklive.translation

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SpeechProcessor {

    private val TAG = "SpeechProcessor"
    private val SAMPLE_RATE = 16000
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT) * 4

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startRecording(onChunkReady: (ByteArray) -> Unit) {
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE
        )
        isRecording = true
        audioRecord?.startRecording()

        recordingJob = scope.launch {
            val buffer = ByteArray(BUFFER_SIZE)
            val chunkBuffer = ByteArrayOutputStream()
            var chunkSize = 0
            val CHUNK_THRESHOLD = SAMPLE_RATE * 2 * 2 // 2 seconds of 16-bit mono

            while (isRecording) {
                val bytesRead = audioRecord?.read(buffer, 0, BUFFER_SIZE) ?: break
                if (bytesRead > 0) {
                    chunkBuffer.write(buffer, 0, bytesRead)
                    chunkSize += bytesRead
                    if (chunkSize >= CHUNK_THRESHOLD) {
                        val chunk = chunkBuffer.toByteArray()
                        onChunkReady(chunk)
                        chunkBuffer.reset()
                        chunkSize = 0
                    }
                }
            }
        }
    }

    fun stopRecording() {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    fun playAudio(audioBytes: ByteArray) {
        scope.launch {
            try {
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AUDIO_FORMAT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(audioBytes.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(audioBytes, 0, audioBytes.size)
                audioTrack.play()

                // Wait for playback to complete
                Thread.sleep((audioBytes.size.toLong() * 1000) / (SAMPLE_RATE * 2))
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                Log.e(TAG, "Playback error: $e")
            }
        }
    }

    fun saveToWavFile(pcmData: ByteArray, file: File) {
        val totalDataLen = pcmData.size + 36
        val header = ByteArray(44)

        // RIFF header
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0 // PCM
        header[22] = 1; header[23] = 0 // Mono
        header[24] = (SAMPLE_RATE and 0xff).toByte()
        header[25] = ((SAMPLE_RATE shr 8) and 0xff).toByte()
        header[26] = ((SAMPLE_RATE shr 16) and 0xff).toByte()
        header[27] = ((SAMPLE_RATE shr 24) and 0xff).toByte()
        val byteRate = SAMPLE_RATE * 2
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = 2; header[33] = 0 // Block align
        header[34] = 16; header[35] = 0 // Bits per sample
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (pcmData.size and 0xff).toByte()
        header[41] = ((pcmData.size shr 8) and 0xff).toByte()
        header[42] = ((pcmData.size shr 16) and 0xff).toByte()
        header[43] = ((pcmData.size shr 24) and 0xff).toByte()

        FileOutputStream(file).use { fos ->
            fos.write(header)
            fos.write(pcmData)
        }
    }
}