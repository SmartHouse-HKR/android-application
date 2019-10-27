package se.hkr.smarthouse.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import se.hkr.smarthouse.models.AccountCredentials

@Database(
    entities = [
        AccountCredentials::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLoginCredentialsDao(): AccountCredentialsDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}