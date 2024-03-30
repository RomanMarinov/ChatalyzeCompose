package com.dev_marinov.chatalyze.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.http.HttpHeaders
import io.ktor.util.AttributeKey

//class JwtTokenRefresher() {
//    fun intercept(client: HttpClient) {
//        client.requestPipeline.intercept(HttpRequestPipeline.State) {
//            val urlsToRefreshToken = listOf("https://example.com/api/refreshToken1", "https://example.com/api/refreshToken2")
//
//            if (context.url.toString() in urlsToRefreshToken) {
//
//
//
//
//                // Update the Authorization header with the new JWT token
//                context.headers.append(HttpHeaders.Authorization, "Bearer $jwtToken")
//            }
//        }
//    }
//
////    fun intercept(client: HttpClient) {
////        client.requestPipeline.intercept(HttpRequestPipeline.State) {
////            val currentUrl = context.url.toString()
////            if (shouldRefreshToken(currentUrl)) {
////                context.headers.append(HttpHeaders.Authorization, "Bearer $jwtToken")
////            }
////        }
////    }
//}