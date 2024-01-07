package com.dev_marinov.chatalyze.data.firebase.register.dto

import com.dev_marinov.chatalyze.domain.model.firebase.UserFirebase


data class UserFirebaseDTO(
    val registerSenderPhone: String,
    val firebaseToken: String
) {
    fun mapToDomain() : UserFirebase {
        return UserFirebase(
            registerSenderPhone = registerSenderPhone,
            firebaseToken = firebaseToken
        )
    }
}