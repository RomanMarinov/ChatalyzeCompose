package com.dev_marinov.chatalyze.data.store.room.local.online_users

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OnlineUsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(onlineUsersEntity: List<OnlineUsersEntity>)

    @Query("SELECT * FROM online_users")
    fun getAllFlow() : Flow<List<OnlineUsersEntity>>
}