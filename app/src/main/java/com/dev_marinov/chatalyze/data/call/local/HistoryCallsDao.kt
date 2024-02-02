package com.dev_marinov.chatalyze.data.call.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryCallsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyCallsEntity: HistoryCallsEntity)

    @Query("DELETE FROM history_calls")
    suspend fun deleteAll()

    @Query("SELECT * FROM history_calls")
    fun getAllFlow(): Flow<List<HistoryCallsEntity>>
}