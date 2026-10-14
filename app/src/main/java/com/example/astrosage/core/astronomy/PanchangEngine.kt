package com.example.astrosage.core.astronomy

import java.util.Calendar
import java.util.Locale
import kotlin.math.*

data class ChoghadiyaItem(
    val name: String,
    val nameEn: String,
    val type: String, // शुभ, लाभ, अमृत, चर, रोग, काल, उद्वेग
    val nature: String, // Shubh / Ashubh / Neutral
    val startTime: String,
    val endTime: String,
    val lord: String
)

data class MuhuratItem(
    val name: String,
    val timeRange: String,
    val isAuspicious: Boolean,
    val description: String = ""
)

data class PanchangData(
    val dateString: String,
    val dayOfWeekHindi: String,
    val dayOfWeekEnglish: String,
    val tithi: String,
    val tithiPaksha: String, // शुक्ल पक्ष / कृष्ण पक्ष
    val tithiEndTime: String,
    val nextTithi: String = "",
    val nakshatra: String,
    val nakshatraPada: Int,
    val nakshatraEndTime: String,
    val yoga: String,
    val yogaEndTime: String,
    val karana: String,
    val karanaEndTime: String,
    val sunSign: String,
    val moonSign: String,
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val solarNoon: String, // मध्याह्न काल
    val dinMaan: String, // दिनमान (Day duration)
    val ratriMaan: String, // रात्रिमान (Night duration)
    val vikramSamvat: Int,
    val shakaSamvat: Int,
    val samvatsaraName: String,
    val hinduMonthPurnimanta: String,
    val hinduMonthAmanta: String,
    val ritu: String, // ऋतु (Season)
    val ayana: String, // उत्तरायण / दक्षिणायन
    val rahuKaal: String,
    val yamaganda: String,
    val gulikaKaal: String,
    val abhijitMuhurat: String,
    val durMuhurat: String,
    val amritKaal: String,
    val brahmaMuhurat: String,
    val dayChoghadiya: List<ChoghadiyaItem>,
    val nightChoghadiya: List<ChoghadiyaItem>,
    val currentActivePeriod: String,
    val chandrabalSigns: List<String>,
    val festivals: List<String>,
    val dishashool: String,
    val chandraVasa: String,
    val cityName: String,
    val latitude: Double,
    val longitude: Double
)

object PanchangEngine {

    val TITHIS_HI = listOf(
        "प्रतिपदा (Pratipada)", "द्वितीया (Dwitiya)", "तृतीया (Tritiya)", "चतुर्थी (Chaturthi)",
        "पञ्चमी (Panchami)", "षष्ठी (Shashthi)", "सप्तमी (Saptami)", "अष्टमी (Ashtami)",
        "नवमी (Navami)", "दशमी (Dashami)", "एकादशी (Ekadashi)", "द्वादशी (Dwadashi)",
        "त्रयोदशी (Trayodashi)", "चतुर्दशी (Chaturdashi)", "पूर्णिमा (Purnima)",
        "प्रतिपदा (Pratipada)", "द्वितीया (Dwitiya)", "तृतीया (Tritiya)", "चतुर्थी (Chaturthi)",
        "पञ्चमी (Panchami)", "षष्ठी (Shashthi)", "सप्तमी (Saptami)", "अष्टमी (Ashtami)",
        "नवमी (Navami)", "दशमी (Dashami)", "एकादशी (Ekadashi)", "द्वादशी (Dwadashi)",
        "त्रयोदशी (Trayodashi)", "चतुर्दशी (Chaturdashi)", "अमावस्या (Amavasya)"
    )

    val YOGAS_HI = listOf(
        "विष्कुम्भ (Vishkambha)", "प्रीति (Priti)", "आयुष्मान (Ayushman)", "सौभाग्य (Saubhagya)",
        "शोभन (Shobhana)", "अतिगण्ड (Atiganda)", "सुकर्मा (Sukarma)", "धृति (Dhriti)",
        "शूल (Shoola)", "गण्ड (Ganda)", "वृद्धि (Vriddhi)", "ध्रुव (Dhruva)",
        "व्याघात (Vyaghata)", "हर्षण (Harshana)", "वज्र (Vajra)", "सिद्धि (Siddhi)",
        "व्यतीपात (Vyatipata)", "वरीयान (Variyana)", "परिघ (Parigha)", "शिव (Shiva)",
        "सिद्ध (Siddha)", "साध्य (Sadhya)", "शुभ (Shubha)", "शुक्ल (Shukla)",
        "ब्रह्म (Brahma)", "इन्द्र (Indra)", "वैधृति (Vaidhriti)"
    )

    val KARANAS_HI = listOf(
        "बव (Bava)", "बालव (Balava)", "कौलव (Kaulava)", "तैतिल (Taitila)",
        "गर (Gara)", "वणिज (Vanija)", "विष्टि/भद्रा (Vishti/Bhadra)",
        "शकुनि (Shakuni)", "चतुष्पाद (Chatushpada)", "नाग (Naga)", "किंस्तुघ्न (Kimstughna)"
    )

    val SAMVATSARA_NAMES = listOf(
        "प्रभव", "विभव", "शुक्ल", "प्रमोद", "प्रजापति", "अंगिरा", "श्रीमुख", "भाव", "युवा", "धाता",
        "ईश्वर", "बहुधान्य", "प्रमाथी", "विक्रम", "वृषप्रजा", "चित्रभानु", "सुभानु", "तारण", "पार्थिव", "व्यय",
        "सर्वजीत", "सर्वधारी", "विरोधी", "विकृति", "खर", "नन्दन", "विजय", "जय", "मन्मथ", "दुर्मुख",
        "हेमलम्ब", "विलम्ब", "विकारी", "शार्वरी", "प्लव", "शुभकृत", "शोभकृत", "क्रोधन", "विश्वावसु", "पराभव",
        "प्लवंग", "कीलक", "सौम्य", "साधारण", "विरोधकृत", "परिधावी", "प्रमादी", "आनन्द", "राक्षस", "नल",
        "पिंगल", "कालयुक्त", "सिद्धार्थी", "रौद्र", "दुर्मति", "दुन्दुभी", "रुधिरोद्गारी", "रक्ताक्ष", "क्रोधन", "क्षय"
    )

    val HINDU_MONTHS = listOf(
        "चैत्र (Chaitra)", "वैशाख (Vaishakha)", "ज्येष्ठ (Jyeshtha)", "आषाढ़ (Ashadha)",
        "श्रावण (Shravana)", "भाद्रपद (Bhadrapada)", "अश्विन (Ashwin)", "कार्तिक (Kartika)",
        "मार्गशीर्ष (Margashirsha)", "पौष (Pausha)", "माघ (Magha)", "फाल्गुन (Phalguna)"
    )

    val DAYS_HI = listOf("रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार")
    val DAYS_EN = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    /**
     * Accurate Sun and Moon rise/set calculator based on solar/lunar astronomical position
     */
    fun calculateSunTimes(year: Int, month: Int, day: Int, lat: Double, lon: Double): Triple<Double, Double, Double> {
        val jd = EphemerisEngine.toJulianDay(year, month, day, 12, 0, 0)
        val t = (jd - 2451545.0) / 36525.0
        val l0 = (280.46646 + 36000.76983 * t) % 360.0
        val m = (357.52911 + 35999.05029 * t) % 360.0
        val mRad = Math.toRadians(m)
        val c = 1.914602 * sin(mRad) + 0.019993 * sin(2 * mRad)
        val sunLon = Math.toRadians((l0 + c) % 360.0)

        val obliq = Math.toRadians(23.439 - 0.0000004 * (jd - 2451545.0))
        val sinDec = sin(obliq) * sin(sunLon)
        val dec = asin(sinDec)
        val cosDec = cos(dec)

        // Equation of time in minutes
        val y = tan(obliq / 2).pow(2)
        val eqTime = 4 * Math.toDegrees(y * sin(2 * Math.toRadians(l0)) - 2 * 0.0167 * sin(mRad))

        // Solar noon in hours UTC
        val solarNoonUtc = 12.0 - (lon / 15.0) - (eqTime / 60.0)

        // Sunrise/Sunset hour angle
        // Zenith for sunrise/sunset is 90.833 degrees (includes atmospheric refraction)
        val latRad = Math.toRadians(lat)
        val zenithRad = Math.toRadians(90.833)
        val cosH = (cos(zenithRad) - sin(latRad) * sinDec) / (cos(latRad) * cosDec)

        val h = if (cosH > 1.0) 0.0 else if (cosH < -1.0) 180.0 else Math.toDegrees(acos(cosH))
        val hourOffset = h / 15.0

        val sunriseUtc = solarNoonUtc - hourOffset
        val sunsetUtc = solarNoonUtc + hourOffset

        // Convert UTC to Indian Standard Time (UTC+5.5)
        val istOffset = 5.5
        val sunrise = (sunriseUtc + istOffset) % 24.0
        val sunset = (sunsetUtc + istOffset) % 24.0
        val noon = (solarNoonUtc + istOffset) % 24.0

        return Triple(if (sunrise < 0) sunrise + 24 else sunrise, if (sunset < 0) sunset + 24 else sunset, noon)
    }

    /**
     * Accurate Moonrise / Moonset approximate times in IST
     */
    fun calculateMoonTimes(year: Int, month: Int, day: Int, sunriseHours: Double, sunsetHours: Double): Pair<String, String> {
        val jd = EphemerisEngine.toJulianDay(year, month, day, 12, 0, 0)
        val t = (jd - 2451545.0) / 36525.0
        val (sunTropical, _) = EphemerisEngine.calculateGeocentricLongitude(Planet.SUN, t, jd)
        val (moonTropical, _) = EphemerisEngine.calculateGeocentricLongitude(Planet.MOON, t, jd)

        var elong = (moonTropical - sunTropical) % 360.0
        if (elong < 0) elong += 360.0

        // Moon rises approx 50 minutes later each day; lag is proportional to elongation
        val moonriseHour = (sunriseHours + (elong / 360.0) * 24.0) % 24.0
        val moonsetHour = (sunsetHours + (elong / 360.0) * 24.0) % 24.0

        return Pair(formatHours(moonriseHour), formatHours(moonsetHour))
    }

    fun formatHours(hoursDouble: Double): String {
        var h = hoursDouble % 24.0
        if (h < 0) h += 24.0
        val hour = h.toInt()
        val minute = ((h - hour) * 60).toInt()
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        return String.format(Locale.US, "%02d:%02d %s", hour12, minute, amPm)
    }

    fun calculatePanchang(
        calendar: Calendar = Calendar.getInstance(),
        cityName: String = "नई दिल्ली (New Delhi)",
        latitude: Double = 28.6139,
        longitude: Double = 77.2090
    ): PanchangData {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

        val jd = EphemerisEngine.toJulianDay(year, month, day, 6, 0, 0) // At 6:00 AM IST
        val t = (jd - 2451545.0) / 36525.0
        val ayanamsha = EphemerisEngine.calculateLahiriAyanamsha(jd)

        val (sunTrop, _) = EphemerisEngine.calculateGeocentricLongitude(Planet.SUN, t, jd)
        val (moonTrop, _) = EphemerisEngine.calculateGeocentricLongitude(Planet.MOON, t, jd)

        val sunSidereal = (sunTrop - ayanamsha + 360.0) % 360.0
        val moonSidereal = (moonTrop - ayanamsha + 360.0) % 360.0

        // 1. TITHI Calculation (Each Tithi = 12 degrees difference between Moon and Sun)
        var diff = (moonTrop - sunTrop) % 360.0
        if (diff < 0) diff += 360.0
        val tithiIndex = (diff / 12.0).toInt().coerceIn(0, 29)
        val tithiRem = 12.0 - (diff % 12.0)
        // Relative moon speed ~ 12.2 deg/day => hours to end = tithiRem / 12.19 * 24
        val tithiHoursToEnd = (tithiRem / 12.19) * 24.0
        val tithiEndTimeHour = (6.0 + tithiHoursToEnd) % 24.0

        val isShuklaPaksha = tithiIndex < 15
        val pakshaName = if (isShuklaPaksha) "शुक्ल पक्ष (Shukla Paksha)" else "कृष्ण पक्ष (Krishna Paksha)"
        val tithiName = TITHIS_HI[tithiIndex]
        val nextTithiName = TITHIS_HI[(tithiIndex + 1) % 30]

        // 2. NAKSHATRA Calculation (Each Nakshatra = 13° 20' = 13.3333°)
        val nakshatraSpan = 360.0 / 27.0
        val nakshatraIndex = (moonSidereal / nakshatraSpan).toInt().coerceIn(0, 26)
        val nakPadaSpan = nakshatraSpan / 4.0
        val pada = ((moonSidereal % nakshatraSpan) / nakPadaSpan).toInt() + 1
        val nakRem = nakshatraSpan - (moonSidereal % nakshatraSpan)
        val nakHoursToEnd = (nakRem / 13.176) * 24.0
        val nakEndTimeHour = (6.0 + nakHoursToEnd) % 24.0
        val nakshatraName = EphemerisEngine.NAKSHATRAS_HI[nakshatraIndex]

        // 3. YOGA Calculation (Sum of Sun and Moon longitude / 13° 20')
        val sumLon = (sunSidereal + moonSidereal) % 360.0
        val yogaIndex = (sumLon / nakshatraSpan).toInt().coerceIn(0, 26)
        val yogaRem = nakshatraSpan - (sumLon % nakshatraSpan)
        val yogaHoursToEnd = (yogaRem / 14.16) * 24.0
        val yogaEndTimeHour = (6.0 + yogaHoursToEnd) % 24.0
        val yogaName = YOGAS_HI[yogaIndex]

        // 4. KARANA Calculation (Half of a Tithi = 6 degrees)
        val karanaIndex = (diff / 6.0).toInt().coerceIn(0, 59)
        val karanaName = when {
            karanaIndex == 0 -> KARANAS_HI[10] // Kimstughna
            karanaIndex in 1..56 -> KARANAS_HI[(karanaIndex - 1) % 7] // 7 repeating movable karanas
            karanaIndex == 57 -> KARANAS_HI[7] // Shakuni
            karanaIndex == 58 -> KARANAS_HI[8] // Chatushpada
            else -> KARANAS_HI[9] // Naga
        }
        val karanaRem = 6.0 - (diff % 6.0)
        val karanaEndTimeHour = (6.0 + (karanaRem / 6.1) * 12.0) % 24.0

        // 5. Sun and Moon Signs
        val sunSignIndex = (sunSidereal / 30.0).toInt().coerceIn(0, 11)
        val moonSignIndex = (moonSidereal / 30.0).toInt().coerceIn(0, 11)
        val sunSignName = "${EphemerisEngine.ZODIAC_SIGNS_HI[sunSignIndex]} (${EphemerisEngine.ZODIAC_SIGNS_EN[sunSignIndex]})"
        val moonSignName = "${EphemerisEngine.ZODIAC_SIGNS_HI[moonSignIndex]} (${EphemerisEngine.ZODIAC_SIGNS_EN[moonSignIndex]})"

        // 6. Astronomical Sunrise, Sunset, Moonrise, Moonset
        val (sunriseH, sunsetH, noonH) = calculateSunTimes(year, month, day, latitude, longitude)
        val (moonriseStr, moonsetStr) = calculateMoonTimes(year, month, day, sunriseH, sunsetH)

        val sunriseStr = formatHours(sunriseH)
        val sunsetStr = formatHours(sunsetH)
        val solarNoonStr = formatHours(noonH)

        val dayDurationHours = sunsetH - sunriseH
        val dinMaanHours = dayDurationHours.toInt()
        val dinMaanMins = ((dayDurationHours - dinMaanHours) * 60).toInt()
        val dinMaanStr = "${dinMaanHours} घण्टे ${dinMaanMins} मिनट"

        val nightDurationHours = 24.0 - dayDurationHours
        val ratriMaanHours = nightDurationHours.toInt()
        val ratriMaanMins = ((nightDurationHours - ratriMaanHours) * 60).toInt()
        val ratriMaanStr = "${ratriMaanHours} घण्टे ${ratriMaanMins} मिनट"

        // 7. Vedic Muhurats
        // Rahu Kaal, Yamaganda, Gulika Kaal divisions of day into 8 equal parts (Muhurta / Ashtama-bhaga)
        val partH = dayDurationHours / 8.0
        val rahuIndex = listOf(8, 2, 7, 5, 6, 4, 3)[dayOfWeekIndex] - 1
        val yamagandaIndex = listOf(5, 4, 3, 2, 1, 7, 6)[dayOfWeekIndex] - 1
        val gulikaIndex = listOf(7, 6, 5, 4, 3, 2, 1)[dayOfWeekIndex] - 1

        val rahuStart = sunriseH + rahuIndex * partH
        val rahuEnd = rahuStart + partH
        val rahuKaalStr = "${formatHours(rahuStart)} से ${formatHours(rahuEnd)}"

        val yamaStart = sunriseH + yamagandaIndex * partH
        val yamaEnd = yamaStart + partH
        val yamagandaStr = "${formatHours(yamaStart)} से ${formatHours(yamaEnd)}"

        val gulikaStart = sunriseH + gulikaIndex * partH
        val gulikaEnd = gulikaStart + partH
        val gulikaKaalStr = "${formatHours(gulikaStart)} से ${formatHours(gulikaEnd)}"

        // Abhijit Muhurat: 8th Muhurat of 15 divisions of the day (centered on solar noon)
        val abhijitDuration = dayDurationHours / 15.0
        val abhijitStart = noonH - (abhijitDuration / 2.0)
        val abhijitEnd = noonH + (abhijitDuration / 2.0)
        val abhijitStr = if (dayOfWeekIndex == 3) "बुधवार को अभिजित मुहूर्त निषेध है" else "${formatHours(abhijitStart)} से ${formatHours(abhijitEnd)}"

        // Brahma Muhurat: 2 Muhurats (approx 96 min) before sunrise
        val brahmaStart = sunriseH - (96.0 / 60.0)
        val brahmaEnd = sunriseH - (48.0 / 60.0)
        val brahmaMuhuratStr = "${formatHours(brahmaStart)} से ${formatHours(brahmaEnd)}"

        // Amrit Kaal and Dur Muhurat
        val durMuhuratStart = sunriseH + 2.5 * partH
        val durMuhuratEnd = durMuhuratStart + 0.8
        val durMuhuratStr = "${formatHours(durMuhuratStart)} से ${formatHours(durMuhuratEnd)}"

        val amritKaalStart = sunriseH + 4.2 * partH
        val amritKaalEnd = amritKaalStart + 1.2
        val amritKaalStr = "${formatHours(amritKaalStart)} से ${formatHours(amritKaalEnd)}"

        // 8. Day & Night Choghadiya
        val choghadiyaDayOrder = listOf(
            listOf("उद्वेग", "चल", "लाभ", "अमृत", "काल", "शुभ", "रोग", "उद्वेग"), // Sun
            listOf("अमृत", "काल", "शुभ", "रोग", "उद्वेग", "चल", "लाभ", "अमृत"), // Mon
            listOf("रोग", "उद्वेग", "चल", "लाभ", "अमृत", "काल", "शुभ", "रोग"), // Tue
            listOf("लाभ", "अमृत", "काल", "शुभ", "रोग", "उद्वेग", "चल", "लाभ"), // Wed
            listOf("शुभ", "रोग", "उद्वेग", "चल", "लाभ", "अमृत", "काल", "शुभ"), // Thu
            listOf("चल", "लाभ", "अमृत", "काल", "शुभ", "रोग", "उद्वेग", "चल"), // Fri
            listOf("काल", "शुभ", "रोग", "उद्वेग", "चल", "लाभ", "अमृत", "काल")  // Sat
        )[dayOfWeekIndex]

        val choghadiyaNightOrder = listOf(
            listOf("शुभ", "अमृत", "चल", "रोग", "काल", "लाभ", "उद्वेग", "शुभ"), // Sun
            listOf("चल", "रोग", "काल", "लाभ", "उद्वेग", "शुभ", "अमृत", "चल"), // Mon
            listOf("काल", "लाभ", "उद्वेग", "शुभ", "अमृत", "चल", "रोग", "काल"), // Tue
            listOf("उद्वेग", "शुभ", "अमृत", "चल", "रोग", "काल", "लाभ", "उद्वेग"), // Wed
            listOf("अमृत", "चल", "रोग", "काल", "लाभ", "उद्वेग", "शुभ", "अमृत"), // Thu
            listOf("रोग", "काल", "लाभ", "उद्वेग", "शुभ", "अमृत", "चल", "रोग"), // Fri
            listOf("लाभ", "उद्वेग", "शुभ", "अमृत", "चल", "रोग", "काल", "लाभ")  // Sat
        )[dayOfWeekIndex]

        val dayChoghadiyaList = choghadiyaDayOrder.mapIndexed { idx, name ->
            val start = sunriseH + idx * partH
            val end = start + partH
            val nature = when (name) {
                "शुभ", "लाभ", "अमृत" -> "शुभ (Auspicious)"
                "चल" -> "सामान्य (Neutral)"
                else -> "अशुभ (Inauspicious)"
            }
            ChoghadiyaItem(
                name = name,
                nameEn = getChoghadiyaEn(name),
                type = name,
                nature = nature,
                startTime = formatHours(start),
                endTime = formatHours(end),
                lord = getChoghadiyaLord(name)
            )
        }

        val nightPartH = nightDurationHours / 8.0
        val nightChoghadiyaList = choghadiyaNightOrder.mapIndexed { idx, name ->
            val start = sunsetH + idx * nightPartH
            val end = start + nightPartH
            val nature = when (name) {
                "शुभ", "लाभ", "अमृत" -> "शुभ (Auspicious)"
                "चल" -> "सामान्य (Neutral)"
                else -> "अशुभ (Inauspicious)"
            }
            ChoghadiyaItem(
                name = name,
                nameEn = getChoghadiyaEn(name),
                type = name,
                nature = nature,
                startTime = formatHours(start),
                endTime = formatHours(end),
                lord = getChoghadiyaLord(name)
            )
        }

        // 9. Samvat & Calendar elements
        val vikramSamvat = year + 57
        val shakaSamvat = year - 78
        val samvatsaraIdx = ((year - 1987) % 60 + 60) % 60
        val samvatsaraName = SAMVATSARA_NAMES[samvatsaraIdx]

        // Month calculation based on solar transit and tithi
        val monthIdx = (sunSignIndex + 11) % 12
        val hinduMonthPurnimanta = HINDU_MONTHS[monthIdx]
        val hinduMonthAmanta = HINDU_MONTHS[(monthIdx + 11) % 12]

        val ritu = when (month) {
            3, 4 -> "वसन्त ऋतु (Spring)"
            5, 6 -> "ग्रीष्म ऋतु (Summer)"
            7, 8 -> "वर्षा ऋतु (Monsoon)"
            9, 10 -> "शरद ऋतु (Autumn)"
            11, 12 -> "हेमन्त ऋतु (Pre-winter)"
            else -> "शिशिर ऋतु (Winter)"
        }

        val ayana = if (sunSidereal in 270.0..360.0 || sunSidereal in 0.0..90.0) "उत्तरायण (Uttarayana)" else "दक्षिणायन (Dakshinayana)"

        // 10. Chandrabal Signs (Auspicious signs with moon position 1, 3, 6, 7, 10, 11 from Moon)
        val auspiciousOffsets = listOf(0, 2, 5, 6, 9, 10)
        val chandrabalSigns = auspiciousOffsets.map { offset ->
            val signIdx = (moonSignIndex + offset) % 12
            EphemerisEngine.ZODIAC_SIGNS_HI[signIdx]
        }

        // 11. Dishashool (Inauspicious direction for travel)
        val dishashool = when (dayOfWeekIndex) {
            0, 5 -> "पश्चिम (West)"
            1 -> "पूर्व (East)"
            2, 3 -> "उत्तर (North)"
            4 -> "दक्षिण (South)"
            6 -> "पूर्व (East)"
            else -> "कोई नहीं"
        }

        // 12. Chandra Vasa (Direction in which Moon resides)
        val chandraVasa = when (moonSignIndex) {
            0, 4, 8 -> "पूर्व दिशा (East)" // Mesha, Simha, Dhanu
            1, 5, 9 -> "दक्षिण दिशा (South)" // Vrishabha, Kanya, Makara
            2, 6, 10 -> "पश्चिम दिशा (West)" // Mithuna, Tula, Kumbha
            else -> "उत्तर दिशा (North)" // Karka, Vrischika, Meena
        }

        // 13. Festivals and Vrats for the day
        val festivals = mutableListOf<String>()
        if (tithiIndex == 10 || tithiIndex == 25) {
            festivals.add("एकादशी व्रत (Ekadashi Vrat)")
        }
        if (tithiIndex == 12 || tithiIndex == 27) {
            festivals.add("प्रदोष व्रत (Pradosh Vrat)")
        }
        if (tithiIndex == 14) {
            festivals.add("पूर्णिमा व्रत / सत्यनारायण पूजा (Purnima Vrat)")
        }
        if (tithiIndex == 29) {
            festivals.add("अमावस्या (Amavasya Shraddha / Pitru Puja)")
        }
        if (tithiIndex == 3 && dayOfWeekIndex == 2) {
            festivals.add("अंगारकी संकष्टी चतुर्थी (Angarki Sankashti Chaturthi)")
        } else if (tithiIndex == 3 || tithiIndex == 18) {
            festivals.add("विनायक / संकष्टी चतुर्थी (Chaturthi Vrat)")
        }
        if (month == 1 && day in 14..15) {
            festivals.add("मकर संक्रान्ति (Makar Sankranti)")
        }

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) / 60.0
        val currentActive = when {
            currentHour in rahuStart..rahuEnd -> "चेतावनी: राहुकाल सक्रिय है (${formatHours(rahuStart)} - ${formatHours(rahuEnd)})"
            currentHour in abhijitStart..abhijitEnd && dayOfWeekIndex != 3 -> "अति शुभ: अभिजित मुहूर्त सक्रिय है (${formatHours(abhijitStart)} - ${formatHours(abhijitEnd)})"
            currentHour in sunriseH..sunsetH -> {
                val currentPart = ((currentHour - sunriseH) / partH).toInt().coerceIn(0, 7)
                val activeChoghadiya = dayChoghadiyaList.getOrNull(currentPart)
                "वर्तमान चौघड़िया: ${activeChoghadiya?.name ?: "शुभ"} (${activeChoghadiya?.nature ?: ""})"
            }
            else -> "रात्रि काल: ध्यान एवं विश्राम मुहूर्त"
        }

        val dateStr = String.format(Locale.US, "%02d/%02d/%04d", day, month, year)

        return PanchangData(
            dateString = dateStr,
            dayOfWeekHindi = DAYS_HI[dayOfWeekIndex],
            dayOfWeekEnglish = DAYS_EN[dayOfWeekIndex],
            tithi = tithiName,
            tithiPaksha = pakshaName,
            tithiEndTime = formatHours(tithiEndTimeHour),
            nextTithi = nextTithiName,
            nakshatra = nakshatraName,
            nakshatraPada = pada,
            nakshatraEndTime = formatHours(nakEndTimeHour),
            yoga = yogaName,
            yogaEndTime = formatHours(yogaEndTimeHour),
            karana = karanaName,
            karanaEndTime = formatHours(karanaEndTimeHour),
            sunSign = sunSignName,
            moonSign = moonSignName,
            sunrise = sunriseStr,
            sunset = sunsetStr,
            moonrise = moonriseStr,
            moonset = moonsetStr,
            solarNoon = solarNoonStr,
            dinMaan = dinMaanStr,
            ratriMaan = ratriMaanStr,
            vikramSamvat = vikramSamvat,
            shakaSamvat = shakaSamvat,
            samvatsaraName = samvatsaraName,
            hinduMonthPurnimanta = hinduMonthPurnimanta,
            hinduMonthAmanta = hinduMonthAmanta,
            ritu = ritu,
            ayana = ayana,
            rahuKaal = rahuKaalStr,
            yamaganda = yamagandaStr,
            gulikaKaal = gulikaKaalStr,
            abhijitMuhurat = abhijitStr,
            durMuhurat = durMuhuratStr,
            amritKaal = amritKaalStr,
            brahmaMuhurat = brahmaMuhuratStr,
            dayChoghadiya = dayChoghadiyaList,
            nightChoghadiya = nightChoghadiyaList,
            currentActivePeriod = currentActive,
            chandrabalSigns = chandrabalSigns,
            festivals = festivals,
            dishashool = dishashool,
            chandraVasa = chandraVasa,
            cityName = cityName,
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun getChoghadiyaEn(name: String): String = when (name) {
        "शुभ" -> "Shubh"
        "लाभ" -> "Labh"
        "अमृत" -> "Amrit"
        "चल" -> "Chal"
        "रोग" -> "Rog"
        "काल" -> "Kaal"
        "उद्वेग" -> "Udveg"
        else -> name
    }

    private fun getChoghadiyaLord(name: String): String = when (name) {
        "उद्वेग" -> "सूर्य (Sun)"
        "चल" -> "शुक्र (Venus)"
        "लाभ" -> "बुध (Mercury)"
        "अमृत" -> "चन्द्र (Moon)"
        "काल" -> "शनि (Saturn)"
        "शुभ" -> "गुरु (Jupiter)"
        "रोग" -> "मंगल (Mars)"
        else -> ""
    }
}
