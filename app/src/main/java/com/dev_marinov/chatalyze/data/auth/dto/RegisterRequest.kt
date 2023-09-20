package com.dev_marinov.chatalyze.data.auth.dto

data class RegisterRequest(
    val login: String,
    val password: String,
    val email: String
)
