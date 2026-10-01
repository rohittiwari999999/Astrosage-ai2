package com.example.astrosage.core.astronomy

import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

data class PanchangData(
    val dateString: String,
    val tithi: String,
    val paksha: String,
    val nakshatra: String,
    val yoga: String,
    val karana: String,
    val vaar: String,
    val sunrise: String,
    val sunset: String,
    val rahuKaal: String,
    val abhijitMuhurat: String
)

object PanchangEngine {

    private val YOGAS = listOf(
        "Vishkumbha", "Priti", "Ayushman", "Saubhagya", "Sobhana",
        "Atiganda", "Sukarma", "Dhriti", "Shula", "Ganda",
        "Vriddhi", "Dhruva", "Vyaghata", "Harshana", "Vajra",
        "Siddhi", "Vyatipata", "Variyan", "Parigha", "Shiva",
        "Siddha", "Sadhya", "Shubha", "Shukla", "Brahma",
        "Indra", "Vaidhriti"
    )

    private val MOVABLE_KARANAS = listOf(
        "Bava", "Balava", "Kaulava", "Taitila", "Gara", "Vanija", "Vishti (Bhadra)"
    )

    private val TITHIS = listOf(
        "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
        "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
        "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima / Amavasya"
    )

    fun calculatePanchang(cal: Calendar, lat: Double, lon: Double): PanchangData {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val hour = 6 // Standard sunrise reference
        val minute = 0

        val jd = EphemerisEngine.calculateJulianDay(year, month, day, hour, minute, 5.5)
        val ayanamsha = EphemerisEngine.calculateLahiriAyanamsha(jd)

        val birthDetails = BirthDetails(
            year = year,
            month = month,
            day = day,
            hour = hour,
            minute = minute,
            latitude = lat,
            longitude = lon,
            timezoneOffsetHours = 5.5
        )

        val chart = EphemerisEngine.calculateKundali(birthDetails)
        val sun = chart.planets.first { it.planet == Planet.SUN }
        val moon = chart.planets.first { it.planet == Planet.MOON }

        // Tithi calculation: (MoonLong - SunLong) % 360 / 12
        val diff = EphemerisEngine.normalizeDegrees(moon.nirayanaLongitude - sun.nirayanaLongitude)
        val tithiIndex = floor(diff / 12.0).toInt() // 0..29
        val paksha = if (tithiIndex < 15) "Shukla Paksha" else "Krishna Paksha"
        val tithiName = TITHIS[tithiIndex % 15]

        // Nakshatra
        val nakName = "${moon.nakshatra.englishName} (Pada ${moon.pada})"

        // Yoga: (SunLong + MoonLong) % 360 / (360/27)
        val sum = EphemerisEngine.normalizeDegrees(sun.nirayanaLongitude + moon.nirayanaLongitude)
        val yogaIndex = floor(sum / (360.0 / 27.0)).toInt() % 27
        val yogaName = YOGAS[yogaIndex]

        // Karana: diff / 6.0
        val karanaIndex = floor(diff / 6.0).toInt()
        val karanaName = when {
            karanaIndex == 0 -> "Kintughna"
            karanaIndex in 1..56 -> MOVABLE_KARANAS[(karanaIndex - 1) % 7]
            karanaIndex == 57 -> "Shakuni"
            karanaIndex == 58 -> "Chatushpada"
            else -> "Naga"
        }

        // Vaar
        val vaar = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Ravivar (Sun)"
            Calendar.MONDAY -> "Somvar (Moon)"
            Calendar.TUESDAY -> "Mangalvar (Mars)"
            Calendar.WEDNESDAY -> "Budhvar (Mercury)"
            Calendar.THURSDAY -> "Guruvar (Jupiter)"
            Calendar.FRIDAY -> "Shukravar (Venus)"
            else -> "Shanivar (Saturn)"
        }

        // Sunrise & Sunset approximate calculation
        val sunriseHour = 6
        val sunriseMin = 12
        val sunsetHour = 18
        val sunsetMin = 28

        val sunriseStr = String.format(Locale.US, "%02d:%02d AM", sunriseHour, sunriseMin)
        val sunsetStr = String.format(Locale.US, "%02d:%02d PM", sunsetHour - 12, sunsetMin)

        // Rahu Kaal depends on day of week
        val rahuKaal = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "04:30 PM - 06:00 PM"
            Calendar.MONDAY -> "07:30 AM - 09:00 AM"
            Calendar.TUESDAY -> "03:00 PM - 04:30 PM"
            Calendar.WEDNESDAY -> "12:00 PM - 01:30 PM"
            Calendar.THURSDAY -> "01:30 PM - 03:00 PM"
            Calendar.FRIDAY -> "10:30 AM - 12:00 PM"
            else -> "09:00 AM - 10:30 AM" // Saturday
        }

        val abhijitMuhurat = "11:52 AM - 12:44 PM"

        val dateStr = String.format(
            Locale.US,
            "%02d %s %04d",
            day,
            cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH),
            year
        )

        return PanchangData(
            dateString = dateStr,
            tithi = tithiName,
            paksha = paksha,
            nakshatra = nakName,
            yoga = yogaName,
            karana = karanaName,
            vaar = vaar,
            sunrise = sunriseStr,
            sunset = sunsetStr,
            rahuKaal = rahuKaal,
            abhijitMuhurat = abhijitMuhurat
        )
    }
}
