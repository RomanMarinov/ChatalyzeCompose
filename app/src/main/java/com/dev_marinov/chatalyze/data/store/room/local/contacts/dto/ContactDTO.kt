package com.dev_marinov.chatalyze.data.store.room.local.contacts.dto

import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact

data class ContactDTO(
    val name: String,
    val phoneNumber: String,
    val photo: String?
) {
    fun mapToDomain() : Contact {
        return Contact(
            name = name,
            phoneNumber = phoneNumber,
            photo = photo
        )
    }
}

