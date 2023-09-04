package com.dev_marinov.chatalyze.data.model.auth

data class RegisterRequest(
    val login: String,
    val password: String,
    val email: String
)
