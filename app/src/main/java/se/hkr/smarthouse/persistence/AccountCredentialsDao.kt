package se.hkr.smarthouse.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import se.hkr.smarthouse.models.AccountCredentials

@Dao
interface AccountCredentialsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountCredentials: AccountCredentials): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountCredentials: AccountCredentials): Long

    @Query("SELECT * FROM account_credentials WHERE pk = :pk")
    fun searchByPk(pk: Int): AccountCredentials?

    @Query("SELECT * FROM account_credentials WHERE email = :email")
    fun searchByEmail(email: String): AccountCredentials?
}