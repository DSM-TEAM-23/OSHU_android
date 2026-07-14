package com.example.oshu_android.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GoogleOAuthCallbackIntakeTest {

    @Test
    fun emptyActivityIntent_doesNotEmitGoogleOAuthCallbackFailure() {
        val intake = GoogleOAuthCallbackIntake()

        intake.receive(null)

        assertNull(intake.callbacks.value)
    }

    @Test
    fun serverCustomSchemeCallback_withoutClientState_emitsTicket() {
        val intake = GoogleOAuthCallbackIntake()

        intake.receive("oshu://auth/callback?code=ticket-123")

        assertEquals(
            GoogleOAuthCallbackResult.Success(code = "ticket-123"),
            intake.callbacks.value,
        )

        intake.consume()
        assertNull(intake.callbacks.value)
    }

    @Test
    fun wrongSchemeHostPathOrDuplicateCode_isRejected() {
        val intake = GoogleOAuthCallbackIntake()

        intake.receive("https://kangyu.shop/mobile/oauth/google?code=ticket-123")
        assertEquals(GoogleOAuthCallbackResult.Failure.InvalidCallback, intake.callbacks.value)

        intake.receive("oshu://auth/callback?code=one&code=two")
        assertEquals(GoogleOAuthCallbackResult.Failure.InvalidCallback, intake.callbacks.value)
    }

    @Test
    fun serverErrorCallback_isReportedWithoutTicket() {
        val intake = GoogleOAuthCallbackIntake()

        intake.receive("oshu://auth/callback?error=google_oauth_failed")

        assertEquals(
            GoogleOAuthCallbackResult.Failure.AuthorizationDenied,
            intake.callbacks.value,
        )
    }
}
