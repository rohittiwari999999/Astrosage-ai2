package com.example.astrosage.core.astronomy

import kotlin.math.*

enum class Planet(val hindiName: String, val englishName: String, val symbol: String) {
    SUN("सूर्य", "Sun", "☉"),
    MOON("चन्द्र", "Moon", "☽"),
    MARS("मंगल", "Mars", "♂"),
    MERCURY("बुध", "Mercury", "☿"),
    JUPITER("गुरु", "Jupiter", "♃"),
    VENUS("शुक्र", "Venus", "♀"),
    SATURN("शनि", "Saturn", "♄"),
    RAHU("राहु", "Rahu", "☊"),
    KETU("केतु", "Ketu", "☋")
}

data class PlanetPosition(
    val planet: Planet,
    val longitude: Double,
    val signIndex: Int,
    val signNameHi: String,
    val signNameEn: String,
    val degreeInSign: Double,
    val formattedDegree: String,
    val isRetrograde: Boolean = false,
    val nakshatraIndex: Int,
    val nakshatraNameHi: String,
    val nakshatraPada: Int
)

object EphemerisEngine {
    val ZODIAC_SIGNS_HI = listOf(
        "मेष", "वृषभ", "मिथुन", "कर्क", "सिंह", "कन्या",
        "तुला", "वृश्चिक", "धनु", "मकर", "कुम्भ", "मीन"
    )
    val ZODIAC_SIGNS_EN = listOf(
        "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
        "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    )

    val NAKSHATRAS_HI = listOf(
        "अश्विनी", "भरणी", "कृत्तिका", "रोहिणी", "मृगशिरा", "आर्द्रा",
        "पुनर्वसु", "पुष्य", "अश्लेषा", "मघा", "पूर्वाफाल्गुनी", "उत्तराफाल्गुनी",
        "हस्त", "चित्रा", "स्वाति", "विशाखा", "अनुराधा", "ज्येष्ठा",
        "मूल", "पूर्वाषाढ़ा", "उत्तराषाढ़ा", "श्रवण", "धनिष्ठा", "शतभिषा",
        "पूर्वाभाद्रपद", "उत्तराभाद्रपद", "रेवती"
    )

    fun toJulianDay(year: Int, month: Int, day: Int, hour: Int = 12, minute: Int = 0, second: Int = 0): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = y / 100
        val b = 2 - a + (a / 4)
        val dayFraction = (hour + (minute / 60.0) + (second / 3600.0)) / 24.0
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + dayFraction + b - 1524.5
    }

    /**
     * Lahiri Ayanamsha (Chitra Paksha) calculation based on standard IAU precession.
     */
    fun calculateLahiriAyanamsha(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        // Standard Lahiri Ayanamsha at J2000 is approx 23° 51' 25.5" (23.85708 deg)
        // Precession rate is approx 50.29 arcseconds / year = 1.3969 deg / century
        return normalizeDegrees(23.85708 + 1.39697 * t - 0.0003 * t * t)
    }

    private fun normalizeDegrees(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0) d += 360.0
        return d
    }

    /**
     * Geocentric ecliptic longitude calculation for planets using astronomical algorithms.
     */
    internal fun calculateGeocentricLongitude(planet: Planet, t: Double, jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        var speed = 1.0

        val lon = when (planet) {
            Planet.SUN -> {
                val l0 = 280.46646 + 36000.76983 * t + 0.0003032 * t * t
                val m = 357.52911 + 35999.05029 * t - 0.0001537 * t * t
                val mr = Math.toRadians(normalizeDegrees(m))
                val c = (1.914602 - 0.004817 * t) * sin(mr) + (0.019993 - 0.000101 * t) * sin(2 * mr) + 0.000289 * sin(3 * mr)
                speed = 0.9856
                normalizeDegrees(l0 + c)
            }
            Planet.MOON -> {
                val lp = 218.3164477 + 481267.88123421 * t
                val dEl = 297.8501921 + 445267.1114034 * t
                val m = 357.5291092 + 35999.0502909 * t
                val mp = 134.9633964 + 477198.8675055 * t
                val f = 93.2720950 + 483202.0175233 * t

                val dRad = Math.toRadians(dEl)
                val mRad = Math.toRadians(m)
                val mpRad = Math.toRadians(mp)
                val fRad = Math.toRadians(f)

                val corr = 6.288774 * sin(mpRad) +
                        1.274027 * sin(2 * dRad - mpRad) +
                        0.658314 * sin(2 * dRad) +
                        0.213618 * sin(2 * mpRad) -
                        0.185116 * sin(mRad) -
                        0.114332 * sin(2 * fRad)

                speed = 13.176
                normalizeDegrees(lp + corr)
            }
            Planet.MARS -> {
                val m = 19.373 + 19140.299 * t
                val l = 355.433 + 19140.299 * t + 10.691 * sin(Math.toRadians(m))
                speed = 0.524
                normalizeDegrees(l)
            }
            Planet.MERCURY -> {
                val m = 174.794 + 149472.674 * t
                val l = 252.251 + 149472.674 * t + 23.44 * sin(Math.toRadians(m))
                speed = 1.383
                normalizeDegrees(l)
            }
            Planet.JUPITER -> {
                val m = 20.020 + 3034.906 * t
                val l = 34.351 + 3034.906 * t + 5.555 * sin(Math.toRadians(m))
                speed = 0.083
                normalizeDegrees(l)
            }
            Planet.VENUS -> {
                val m = 50.116 + 58517.815 * t
                val l = 181.979 + 58517.815 * t + 0.776 * sin(Math.toRadians(m))
                speed = 1.2
                normalizeDegrees(l)
            }
            Planet.SATURN -> {
                val m = 317.021 + 1222.114 * t
                val l = 50.077 + 1222.114 * t + 6.358 * sin(Math.toRadians(m))
                speed = 0.033
                normalizeDegrees(l)
            }
            Planet.RAHU -> {
                val omega = 125.04452 - 1934.136261 * t + 0.0020708 * t * t
                speed = -0.0529
                normalizeDegrees(omega)
            }
            Planet.KETU -> {
                val omega = 125.04452 - 1934.136261 * t + 0.0020708 * t * t
                speed = -0.0529
                normalizeDegrees(omega + 180.0)
            }
        }
        return Pair(lon, speed)
    }

    fun calculatePlanets(jd: Double): List<PlanetPosition> {
        val t = (jd - 2451545.0) / 36525.0
        val ayanamsha = calculateLahiriAyanamsha(jd)

        return Planet.values().map { planet ->
            val (tropicalLon, speed) = calculateGeocentricLongitude(planet, t, jd)
            val siderealLon = normalizeDegrees(tropicalLon - ayanamsha)

            val signIndex = (siderealLon / 30.0).toInt().coerceIn(0, 11)
            val degInSign = siderealLon % 30.0

            val nakshatraTotalSpan = 360.0 / 27.0 // 13° 20' = 13.333333°
            val nakshatraIndex = (siderealLon / nakshatraTotalSpan).toInt().coerceIn(0, 26)
            val padaSpan = nakshatraTotalSpan / 4.0
            val pada = ((siderealLon % nakshatraTotalSpan) / padaSpan).toInt() + 1

            val deg = degInSign.toInt()
            val min = ((degInSign - deg) * 60).toInt()
            val sec = ((((degInSign - deg) * 60) - min) * 60).toInt()
            val formatted = String.format("%02d° %02d' %02d\"", deg, min, sec)

            PlanetPosition(
                planet = planet,
                longitude = siderealLon,
                signIndex = signIndex,
                signNameHi = ZODIAC_SIGNS_HI[signIndex],
                signNameEn = ZODIAC_SIGNS_EN[signIndex],
                degreeInSign = degInSign,
                formattedDegree = formatted,
                isRetrograde = speed < 0 && planet != Planet.RAHU && planet != Planet.KETU,
                nakshatraIndex = nakshatraIndex,
                nakshatraNameHi = NAKSHATRAS_HI[nakshatraIndex],
                nakshatraPada = pada
            )
        }
    }
}
