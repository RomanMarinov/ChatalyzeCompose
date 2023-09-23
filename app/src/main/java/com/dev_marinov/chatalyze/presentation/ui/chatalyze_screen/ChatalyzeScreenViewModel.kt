package com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.domain.repository.DataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.util.ScreenRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatalyzeScreenViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val isHideBottomBar = dataStoreRepository.getHideBottomBar

    fun saveHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.saveHideNavigationBar(Constants.HIDE_BOTTOM_BAR, isHide = isHide)
        }
    }

    fun customBackStackBottomControl(currentRoute: String, navHostController: NavHostController) {
//        if (currentRoute == ScreenRoute.ChatsScreen.route) {
//
//        }
//        else {
//         //   navHostController.navigateUp()
//        }

        if (currentRoute == ScreenRoute.CallScreen.route) {
            navHostController.navigate(ScreenRoute.ChatsScreen.route)
        }
        if (currentRoute == ScreenRoute.SettingScreen.route) {
            navHostController.navigate(ScreenRoute.ChatsScreen.route)
        }
    }
}