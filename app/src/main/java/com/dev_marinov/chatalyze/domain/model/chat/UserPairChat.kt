package com.dev_marinov.chatalyze.domain.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class UserPairChat(
    val sender: String? = null,
    val recipient: String? = null
)
