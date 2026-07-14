package com.example.oshu_android.feature.auth.login

import org.junit.Assert.assertEquals
import org.junit.Test

class GoogleAuthorizationBrowserLauncherTest {

    @Test
    fun validHttpsUrl_launchesExactlyOneActionViewRequest() {
        val launches = mutableListOf<BrowserViewRequest>()

        val result = launchGoogleAuthorization(
            authorizationUrl = "https://kangyu.shop/oauth2/authorization/google",
            launch = launches::add,
        )

        assertEquals(GoogleAuthorizationLaunchResult.Launched, result)
        assertEquals(
            listOf(
                BrowserViewRequest(
                    action = "android.intent.action.VIEW",
                    uri = "https://kangyu.shop/oauth2/authorization/google",
                ),
            ),
            launches,
        )
    }

    @Test
    fun blankOrNonHttpsUrl_doesNotLaunchAndReportsInvalidConfiguration() {
        listOf("", "http://kangyu.shop/oauth2/authorization/google").forEach { url ->
            val launches = mutableListOf<BrowserViewRequest>()

            val result = launchGoogleAuthorization(
                authorizationUrl = url,
                launch = launches::add,
            )

            assertEquals(GoogleAuthorizationLaunchResult.InvalidConfiguration, result)
            assertEquals(emptyList<BrowserViewRequest>(), launches)
        }
    }
}
