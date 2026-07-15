package com.example.oshu_android.data.store

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object TimeSaleSchedule {
    fun remainingText(
        endAt: String?,
        nowMillis: Long,
    ): String? {
        val endAtMillis = parseMillis(endAt) ?: return null
        val remainingMillis = endAtMillis - nowMillis

        if (remainingMillis <= 0L) return null

        val totalSeconds = remainingMillis / 1_000L
        val hours = totalSeconds / 3_600L
        val minutes = totalSeconds % 3_600L / 60L
        val seconds = totalSeconds % 60L

        return String.format(
            Locale.KOREA,
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds,
        )
    }

    private fun parseMillis(value: String?): Long? {
        val timestamp = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null

        return TIME_FORMATS.firstNotNullOfOrNull { pattern ->
            runCatching {
                SimpleDateFormat(pattern, Locale.US).apply {
                    isLenient = false
                    if (!pattern.endsWith("X")) {
                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
                    }
                }.parse(timestamp)?.time
            }.getOrNull()
        }
    }

    private val TIME_FORMATS = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ssX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
    )
}
