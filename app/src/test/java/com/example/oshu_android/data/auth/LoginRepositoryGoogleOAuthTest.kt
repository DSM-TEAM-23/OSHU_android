package com.example.oshu_android.data.auth

import com.example.oshu_android.feature.auth.login.LoginResult
import java.io.IOException
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class LoginRepositoryGoogleOAuthTest {

    @Test
    fun successfulExchangeSavesOnlyTheReturnedBearerToken() = runBlocking {
        val api = FakeAuthApi(
            exchangeResult = {
                Response.success(
                    GoogleTokenExchangeResponse(
                        accessToken = "new-access-token",
                        tokenType = "bEaReR",
                    ),
                )
            },
        )
        val sessionStore = FakeSessionStore(storedAccessToken = "existing-session")

        val result = LoginRepositoryImpl(api, sessionStore).loginWithGoogleTicket(
            code = "one-time-ticket",
            keepLoggedIn = true,
        )

        assertEquals(LoginResult.Success, result)
        assertEquals(listOf("one-time-ticket"), api.exchangeCodes)
        assertEquals("new-access-token", sessionStore.storedAccessToken)
        assertEquals(listOf(true), sessionStore.persistRequests)
    }

    @Test
    fun blankTicketDoesNotCallApiOrReplaceExistingSession() = runBlocking {
        val api = FakeAuthApi(exchangeResult = { Response.success(validResponse()) })
        val sessionStore = FakeSessionStore(storedAccessToken = "existing-session")

        val result = LoginRepositoryImpl(api, sessionStore).loginWithGoogleTicket(
            code = "   ",
            keepLoggedIn = false,
        )

        assertTrue(result is LoginResult.Failure)
        assertTrue(api.exchangeCodes.isEmpty())
        assertEquals("existing-session", sessionStore.storedAccessToken)
        assertTrue(sessionStore.persistRequests.isEmpty())
    }

    @Test
    fun malformedSuccessfulResponseDoesNotReplaceExistingSession() = runBlocking {
        val malformedResponses = listOf(
            GoogleTokenExchangeResponse(accessToken = "", tokenType = "Bearer"),
            GoogleTokenExchangeResponse(accessToken = "candidate-token", tokenType = "Basic"),
        )

        malformedResponses.forEach { response ->
            val sessionStore = FakeSessionStore(storedAccessToken = "existing-session")
            val result = LoginRepositoryImpl(
                FakeAuthApi(exchangeResult = { Response.success(response) }),
                sessionStore,
            ).loginWithGoogleTicket(code = "one-time-ticket", keepLoggedIn = false)

            assertTrue(result is LoginResult.Failure)
            assertEquals("existing-session", sessionStore.storedAccessToken)
            assertTrue(sessionStore.persistRequests.isEmpty())
        }
    }

    @Test
    fun httpAndNetworkFailuresDoNotReplaceExistingSession() = runBlocking {
        val failingExchanges: List<suspend () -> Response<GoogleTokenExchangeResponse>> = listOf(
            { Response.error(401, "denied".toResponseBody("text/plain".toMediaType())) },
            { Response.error(500, "failure".toResponseBody("text/plain".toMediaType())) },
            { throw IOException("offline") },
        )

        failingExchanges.forEach { exchangeResult ->
            val sessionStore = FakeSessionStore(storedAccessToken = "existing-session")
            val result = LoginRepositoryImpl(
                FakeAuthApi(exchangeResult = exchangeResult),
                sessionStore,
            ).loginWithGoogleTicket(code = "one-time-ticket", keepLoggedIn = false)

            assertFalse(result is LoginResult.Success)
            assertEquals("existing-session", sessionStore.storedAccessToken)
            assertTrue(sessionStore.persistRequests.isEmpty())
        }
    }

    private fun validResponse() = GoogleTokenExchangeResponse(
        accessToken = "new-access-token",
        tokenType = "Bearer",
    )

    private class FakeAuthApi(
        private val exchangeResult: suspend () -> Response<GoogleTokenExchangeResponse>,
    ) : AuthApi {
        val exchangeCodes = mutableListOf<String>()

        override suspend fun login(request: LoginRequest): Response<LoginResponse> =
            error("login should not be called")

        override suspend fun exchangeGoogleTicket(
            request: GoogleTokenExchangeRequest,
        ): Response<GoogleTokenExchangeResponse> {
            exchangeCodes += request.code
            return exchangeResult()
        }

        override suspend fun signUp(request: SignUpRequest): Response<SignUpResponse> =
            error("signUp should not be called")
    }

    private class FakeSessionStore(
        var storedAccessToken: String?,
    ) : SessionStore {
        val persistRequests = mutableListOf<Boolean>()

        override fun getAccessToken(): String? = storedAccessToken

        override suspend fun saveAccessToken(accessToken: String, persist: Boolean) {
            storedAccessToken = accessToken
            persistRequests += persist
        }

        override suspend fun clear() {
            storedAccessToken = null
        }
    }
}
