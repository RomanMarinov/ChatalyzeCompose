package com.dev_marinov.chatalyze.data.chatMessage.dto

import kotlinx.serialization.Serializable


@Serializable
data class TestClassText(
    val text: String
)

@Serializable
data class OnlineUsersDTO(
    val onlineUsers: List<OnlineUserState>
) {
    fun mapToDomain() : OnlineUsers {
        return OnlineUsers(
            onlineUsers = onlineUsers
        )
    }
}


@Serializable
data class OnlineUsers(
    val onlineUsers: List<OnlineUserState>
)



@Serializable
data class OnlineUserStateDTO(
    val userPhone: String,
    val onlineOrDate: String
) {
    fun mapToDomain() : OnlineUserState {
        return OnlineUserState(
            userPhone = userPhone,
            onlineOrDate = onlineOrDate
        )
    }
}
