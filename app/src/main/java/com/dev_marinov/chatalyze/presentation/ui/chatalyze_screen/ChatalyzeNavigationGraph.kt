package com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dev_marinov.chatalyze.presentation.ui.call_screen.CallScreen
import com.dev_marinov.chatalyze.presentation.ui.chat_screen.ChatScreen
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.ChatsScreen
import com.dev_marinov.chatalyze.presentation.ui.setting_screen.SettingScreen
import com.dev_marinov.chatalyze.util.ScreenRoute
import kotlin.system.exitProcess

@Composable
fun ChatalyzeNavigationGraph(
    navHostController: NavHostController,
    viewModel: ChatalyzeScreenViewModel
) {
    NavHost(navController = navHostController, startDestination = ScreenRoute.ChatsScreen.route) {
        composable(route = ScreenRoute.ChatsScreen.route) {
            ChatsScreen(navController = navHostController)
        }
        composable(route = ScreenRoute.CallScreen.route) {
            CallScreen(navHostController = navHostController)
        }
        composable(route = ScreenRoute.SettingScreen.route) {
            SettingScreen(navHostController = navHostController)
        }

        composable(route = ScreenRoute.ChatScreen.route) {
            ChatScreen(navHostController = navHostController)
            //ChatScreen4(navHostController = navHostController)
        }
    }

    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route

    BackHandler(enabled = currentRoute != null) {
        if (currentRoute == ScreenRoute.ChatsScreen.route) {
            exitProcess(0)
        }
        else {
            navHostController.navigateUp()
        }

        if (currentRoute == ScreenRoute.CallScreen.route) {
            navHostController.navigate(ScreenRoute.ChatsScreen.route)
        }
        if (currentRoute == ScreenRoute.SettingScreen.route) {
            navHostController.navigate(ScreenRoute.ChatsScreen.route)
        }
        if (currentRoute == ScreenRoute.ChatScreen.route) {
            navHostController.navigate(ScreenRoute.ChatsScreen.route)
        }
    }
}