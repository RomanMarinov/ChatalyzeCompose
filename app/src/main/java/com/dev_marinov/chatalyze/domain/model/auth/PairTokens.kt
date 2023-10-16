package com.dev_marinov.chatalyze.domain.model.auth

data class PairTokens(
    val accessToken: String,
    val refreshToken: String
)
