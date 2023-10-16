package com.dev_marinov.chatalyze.data.auth.dto

import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.google.gson.annotations.SerializedName

data class MessageResponseDTO(
    @SerializedName("httpStatusCode")
    val httpStatusCode: Int,
    @SerializedName("message")
    val message: String
) {
    fun mapToDomain(): MessageResponse {
        return MessageResponse(
            httpStatusCode = httpStatusCode,
            message = message
        )
    }
}

