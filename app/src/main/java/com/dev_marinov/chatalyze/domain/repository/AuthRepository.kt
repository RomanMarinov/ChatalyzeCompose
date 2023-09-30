package com.dev_marinov.chatalyze.domain.repository

interface AuthRepository {
    suspend fun registerUser(email: String, password: String) : String?
    suspend fun signInUser(email: String, password: String)
    suspend fun sendEmail(email: String)
}