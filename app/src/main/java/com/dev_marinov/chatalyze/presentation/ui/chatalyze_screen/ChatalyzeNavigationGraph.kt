package com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev_marinov.chatalyze.presentation.ui.call_screen.CallsScreen
import com.dev_marinov.chatalyze.presentation.ui.chat_screen.ChatScreen
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.ChatsScreen
import com.dev_marinov.chatalyze.presentation.ui.profile_screen.ProfileScreen
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute

@Composable
fun ChatalyzeNavigationGraph(
    navHostController: NavHostController,
    authHostController: NavHostController,
    viewModel: ChatalyzeScreenViewModel
) {
    NavHost(navController = navHostController, startDestination = ScreenRoute.ChatsScreen.route) {
        composable(route = ScreenRoute.ChatsScreen.route) {
            ChatsScreen(navController = navHostController)
        }
        composable(route = ScreenRoute.CallScreen.route) {
            CallsScreen(navHostController = navHostController)
        }
        composable(route = ScreenRoute.ProfileScreen.route) {
            ProfileScreen(
                navHostController = navHostController,
                authHostController = authHostController
            )
        }

        composable(route = ScreenRoute.ChatScreen.route) {
            ChatScreen(navHostController = navHostController)
        }
    }

//    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
//    BackHandler(enabled = currentRoute != null) {
//        currentRoute?.let {
//            viewModel.customBackStackBottomControl(
//                navHostController = navHostController,
//                currentRoute = currentRoute)
//        }
//    }
}