package com.osastudio.lingualinklive.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.osastudio.lingualinklive.model.Contact

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :contactId")
    suspend fun getContactById(contactId: String): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("UPDATE contacts SET language = :language, languageCode = :languageCode WHERE id = :contactId")
    suspend fun updateContactLanguage(contactId: String, language: String, languageCode: String)
}