package com.bk.mmovies.util

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

// Google resets the Gemini API's daily quotas at midnight in this zone. The
// zone-database name (not a fixed UTC-7/UTC-8 offset) so daylight saving is
// applied automatically.
private val PACIFIC_ZONE: ZoneId = ZoneId.of("America/Los_Angeles")

/**
 * Today's calendar date as seen in Pacific time, as the number of days since
 * 1970-01-01 (e.g. 2026-09-24 -> 20720).
 *
 * Reads the current moment from [clock] (the device clock by default), looks
 * at it in Los Angeles, keeps only the date, and counts days. The number
 * carries no time zone itself - the zone only decides *which day* it is - so
 * two values can be compared directly: a smaller one is an earlier Pacific
 * day. Pass a fixed [Clock] in tests to pretend it is a different day.
 */
fun currentPacificEpochDay(clock: Clock = Clock.systemUTC()): Long =
        LocalDate.now(clock.withZone(PACIFIC_ZONE)).toEpochDay()

/**
 * The start of the next Pacific day - the moment Google resets daily quotas -
 * as a zoned date-time in Pacific time. Convert it to another zone with
 * `withZoneSameInstant` to show it in local time.
 */
fun nextPacificMidnight(clock: Clock = Clock.systemUTC()): ZonedDateTime =
        LocalDate.now(clock.withZone(PACIFIC_ZONE)).plusDays(1).atStartOfDay(PACIFIC_ZONE)
