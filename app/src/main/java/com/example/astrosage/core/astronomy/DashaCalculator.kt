package com.example.astrosage.core.astronomy

import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

object DashaCalculator {

    val DASHA_LORDS_ORDER = listOf(
        Planet.KETU,
        Planet.VENUS,
        Planet.SUN,
        Planet.MOON,
        Planet.MARS,
        Planet.RAHU,
        Planet.JUPITER,
        Planet.SATURN,
        Planet.MERCURY
    )

    fun calculateDashaTimeline(
        moonLongitude: Double,
        birthYear: Int,
        birthMonth: Int,
        birthDay: Int
    ): List<DashaPeriod> {
        val (nak, _) = Nakshatra.fromLongitude(moonLongitude)
        val nakSpan = 360.0 / 27.0 // 13.333333
        val normalized = (moonLongitude % 360.0 + 360.0) % 360.0
        val nakIndex = (nak.number - 1)
        val posInNak = normalized - (nakIndex * nakSpan)
        val fractionRemaining = 1.0 - (posInNak / nakSpan).coerceIn(0.0, 1.0)

        // Find starting lord in sequence
        val startLordIndex = DASHA_LORDS_ORDER.indexOf(nak.lord)
        val periods = mutableListOf<DashaPeriod>()

        var currentCal = Calendar.getInstance()
        currentCal.set(birthYear, birthMonth - 1, birthDay, 0, 0, 0)
        val todayCal = Calendar.getInstance()

        var lordIdx = startLordIndex
        // First Mahadasha balance
        val firstLord = DASHA_LORDS_ORDER[lordIdx]
        val firstLordYears = firstLord.dashaYears * fractionRemaining
        val firstStartCal = currentCal.clone() as Calendar

        val firstEndCal = currentCal.clone() as Calendar
        addFractionalYears(firstEndCal, firstLordYears)

        val firstSubPeriods = calculateAntardashas(firstLord, firstStartCal, firstEndCal, todayCal)
        val isFirstCurrent = !todayCal.before(firstStartCal) && !todayCal.after(firstEndCal)

        periods.add(
            DashaPeriod(
                lord = firstLord,
                startDate = formatDate(firstStartCal),
                endDate = formatDate(firstEndCal),
                subPeriods = firstSubPeriods,
                isCurrent = isFirstCurrent
            )
        )

        currentCal = firstEndCal.clone() as Calendar
        lordIdx = (lordIdx + 1) % DASHA_LORDS_ORDER.size

        // Calculate subsequent Mahadashas up to 100+ years of age
        for (i in 1..8) {
            val lord = DASHA_LORDS_ORDER[lordIdx]
            val mStartCal = currentCal.clone() as Calendar
            val mEndCal = currentCal.clone() as Calendar
            addFractionalYears(mEndCal, lord.dashaYears.toDouble())

            val isCurrent = !todayCal.before(mStartCal) && !todayCal.after(mEndCal)
            val subPeriods = calculateAntardashas(lord, mStartCal, mEndCal, todayCal)

            periods.add(
                DashaPeriod(
                    lord = lord,
                    startDate = formatDate(mStartCal),
                    endDate = formatDate(mEndCal),
                    subPeriods = subPeriods,
                    isCurrent = isCurrent
                )
            )

            currentCal = mEndCal.clone() as Calendar
            lordIdx = (lordIdx + 1) % DASHA_LORDS_ORDER.size
        }

        return periods
    }

    private fun calculateAntardashas(
        mahaLord: Planet,
        mStart: Calendar,
        mEnd: Calendar,
        todayCal: Calendar
    ): List<DashaPeriod> {
        val subPeriods = mutableListOf<DashaPeriod>()
        val startIdx = DASHA_LORDS_ORDER.indexOf(mahaLord)
        var cursor = mStart.clone() as Calendar
        val totalMahaDays = (mEnd.timeInMillis - mStart.timeInMillis).toDouble() / (1000 * 60 * 60 * 24)

        for (i in 0..8) {
            val antarLord = DASHA_LORDS_ORDER[(startIdx + i) % DASHA_LORDS_ORDER.size]
            // Antardasha proportion = (MahaYears * AntarYears) / 120
            val fraction = (mahaLord.dashaYears * antarLord.dashaYears) / (120.0 * mahaLord.dashaYears)
            val antarDays = totalMahaDays * fraction

            val aStart = cursor.clone() as Calendar
            val aEnd = cursor.clone() as Calendar
            aEnd.add(Calendar.DAY_OF_YEAR, floor(antarDays).toInt())

            val isCurrent = !todayCal.before(aStart) && !todayCal.after(aEnd)
            subPeriods.add(
                DashaPeriod(
                    lord = antarLord,
                    startDate = formatDate(aStart),
                    endDate = formatDate(aEnd),
                    isCurrent = isCurrent
                )
            )
            cursor = aEnd.clone() as Calendar
        }
        return subPeriods
    }

    fun getCurrentDasha(periods: List<DashaPeriod>): Pair<Planet, Planet> {
        val currMaha = periods.firstOrNull { it.isCurrent } ?: periods.firstOrNull() ?: DashaPeriod(Planet.JUPITER, "", "")
        val currAntar = currMaha.subPeriods.firstOrNull { it.isCurrent }?.lord
            ?: currMaha.subPeriods.firstOrNull()?.lord
            ?: currMaha.lord
        return Pair(currMaha.lord, currAntar)
    }

    private fun addFractionalYears(cal: Calendar, years: Double) {
        val wholeYears = floor(years).toInt()
        val remainingDays = floor((years - wholeYears) * 365.25).toInt()
        cal.add(Calendar.YEAR, wholeYears)
        cal.add(Calendar.DAY_OF_YEAR, remainingDays)
    }

    private fun formatDate(cal: Calendar): String {
        return String.format(
            Locale.US,
            "%02d/%02d/%04d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)
        )
    }
}
