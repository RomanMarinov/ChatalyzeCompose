package com.dev_marinov.chatalyze.presentation.util


const val RECIPIENT_NAME = "RECIPIENT_NAME"
const val RECIPIENT_PHONE = "RECIPIENT_PHONE"
const val SENDER_PHONE = "SENDER_PHONE"
const val TYPE_EVENT = "TYPE_EVENT"

sealed class ScreenRoute(val route: String) {
    object SplashScreen : ScreenRoute("splash_screen")
    object AuthScreen : ScreenRoute("auth_screen")
    object ForgotPasswordScreen : ScreenRoute("forgot_password_screen")
    object CodeScreen : ScreenRoute("code_password_screen")
    object CreatePasswordScreen : ScreenRoute("create_password_screen")
    object SignUpScreen : ScreenRoute("sign_up_screen")

    object ChatalyzeScreen : ScreenRoute("chatalyze_screen")
    object ChatsScreen : ScreenRoute("chats_screen")
    object CallsScreen : ScreenRoute("calls_screen")
    object ProfileScreen : ScreenRoute("profile_screen")

    object ChatScreen : ScreenRoute("chat_screen/{$RECIPIENT_NAME}/{$RECIPIENT_PHONE}/{$SENDER_PHONE}") {
        fun withArgs(recipientName: String, recipientPhone: String, senderPhone: String) : String {
            return "chat_screen/$recipientName/$recipientPhone/$senderPhone"
        }
    }

//    object CallScreen : ScreenRoute("call_screen/{$RECIPIENT_NAME}/{$RECIPIENT_PHONE}/{$SENDER_PHONE}/{$TYPE_EVENT}") {
//        fun withArgs2(recipientName: String, recipientPhone: String, senderPhone: String, typeEvent: String) : String {
//            return "call_screen/$recipientName/$recipientPhone/$senderPhone/$typeEvent"
//        }
//    }

    object CallScreen : ScreenRoute("call_screen/{$RECIPIENT_NAME}/{$RECIPIENT_PHONE}/{$SENDER_PHONE}/{$TYPE_EVENT}") {
        fun withArgs2(recipientName: String, recipientPhone: String, senderPhone: String, typeEvent: String) : String {
            return "call_screen/$recipientName/$recipientPhone/$senderPhone/$typeEvent"
        }
    }



//    fun withArgs(args: String): String {
//        return "$route/$args"
//    }

//    fun withArgs(vararg args: String): String {
//        return "$route/${args.joinToString("/")}"
//    }

}

