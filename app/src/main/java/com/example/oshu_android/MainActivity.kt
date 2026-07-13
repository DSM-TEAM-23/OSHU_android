package com.example.oshu_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oshu_android.data.AppContainer
import com.example.oshu_android.feature.auth.login.LoginRoute
import com.example.oshu_android.feature.auth.login.LoginViewModel
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
                var isLoggedIn by rememberSaveable {
                    mutableStateOf(false)
                }

                if (isLoggedIn) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "로그인 성공",
                            color = MaterialTheme
                                .colorScheme
                                .onBackground,
                        )
                    }
                } else {
                    val loginViewModel:
                            LoginViewModel = viewModel(
                        factory = LoginViewModel.Factory(
                            loginRepository =
                                appContainer.loginRepository,
                        ),
                    )

                    LoginRoute(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            isLoggedIn = true
                        },
                        onSignUpClick = {
                        },
                    )
                }
            }
        }
    }
}