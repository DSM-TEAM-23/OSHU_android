package com.example.oshu_android.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oshu_android.data.AppContainer
import com.example.oshu_android.feature.auth.login.LoginRoute
import com.example.oshu_android.feature.auth.login.LoginViewModel

@Composable
fun OshuNavGraph(
    appContainer: AppContainer,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OshuRoutes.LOGIN,
    ) {
        composable(OshuRoutes.LOGIN) {
            val loginViewModel: LoginViewModel =
                viewModel(
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
            SignUpPlaceholderScreen(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable(OshuRoutes.HOME) {
            HomePlaceholderScreen()
        }
    }
}