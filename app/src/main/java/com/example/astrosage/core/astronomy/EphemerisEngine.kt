package com.example.astrosage.core.astronomy

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

object EphemerisEngine {

    /**
     * Calculate Julian Day from Gregorian calendar date and UTC time.
     */
    fun calculateJulianDay(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        timezoneOffsetHours: Double
    ): Double {
        val totalHoursUtc = hour + (minute / 60.0) - timezoneOffsetHours
        var y = year
        var m = month
        var d = day + (totalHoursUtc / 24.0)

        if (m <= 2) {
            y -= 1
            m += 12
        }

        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)

        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + d + b - 1524.5
    }

    /**
     * Lahiri (Chitra Paksha) Ayanamsha calculation.
     * At J2000.0 (JD 2451545.0), Lahiri Ayanamsha was 23° 51' 11" = 23.8530556°
     * Annual precession rate is ~50.290966 arcseconds / year.
     */
    fun calculateLahiriAyanamsha(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0 // Julian centuries since J2000.0
        // Lahiri Ayanamsha formula as adopted by Indian Ephemeris
        val baseJ2000 = 23.8530556
        val precession = 1.396888 * t + 0.000308 * t * t
        return normalizeDegrees(baseJ2000 + precession)
    }

    /**
     * Calculate Sidereal Nirayana Kundali chart data.
     */
    fun calculateKundali(birthDetails: BirthDetails): KundaliChartData {
        val jd = calculateJulianDay(
            birthDetails.year,
            birthDetails.month,
            birthDetails.day,
            birthDetails.hour,
            birthDetails.minute,
            birthDetails.timezoneOffsetHours
        )

        val ayanamsha = calculateLahiriAyanamsha(jd)
        val t = (jd - 2451545.0) / 36525.0

        // 1. Calculate Tropical Ascendant
        val tropicalAsc = calculateTropicalAscendant(
            jd,
            birthDetails.latitude,
            birthDetails.longitude,
            birthDetails.timezoneOffsetHours,
            birthDetails.hour,
            birthDetails.minute
        )
        val nirayanaAsc = normalizeDegrees(tropicalAsc - ayanamsha)
        val ascSign = ZodiacSign.fromLongitude(nirayanaAsc)
        val ascSignDeg = nirayanaAsc % 30.0
        val (ascNak, ascPada) = Nakshatra.fromLongitude(nirayanaAsc)
        val ascNavamsha = calculateNavamshaSign(ascSign, ascSignDeg)

        val lagnaPos = PlanetaryPosition(
            planet = Planet.ASCENDANT,
            nirayanaLongitude = nirayanaAsc,
            speed = 1.0,
            isRetrograde = false,
            sign = ascSign,
            signDegree = ascSignDeg,
            house = 1,
            nakshatra = ascNak,
            pada = ascPada,
            dignity = "Self",
            navamshaSign = ascNavamsha
        )

        // 2. Calculate Planets (Sayana) then convert to Nirayana
        val planetPositions = mutableListOf<PlanetaryPosition>()
        val primaryPlanets = Planet.primaryNine()

        for (planet in primaryPlanets) {
            val (sayanaLong, speed) = calculateGeocentricLongitude(planet, t, jd)
            val nirayanaLong = normalizeDegrees(sayanaLong - ayanamsha)
            val sign = ZodiacSign.fromLongitude(nirayanaLong)
            val signDeg = nirayanaLong % 30.0
            val house = calculateHouse(nirayanaLong, nirayanaAsc)
            val (nak, pada) = Nakshatra.fromLongitude(nirayanaLong)
            val navamsha = calculateNavamshaSign(sign, signDeg)
            val isRetro = (planet != Planet.SUN && planet != Planet.MOON && planet != Planet.RAHU && planet != Planet.KETU && speed < 0) ||
                    (planet == Planet.RAHU || planet == Planet.KETU)
            val dignity = determineDignity(planet, sign, signDeg)

            planetPositions.add(
                PlanetaryPosition(
                    planet = planet,
                    nirayanaLongitude = nirayanaLong,
                    speed = speed,
                    isRetrograde = isRetro,
                    sign = sign,
                    signDegree = signDeg,
                    house = house,
                    nakshatra = nak,
                    pada = pada,
                    dignity = dignity,
                    navamshaSign = navamsha
                )
            )
        }

        // Map houses for D1 (Lagna Chart)
        val houseMap = mutableMapOf<Int, MutableList<PlanetaryPosition>>()
        for (h in 1..12) houseMap[h] = mutableListOf()
        for (p in planetPositions) {
            houseMap[p.house]?.add(p)
        }

        // Map houses for D9 (Navamsha Chart)
        val navamshaHouseMap = mutableMapOf<Int, MutableList<PlanetaryPosition>>()
        for (h in 1..12) navamshaHouseMap[h] = mutableListOf()
        val navamshaAscSignNum = ascNavamsha.number
        for (p in planetPositions) {
            val navSignNum = p.navamshaSign.number
            val navHouse = ((navSignNum - navamshaAscSignNum + 12) % 12) + 1
            navamshaHouseMap[navHouse]?.add(p)
        }

        // Map houses for Moon Chart (Chandra Kundali)
        val moonPos = planetPositions.first { it.planet == Planet.MOON }
        val moonSignNum = moonPos.sign.number
        val moonHouseMap = mutableMapOf<Int, MutableList<PlanetaryPosition>>()
        for (h in 1..12) moonHouseMap[h] = mutableListOf()
        for (p in planetPositions) {
            val mHouse = ((p.sign.number - moonSignNum + 12) % 12) + 1
            moonHouseMap[mHouse]?.add(p)
        }

        // Vimshottari Dasha calculation
        val dashaTimeline = DashaCalculator.calculateDashaTimeline(
            moonLongitude = moonPos.nirayanaLongitude,
            birthYear = birthDetails.year,
            birthMonth = birthDetails.month,
            birthDay = birthDetails.day
        )
        val (currMaha, currAntar) = DashaCalculator.getCurrentDasha(dashaTimeline)

        // Build structured JSON summary for Gemini
        val planetaryJson = buildPlanetaryJson(
            birthDetails = birthDetails,
            ayanamsha = ayanamsha,
            lagna = lagnaPos,
            planets = planetPositions,
            currMaha = currMaha,
            currAntar = currAntar
        )

        return KundaliChartData(
            birthDetails = birthDetails,
            ayanamsha = ayanamsha,
            lagna = lagnaPos,
            planets = planetPositions,
            houses = houseMap,
            navamshaHouses = navamshaHouseMap,
            moonChartHouses = moonHouseMap,
            currentMahadasha = currMaha,
            currentAntardasha = currAntar,
            dashaTimeline = dashaTimeline,
            planetaryJson = planetaryJson
        )
    }

    private fun calculateHouse(planetLongitude: Double, ascendantLongitude: Double): Int {
        val ascSign = ZodiacSign.fromLongitude(ascendantLongitude).number
        val planetSign = ZodiacSign.fromLongitude(planetLongitude).number
        return ((planetSign - ascSign + 12) % 12) + 1
    }

    fun calculateNavamshaSign(sign: ZodiacSign, signDegree: Double): ZodiacSign {
        val padaIndex = floor(signDegree / (30.0 / 9.0)).toInt() % 9 // 0..8
        val startingSignNum = when (sign.element) {
            "Fire" -> 1   // Aries
            "Earth" -> 10 // Capricorn
            "Air" -> 7    // Libra
            "Water" -> 4  // Cancer
            else -> 1
        }
        val navSignNum = ((startingSignNum - 1 + padaIndex) % 12) + 1
        return ZodiacSign.fromNumber(navSignNum)
    }

    private fun determineDignity(planet: Planet, sign: ZodiacSign, deg: Double): String {
        return when (planet) {
            Planet.SUN -> when {
                sign == ZodiacSign.ARIES && deg <= 10.0 -> "Exalted (Param Uchcha)"
                sign == ZodiacSign.ARIES -> "Exalted"
                sign == ZodiacSign.LIBRA && deg <= 10.0 -> "Debilitated (Param Neecha)"
                sign == ZodiacSign.LIBRA -> "Debilitated"
                sign == ZodiacSign.LEO && deg <= 20.0 -> "Moolatrikona"
                sign == ZodiacSign.LEO -> "Own Sign (Swakshetra)"
                sign in listOf(ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES, ZodiacSign.CANCER, ZodiacSign.SCORPIO) -> "Friendly"
                sign in listOf(ZodiacSign.GEMINI, ZodiacSign.VIRGO) -> "Neutral"
                else -> "Enemy"
            }
            Planet.MOON -> when {
                sign == ZodiacSign.TAURUS && deg <= 3.0 -> "Exalted (Param Uchcha)"
                sign == ZodiacSign.TAURUS -> "Exalted"
                sign == ZodiacSign.SCORPIO && deg <= 3.0 -> "Debilitated (Param Neecha)"
                sign == ZodiacSign.SCORPIO -> "Debilitated"
                sign == ZodiacSign.CANCER -> "Own Sign (Swakshetra)"
                sign in listOf(ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SAGITTARIUS, ZodiacSign.GEMINI) -> "Friendly"
                else -> "Neutral"
            }
            Planet.MARS -> when {
                sign == ZodiacSign.CAPRICORN && deg <= 28.0 -> "Exalted (Param Uchcha)"
                sign == ZodiacSign.CAPRICORN -> "Exalted"
                sign == ZodiacSign.CANCER && deg <= 28.0 -> "Debilitated (Param Neecha)"
                sign == ZodiacSign.CANCER -> "Debilitated"
                sign == ZodiacSign.ARIES && deg <= 12.0 -> "Moolatrikona"
                sign == ZodiacSign.ARIES || sign == ZodiacSign.SCORPIO -> "Own Sign"
                sign in listOf(ZodiacSign.LEO, ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES) -> "Friendly"
                sign in listOf(ZodiacSign.TAURUS, ZodiacSign.LIBRA, ZodiacSign.AQUARIUS) -> "Neutral"
                else -> "Enemy"
            }
            Planet.MERCURY -> when {
                sign == ZodiacSign.VIRGO && deg <= 15.0 -> "Exalted (Param Uchcha)"
                sign == ZodiacSign.VIRGO && deg <= 20.0 -> "Moolatrikona"
                sign == ZodiacSign.VIRGO || sign == ZodiacSign.GEMINI -> "Own Sign"
                sign == ZodiacSign.PISCES && deg <= 15.0 -> "Debilitated (Param Neecha)"
                sign == ZodiacSign.PISCES -> "Debilitated"
                sign in listOf(ZodiacSign.TAURUS, ZodiacSign.LIBRA, ZodiacSign.LEO) -> "Friendly"
                sign in listOf(ZodiacSign.ARIES, ZodiacSign.SCORPIO, ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS) -> "Neutral"
                else -> "Enemy"
            }
            Planet.JUPITER -> when {
                sign == ZodiacSign.CANCER && deg <= 5.0 -> "Exalted (Param Uchcha)"
                sign == ZodiacSign.CANCER -> "Exalted"
                sign == ZodiacSign.CAPRICORN && deg <= 5.0 -> "Debilitated (Param Neecha)"
                sign == ZodiacSign.CAPRICORN -> "Debilitated"
                sign == ZodiacSign.SAGITTARIUS && deg <= 10.0 -> "Moolatrikona"
                sign == ZodiacSign.SAGITTARIUS || sign == ZodiacSign.PISCES -> "Own Sign"
                sign in listOf(ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SCORPIO) -> "Friendly"
                sign == ZodiacSign.AQUARIUS -> "Neutral"
                else -> "Enemy"
            }
            Planet.VENUS -> when {
                sign == ZodiacSign.PISCES && deg <= 27.0 -> "Exalted (Param Uchcha)"
                sign == ZodiacSign.PISCES -> "Exalted"
                sign == ZodiacSign.VIRGO && deg <= 27.0 -> "Debilitated (Param Neecha)"
                sign == ZodiacSign.VIRGO -> "Debilitated"
                sign == ZodiacSign.LIBRA && deg <= 15.0 -> "Moolatrikona"
                sign == ZodiacSign.TAURUS || sign == ZodiacSign.LIBRA -> "Own Sign"
                sign in listOf(ZodiacSign.GEMINI, ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS) -> "Friendly"
                sign in listOf(ZodiacSign.ARIES, ZodiacSign.SCORPIO) -> "Neutral"
                else -> "Enemy"
            }
            Planet.SATURN -> when {
                sign == ZodiacSign.LIBRA && deg <= 20.0 -> "Exalted (Param Uchcha)"
                sign == ZodiacSign.LIBRA -> "Exalted"
                sign == ZodiacSign.ARIES && deg <= 20.0 -> "Debilitated (Param Neecha)"
                sign == ZodiacSign.ARIES -> "Debilitated"
                sign == ZodiacSign.AQUARIUS && deg <= 20.0 -> "Moolatrikona"
                sign == ZodiacSign.CAPRICORN || sign == ZodiacSign.AQUARIUS -> "Own Sign"
                sign in listOf(ZodiacSign.GEMINI, ZodiacSign.VIRGO, ZodiacSign.TAURUS) -> "Friendly"
                sign == ZodiacSign.SAGITTARIUS || sign == ZodiacSign.PISCES -> "Neutral"
                else -> "Enemy"
            }
            Planet.RAHU -> when {
                sign == ZodiacSign.TAURUS || sign == ZodiacSign.GEMINI -> "Exalted"
                sign == ZodiacSign.VIRGO -> "Moolatrikona"
                sign in listOf(ZodiacSign.LIBRA, ZodiacSign.AQUARIUS) -> "Friendly"
                sign in listOf(ZodiacSign.SCORPIO, ZodiacSign.SAGITTARIUS) -> "Debilitated"
                else -> "Neutral"
            }
            Planet.KETU -> when {
                sign == ZodiacSign.SCORPIO || sign == ZodiacSign.SAGITTARIUS -> "Exalted"
                sign == ZodiacSign.PISCES -> "Moolatrikona"
                sign in listOf(ZodiacSign.ARIES, ZodiacSign.LEO) -> "Friendly"
                sign in listOf(ZodiacSign.TAURUS, ZodiacSign.GEMINI) -> "Debilitated"
                else -> "Neutral"
            }
            else -> "Neutral"
        }
    }

    /**
     * Compute Tropical Ascendant (Lagna)
     */
    private fun calculateTropicalAscendant(
        jd: Double,
        latDeg: Double,
        lonDeg: Double,
        tzOffset: Double,
        hour: Int,
        minute: Int
    ): Double {
        // Universal Time in fractional hours
        val utHours = hour + (minute / 60.0) - tzOffset
        val d = jd - 2451545.0

        // Greenwich Mean Sidereal Time (GMST) in degrees
        var gmst = 280.46061837 + 360.98564736629 * d
        gmst = normalizeDegrees(gmst)

        // Local Sidereal Time (RAMC in degrees)
        val ramc = normalizeDegrees(gmst + lonDeg)

        // Obliquity of ecliptic (eps)
        val t = d / 36525.0
        val eps = 23.4392911 - 0.0130042 * t

        val ramcRad = Math.toRadians(ramc)
        val epsRad = Math.toRadians(eps)
        val latRad = Math.toRadians(latDeg)

        // Formula: tan(Asc) = cos(RAMC) / (-sin(RAMC)*cos(eps) - tan(lat)*sin(eps))
        val y = cos(ramcRad)
        val x = -sin(ramcRad) * cos(epsRad) - tan(latRad) * sin(epsRad)

        var asc = Math.toDegrees(atan2(y, x))
        asc = normalizeDegrees(asc)
        return asc
    }

    /**
     * Geocentric ecliptic longitude calculation for planets using astronomical algorithms.
     */
    private fun calculateGeocentricLongitude(planet: Planet, t: Double, jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        return when (planet) {
            Planet.SUN -> {
                val l0 = 280.46646 + 36000.76983 * t + 0.0003032 * t * t
                val m = 357.52911 + 35999.05029 * t - 0.0001537 * t * t
                val mRad = Math.toRadians(normalizeDegrees(m))
                val c = (1.914602 - 0.004817 * t) * sin(mRad) +
                        (0.019993 - 0.000101 * t) * sin(2 * mRad) +
                        0.000289 * sin(3 * mRad)
                val trueLong = normalizeDegrees(l0 + c)
                val speed = 0.9856
                Pair(trueLong, speed)
            }
            Planet.MOON -> {
                // High-precision lunar perturbation terms (Brown's / Meeus)
                val lp = 218.3164477 + 481267.88123421 * t
                val dElong = 297.8501921 + 445267.1114034 * t
                val mSun = 357.5291092 + 35999.0502909 * t
                val mMoon = 134.9633964 + 477198.8675055 * t
                val f = 93.2720950 + 483202.0175233 * t

                val dRad = Math.toRadians(normalizeDegrees(dElong))
                val mSunRad = Math.toRadians(normalizeDegrees(mSun))
                val mMoonRad = Math.toRadians(normalizeDegrees(mMoon))
                val fRad = Math.toRadians(normalizeDegrees(f))

                // Perturbation terms
                val dl = 6.288774 * sin(mMoonRad) +
                        1.274027 * sin(2 * dRad - mMoonRad) +
                        0.658314 * sin(2 * dRad) +
                        0.213618 * sin(2 * mMoonRad) -
                        0.185116 * sin(mSunRad) -
                        0.114332 * sin(2 * fRad) +
                        0.058793 * sin(2 * dRad - 2 * mMoonRad) +
                        0.057066 * sin(2 * dRad - mSunRad - mMoonRad) +
                        0.053322 * sin(2 * dRad + mMoonRad) +
                        0.046153 * sin(2 * dRad - mSunRad)

                val moonLong = normalizeDegrees(lp + dl)
                val speed = 13.176
                Pair(moonLong, speed)
            }
            Planet.MARS -> {
                val l = 355.433 + 19140.299 * t
                val m = 19.373 + 19139.854 * t
                val mRad = Math.toRadians(normalizeDegrees(m))
                val c = 10.691 * sin(mRad) + 0.623 * sin(2 * mRad)
                val helio = normalizeDegrees(l + c)
                val geo = calculateGeoOffset(helio, 1.524, t, 0.524)
                Pair(geo.first, geo.second)
            }
            Planet.MERCURY -> {
                val l = 252.251 + 149472.675 * t
                val m = 174.795 + 149472.515 * t
                val mRad = Math.toRadians(normalizeDegrees(m))
                val c = 23.440 * sin(mRad) + 2.982 * sin(2 * mRad)
                val helio = normalizeDegrees(l + c)
                val geo = calculateGeoOffset(helio, 0.387, t, 4.092)
                Pair(geo.first, geo.second)
            }
            Planet.VENUS -> {
                val l = 181.979 + 58517.816 * t
                val m = 50.116 + 58517.804 * t
                val mRad = Math.toRadians(normalizeDegrees(m))
                val c = 0.776 * sin(mRad) + 0.003 * sin(2 * mRad)
                val helio = normalizeDegrees(l + c)
                val geo = calculateGeoOffset(helio, 0.723, t, 1.602)
                Pair(geo.first, geo.second)
            }
            Planet.JUPITER -> {
                val l = 34.351 + 3034.906 * t
                val m = 20.020 + 3034.690 * t
                val mRad = Math.toRadians(normalizeDegrees(m))
                val c = 5.555 * sin(mRad) + 0.168 * sin(2 * mRad)
                val helio = normalizeDegrees(l + c)
                val geo = calculateGeoOffset(helio, 5.204, t, 0.083)
                Pair(geo.first, geo.second)
            }
            Planet.SATURN -> {
                val l = 50.077 + 1222.114 * t
                val m = 317.021 + 1221.551 * t
                val mRad = Math.toRadians(normalizeDegrees(m))
                val c = 6.358 * sin(mRad) + 0.220 * sin(2 * mRad)
                val helio = normalizeDegrees(l + c)
                val geo = calculateGeoOffset(helio, 9.582, t, 0.033)
                Pair(geo.first, geo.second)
            }
            Planet.RAHU -> {
                // Mean Lunar Node (Rahu)
                val omega = 125.04452 - 1934.136261 * t + 0.0020708 * t * t
                val rahuLong = normalizeDegrees(omega)
                Pair(rahuLong, -0.05295) // Always retrograde
            }
            Planet.KETU -> {
                val omega = 125.04452 - 1934.136261 * t + 0.0020708 * t * t
                val ketuLong = normalizeDegrees(omega + 180.0)
                Pair(ketuLong, -0.05295) // Always retrograde
            }
            else -> Pair(0.0, 1.0)
        }
    }

    private fun calculateGeoOffset(helioLong: Double, a: Double, t: Double, meanMotion: Double): Pair<Double, Double> {
        val sunL0 = 280.46646 + 36000.76983 * t
        val sunRad = Math.toRadians(normalizeDegrees(sunL0))
        val helioRad = Math.toRadians(helioLong)

        // Earth-Sun distance approx 1.0 AU
        val xHelio = a * cos(helioRad)
        val yHelio = a * sin(helioRad)

        val xEarth = cos(sunRad)
        val yEarth = sin(sunRad)

        val xGeo = xHelio - xEarth
        val yGeo = yHelio - yEarth

        val geoLong = normalizeDegrees(Math.toDegrees(atan2(yGeo, xGeo)))
        // Approximate daily motion rate & retrograde detection
        val diff = normalizeDegrees(geoLong - sunL0)
        val speed = if (a > 1.0) {
            if (diff in 160.0..200.0) -0.15 else meanMotion
        } else {
            if (diff in 0.0..20.0 || diff in 340.0..360.0) -0.3 else meanMotion
        }
        return Pair(geoLong, speed)
    }

    fun normalizeDegrees(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0.0) d += 360.0
        return d
    }

    private fun buildPlanetaryJson(
        birthDetails: BirthDetails,
        ayanamsha: Double,
        lagna: PlanetaryPosition,
        planets: List<PlanetaryPosition>,
        currMaha: Planet,
        currAntar: Planet
    ): String {
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"birth_details\": {\n")
        sb.append("    \"name\": \"${birthDetails.name}\",\n")
        sb.append("    \"datetime\": \"${birthDetails.formattedDateTime()}\",\n")
        sb.append("    \"place\": \"${birthDetails.placeName}\",\n")
        sb.append("    \"latitude\": ${birthDetails.latitude},\n")
        sb.append("    \"longitude\": ${birthDetails.longitude},\n")
        sb.append("    \"ayanamsha_lahiri\": ${String.format(Locale.US, "%.4f", ayanamsha)}\n")
        sb.append("  },\n")

        sb.append("  \"lagna\": {\n")
        sb.append("    \"sign\": \"${lagna.sign.sanskritName} (${lagna.sign.englishName})\",\n")
        sb.append("    \"degree\": ${String.format(Locale.US, "%.2f", lagna.signDegree)},\n")
        sb.append("    \"nakshatra\": \"${lagna.nakshatra.englishName}\",\n")
        sb.append("    \"pada\": ${lagna.pada},\n")
        sb.append("    \"navamsha_sign\": \"${lagna.navamshaSign.englishName}\"\n")
        sb.append("  },\n")

        sb.append("  \"current_dasha\": {\n")
        sb.append("    \"mahadasha\": \"${currMaha.sanskritName}\",\n")
        sb.append("    \"antardasha\": \"${currAntar.sanskritName}\"\n")
        sb.append("  },\n")

        sb.append("  \"planets\": [\n")
        for ((idx, p) in planets.withIndex()) {
            sb.append("    {\n")
            sb.append("      \"planet\": \"${p.planet.sanskritName} (${p.planet.englishName})\",\n")
            sb.append("      \"nirayana_longitude\": ${String.format(Locale.US, "%.2f", p.nirayanaLongitude)},\n")
            sb.append("      \"sign\": \"${p.sign.sanskritName} (${p.sign.englishName})\",\n")
            sb.append("      \"sign_degree\": ${String.format(Locale.US, "%.2f", p.signDegree)},\n")
            sb.append("      \"house\": ${p.house},\n")
            sb.append("      \"retrograde\": ${p.isRetrograde},\n")
            sb.append("      \"dignity\": \"${p.dignity}\",\n")
            sb.append("      \"nakshatra\": \"${p.nakshatra.englishName}\",\n")
            sb.append("      \"pada\": ${p.pada},\n")
            sb.append("      \"navamsha\": \"${p.navamshaSign.englishName}\"\n")
            sb.append("    }")
            if (idx < planets.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ]\n")
        sb.append("}")
        return sb.toString()
    }
}
