package com.dev_marinov.chatalyze.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(email: String, password: String) : String?
    suspend fun signInUser(email: String, password: String) : String?
    suspend fun sendEmail(email: String)
}