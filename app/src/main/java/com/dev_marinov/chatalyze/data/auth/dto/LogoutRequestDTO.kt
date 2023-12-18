package com.dev_marinov.chatalyze.data.auth.dto

data class LogoutRequestDTO(
    val refreshToken: String,
    val senderPhone: String
)
