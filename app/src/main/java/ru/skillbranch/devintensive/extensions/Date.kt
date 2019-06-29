package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnit = TimeUnit.SECOND): Date {
    var time = this.time
    time +=
        when(units) {
            TimeUnit.SECOND -> value * SECOND
            TimeUnit.MINUTE -> value * MINUTE
            TimeUnit.HOUR -> value * HOUR
            TimeUnit.DAY -> value * DAY
        }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()) : String {
    val diff = date.time - this.time
    return if (diff >= 0)
                when {
                    diff <= 1 * SECOND -> "только что"
                    diff <= 45 * SECOND -> "несколько секунд назад"
                    diff <= 75 * SECOND -> "минуту назад"
                    diff <= 45 * MINUTE -> "${diff / MINUTE} ${pluralsNumber((diff / MINUTE).toInt(), TimeUnit.MINUTE)} назад"
                    diff <= 75 * MINUTE -> "час назад"
                    diff <= 22 * HOUR -> "${diff / HOUR} ${pluralsNumber((diff / HOUR).toInt(), TimeUnit.HOUR)} назад"
                    diff <= 26 * HOUR -> "день назад"
                    diff <= 360 * DAY -> "${diff / DAY} ${pluralsNumber((diff / DAY).toInt(), TimeUnit.DAY)} назад"
                    else -> "более года назад"
                }
            else {
                when {
                    diff.absoluteValue >= 360 * DAY -> "более чем через год"
                    diff.absoluteValue >= 26 * HOUR -> "через ${diff.absoluteValue / DAY} ${pluralsNumber((diff.absoluteValue / DAY).toInt(), TimeUnit.DAY)}"
                    diff.absoluteValue >= 22 * HOUR -> "через день"
                    diff.absoluteValue >= 75 * MINUTE -> "через ${diff.absoluteValue / HOUR} ${pluralsNumber((diff.absoluteValue / HOUR).toInt(), TimeUnit.HOUR)}"
                    diff.absoluteValue >= 45 * MINUTE -> "через час"
                    diff.absoluteValue >= 75 * SECOND -> "через ${diff.absoluteValue / MINUTE} ${pluralsNumber((diff.absoluteValue / MINUTE).toInt(), TimeUnit.MINUTE)}"
                    diff.absoluteValue >= 45 * SECOND -> "через минуту"
                    else -> "через несколько секунд"
                }
            }
}

private fun pluralsNumber(value: Int, number: TimeUnit) = when(number) {
    TimeUnit.SECOND ->
        if (value in 11..19) {
            "секунд"
        } else {
            when (value % 10) {
                1 -> "секунду"
                2, 3, 4 -> "секунды"
                else -> "секунд"
            }
        }
    TimeUnit.MINUTE ->
        if (value in 11..19) {
            "минут"
        } else {
            when (value % 10) {
                1 -> "минуту"
                2, 3, 4 -> "минуты"
                else -> "минут"
            }
        }
    TimeUnit.HOUR ->
        if (value in 11..19) {
            "часов"
        } else {
            when (value % 10) {
                1 -> "час"
                2, 3, 4 -> "часа"
                else -> "часов"
            }
        }
    TimeUnit.DAY ->
        if (value in 11..19) {
            "дней"
        } else {
            when (value % 10) {
                1 -> "день"
                2, 3, 4 -> "дня"
                else -> "дней"
            }
        }
}

enum class TimeUnit {
    SECOND,
    MINUTE,
    HOUR,
    DAY
}