package com.example.oshu_android.data.store

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TimeSaleScheduleTest {
    @Test
    fun formatsRemainingTimeFromUtcEndAt() {
        assertEquals(
            "00:45:12",
            TimeSaleSchedule.remainingText(
                endAt = "2026-07-14T18:23:10.116Z",
                nowMillis = 1_784_050_678_116L,
            ),
        )
    }

    @Test
    fun omitsRemainingTimeWhenTheSaleHasEnded() {
        assertNull(
            TimeSaleSchedule.remainingText(
                endAt = "2026-07-14T18:23:10.116Z",
                nowMillis = 1_784_053_390_116L,
            ),
        )
    }

    @Test
    fun formatsRemainingTimeFromKoreanLocalEndAt() {
        assertEquals(
            "01:00:00",
            TimeSaleSchedule.remainingText(
                endAt = "2026-07-17T03:00:00",
                nowMillis = 1_784_221_200_000L,
            ),
        )
    }
}
