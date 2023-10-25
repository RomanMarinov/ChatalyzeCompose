package com.dev_marinov.chatalyze.data.auth.dto

data class ForgotPasswordCodeDTO(
    val email: String,
    val code: Int
)
