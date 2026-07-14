package com.example.oshu_android.data.auth

import androidx.compose.runtime.staticCompositionLocalOf
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val GOOGLE_OAUTH_CALLBACK_SCHEME = "oshu"
private const val GOOGLE_OAUTH_CALLBACK_HOST = "auth"
private const val GOOGLE_OAUTH_CALLBACK_PATH = "/callback"

sealed interface GoogleOAuthCallbackResult {

    data class Success(
        val code: String,
    ) : GoogleOAuthCallbackResult

    sealed interface Failure : GoogleOAuthCallbackResult {

        data object InvalidCallback : Failure

        data object InvalidState : Failure

        data object AuthorizationDenied : Failure
    }
}

class GoogleOAuthCallbackIntake {

    private val _callbacks = MutableStateFlow<GoogleOAuthCallbackResult?>(null)

    val callbacks: StateFlow<GoogleOAuthCallbackResult?> = _callbacks

    fun receive(uriString: String?) {
        if (uriString == null) {
            return
        }

        val callback = parseCallback(uriString)
            ?: return emit(GoogleOAuthCallbackResult.Failure.InvalidCallback)

        if (callback.error != null) {
            return emit(GoogleOAuthCallbackResult.Failure.AuthorizationDenied)
        }

        emit(
            GoogleOAuthCallbackResult.Success(
                code = callback.code.orEmpty(),
            ),
        )
    }

    fun consume() {
        _callbacks.value = null
    }

    private fun emit(result: GoogleOAuthCallbackResult) {
        _callbacks.value = result
    }

    private fun parseCallback(uriString: String?): ParsedGoogleOAuthCallback? {
        val uri = runCatching {
            URI(uriString)
        }.getOrNull() ?: return null

        if (
            !uri.scheme.equals(GOOGLE_OAUTH_CALLBACK_SCHEME, ignoreCase = true) ||
            !uri.host.equals(GOOGLE_OAUTH_CALLBACK_HOST, ignoreCase = true) ||
            uri.path != GOOGLE_OAUTH_CALLBACK_PATH ||
            uri.port != -1
        ) {
            return null
        }

        val parameters = parseQueryParameters(uri.rawQuery)
            ?: return null
        val error = parameters.singleOrNull("error")
            ?.takeIf(String::isNotBlank)
        val code = parameters.singleOrNull("code")
            ?.takeIf(String::isNotBlank)

        if (error == null && code == null) {
            return null
        }

        return ParsedGoogleOAuthCallback(
            code = code,
            error = error,
        )
    }

    private fun parseQueryParameters(rawQuery: String?): Map<String, List<String>>? {
        if (rawQuery.isNullOrEmpty()) {
            return emptyMap()
        }

        return runCatching {
            rawQuery.split('&')
                .map { part ->
                    val separatorIndex = part.indexOf('=')
                    val rawName = if (separatorIndex >= 0) part.substring(0, separatorIndex) else part
                    val rawValue = if (separatorIndex >= 0) part.substring(separatorIndex + 1) else ""

                    URLDecoder.decode(rawName, StandardCharsets.UTF_8) to
                        URLDecoder.decode(rawValue, StandardCharsets.UTF_8)
                }
                .groupBy(
                    keySelector = { (name, _) -> name },
                    valueTransform = { (_, value) -> value },
                )
        }.getOrNull()
    }

    private fun Map<String, List<String>>.singleOrNull(name: String): String? {
        return this[name]
            ?.takeIf { values -> values.size == 1 }
            ?.singleOrNull()
    }

    private data class ParsedGoogleOAuthCallback(
        val code: String?,
        val error: String?,
    )
}

object GoogleOAuthCallbackStateHolder {

    @Volatile
    private var instance: GoogleOAuthCallbackIntake? = null

    fun get(): GoogleOAuthCallbackIntake {
        return instance ?: synchronized(this) {
            instance ?: GoogleOAuthCallbackIntake().also { intake ->
                instance = intake
            }
        }
    }
}

val LocalGoogleOAuthCallbackIntake = staticCompositionLocalOf<GoogleOAuthCallbackIntake> {
    error("Google OAuth callback intake is unavailable.")
}
