package com.dev_marinov.chatalyze.presentation.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import java.util.Base64

data class TokenPayload(
    val userId: Int?,
    val expiresIn: Long?,
)

object DecodeToken {

    private val timestampCurMillis = System.currentTimeMillis() // Временная отметка прямо сейчас
    val timestampCurSeconds = timestampCurMillis / 1000


    fun execute(token: String): TokenPayload {
        val parts = token.split(".")
        return try {
            val charset = Charsets.UTF_8
            val payload = String(Base64.getUrlDecoder().decode(parts[1]), charset)

            val jsonObject = Json.parseToJsonElement(payload).jsonObject
            val userId = jsonObject["userId"]?.jsonPrimitive?.int
            val exp = jsonObject["exp"]?.jsonPrimitive?.long

            TokenPayload(userId = userId, expiresIn = exp)
        } catch (e: Exception) {
            TokenPayload(userId = null, expiresIn = null)
        }
    }

    fun howManyMoreMinutes(tokenTimestamp: Long): String {
        return (timestampCurSeconds - ((tokenTimestamp / 1000)) / 60).toString()
    }
}