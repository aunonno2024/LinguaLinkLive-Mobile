package com.osastudio.lingualinklive.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.osastudio.lingualinklive.model.Contact
import com.osastudio.lingualinklive.model.Message

@Database(entities = [Contact::class, Message::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lingua_link_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}