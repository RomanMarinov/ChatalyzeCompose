package com.dev_marinov.chatalyze.presentation.ui.deep_link_activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev_marinov.chatalyze.presentation.ui.deep_link_activity.ui.theme.ChatalyzeTheme
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.MainActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi

class DeepLinkActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatalyzeTheme {
                val intent = Intent(this@DeepLinkActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        }
    }
}