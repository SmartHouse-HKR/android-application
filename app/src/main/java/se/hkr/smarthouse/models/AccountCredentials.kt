package se.hkr.smarthouse.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "account_credential"
)
data class AccountCredentials(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_pk")
    val account_pk: Int,
    @ColumnInfo(name = "username")
    var username: String? = null,
    @ColumnInfo(name = "password")
    var password: String? = null,
    @ColumnInfo(name = "hostUrl")
    var hostUrl: String? = null,
    @ColumnInfo(name = "clientId")
    var clientId: String = UUID.randomUUID().toString()
)