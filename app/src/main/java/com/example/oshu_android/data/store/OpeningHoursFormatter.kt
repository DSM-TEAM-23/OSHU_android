package com.example.oshu_android.data.store

fun String?.toOpeningHoursLabel(): String? {
    val value = this?.trim().orEmpty()

    if (value.isBlank()) {
        return null
    }

    val times = Regex("(\\d{1,2}):(\\d{2})")
        .findAll(value)
        .map { match ->
            "%02d:%s".format(
                match.groupValues[1].toInt(),
                match.groupValues[2],
            )
        }
        .toList()

    return if (times.size >= 2) {
        "${times[0]} ~ ${times[1]}"
    } else {
        value
    }
}
