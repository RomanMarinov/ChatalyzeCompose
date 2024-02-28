package com.dev_marinov.chatalyze.presentation.util

import android.util.Log
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.getCurrentDateTimeString2
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object GetTheRemainingTime {
    fun execute(timeStamp: Long): List<Long> {
        val listTime: MutableList<Long> = mutableListOf()

        val data = Date(timeStamp * 1000)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val firstDate = LocalDateTime.parse(simpleDateFormat, formatter)
        val secondDate = LocalDateTime.parse(getCurrentDateTimeString2(), formatter)

        val duration = Duration.between(secondDate, firstDate)

        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60

        listTime.add(0, days)
        listTime.add(1, hours)
        listTime.add(2, minutes)
        listTime.add(3, seconds)
        return listTime
    }
}