package com.dev_marinov.chatalyze.data.chatMessage.dto

import kotlinx.serialization.Serializable

@Serializable
data class OnlineOrDateDTO(
    val userPhone: String,
    val onlineOrDate: String
) {
    fun mapToDomain() : OnlineOrDate {
        return OnlineOrDate(
            userPhone = userPhone,
            onlineOrDate = onlineOrDate
        )
    }
}
