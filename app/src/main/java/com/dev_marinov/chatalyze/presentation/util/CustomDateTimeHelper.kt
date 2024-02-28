package com.dev_marinov.chatalyze.presentation.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object CustomDateTimeHelper {
    fun formatDateTime(dateTimeString: String): String {
        val sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        val targetFormatter = DateTimeFormatter.ofPattern("HH:mm / yyyy-MM-dd")

        val parsedDateTime = LocalDateTime.parse(dateTimeString, sourceFormatter)
        val utcDateTime = OffsetDateTime.of(parsedDateTime, ZoneOffset.UTC)
        return utcDateTime.format(targetFormatter)
    }
}