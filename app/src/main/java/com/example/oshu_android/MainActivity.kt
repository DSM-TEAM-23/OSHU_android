package com.example.oshu_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.oshu_android.data.AppContainer
import com.example.oshu_android.navigation.OshuNavGraph
import com.example.oshu_android.ui.theme.OSHUAndroidTheme

class MainActivity : ComponentActivity() {

    private val appContainer: AppContainer by lazy {
        AppContainer(applicationContext)
    }

    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            OSHUAndroidTheme {
                OshuNavGraph(
                    appContainer = appContainer,
                )
            }
        }
    }
}