package com.dev_marinov.chatalyze.data.chatMessage.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResponseOnlineStateDTO(
    val onlineUserStates: List<OnlineUserStateDTO>
) {
    fun mapToDomain() : ResponseOnlineState {
        return ResponseOnlineState(
            onlineUserStates = onlineUserStates.map { it.mapToDomain() }
        )
    }
}
