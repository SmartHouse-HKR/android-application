package se.hkr.smarthouse.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import se.hkr.smarthouse.models.AccountProperties
import se.hkr.smarthouse.models.AuthToken

@Database(
    entities = [
        AccountProperties::class,
        AuthToken::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}