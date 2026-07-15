package com.example.oshu_android.feature.storedetail

import com.example.oshu_android.data.store.TimeSaleResponse
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeSaleStatusTest {
    @Test
    fun keepsOnlyTimeSalesWithinTheirScheduledPeriod() {
        val active = TimeSaleResponse(
            timeSaleId = 1L,
            startAt = "2026-07-15T09:00:00.000000",
            endAt = "2026-07-15T10:14:52.000000",
        )
        val expired = TimeSaleResponse(
            timeSaleId = 2L,
            startAt = "2026-07-15T08:00:00.000000",
            endAt = "2026-07-15T09:00:00.000000",
        )

        assertEquals(
            listOf(active),
            activeTimeSales(
                timeSales = listOf(active, expired),
                nowMillis = millisAt("2026-07-15T10:00:30"),
            ),
        )
    }

    @Test
    fun formatsTheTimeRemainingForTheSectionHeader() {
        assertEquals("14:22", timeRemainingText(862_000L))
        assertEquals("1:14:22", timeRemainingText(4_462_000L))
    }

    private fun millisAt(value: String): Long {
        return requireNotNull(
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.US,
            ).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }.parse(value),
        ).time
    }
}
