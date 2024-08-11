package com.dev_marinov.chatalyze.presentation.ui.deep_link_activity

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.dev_marinov.chatalyze.presentation.ui.deep_link_activity.ui.theme.ChatalyzeTheme
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.MainActivity
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi

class DeepLinkActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatalyzeTheme {

                when (intent?.action) {
                    "notification_action" -> {

                        val name = intent.getStringExtra("name")
                        val sender = intent.getStringExtra("sender")
                        val recipient = intent.getStringExtra("recipient")
                        val deepLink =
                            Uri.parse("scheme_chatalyze://chat_screen/{$name}/{$sender}/{$recipient}")
                        val taskDetailIntent = Intent(
                            Intent.ACTION_VIEW,
                            deepLink,
                            this,
                            DeepLinkActivity::class.java
                        )

                        taskDetailIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        val pendingIntent: PendingIntent =
                            TaskStackBuilder.create(applicationContext).run {
                                addNextIntentWithParentStack(taskDetailIntent)
                                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
                            }
                        pendingIntent.send()
                    }
                }


                val localLifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(
                    key1 = localLifecycleOwner,
                    effect = {
                        val observer = LifecycleEventObserver { _, event ->
                            when (event) {
                                Lifecycle.Event.ON_START -> {
                                    Log.d("4444", " DeepLinkActivity Lifecycle.Event.ON_START")
                                }

                                Lifecycle.Event.ON_STOP -> { // когда свернул
                                    Log.d("4444", " DeepLinkActivity Lifecycle.Event.ON_STOP")
                                }

                                Lifecycle.Event.ON_DESTROY -> { // когда удалил из стека
                                    Log.d("4444", " DeepLinkActivity Lifecycle.Event.ON_DESTROY")
                                }

                                else -> {}
                            }
                        }
                        localLifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            localLifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        when (intent?.action) {
            "notification_action" -> {
                val name = intent.getStringExtra("name")
                val sender = intent.getStringExtra("sender")
                val recipient = intent.getStringExtra("recipient")
                val deepLink =
                    Uri.parse("scheme_chatalyze://chat_screen/{$name}/{$sender}/{$recipient}")
                val taskDetailIntent = Intent(
                    Intent.ACTION_VIEW,
                    deepLink,
//                    this,
//                    MainScreensActivity::class.java
                )

                val pendingIntent: PendingIntent = TaskStackBuilder.create(applicationContext).run {
                    addNextIntentWithParentStack(taskDetailIntent)
                    getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
                }
                pendingIntent.send()
            }
        }
    }
}