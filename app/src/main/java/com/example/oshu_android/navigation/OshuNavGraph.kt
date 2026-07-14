package com.example.oshu_android.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.oshu_android.data.AppContainer
import com.example.oshu_android.feature.auth.login.LoginRoute
import com.example.oshu_android.feature.auth.login.LoginViewModel
import com.example.oshu_android.feature.auth.signup.SignUpRoute
import com.example.oshu_android.feature.auth.signup.SignUpViewModel
import com.example.oshu_android.feature.map.MapRoute
import com.example.oshu_android.feature.map.MapViewModel
import com.example.oshu_android.feature.onboarding.OnboardingScreen
import com.example.oshu_android.feature.onboarding.SplashScreen
import com.example.oshu_android.feature.promotion.PromotionRoute
import com.example.oshu_android.feature.promotion.PromotionViewModel
import com.example.oshu_android.feature.storelist.StoreListRoute
import com.example.oshu_android.feature.storelist.StoreListViewModel
import com.example.oshu_android.feature.storedetail.StoreDetailRoute
import com.example.oshu_android.feature.storedetail.StoreDetailViewModel
import com.example.oshu_android.feature.inquiry.InquiryRoute
import com.example.oshu_android.feature.inquiry.InquiryViewModel

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
                onStoreDetailClick = { storeId ->
                    navController.navigate("${OshuRoutes.STORE_DETAIL}/$storeId")
                },
            )
        }

        composable(
            route = OshuRoutes.STORE_LIST,
        ) {
            val storeListViewModel: StoreListViewModel = viewModel(
                factory = StoreListViewModel.Factory(
                    storeRepository = appContainer.storeRepository,
                ),
            )

            StoreListRoute(
                viewModel = storeListViewModel,
                stores = emptyList(),
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
                    storeId ->
                    navController.navigate("${OshuRoutes.STORE_DETAIL}/$storeId")
                },
            )
        }

        composable(
            route = OshuRoutes.PROMOTION,
        ) {
            val promotionViewModel: PromotionViewModel = viewModel(
                factory = PromotionViewModel.Factory(
                    storeRepository = appContainer.storeRepository,
                ),
            )

            PromotionRoute(
                viewModel = promotionViewModel,
                onMapClick = {
                    navController.navigateToMainScreen(
                        OshuRoutes.HOME,
                    )
                },
                onListClick = {
                    navController.navigateToMainScreen(
                        OshuRoutes.STORE_LIST,
                    )
                },
                onPromotionClick = { promotionId ->
                    navController.navigate("${OshuRoutes.STORE_DETAIL}/$promotionId")
                },
            )
        }

        composable(
            route = "${OshuRoutes.STORE_DETAIL}/{storeId}",
            arguments = listOf(
                navArgument("storeId") {
                    type = NavType.LongType
                },
            ),
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: return@composable
            val storeDetailViewModel: StoreDetailViewModel = viewModel(
                factory = StoreDetailViewModel.Factory(
                    storeRepository = appContainer.storeRepository,
                    storeId = storeId,
                ),
            )

            StoreDetailRoute(
                viewModel = storeDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onInquiryClick = {
                    navController.navigate("${OshuRoutes.INQUIRY}/$storeId")
                },
            )
        }

        composable(
            route = "${OshuRoutes.INQUIRY}/{storeId}",
            arguments = listOf(
                navArgument("storeId") { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: return@composable
            val inquiryViewModel: InquiryViewModel = viewModel(
                factory = InquiryViewModel.Factory(
                    inquiryRepository = appContainer.inquiryRepository,
                    storeId = storeId,
                ),
            )
            InquiryRoute(
                viewModel = inquiryViewModel,
                onBackClick = { navController.popBackStack() },
            )
        }
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
