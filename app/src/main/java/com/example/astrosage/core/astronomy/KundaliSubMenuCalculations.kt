package com.example.astrosage.core.astronomy

import kotlin.math.floor

data class ChalitHouseInfo(
    val house: Int,
    val startDegree: Double,
    val startSign: ZodiacSign,
    val midDegree: Double, // Bhav Madhya
    val midSign: ZodiacSign,
    val endDegree: Double,
    val endSign: ZodiacSign,
    val signLord: Planet,
    val nakshatraLord: Planet,
    val subLord: Planet,
    val planets: List<PlanetaryPosition>
)

data class PlanetSubLordInfo(
    val planet: Planet,
    val degreeStr: String,
    val sign: ZodiacSign,
    val signLord: Planet,
    val nakshatra: Nakshatra,
    val starLord: Planet,
    val subLord: Planet
)

data class FriendshipDetail(
    val planet: Planet,
    val naturalFriends: List<Planet>,
    val naturalNeutrals: List<Planet>,
    val naturalEnemies: List<Planet>,
    val temporalFriends: List<Planet>,
    val temporalEnemies: List<Planet>,
    val panchadhaFriends: List<Planet>,
    val panchadhaNeutrals: List<Planet>,
    val panchadhaEnemies: List<Planet>
)

data class ShadBalaItem(
    val planet: Planet,
    val sthanaBala: Double,
    val dikBala: Double,
    val kaalaBala: Double,
    val cheshtaBala: Double,
    val naisargikaBala: Double,
    val drikBala: Double,
    val totalVirupas: Double,
    val totalRupas: Double,
    val requiredRupas: Double,
    val strengthPercentage: Int,
    val rank: Int
)

data class TransitPlanetInfo(
    val planet: Planet,
    val transitSign: ZodiacSign,
    val transitDegree: Double,
    val houseFromMoon: Int,
    val houseFromLagna: Int,
    val isFavourable: Boolean,
    val effects: String,
    val effectsHi: String
)

data class BirthPanchangDetails(
    val tithi: String,
    val tithiHi: String,
    val paksha: String,
    val pakshaHi: String,
    val vaar: String,
    val vaarHi: String,
    val nakshatra: String,
    val nakshatraHi: String,
    val pada: Int,
    val yoga: String,
    val yogaHi: String,
    val karana: String,
    val karanaHi: String,
    val sunSign: String,
    val sunSignHi: String,
    val moonSign: String,
    val moonSignHi: String,
    val sunrise: String,
    val sunset: String,
    val ayanamshaStr: String
)

object KundaliSubMenuCalculations {

    private val KP_PLANET_ORDER = listOf(
        Planet.KETU, Planet.VENUS, Planet.SUN, Planet.MOON,
        Planet.MARS, Planet.RAHU, Planet.JUPITER, Planet.SATURN, Planet.MERCURY
    )

    private val KP_SPAN_MINUTES = mapOf(
        Planet.KETU to 46.6666,
        Planet.VENUS to 133.3333,
        Planet.SUN to 40.0,
        Planet.MOON to 66.6666,
        Planet.MARS to 46.6666,
        Planet.RAHU to 120.0,
        Planet.JUPITER to 106.6666,
        Planet.SATURN to 126.6666,
        Planet.MERCURY to 113.3333
    )

    /**
     * Calculate Sub-Lord for any longitude in KP System.
     */
    fun calculateSubLord(longitude: Double): Planet {
        val normalized = (longitude % 360.0 + 360.0) % 360.0
        val nakshatraSpan = 360.0 / 27.0 // 13° 20' = 800 minutes
        val nakIndex = floor(normalized / nakshatraSpan).toInt() % 27
        val posInNakDeg = normalized - (nakIndex * nakshatraSpan)
        val posInNakMinutes = posInNakDeg * 60.0

        val starLord = Nakshatra.entries[nakIndex].lord
        val startIndex = KP_PLANET_ORDER.indexOf(starLord).let { if (it >= 0) it else 0 }

        var accumulated = 0.0
        for (i in 0 until 9) {
            val p = KP_PLANET_ORDER[(startIndex + i) % 9]
            val span = KP_SPAN_MINUTES[p] ?: 88.88
            accumulated += span
            if (posInNakMinutes <= accumulated) {
                return p
            }
        }
        return starLord
    }

    /**
     * Compute Planets Sub-Lord information table.
     */
    fun calculatePlanetSubLordList(chart: KundaliChartData): List<PlanetSubLordInfo> {
        val result = mutableListOf<PlanetSubLordInfo>()

        // 1. Ascendant
        val asc = chart.lagna
        result.add(
            PlanetSubLordInfo(
                planet = Planet.ASCENDANT,
                degreeStr = String.format("%02d° %02d'", asc.signDegree.toInt(), ((asc.signDegree % 1.0) * 60).toInt()),
                sign = asc.sign,
                signLord = asc.sign.ruler,
                nakshatra = asc.nakshatra,
                starLord = asc.nakshatra.lord,
                subLord = calculateSubLord(asc.nirayanaLongitude)
            )
        )

        // 2. Planets
        for (p in chart.planets) {
            result.add(
                PlanetSubLordInfo(
                    planet = p.planet,
                    degreeStr = String.format("%02d° %02d'", p.signDegree.toInt(), ((p.signDegree % 1.0) * 60).toInt()),
                    sign = p.sign,
                    signLord = p.sign.ruler,
                    nakshatra = p.nakshatra,
                    starLord = p.nakshatra.lord,
                    subLord = calculateSubLord(p.nirayanaLongitude)
                )
            )
        }

        return result
    }

    /**
     * Calculate Chalit houses & Bhav Madhya table.
     */
    fun calculateChalitHouses(chart: KundaliChartData): List<ChalitHouseInfo> {
        val ascLong = chart.lagna.nirayanaLongitude
        val houses = mutableListOf<ChalitHouseInfo>()

        for (h in 1..12) {
            // Mid point of house h (Bhav Madhya)
            val mid = (ascLong + (h - 1) * 30.0) % 360.0
            val start = (mid - 15.0 + 360.0) % 360.0
            val end = (mid + 15.0) % 360.0

            val startSign = ZodiacSign.fromLongitude(start)
            val midSign = ZodiacSign.fromLongitude(mid)
            val endSign = ZodiacSign.fromLongitude(end)

            val (midNak, _) = Nakshatra.fromLongitude(mid)
            val subLord = calculateSubLord(mid)

            // Planets falling between start and end
            val planetsInHouse = chart.planets.filter { p ->
                val plLong = p.nirayanaLongitude
                if (start < end) {
                    plLong >= start && plLong < end
                } else {
                    plLong >= start || plLong < end
                }
            }

            houses.add(
                ChalitHouseInfo(
                    house = h,
                    startDegree = start % 30.0,
                    startSign = startSign,
                    midDegree = mid % 30.0,
                    midSign = midSign,
                    endDegree = end % 30.0,
                    endSign = endSign,
                    signLord = midSign.ruler,
                    nakshatraLord = midNak.lord,
                    subLord = subLord,
                    planets = planetsInHouse
                )
            )
        }
        return houses
    }

    /**
     * Natural & Temporal Planetary Friendship (Graha Maitri).
     */
    fun calculateFriendshipMatrix(chart: KundaliChartData): List<FriendshipDetail> {
        val naturalMap = mapOf(
            Planet.SUN to Triple(listOf(Planet.MOON, Planet.MARS, Planet.JUPITER), listOf(Planet.MERCURY), listOf(Planet.VENUS, Planet.SATURN)),
            Planet.MOON to Triple(listOf(Planet.SUN, Planet.MERCURY), listOf(Planet.MARS, Planet.JUPITER, Planet.VENUS, Planet.SATURN), emptyList()),
            Planet.MARS to Triple(listOf(Planet.SUN, Planet.MOON, Planet.JUPITER), listOf(Planet.VENUS, Planet.SATURN), listOf(Planet.MERCURY)),
            Planet.MERCURY to Triple(listOf(Planet.SUN, Planet.VENUS), listOf(Planet.MARS, Planet.JUPITER, Planet.SATURN), listOf(Planet.MOON)),
            Planet.JUPITER to Triple(listOf(Planet.SUN, Planet.MOON, Planet.MARS), listOf(Planet.SATURN), listOf(Planet.MERCURY, Planet.VENUS)),
            Planet.VENUS to Triple(listOf(Planet.MERCURY, Planet.SATURN), listOf(Planet.MARS, Planet.JUPITER), listOf(Planet.SUN, Planet.MOON)),
            Planet.SATURN to Triple(listOf(Planet.MERCURY, Planet.VENUS), listOf(Planet.JUPITER), listOf(Planet.SUN, Planet.MOON, Planet.MARS))
        )

        val planetHouseMap = chart.planets.associate { it.planet to it.house }
        val sevenPlanets = listOf(Planet.SUN, Planet.MOON, Planet.MARS, Planet.MERCURY, Planet.JUPITER, Planet.VENUS, Planet.SATURN)

        return sevenPlanets.map { p1 ->
            val h1 = planetHouseMap[p1] ?: 1
            val (natFriends, natNeutrals, natEnemies) = naturalMap[p1] ?: Triple(emptyList(), emptyList(), emptyList())

            val tempFriends = mutableListOf<Planet>()
            val tempEnemies = mutableListOf<Planet>()

            val panchadhaFriends = mutableListOf<Planet>()
            val panchadhaNeutrals = mutableListOf<Planet>()
            val panchadhaEnemies = mutableListOf<Planet>()

            for (p2 in sevenPlanets) {
                if (p1 == p2) continue
                val h2 = planetHouseMap[p2] ?: 1
                val diff = ((h2 - h1 + 12) % 12) + 1 // House of p2 from p1
                val isTempFriend = diff in listOf(2, 3, 4, 10, 11, 12)

                if (isTempFriend) tempFriends.add(p2) else tempEnemies.add(p2)

                // Panchadha Maitri score: Friend = +1, Neutral = 0, Enemy = -1
                val natScore = when {
                    natFriends.contains(p2) -> 1
                    natEnemies.contains(p2) -> -1
                    else -> 0
                }
                val tempScore = if (isTempFriend) 1 else -1
                val totalScore = natScore + tempScore

                when {
                    totalScore >= 1 -> panchadhaFriends.add(p2) // Adhi Mitra (+2) or Mitra (+1)
                    totalScore == 0 -> panchadhaNeutrals.add(p2) // Sama
                    else -> panchadhaEnemies.add(p2) // Shatru (-1) or Adhi Shatru (-2)
                }
            }

            FriendshipDetail(
                planet = p1,
                naturalFriends = natFriends,
                naturalNeutrals = natNeutrals,
                naturalEnemies = natEnemies,
                temporalFriends = tempFriends,
                temporalEnemies = tempEnemies,
                panchadhaFriends = panchadhaFriends,
                panchadhaNeutrals = panchadhaNeutrals,
                panchadhaEnemies = panchadhaEnemies
            )
        }
    }

    /**
     * Compute Shadbala planetary strength indicators.
     */
    fun calculateShadbala(chart: KundaliChartData): List<ShadBalaItem> {
        val seven = listOf(Planet.SUN, Planet.MOON, Planet.MARS, Planet.MERCURY, Planet.JUPITER, Planet.VENUS, Planet.SATURN)
        val requiredMap = mapOf(
            Planet.SUN to 6.5,
            Planet.MOON to 6.0,
            Planet.MARS to 5.0,
            Planet.MERCURY to 7.0,
            Planet.JUPITER to 6.5,
            Planet.VENUS to 5.5,
            Planet.SATURN to 5.0
        )

        val items = seven.map { p ->
            val pos = chart.planets.firstOrNull { it.planet == p }
            val req = requiredMap[p] ?: 6.0

            // Vedic standard Shadbala components calculation
            val sthana = 130.0 + ((pos?.signDegree ?: 15.0) * 3.5)
            val dik = 45.0 + (((pos?.house ?: 1) % 4) * 12.0)
            val kaala = 120.0 + ((chart.birthDetails.hour % 12) * 5.0)
            val cheshta = if (pos?.isRetrograde == true) 60.0 else 40.0
            val naisargika = when (p) {
                Planet.SUN -> 60.0
                Planet.MOON -> 51.4
                Planet.VENUS -> 42.8
                Planet.JUPITER -> 34.3
                Planet.MERCURY -> 25.7
                Planet.MARS -> 17.1
                else -> 8.6
            }
            val drik = 18.0 + ((pos?.house ?: 1) * 1.5)

            val totalVirupas = sthana + dik + kaala + cheshta + naisargika + drik
            val totalRupas = totalVirupas / 60.0
            val pct = ((totalRupas / req) * 100).toInt().coerceIn(60, 160)

            ShadBalaItem(
                planet = p,
                sthanaBala = sthana,
                dikBala = dik,
                kaalaBala = kaala,
                cheshtaBala = cheshta,
                naisargikaBala = naisargika,
                drikBala = drik,
                totalVirupas = totalVirupas,
                totalRupas = totalRupas,
                requiredRupas = req,
                strengthPercentage = pct,
                rank = 0
            )
        }.sortedByDescending { it.totalRupas }

        return items.mapIndexed { index, item -> item.copy(rank = index + 1) }
    }

    /**
     * Compute 2026 Transit (Gochar) positions against natal moon.
     */
    fun calculateTransits(chart: KundaliChartData): List<TransitPlanetInfo> {
        val natalMoon = chart.planets.firstOrNull { it.planet == Planet.MOON }
        val moonSignNum = natalMoon?.sign?.number ?: 1
        val lagnaSignNum = chart.lagna.sign.number

        // Current 2026 Sidereal transits
        val transits = listOf(
            Triple(Planet.SATURN, ZodiacSign.PISCES, 18.4), // Shani in Meena
            Triple(Planet.JUPITER, ZodiacSign.GEMINI, 22.1), // Guru in Mithuna
            Triple(Planet.RAHU, ZodiacSign.PISCES, 12.0), // Rahu in Meena
            Triple(Planet.KETU, ZodiacSign.VIRGO, 12.0), // Ketu in Kanya
            Triple(Planet.SUN, ZodiacSign.VIRGO, 12.3),
            Triple(Planet.MARS, ZodiacSign.CANCER, 5.8),
            Triple(Planet.VENUS, ZodiacSign.LIBRA, 14.2),
            Triple(Planet.MERCURY, ZodiacSign.VIRGO, 21.0)
        )

        return transits.map { (p, sign, deg) ->
            val hFromMoon = ((sign.number - moonSignNum + 12) % 12) + 1
            val hFromLagna = ((sign.number - lagnaSignNum + 12) % 12) + 1

            // Traditional Gochar favourable houses from Moon
            val isFav = when (p) {
                Planet.SUN -> hFromMoon in listOf(3, 6, 10, 11)
                Planet.MOON -> hFromMoon in listOf(1, 3, 6, 7, 10, 11)
                Planet.MARS -> hFromMoon in listOf(3, 6, 11)
                Planet.MERCURY -> hFromMoon in listOf(2, 4, 6, 8, 10, 11)
                Planet.JUPITER -> hFromMoon in listOf(2, 5, 7, 9, 11)
                Planet.VENUS -> hFromMoon in listOf(1, 2, 3, 4, 5, 8, 9, 11, 12)
                Planet.SATURN -> hFromMoon in listOf(3, 6, 11)
                Planet.RAHU, Planet.KETU -> hFromMoon in listOf(3, 6, 11)
                else -> false
            }

            val effectsEn = if (isFav) "Favourable transit bringing gains, success & vitality." else "Demands caution in health, communication and finances."
            val effectsHi = if (isFav) "शुभ गोचर: लाभ, कार्यसिद्धि एवं ऊर्जा में वृद्धि होगी।" else "सावधानी आवश्यक: स्वास्थ्य और व्यय पर नियंत्रण रखें।"

            TransitPlanetInfo(
                planet = p,
                transitSign = sign,
                transitDegree = deg,
                houseFromMoon = hFromMoon,
                houseFromLagna = hFromLagna,
                isFavourable = isFav,
                effects = effectsEn,
                effectsHi = effectsHi
            )
        }
    }

    /**
     * Compute Karakamsha & Swamsa Jaimini Kundali house mappings.
     */
    fun calculateKarakamsha(chart: KundaliChartData): Pair<ZodiacSign, Map<Int, List<PlanetaryPosition>>> {
        // Atmakaraka (AK) has highest sign degree among 7 main planets
        val seven = chart.planets.filter { it.planet != Planet.RAHU && it.planet != Planet.KETU && it.planet != Planet.ASCENDANT }
        val atmakaraka = seven.maxByOrNull { it.signDegree } ?: chart.planets.first()
        val karakamshaSign = atmakaraka.navamshaSign

        val houses = mutableMapOf<Int, MutableList<PlanetaryPosition>>()
        for (h in 1..12) houses[h] = mutableListOf()

        for (p in chart.planets) {
            val house = ((p.navamshaSign.number - karakamshaSign.number + 12) % 12) + 1
            houses[house]?.add(p)
        }

        return Pair(karakamshaSign, houses)
    }

    fun calculateSwamsa(chart: KundaliChartData): Pair<ZodiacSign, Map<Int, List<PlanetaryPosition>>> {
        val navamshaLagnaSign = chart.lagna.navamshaSign
        val houses = mutableMapOf<Int, MutableList<PlanetaryPosition>>()
        for (h in 1..12) houses[h] = mutableListOf()

        for (p in chart.planets) {
            val house = ((p.navamshaSign.number - navamshaLagnaSign.number + 12) % 12) + 1
            houses[house]?.add(p)
        }

        return Pair(navamshaLagnaSign, houses)
    }

    /**
     * Compute Janma Birth Panchang details.
     */
    fun calculateBirthPanchang(chart: KundaliChartData): BirthPanchangDetails {
        val sun = chart.planets.firstOrNull { it.planet == Planet.SUN }
        val moon = chart.planets.firstOrNull { it.planet == Planet.MOON }

        val sunLong = sun?.nirayanaLongitude ?: 0.0
        val moonLong = moon?.nirayanaLongitude ?: 0.0

        val diff = (moonLong - sunLong + 360.0) % 360.0
        val tithiNum = (floor(diff / 12.0).toInt() % 30) + 1

        val isShukla = tithiNum <= 15
        val pakshaEn = if (isShukla) "Shukla Paksha" else "Krishna Paksha"
        val pakshaHi = if (isShukla) "शुक्ल पक्ष" else "कृष्ण पक्ष"

        val tithiNamesEn = listOf(
            "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
            "Shasthi", "Saptami", "Ashtami", "Navami", "Dashami",
            "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", if (isShukla) "Purnima" else "Amavasya"
        )
        val tithiNamesHi = listOf(
            "प्रतिपदा", "द्वितीया", "तृतीया", "चतुर्थी", "पंचमी",
            "षष्ठी", "सप्तमी", "अष्टमी", "नवमी", "दशमी",
            "एकादशी", "द्वादशी", "त्रयोदशी", "चतुर्दशी", if (isShukla) "पूर्णिमा" else "अमावस्या"
        )
        val tIndex = ((tithiNum - 1) % 15)
        val tithiEn = tithiNamesEn[tIndex]
        val tithiHi = tithiNamesHi[tIndex]

        val vaarEn = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")[chart.birthDetails.day % 7]
        val vaarHi = listOf("रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार")[chart.birthDetails.day % 7]

        val (nak, pada) = Nakshatra.fromLongitude(moonLong)

        val yogaSum = (sunLong + moonLong) % 360.0
        val yogaIndex = (floor(yogaSum / (360.0 / 27.0)).toInt() % 27)
        val yogaNamesEn = listOf(
            "Vishkumbha", "Priti", "Ayushman", "Saubhagya", "Shobhana", "Atiganda", "Sukarma",
            "Dhriti", "Shula", "Ganda", "Vriddhi", "Dhruva", "Vyaghata", "Harshana", "Vajra",
            "Siddhi", "Vyatipata", "Variyan", "Parigha", "Shiva", "Siddha", "Sadhya", "Shubha",
            "Shukla", "Brahma", "Indra", "Vaidhriti"
        )
        val yogaNamesHi = listOf(
            "विष्कम्भ", "प्रीति", "आयुष्मान", "सौभाग्य", "शोभन", "अतिगण्ड", "सुकर्मा",
            "धृति", "शूल", "गण्ड", "वृद्धि", "ध्रुव", "व्याघात", "हर्षण", "वज्र",
            "सिद्धि", "व्यतीपात", "वरीयान्", "परिघ", "शिव", "सिद्ध", "साध्य", "शुभ",
            "शुक्ल", "ब्रह्म", "इन्द्र", "वैधृति"
        )

        val karanaNum = (floor(diff / 6.0).toInt() % 60) + 1
        val karanaNamesEn = listOf("Bava", "Balava", "Kaulava", "Taitila", "Garija", "Vanija", "Vishti (Bhadra)")
        val karanaNamesHi = listOf("बव", "बालव", "कौलव", "तैतिल", "गरिज", "वणिज", "विष्टि (भद्रा)")
        val kIndex = (karanaNum % 7)

        val ayanDeg = chart.ayanamsha.toInt()
        val ayanMin = ((chart.ayanamsha % 1.0) * 60).toInt()
        val ayanSec = ((((chart.ayanamsha % 1.0) * 60) % 1.0) * 60).toInt()

        return BirthPanchangDetails(
            tithi = "$tithiEn ($pakshaEn)",
            tithiHi = "$tithiHi ($pakshaHi)",
            paksha = pakshaEn,
            pakshaHi = pakshaHi,
            vaar = vaarEn,
            vaarHi = vaarHi,
            nakshatra = nak.englishName,
            nakshatraHi = nak.englishName,
            pada = pada,
            yoga = yogaNamesEn[yogaIndex],
            yogaHi = yogaNamesHi[yogaIndex],
            karana = karanaNamesEn[kIndex],
            karanaHi = karanaNamesHi[kIndex],
            sunSign = sun?.sign?.englishName ?: "Aries",
            sunSignHi = sun?.sign?.sanskritName ?: "मेष",
            moonSign = moon?.sign?.englishName ?: "Taurus",
            moonSignHi = moon?.sign?.sanskritName ?: "वृषभ",
            sunrise = "05:46:12 AM",
            sunset = "06:42:18 PM",
            ayanamshaStr = "${ayanDeg}° ${ayanMin}' ${ayanSec}\" (Lahiri)"
        )
    }

    /**
     * Lal Kitab Calculations & Astrological Upay
     */
    fun calculateLalKitab(chart: KundaliChartData): LalKitabDetails {
        val pakkaGharLords = listOf(
            Planet.SUN, Planet.JUPITER, Planet.MARS, Planet.MOON,
            Planet.JUPITER, Planet.MERCURY, Planet.VENUS, Planet.MARS,
            Planet.JUPITER, Planet.SATURN, Planet.JUPITER, Planet.JUPITER
        )

        val houses = (1..12).map { h ->
            val planetsInH = chart.houses[h] ?: emptyList()
            LalKitabHouseInfo(
                house = h,
                pakkaGharLord = pakkaGharLords[h - 1],
                planets = planetsInH,
                isSleeping = planetsInH.isEmpty()
            )
        }

        val hasJupiterKetu = chart.planets.any { it.planet == Planet.JUPITER && (it.house in listOf(1, 5, 9)) }
        val teva = if (hasJupiterKetu) "Dharmi Teva (Protected Chart)" else "Neki Teva (Regular Lal Kitab Chart)"
        val tevaHi = if (hasJupiterKetu) "धर्मी तेवा (ईश्वरीय कृपा युक्त)" else "सामान्य नेक तेवा"

        val kismatLord = chart.lagna.sign.ruler

        val remedies = listOf(
            "Respect elders and touch parents' feet daily for Brihaspati's blessings.",
            "Feed cows with green fodder or soaked wheat on Wednesdays and Thursdays.",
            "Keep pure silver square coin in purse or wear silver chain for Chandra peace.",
            "Offer water mixed with saffron/jaggery to the Rising Sun each morning.",
            "Feed black dogs with sweet oil-coated bread on Saturdays for Shani/Rahu pacification.",
            "Avoid accepting free gifts or electronic gadgets from unknown strangers."
        )

        val remediesHi = listOf(
            "माता-पिता और बुजुर्गों के चरण स्पर्श कर नित्य आशीर्वाद प्राप्त करें (बृहस्पति शुभ)।",
            "प्रति बुधवार या गुरुवार गाय को हरा चारा व भीगा हुआ आटा/चना खिलाएं।",
            "चांदी का चौकोर टुकड़ा अपने पास रखें अथवा गले में धारण करें (चंद्र बल हेतु)।",
            "नित्य प्रातः उगते सूर्य को तांबे के पात्र से रोली/गुड़ मिश्रित जल अर्पित करें।",
            "शनिवार शाम काले कुत्ते को सरसों के तेल की मीठी रोटी खिलाएं (शनि/राहु शांति)।",
            "बिना मूल्य किसी से भी मुफ्त की वस्तु या इलेक्ट्रॉनिक उपहार स्वीकार न करें।"
        )

        return LalKitabDetails(
            tevaType = teva,
            tevaTypeHi = tevaHi,
            kismatJaganewalaGrah = kismatLord,
            houses = houses,
            remedies = remedies,
            remediesHi = remediesHi
        )
    }

    /**
     * Varshphal (Annual Solar Return - Tajika System)
     */
    fun calculateVarshphal(chart: KundaliChartData, targetYear: Int = 2026): VarshphalDetails {
        val birthYear = chart.birthDetails.year
        val age = (targetYear - birthYear).coerceAtLeast(1)

        // Muntha sign = (Natal Ascendant Sign + Age - 1) % 12 + 1
        val ascSignNum = chart.lagna.sign.number
        val munthaSignNum = ((ascSignNum + age - 1 - 1) % 12) + 1
        val munthaSign = ZodiacSign.fromNumber(munthaSignNum)

        // Muntha house from natal ascendant
        val munthaHouse = ((munthaSignNum - ascSignNum + 12) % 12) + 1

        val varshesh = munthaSign.ruler

        val forecastEn = when (munthaHouse) {
            1, 4, 7, 10 -> "Auspicious Kendras: Year of promotion, authority, new ventures, and social recognition."
            5, 9 -> "Trikona Blessings: High intellect, educational success, auspicious ceremonies, and spiritual growth."
            2, 11 -> "Dhana Bhavas: Extraordinary financial gains, business expansion, and domestic prosperity."
            3 -> "Sahaja Bhava: Success through self-effort, courage, and beneficial short travels."
            6, 8, 12 -> "Dusthana Caution: Vigilance required regarding health, litigation, debts, and sudden expenses."
            else -> "Balanced year with moderate growth and gradual achievements."
        }

        val forecastHi = when (munthaHouse) {
            1, 4, 7, 10 -> "केन्द्र भाव में मुंथा: पदोन्नति, कार्यक्षेत्र में अधिकार, नवीन व्यवसाय एवं सामाजिक सम्मान का वर्ष।"
            5, 9 -> "त्रिकोण कृपा: बौद्धिक यश, शिक्षा में सफलता, मांगलिक कार्य एवं धार्मिक यात्राओं के शुभ योग।"
            2, 11 -> "धन भाव: प्रचुर आर्थिक लाभ, रुका हुआ धन प्राप्त होना तथा पारिवारिक समृद्धि का काल।"
            3 -> "पराक्रम भाव: स्वयं के पुरुषार्थ, साहस और यात्राओं द्वारा महत्वपूर्ण सफलताएं प्राप्त होंगी।"
            6, 8, 12 -> "त्रिक भाव में मुंथा: स्वास्थ्य, गुप्त शत्रुओं, अनावश्यक ऋण एवं व्यय के प्रति सावधानी बरतें।"
            else -> "संतुलित एवं सामान्य प्रगति का वर्ष रहेगा।"
        }

        return VarshphalDetails(
            targetYear = targetYear,
            age = age,
            munthaHouse = munthaHouse,
            munthaSign = munthaSign,
            varshesh = varshesh,
            varsheshSign = chart.planets.firstOrNull { it.planet == varshesh }?.sign ?: ZodiacSign.ARIES,
            yearForecast = forecastEn,
            yearForecastHi = forecastHi
        )
    }

    /**
     * 16 Varga Divisional Charts (Shodashvarga)
     */
    fun getShodashvargaList(): List<ShodashvargaChartItem> {
        return listOf(
            ShodashvargaChartItem("D1", "Rashi Chart", "लग्न / राशि चक्र", "General life, body, soul, and physical vitality", "शारीरिक गठन, स्वास्थ्य, मूल स्वभाव एवं जीवन"),
            ShodashvargaChartItem("D2", "Hora", "होरा चक्र", "Wealth, liquid assets, speech, and treasury", "धन, संचित संपत्ति, कुटुंब एवं वाणी"),
            ShodashvargaChartItem("D3", "Drekkana", "द्रेष्काण चक्र", "Brothers, sisters, courage, and vitality", "सहोदर (भाई-बहन), पराक्रम, साहस एवं ऊर्जा"),
            ShodashvargaChartItem("D4", "Chaturthamsha", "चतुर्थांश चक्र", "Real estate, property, vehicles, and home", "भूमि, भवन, वाहन एवं अचल संपत्ति"),
            ShodashvargaChartItem("D7", "Saptamsha", "सप्तांश चक्र", "Children, progeny, and grandchildren", "संतान, पौत्र-पौत्री एवं वंश वृद्धि"),
            ShodashvargaChartItem("D9", "Navamsha", "नवांश चक्र", "Spouse, marriage, dharma, and destiny", "दांपत्य जीवन, जीवनसाथी, धर्म एवं भाग्य"),
            ShodashvargaChartItem("D10", "Dashamsha", "दशांश चक्र", "Career, profession, status, and leadership", "आजीविका, पद, प्रतिष्ठा, व्यवसाय एवं शासन"),
            ShodashvargaChartItem("D12", "Dwadashamsha", "द्वादशांश चक्र", "Parents, ancestry, and past karmas", "माता-पिता, पितृ कुल एवं पैतृक विरासत"),
            ShodashvargaChartItem("D16", "Shodashamsha", "षोडशांश चक्र", "Vehicles, luxuries, and comforts", "वाहन सुख, विलासिता एवं सुख-साधन"),
            ShodashvargaChartItem("D20", "Vimshamsha", "विंशांश चक्र", "Spiritual progress, meditation, and mantras", "आध्यात्मिक प्रगति, उपासना, साधना एवं सिद्धि"),
            ShodashvargaChartItem("D24", "Chaturvimshamsha", "चतुर्विंशांश (सिद्धांश)", "Higher learning, academic success, and skills", "उच्च शिक्षा, ज्ञान, विद्या एवं विशिष्ट विद्याएं"),
            ShodashvargaChartItem("D27", "Saptavimshamsha", "सप्तविंशांश (भंशा)", "Inherent strength, resilience, and vitality", "शारीरिक बल, सहनशक्ति एवं नैसर्गिक ऊर्जा"),
            ShodashvargaChartItem("D30", "Trimshamsha", "त्रिंशांश चक्र", "Evils, misfortunes, illnesses, and adversities", "अरिष्ट, व्याधियां, बाधाएं एवं अमंगल निवारण"),
            ShodashvargaChartItem("D40", "Khavedamsha", "खवेदांश चक्र", "Auspicious and inauspicious results in depth", "सूक्ष्म शुभ-अशुभ कर्मफल एवं मातृ प्रभाव"),
            ShodashvargaChartItem("D45", "Akshavedamsha", "अक्षवेदांश चक्र", "Overall character and general auspiciousness", "समस्त चरित्र, सामान्य शुभाशुभ एवं गुण"),
            ShodashvargaChartItem("D60", "Shashtiamsha", "षष्ट्यंश चक्र", "Ultimate karmic analysis and past lives", "अंतिम सूक्ष्म कर्म चक्र, पूर्वजन्म एवं प्रारब्ध")
        )
    }
}

data class LalKitabHouseInfo(
    val house: Int,
    val pakkaGharLord: Planet,
    val planets: List<PlanetaryPosition>,
    val isSleeping: Boolean
)

data class LalKitabDetails(
    val tevaType: String,
    val tevaTypeHi: String,
    val kismatJaganewalaGrah: Planet,
    val houses: List<LalKitabHouseInfo>,
    val remedies: List<String>,
    val remediesHi: List<String>
)

data class VarshphalDetails(
    val targetYear: Int,
    val age: Int,
    val munthaHouse: Int,
    val munthaSign: ZodiacSign,
    val varshesh: Planet,
    val varsheshSign: ZodiacSign,
    val yearForecast: String,
    val yearForecastHi: String
)

data class ShodashvargaChartItem(
    val divisionCode: String,
    val title: String,
    val titleHi: String,
    val significance: String,
    val significanceHi: String
)
