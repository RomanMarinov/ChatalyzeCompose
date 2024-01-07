package com.dev_marinov.chatalyze.data.store.room.local.contacts

import androidx.room.*
import com.dev_marinov.chatalyze.data.store.room.local.contacts.dto.ContactDTO
import com.dev_marinov.chatalyze.data.util.DataConvertersForList

@Entity(tableName = "contacts")
data class ContactsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @ColumnInfo(name = "photo")
    val photo: String?
) {
//    companion object {
//        fun mapFromDomain(contact: Contact): ContactsEntity {
//            return ContactsEntity(
//                id = videos.id,
//                contacts = videos.trailers
//            )
//        }
//    }
//
//    fun mapToDomain(): Videos {
//        return Videos(
//            id = id,
//            trailers = contacts,
//        )
//    }
}