package com.dev_marinov.chatalyze.data.auth.dto

data class UserTokensDetails(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int?
)
