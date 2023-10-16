package com.dev_marinov.chatalyze.domain.model.auth

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    val httpStatusCode: Int,
    val message: String
)
