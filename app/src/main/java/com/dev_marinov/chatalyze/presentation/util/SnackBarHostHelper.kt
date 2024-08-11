package com.dev_marinov.chatalyze.presentation.util

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dev_marinov.chatalyze.R
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
object SnackBarHostHelper {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun Show(message: String) {
        var isSnackBarVisible by remember { mutableStateOf(true) }
        if (isSnackBarVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
            ) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    actionOnNewLine = true,
                    action = {
                        TextButton(onClick = {
                            isSnackBarVisible = false
                        }) {
                            Text(
                                text = "Ok",
                                color = colorResource(id = R.color.main_yellow_new_chat_screen),
                            )
                        }
                    }
                ) {
                    Text(
                        text = message,
                        color = colorResource(id = R.color.main_yellow_new_chat_screen)
                    )
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun ShowWithoutOkButton(message: String) {
        var isSnackBarVisible by remember { mutableStateOf(true) }
        if (isSnackBarVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
            ) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    actionOnNewLine = true,
                    action = {
                    }
                ) {
                    Text(
                        text = message,
                        color = colorResource(id = R.color.main_yellow_new_chat_screen)
                    )
                }
            }
        }
    }
}