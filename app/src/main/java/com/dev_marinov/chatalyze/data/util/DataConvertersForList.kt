package com.dev_marinov.chatalyze.data.util

import androidx.room.TypeConverter
import com.dev_marinov.chatalyze.data.store.room.local.contacts.dto.ContactDTO
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DataConvertersForList {

    @TypeConverter
    fun fromContactDtoList(contactDtoList: List<ContactDTO>): String {
        val gson = Gson()
        return gson.toJson(contactDtoList)
    }

    @TypeConverter
    fun toContactDtoList(contactDtoString: String): List<ContactDTO> {
        val listType = object : TypeToken<List<ContactDTO>>() {}.type
        val gson = Gson()
        return gson.fromJson(contactDtoString, listType)
    }
}