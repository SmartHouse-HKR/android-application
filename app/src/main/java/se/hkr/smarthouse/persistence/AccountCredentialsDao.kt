package se.hkr.smarthouse.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import se.hkr.smarthouse.models.AccountCredentials

@Dao
interface AccountCredentialsDao {
    @Insert
    fun insert(accountCredentials: AccountCredentials)

    @Update
    fun update(accountCredentials: AccountCredentials)

    @Delete
    fun delete(accountCredentials: AccountCredentials)

    @Query(
        """SELECT * FROM account_credential 
        WHERE hostUrl = :hostUrl"""
    )
    fun findUsersByHostUrl(
        hostUrl: String
    ): AccountCredentials
}