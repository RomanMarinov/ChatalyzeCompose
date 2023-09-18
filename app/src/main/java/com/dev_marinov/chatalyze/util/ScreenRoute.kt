package com.dev_marinov.chatalyze.util

sealed class ScreenRoute(val route: String) {
    object SplashScreen : ScreenRoute("splash_screen")
    object AuthScreen : ScreenRoute("auth_screen")
    object ForgotScreen : ScreenRoute("forgot_password_screen")
    object SignUpScreen : ScreenRoute("sign_up_screen")

    object ChatalyzeScreen : ScreenRoute("chatalyze_screen")
    object ChatsScreen : ScreenRoute("chats_screen")
    object CallScreen : ScreenRoute("call_screen")
    object SettingScreen : ScreenRoute("setting_screen")

    object ChatScreen : ScreenRoute("chat_screen")
}
