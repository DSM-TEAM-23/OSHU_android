package com.example.oshu_android.feature.auth.login

import java.net.URI

internal data class BrowserViewRequest(
    val action: String = ACTION_VIEW,
    val uri: String,
)

internal enum class GoogleAuthorizationLaunchResult {
    Launched,
    InvalidConfiguration,
}

internal fun launchGoogleAuthorization(
    authorizationUrl: String,
    launch: (BrowserViewRequest) -> Unit,
): GoogleAuthorizationLaunchResult {
    val secureAuthorizationUrl = authorizationUrl.trim()
        .takeIf(::isHttpsUrl)
        ?: return GoogleAuthorizationLaunchResult.InvalidConfiguration

    launch(
        BrowserViewRequest(
            uri = secureAuthorizationUrl,
        ),
    )
    return GoogleAuthorizationLaunchResult.Launched
}

private fun isHttpsUrl(value: String): Boolean {
    val uri = runCatching { URI(value) }.getOrNull()
        ?: return false

    return uri.scheme.equals("https", ignoreCase = true) &&
        !uri.host.isNullOrBlank()
}

private const val ACTION_VIEW = "android.intent.action.VIEW"
