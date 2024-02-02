package com.dev_marinov.chatalyze.data.store.room.local.ready_stream

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dev_marinov.chatalyze.data.firebase.model.StateReadyStream

@Entity(tableName = "ready_stream")
data class ReadyStreamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "sender_phone")
    val senderPhone: String? = null,
    @ColumnInfo(name = "recipient_phone")
    val recipientPhone: String? = null,
    @ColumnInfo(name = "type_firebase_command")
    val typeFirebaseCommand: String? = null,
) {
//    companion object {
//        fun mapToStateReadyStream(readyStreamEntity: ReadyStreamEntity): StateReadyStream {
//            return StateReadyStream(
//                senderPhone = readyStreamEntity.senderPhone ?: "",
//                recipientPhone = readyStreamEntity.recipientPhone ?: "",
//                typeFirebaseCommand = readyStreamEntity.typeFirebaseCommand ?: ""
//            )
//        }
//    }
}
