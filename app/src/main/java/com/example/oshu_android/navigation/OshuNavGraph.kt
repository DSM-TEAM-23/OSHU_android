package com.example.oshu_android.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oshu_android.data.AppContainer
import com.example.oshu_android.feature.auth.login.LoginRoute
import com.example.oshu_android.feature.auth.login.LoginViewModel
import com.example.oshu_android.feature.onboarding.OnboardingScreen
import com.example.oshu_android.feature.onboarding.SplashScreen

@Composable
fun OshuNavGraph(
    appContainer: AppContainer,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OshuRoutes.SPLASH,
    ) {
        composable(OshuRoutes.SPLASH) {
            SplashScreen(
                onboardingPreferences =
                    appContainer
                        .onboardingPreferences,
                onOnboardingRequired = {
                    navController.navigate(
                        OshuRoutes.ONBOARDING
                    ) {
                        popUpTo(OshuRoutes.SPLASH) {
                            inclusive = true
                        }
                    }
                },
                onLoginRequired = {
                    navController.navigate(
                        OshuRoutes.LOGIN
                    ) {
                        popUpTo(OshuRoutes.SPLASH) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable(OshuRoutes.ONBOARDING) {
            OnboardingScreen(
                onboardingPreferences =
                    appContainer
                        .onboardingPreferences,
                onFinished = {
                    navController.navigate(
                        OshuRoutes.LOGIN
                    ) {
                        popUpTo(
                            OshuRoutes.ONBOARDING
                        ) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable(OshuRoutes.LOGIN) {
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
                    navController.navigate(
                        OshuRoutes.HOME
                    ) {
                        popUpTo(OshuRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate(
                        OshuRoutes.SIGN_UP
                    )
                },
            )
        }

        composable(OshuRoutes.SIGN_UP) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center,
            ) {
                Text(
                    text = "회원가입 화면",
                    color = MaterialTheme
                        .colorScheme
                        .onBackground,
                )

                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                ) {
                    Text(
                        text = "로그인으로 돌아가기",
                    )
                }
            }
        }

        composable(OshuRoutes.HOME) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "메인 화면",
                    color = MaterialTheme
                        .colorScheme
                        .onBackground,
                    style = MaterialTheme
                        .typography
                        .headlineMedium,
                )
            }
        }
    }
}