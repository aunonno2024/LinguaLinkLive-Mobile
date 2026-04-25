package com.osastudio.lingualinklive.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: String,
    val originalText: String,
    val translatedText: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)