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

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time
    time +=
        when(units) {
            TimeUnits.SECOND -> value * SECOND
            TimeUnits.MINUTE -> value * MINUTE
            TimeUnits.HOUR -> value * HOUR
            TimeUnits.DAY -> value * DAY
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
                    diff <= 45 * MINUTE -> "${diff / MINUTE} ${pluralsNumber((diff / MINUTE).toInt(), TimeUnits.MINUTE)} назад"
                    diff <= 75 * MINUTE -> "час назад"
                    diff <= 22 * HOUR -> "${diff / HOUR} ${pluralsNumber((diff / HOUR).toInt(), TimeUnits.HOUR)} назад"
                    diff <= 26 * HOUR -> "день назад"
                    diff <= 360 * DAY -> "${diff / DAY} ${pluralsNumber((diff / DAY).toInt(), TimeUnits.DAY)} назад"
                    else -> "более года назад"
                }
            else {
                when {
                    diff.absoluteValue >= 360 * DAY -> "более чем через год"
                    diff.absoluteValue >= 26 * HOUR -> "через ${diff.absoluteValue / DAY} ${pluralsNumber((diff.absoluteValue / DAY).toInt(), TimeUnits.DAY)}"
                    diff.absoluteValue >= 22 * HOUR -> "через день"
                    diff.absoluteValue >= 75 * MINUTE -> "через ${diff.absoluteValue / HOUR} ${pluralsNumber((diff.absoluteValue / HOUR).toInt(), TimeUnits.HOUR)}"
                    diff.absoluteValue >= 45 * MINUTE -> "через час"
                    diff.absoluteValue >= 75 * SECOND -> "через ${diff.absoluteValue / MINUTE} ${pluralsNumber((diff.absoluteValue / MINUTE).toInt(), TimeUnits.MINUTE)}"
                    diff.absoluteValue >= 45 * SECOND -> "через минуту"
                    else -> "через несколько секунд"
                }
            }
}

fun TimeUnits.plural(value: Int) =
    when(this) {
        TimeUnits.SECOND -> "$value ${pluralsNumber(value, TimeUnits.SECOND)}"
        TimeUnits.MINUTE -> "$value ${pluralsNumber(value, TimeUnits.MINUTE)}"
        TimeUnits.HOUR -> "$value ${pluralsNumber(value, TimeUnits.HOUR)}"
        TimeUnits.DAY -> "$value ${pluralsNumber(value, TimeUnits.DAY)}"
    }

private fun pluralsNumber(value: Int, number: TimeUnits) = when(number) {
    TimeUnits.SECOND ->
        if (value in 11..19) {
            "секунд"
        } else {
            when (value % 10) {
                1 -> "секунду"
                2, 3, 4 -> "секунды"
                else -> "секунд"
            }
        }
    TimeUnits.MINUTE ->
        if (value in 11..19) {
            "минут"
        } else {
            when (value % 10) {
                1 -> "минуту"
                2, 3, 4 -> "минуты"
                else -> "минут"
            }
        }
    TimeUnits.HOUR ->
        if (value in 11..19) {
            "часов"
        } else {
            when (value % 10) {
                1 -> "час"
                2, 3, 4 -> "часа"
                else -> "часов"
            }
        }
    TimeUnits.DAY ->
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


enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY
}