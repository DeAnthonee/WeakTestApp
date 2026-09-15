package com.kingseptic.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PumpingScheduleCalculatorTest {

    /** Reference values from the Penn State Extension pumping frequency table. */
    private val publishedTable = listOf(
        Triple(1000, 1, 12.4),
        Triple(1000, 2, 5.9),
        Triple(1000, 4, 2.6),
        Triple(1000, 8, 1.0),
        Triple(1500, 3, 5.9),
        Triple(1500, 4, 4.2),
        Triple(500, 4, 1.0),
        Triple(2000, 6, 3.7),
        Triple(2500, 8, 3.4)
    )

    @Test
    fun `raw estimate tracks the published table within a few months`() {
        publishedTable.forEach { (gallons, people, expectedYears) ->
            val estimate = PumpingScheduleCalculator.estimate(gallons, people)
            assertEquals(
                "tank=$gallons people=$people",
                expectedYears, estimate.estimatedYearsUntilFull, 0.3
            )
        }
    }

    @Test
    fun `recommended interval is clamped to a practical range`() {
        val big = PumpingScheduleCalculator.estimate(2500, 1)
        assertTrue(big.estimatedYearsUntilFull > 20)
        assertEquals(PumpingScheduleCalculator.MAX_RECOMMENDED_YEARS, big.recommendedIntervalYears, 0.0)

        val small = PumpingScheduleCalculator.estimate(500, 8)
        assertEquals(PumpingScheduleCalculator.MIN_RECOMMENDED_YEARS, small.recommendedIntervalYears, 0.0)
    }

    @Test
    fun `typical family of four with a 1000 gallon tank is about every two and a half years`() {
        val estimate = PumpingScheduleCalculator.estimate(1000, 4)
        assertEquals(2.6, estimate.recommendedIntervalYears, 0.05)
        assertEquals(31, estimate.recommendedIntervalMonths)
    }

    @Test
    fun `garbage disposal shortens the interval`() {
        val without = PumpingScheduleCalculator.estimate(1000, 4)
        val with = PumpingScheduleCalculator.estimate(1000, 4, hasGarbageDisposal = true)
        assertTrue(with.estimatedYearsUntilFull < without.estimatedYearsUntilFull)
        assertEquals(1.7, with.estimatedYearsUntilFull, 0.05)
    }

    @Test
    fun `next due date adds the recommended months to the last service`() {
        val due = PumpingScheduleCalculator.nextDueDate(LocalDate.of(2024, 3, 15), 1000, 4)
        assertEquals(LocalDate.of(2026, 10, 15), due)
    }

    @Test
    fun `out of range input is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            PumpingScheduleCalculator.estimate(100, 4)
        }
        assertThrows(IllegalArgumentException::class.java) {
            PumpingScheduleCalculator.estimate(1000, 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            PumpingScheduleCalculator.estimate(1000, 21)
        }
    }
}
