package com.dev_marinov.chatalyze.domain.model.auth

data class PairOfTokens(
    val accessToken: String,
    val refreshToken: String
)
