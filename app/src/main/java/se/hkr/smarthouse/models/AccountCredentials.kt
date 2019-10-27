package se.hkr.smarthouse.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "account_credentials")
data class AccountCredentials(
    @SerializedName("pk")
    @Expose
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    var pk: Int,
    @SerializedName("email")
    @Expose
    @ColumnInfo(name = "email")
    var email: String,
    @SerializedName("password")
    @Expose
    @ColumnInfo(name = "password")
    var password: String
)