package com.kingseptic.domain

import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * Estimates how often a septic tank should be pumped.
 *
 * The estimate follows the widely published Penn State Extension pumping
 * frequency table, which is well approximated by
 * `years = 0.013 * tankGallons / occupants - 0.65`. A garbage disposal adds
 * roughly 50% more solids, so the interval is scaled by 2/3 in that case.
 *
 * The raw estimate can be very long for a large tank with one occupant; most
 * professionals still recommend service at least every 5 years, so
 * [Estimate.recommendedIntervalYears] is clamped to a practical range.
 */
object PumpingScheduleCalculator {

    const val MIN_TANK_GALLONS = 250
    const val MAX_TANK_GALLONS = 5000
    const val MIN_OCCUPANTS = 1
    const val MAX_OCCUPANTS = 20

    const val MIN_RECOMMENDED_YEARS = 0.5
    const val MAX_RECOMMENDED_YEARS = 5.0

    private const val GALLONS_FACTOR = 0.013
    private const val OFFSET_YEARS = 0.65
    private const val GARBAGE_DISPOSAL_FACTOR = 2.0 / 3.0

    data class Estimate(
        /** Years until the tank is expected to need pumping, unclamped. */
        val estimatedYearsUntilFull: Double,
        /** Practical service interval, clamped to [MIN_RECOMMENDED_YEARS]..[MAX_RECOMMENDED_YEARS]. */
        val recommendedIntervalYears: Double
    ) {
        val recommendedIntervalMonths: Int get() = (recommendedIntervalYears * 12).roundToInt()
    }

    fun estimate(tankGallons: Int, occupants: Int, hasGarbageDisposal: Boolean = false): Estimate {
        require(tankGallons in MIN_TANK_GALLONS..MAX_TANK_GALLONS) {
            "Tank size must be between $MIN_TANK_GALLONS and $MAX_TANK_GALLONS gallons."
        }
        require(occupants in MIN_OCCUPANTS..MAX_OCCUPANTS) {
            "Household size must be between $MIN_OCCUPANTS and $MAX_OCCUPANTS people."
        }

        var years = GALLONS_FACTOR * tankGallons / occupants - OFFSET_YEARS
        if (hasGarbageDisposal) years *= GARBAGE_DISPOSAL_FACTOR
        years = years.coerceAtLeast(MIN_RECOMMENDED_YEARS)

        val recommended = years.coerceIn(MIN_RECOMMENDED_YEARS, MAX_RECOMMENDED_YEARS)
        return Estimate(
            estimatedYearsUntilFull = roundToTenth(years),
            recommendedIntervalYears = roundToTenth(recommended)
        )
    }

    /** The date the next pumping is due, given when the tank was last pumped. */
    fun nextDueDate(
        lastPumped: LocalDate,
        tankGallons: Int,
        occupants: Int,
        hasGarbageDisposal: Boolean = false
    ): LocalDate {
        val months = estimate(tankGallons, occupants, hasGarbageDisposal).recommendedIntervalMonths
        return lastPumped.plusMonths(months.toLong())
    }

    private fun roundToTenth(value: Double): Double = (value * 10).roundToInt() / 10.0
}
