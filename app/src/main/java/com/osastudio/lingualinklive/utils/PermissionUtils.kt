package com.osastudio.lingualinklive.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val REQUEST_AUDIO_PERMISSIONS = 1001
    const val REQUEST_VIDEO_PERMISSIONS = 1002

    fun hasAudioPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasCameraPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestAudioPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS),
            REQUEST_AUDIO_PERMISSIONS
        )
    }

    fun requestVideoPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            ),
            REQUEST_VIDEO_PERMISSIONS
        )
    }
}