package com.example.oshu_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.example.oshu_android.data.AppContainer
import com.example.oshu_android.data.auth.GoogleOAuthCallbackStateHolder
import com.example.oshu_android.data.auth.LocalGoogleOAuthCallbackIntake
import com.example.oshu_android.navigation.OshuNavGraph
import com.example.oshu_android.ui.theme.OSHUAndroidTheme
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val appContainer: AppContainer by lazy {
        AppContainer(applicationContext)
    }

    private val googleOAuthCallbackIntake by lazy {
        GoogleOAuthCallbackStateHolder.get()
    }

    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        googleOAuthCallbackIntake.receive(intent?.dataString)

        setContent {
            OSHUAndroidTheme {
                CompositionLocalProvider(
                    LocalGoogleOAuthCallbackIntake provides googleOAuthCallbackIntake,
                ) {
                    OshuNavGraph(
                        appContainer = appContainer,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        googleOAuthCallbackIntake.receive(intent.dataString)
    }
}
