package com.dev_marinov.chatalyze.data.store.room.local.online_users

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "online_users")
data class OnlineUsersEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_phone")
    val userPhone: String,
    @ColumnInfo(name = "online_or_offline")
    val onlineOrOffline: String
)
