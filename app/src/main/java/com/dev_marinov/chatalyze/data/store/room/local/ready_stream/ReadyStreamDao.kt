package com.dev_marinov.chatalyze.data.store.room.local.ready_stream

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadyStreamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(readyStreamEntity: ReadyStreamEntity)

    @Query("DELETE FROM ready_stream")
    fun delete()

    @Query("SELECT * FROM ready_stream")
    fun getReadyStream() : Flow<ReadyStreamEntity?>
}