package com.osastudio.lingualinklive.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.osastudio.lingualinklive.model.Message

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE contactId = :contactId ORDER BY timestamp ASC")
    fun getMessagesForContact(contactId: String): LiveData<List<Message>>

    @Insert
    suspend fun insertMessage(message: Message): Long

    @Query("DELETE FROM messages WHERE contactId = :contactId")
    suspend fun deleteMessagesForContact(contactId: String)
}