package me.nathanfallet.uhaconnect.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Instant.timeAgo():String{
    val currDate = Clock.System.now()
    val diffBetween = currDate.minus(this)
    var un = 1
    if (diffBetween.compareTo(un.toDuration(DurationUnit.DAYS))>=0) return diffBetween.inWholeDays.toString() + "d"
    if (diffBetween.compareTo(un.toDuration(DurationUnit.HOURS))>=0) return diffBetween.inWholeHours.toString() + "h"
    if (diffBetween.compareTo(un.toDuration(DurationUnit.MINUTES))>=0) return diffBetween.inWholeMinutes.toString() + "m"
    return diffBetween.inWholeSeconds.toString() + "s"
}