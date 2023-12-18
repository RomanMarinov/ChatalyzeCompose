//package com.dev_marinov.chatalyze.di
//
//import com.dev_marinov.chatalyze.data.chatMessage.ChatSocketRepository
//
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ServiceComponent
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.cio.CIO
//import io.ktor.client.features.json.JsonFeature
//import io.ktor.client.features.json.serializer.KotlinxSerializer
//import io.ktor.client.features.logging.Logging
//import io.ktor.client.features.websocket.WebSockets
//import javax.inject.Singleton
//
//@Module
//@InstallIn(ServiceComponent::class)
//object SocketServiceModule {
//
////    @Singleton
////    @Provides
////    fun provideHttpClient(): HttpClient {
////        return HttpClient(CIO) {
////            install(Logging)
////            install(WebSockets)
////            install(JsonFeature) {
////                serializer = KotlinxSerializer()
////            }
////        }
////    }
//
////    @Singleton
////    @Provides
////    fun provideSocketService(client: HttpClient): SocketService {
////        return SocketService(client = client)
////    }
//
//
////    @Singleton
////    @Provides
////    fun provideChatSocketRepository(socketService: SocketService): ChatSocketRepository {
////        return socketService
////    }
//
////    @Provides
////    @Singleton
////    fun provideSocketService(): SocketService = SocketService()
//
//}