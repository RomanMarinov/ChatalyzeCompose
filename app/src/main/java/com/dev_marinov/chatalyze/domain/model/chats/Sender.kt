package com.dev_marinov.chatalyze.domain.model.chats

import com.dev_marinov.chatalyze.data.chats.dto.SenderDTO

data class Sender(
    val sender: String
) {
    fun mapToData() : SenderDTO {
       return SenderDTO(
           sender = sender
       )
    }
}