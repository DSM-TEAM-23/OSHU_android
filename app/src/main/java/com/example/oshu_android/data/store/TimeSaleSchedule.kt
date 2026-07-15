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
        return remainingText(endAtMillis - nowMillis)
    }

    fun remainingText(remainingMillis: Long): String? {
        if (remainingMillis <= 0L) return null

        val totalMinutes = (remainingMillis / 60_000L).coerceAtLeast(1L)
        val days = totalMinutes / 1_440L
        val hours = totalMinutes % 1_440L / 60L
        val minutes = totalMinutes % 60L

        return listOfNotNull(
            days.takeIf { it > 0L }?.let { "${it}일" },
            hours.takeIf { it > 0L }?.let { "${it}시간" },
            minutes.takeIf { it > 0L }?.let { "${it}분" },
        ).joinToString(" ")
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
