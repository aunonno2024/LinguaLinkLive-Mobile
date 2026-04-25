package com.osastudio.lingualinklive.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: String,
    val name: String,
    val language: String = "English",
    val languageCode: String = "en",
    val phoneNumber: String = "",
    val isOnline: Boolean = false
)