package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.firebase.UserFirebase

interface FirebaseRegisterRepository {
    suspend fun firebaseRegister(userFirebase: UserFirebase): MessageResponse?
}