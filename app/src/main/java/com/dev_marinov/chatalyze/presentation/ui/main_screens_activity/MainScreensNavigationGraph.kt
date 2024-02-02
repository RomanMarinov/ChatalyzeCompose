package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.CallScreen
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen.CallsScreen
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chat_screen.ChatScreen
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.ChatsScreen
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.profile_screen.ProfileScreen
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.stream_screen.StreamScreen
import com.dev_marinov.chatalyze.presentation.util.RECIPIENT_NAME
import com.dev_marinov.chatalyze.presentation.util.RECIPIENT_PHONE
import com.dev_marinov.chatalyze.presentation.util.SENDER_PHONE
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.TYPE_EVENT

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreensNavigationGraph(navHostController: NavHostController) {

    Log.d("4444", " ChatalyzeNavigationGraph loaded")
    NavHost(navController = navHostController, startDestination = ScreenRoute.ChatsScreen.route) {
        composable(route = ScreenRoute.ChatsScreen.route) {
            ChatsScreen(navController = navHostController)
        }
        composable(
            route = ScreenRoute.ChatScreen.route,
            arguments = listOf(
                navArgument(RECIPIENT_NAME) {
                    type = NavType.StringType
                },
                navArgument(RECIPIENT_PHONE) {
                    type = NavType.StringType
                },
                navArgument(SENDER_PHONE) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            ChatScreen(
                navHostController = navHostController,
                recipientName = entry.arguments?.getString(RECIPIENT_NAME),
                recipientPhone = entry.arguments?.getString(RECIPIENT_PHONE),
                senderPhone = entry.arguments?.getString(SENDER_PHONE)
            )
        }

        composable(route = ScreenRoute.CallsScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "app://calls_screen/"
                    //action = Intent.ACTION_VIEW
                }
            )
        )
        {
            CallsScreen(navController = navHostController)
        }
        composable(
            route = ScreenRoute.CallScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "scheme_chatalyze://call_screen/{$RECIPIENT_NAME}/{$RECIPIENT_PHONE}/{$SENDER_PHONE}/{$TYPE_EVENT}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument(RECIPIENT_NAME) {
                    type = NavType.StringType
                },
                navArgument(RECIPIENT_PHONE) {
                    type = NavType.StringType
                },
                navArgument(SENDER_PHONE) {
                    type = NavType.StringType
                },
                navArgument(TYPE_EVENT) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            CallScreen(
                navController = navHostController,
                recipientName = entry.arguments?.getString(RECIPIENT_NAME),
                recipientPhone = entry.arguments?.getString(RECIPIENT_PHONE),
                senderPhone = entry.arguments?.getString(SENDER_PHONE),
                typeEvent = entry.arguments?.getString(TYPE_EVENT)
            )
        }

//        composable(
//            route = ScreenRoute.StreamScreen.route) { entry ->
//            StreamScreen(
//                navController = navHostController
//            )
//        }

        composable(
            route = ScreenRoute.StreamScreen.route,
            arguments = listOf(
                navArgument(RECIPIENT_NAME) {
                    type = NavType.StringType
                },
                navArgument(RECIPIENT_PHONE) {
                    type = NavType.StringType
                },
                navArgument(SENDER_PHONE) {
                    type = NavType.StringType
                },
                navArgument(TYPE_EVENT) {
                    type = NavType.StringType
                }
            )) { entry ->
            StreamScreen(
                navController = navHostController,
                recipientName = entry.arguments?.getString(RECIPIENT_NAME),
                recipientPhone = entry.arguments?.getString(RECIPIENT_PHONE),
                senderPhone = entry.arguments?.getString(SENDER_PHONE),
                typeEvent = entry.arguments?.getString(TYPE_EVENT)
            )
        }

        composable(route = ScreenRoute.ProfileScreen.route) {
            ProfileScreen(
                navHostController = navHostController,
                // authHostController = authHostController
            )
        }
    }
}