package me.nathanfallet.uhaconnect.extensions

import android.content.Context
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.nathanfallet.uhaconnect.R
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Instant.timeAgo(context: Context):String{
    val currDate = Clock.System.now()
    val diffBetween = currDate.minus(this)
    var un = 1
    if (diffBetween.compareTo(un.toDuration(DurationUnit.DAYS))>=0) return context.resources.getString(R.string.instantextension_day,diffBetween.inWholeDays.toString())
    if (diffBetween.compareTo(un.toDuration(DurationUnit.HOURS))>=0) return context.resources.getString(R.string.instantextension_hours,diffBetween.inWholeHours.toString())
    if (diffBetween.compareTo(un.toDuration(DurationUnit.MINUTES))>=0) return context.resources.getString(R.string.instantextension_minutes,diffBetween.inWholeMinutes.toString())
    return context.resources.getString(R.string.instantextension_seconds,diffBetween.inWholeSeconds.toString())
}