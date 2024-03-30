package com.dev_marinov.chatalyze.data.auth.dto

import com.dev_marinov.chatalyze.domain.model.auth.PairOfTokens
import kotlinx.serialization.Serializable

@Serializable
data class PairTokensDTO(
    val accessToken: String = "",
    val refreshToken: String = ""
) {
    fun mapToDomain() : PairOfTokens {
        return PairOfTokens(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}
