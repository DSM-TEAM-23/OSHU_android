package com.example.oshu_android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oshu_android.data.AppContainer
import com.example.oshu_android.feature.auth.login.LoginRoute
import com.example.oshu_android.feature.auth.login.LoginViewModel
import com.example.oshu_android.feature.auth.signup.SignUpRoute
import com.example.oshu_android.feature.auth.signup.SignUpViewModel
import com.example.oshu_android.feature.map.MapRoute
import com.example.oshu_android.feature.map.MapViewModel
import com.example.oshu_android.feature.onboarding.OnboardingScreen
import com.example.oshu_android.feature.onboarding.SplashScreen
import com.example.oshu_android.feature.storelist.StoreListRoute
import com.example.oshu_android.feature.storelist.StoreListViewModel

@Composable
fun OshuNavGraph(
    appContainer: AppContainer,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OshuRoutes.SPLASH,
    ) {
        composable(
            route = OshuRoutes.SPLASH,
        ) {
            SplashScreen(
                onboardingPreferences = appContainer.onboardingPreferences,
                onOnboardingRequired = {
                    navController.navigate(
                        OshuRoutes.ONBOARDING,
                    ) {
                        popUpTo(
                            OshuRoutes.SPLASH,
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
                onLoginRequired = {
                    navController.navigate(
                        OshuRoutes.LOGIN,
                    ) {
                        popUpTo(
                            OshuRoutes.SPLASH,
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = OshuRoutes.ONBOARDING,
        ) {
            OnboardingScreen(
                onboardingPreferences = appContainer.onboardingPreferences,
                onFinished = {
                    navController.navigate(
                        OshuRoutes.LOGIN,
                    ) {
                        popUpTo(
                            OshuRoutes.ONBOARDING,
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = OshuRoutes.LOGIN,
        ) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.Factory(
                    loginRepository = appContainer.loginRepository,
                ),
            )

            LoginRoute(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(
                        OshuRoutes.HOME,
                    ) {
                        popUpTo(
                            OshuRoutes.LOGIN,
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
                onSignUpClick = {
                    navController.navigate(
                        OshuRoutes.SIGN_UP,
                    ) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = OshuRoutes.SIGN_UP,
        ) {
            val signUpViewModel: SignUpViewModel = viewModel(
                factory = SignUpViewModel.Factory(
                    signUpRepository = appContainer.signUpRepository,
                ),
            )

            SignUpRoute(
                viewModel = signUpViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate(
                        OshuRoutes.LOGIN,
                    ) {
                        popUpTo(
                            OshuRoutes.SIGN_UP,
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = OshuRoutes.HOME,
        ) {
            val mapViewModel: MapViewModel = viewModel(
                factory = MapViewModel.Factory(
                    storeRepository = appContainer.storeRepository,
                ),
            )

            MapRoute(
                viewModel = mapViewModel,
                onListClick = {
                    navController.navigateToMainScreen(
                        OshuRoutes.STORE_LIST,
                    )
                },
                onPromotionClick = {
                    navController.navigateToMainScreen(
                        OshuRoutes.PROMOTION,
                    )
                },
            )
        }

        composable(
            route = OshuRoutes.STORE_LIST,
        ) {
            val mapViewModel: MapViewModel = viewModel(
                factory = MapViewModel.Factory(
                    storeRepository = appContainer.storeRepository,
                ),
            )

            val mapUiState by mapViewModel.uiState.collectAsState()

            val storeListViewModel: StoreListViewModel = viewModel()

            StoreListRoute(
                viewModel = storeListViewModel,
                stores = mapUiState.filteredStores,
                onMapClick = {
                    navController.navigateToMainScreen(
                        OshuRoutes.HOME,
                    )
                },
                onPromotionClick = {
                    navController.navigateToMainScreen(
                        OshuRoutes.PROMOTION,
                    )
                },
                onStoreDetailClick = {
                    navController.navigate(
                        "store_detail/$it",
                    )
                },
            )
        }

        composable(
            route = OshuRoutes.PROMOTION,
        ) {
            PromotionPlaceholderScreen()
        }
    }
}

@Composable
private fun PromotionPlaceholderScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "프로모션 준비 중",
        )
    }
}

private fun NavController.navigateToMainScreen(
    route: String,
) {
    navigate(route) {
        popUpTo(
            OshuRoutes.HOME,
        ) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }
}