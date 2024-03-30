package com.dev_marinov.chatalyze.presentation.util

import android.annotation.SuppressLint
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TokenTime {
    @SuppressLint("SimpleDateFormat")
    private fun getTheRemainingTime(timeStamp: Long): List<Long> {
        val listTime: MutableList<Long> = mutableListOf()

        val data = Date(timeStamp * 1000)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val firstDate = LocalDateTime.parse(simpleDateFormat, formatter)
        val secondDate = LocalDateTime.parse(getCurrentDateTimeString(), formatter)

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

    private fun getCurrentDateTimeString(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }


    fun getExpiredStatus(expiresIn: Long) : Boolean {
        var isExpired = false
        var countZeroItemListTime = -1
        val listTime: List<Long> = getTheRemainingTime(expiresIn)
        listTime.forEach { item ->
            if (item > 0) {
                isExpired = false
                return@forEach
            } else {
                countZeroItemListTime++
            }
        }
        if (countZeroItemListTime == 3) {
            isExpired = true
        }
        return isExpired
    }
}