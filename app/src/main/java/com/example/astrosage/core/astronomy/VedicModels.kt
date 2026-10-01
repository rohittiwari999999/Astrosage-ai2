package com.example.astrosage.core.astronomy

import kotlin.math.floor

enum class Planet(
    val englishName: String,
    val sanskritName: String,
    val shortCode: String,
    val symbol: String,
    val isNaturalBenefic: Boolean,
    val dashaYears: Int,
    val hindiName: String,
    val hindiShortCode: String
) {
    SUN("Sun", "Surya", "Su", "☉", false, 6, "सूर्य", "सू"),
    MOON("Moon", "Chandra", "Mo", "☽", true, 10, "चंद्र", "चं"),
    MARS("Mars", "Mangal", "Ma", "♂", false, 7, "मंगल", "मं"),
    MERCURY("Mercury", "Budha", "Me", "☿", true, 17, "बुध", "बु"),
    JUPITER("Jupiter", "Brihaspati", "Ju", "♃", true, 16, "बृहस्पति", "गु"),
    VENUS("Venus", "Shukra", "Ve", "♀", true, 20, "शुक्र", "शु"),
    SATURN("Saturn", "Shani", "Sa", "♄", false, 19, "शनि", "श"),
    RAHU("Rahu", "Rahu", "Ra", "☊", false, 18, "राहु", "रा"),
    KETU("Ketu", "Ketu", "Ke", "☋", false, 7, "केतु", "के"),
    ASCENDANT("Ascendant", "Lagna", "Asc", "Asc", true, 0, "लग्न", "ल");

    fun getDisplayName(isHindi: Boolean): String = if (isHindi) hindiName else sanskritName
    fun getDisplayShortCode(isHindi: Boolean): String = if (isHindi) hindiShortCode else shortCode

    companion object {
        fun primaryNine(): List<Planet> = listOf(SUN, MOON, MARS, MERCURY, JUPITER, VENUS, SATURN, RAHU, KETU)
    }
}

enum class ZodiacSign(
    val number: Int,
    val englishName: String,
    val sanskritName: String,
    val ruler: Planet,
    val element: String,
    val symbol: String,
    val hindiName: String,
    val hindiElement: String
) {
    ARIES(1, "Aries", "Mesha", Planet.MARS, "Fire", "♈", "मेष", "अग्नि"),
    TAURUS(2, "Taurus", "Vrishabha", Planet.VENUS, "Earth", "♉", "वृषभ", "पृथ्वी"),
    GEMINI(3, "Gemini", "Mithuna", Planet.MERCURY, "Air", "♊", "मिथुन", "वायु"),
    CANCER(4, "Cancer", "Karka", Planet.MOON, "Water", "♋", "कर्क", "जल"),
    LEO(5, "Leo", "Simha", Planet.SUN, "Fire", "♌", "सिंह", "अग्नि"),
    VIRGO(6, "Virgo", "Kanya", Planet.MERCURY, "Earth", "♍", "कन्या", "पृथ्वी"),
    LIBRA(7, "Libra", "Tula", Planet.VENUS, "Air", "♎", "तुला", "वायु"),
    SCORPIO(8, "Scorpio", "Vrishchika", Planet.MARS, "Water", "♏", "वृश्चिक", "जल"),
    SAGITTARIUS(9, "Sagittarius", "Dhanu", Planet.JUPITER, "Fire", "♐", "धनु", "अग्नि"),
    CAPRICORN(10, "Capricorn", "Makara", Planet.SATURN, "Earth", "♑", "मकर", "पृथ्वी"),
    AQUARIUS(11, "Aquarius", "Kumbha", Planet.SATURN, "Air", "♒", "कुंभ", "वायु"),
    PISCES(12, "Pisces", "Meena", Planet.JUPITER, "Water", "♓", "मीन", "जल");

    fun getDisplayName(isHindi: Boolean): String = if (isHindi) hindiName else sanskritName
    fun getDisplayElement(isHindi: Boolean): String = if (isHindi) hindiElement else element

    companion object {
        fun fromNumber(num: Int): ZodiacSign {
            val adjusted = ((num - 1) % 12 + 12) % 12 + 1
            return entries.first { it.number == adjusted }
        }

        fun fromLongitude(deg: Double): ZodiacSign {
            val normalized = (deg % 360.0 + 360.0) % 360.0
            val signNum = (floor(normalized / 30.0).toInt() % 12) + 1
            return fromNumber(signNum)
        }
    }
}

enum class Nakshatra(
    val number: Int,
    val englishName: String,
    val lord: Planet,
    val deity: String,
    val gana: String,
    val yoni: String,
    val nadi: String,
    val varna: String,
    val hindiName: String,
    val hindiGana: String,
    val hindiYoni: String,
    val hindiNadi: String,
    val hindiVarna: String
) {
    ASHWINI(1, "Ashwini", Planet.KETU, "Ashwini Kumaras", "Deva", "Horse", "Adi", "Vaishya", "अश्विनी", "देव", "अश्व", "आदि", "वैश्य"),
    BHARANI(2, "Bharani", Planet.VENUS, "Yama", "Manushya", "Elephant", "Madhya", "Shudra", "भरणी", "मनुष्य", "गज", "मध्य", "शूद्र"),
    KRITTIKA(3, "Krittika", Planet.SUN, "Agni", "Rakshasa", "Sheep", "Antya", "Brahmin", "कृत्तिका", "राक्षस", "मेष", "अंत्य", "ब्राह्मण"),
    ROHINI(4, "Rohini", Planet.MOON, "Brahma", "Manushya", "Serpent", "Antya", "Shudra", "रोहिणी", "मनुष्य", "सर्प", "अंत्य", "शूद्र"),
    MRIGASHIRA(5, "Mrigashira", Planet.MARS, "Soma", "Deva", "Serpent", "Madhya", "Vaishya", "मृगशिरा", "देव", "सर्प", "मध्य", "वैश्य"),
    ARDRA(6, "Ardra", Planet.RAHU, "Rudra", "Manushya", "Dog", "Adi", "Brahmin", "आर्द्रा", "मनुष्य", "श्वान", "आदि", "ब्राह्मण"),
    PUNARVASU(7, "Punarvasu", Planet.JUPITER, "Aditi", "Deva", "Cat", "Adi", "Vaishya", "पुनर्वसु", "देव", "मार्जार", "आदि", "वैश्य"),
    PUSHYA(8, "Pushya", Planet.SATURN, "Brihaspati", "Deva", "Goat", "Madhya", "Kshatriya", "पुष्य", "देव", "अज", "मध्य", "क्षत्रिय"),
    ASHLESHA(9, "Ashlesha", Planet.MERCURY, "Sarpas", "Rakshasa", "Cat", "Antya", "Shudra", "आश्लेषा", "राक्षस", "मार्जार", "अंत्य", "शूद्र"),
    MAGHA(10, "Magha", Planet.KETU, "Pitris", "Rakshasa", "Rat", "Antya", "Shudra", "मघा", "राक्षस", "मूषक", "अंत्य", "शूद्र"),
    PURVA_PHALGUNI(11, "Purva PhALGUNI", Planet.VENUS, "Bhaga", "Manushya", "Rat", "Madhya", "Brahmin", "पूर्वाफाल्गुनी", "मनुष्य", "मूषक", "मध्य", "ब्राह्मण"),
    UTTARA_PHALGUNI(12, "Uttara Phalguni", Planet.SUN, "Aryaman", "Manushya", "Cow", "Adi", "Kshatriya", "उत्तराफाल्गुनी", "मनुष्य", "गौ", "आदि", "क्षत्रिय"),
    HASTA(13, "Hasta", Planet.MOON, "Savitr", "Deva", "Buffalo", "Adi", "Vaishya", "हस्त", "देव", "महिष", "आदि", "वैश्य"),
    CHITRA(14, "Chitra", Planet.MARS, "Tvashtar", "Rakshasa", "Tiger", "Madhya", "Shudra", "चित्रा", "राक्षस", "व्याघ्र", "मध्य", "शूद्र"),
    SWATI(15, "Swati", Planet.RAHU, "Vayu", "Deva", "Buffalo", "Antya", "Shudra", "स्वाती", "देव", "महिष", "अंत्य", "शूद्र"),
    VISHAKHA(16, "Vishakha", Planet.JUPITER, "Indragni", "Rakshasa", "Tiger", "Antya", "Brahmin", "विशाखा", "राक्षस", "व्याघ्र", "अंत्य", "ब्राह्मण"),
    ANURADHA(17, "Anuradha", Planet.SATURN, "Mitra", "Deva", "Deer", "Madhya", "Kshatriya", "अनुराधा", "देव", "मृग", "मध्य", "क्षत्रिय"),
    JYESHTHA(18, "Jyeshtha", Planet.MERCURY, "Indra", "Rakshasa", "Deer", "Adi", "Vaishya", "ज्येष्ठा", "राक्षस", "मृग", "आदि", "वैश्य"),
    MULA(19, "Mula", Planet.KETU, "Nirriti", "Rakshasa", "Dog", "Adi", "Shudra", "मूल", "राक्षस", "श्वान", "आदि", "शूद्र"),
    PURVA_ASHADHA(20, "Purva Ashadha", Planet.VENUS, "Apas", "Manushya", "Monkey", "Madhya", "Brahmin", "पूर्वाषाढ़ा", "मनुष्य", "वानर", "मध्य", "ब्राह्मण"),
    UTTARA_ASHADHA(21, "Uttara Ashadha", Planet.SUN, "Vishvadevas", "Manushya", "Mongoose", "Antya", "Kshatriya", "उत्तराषाढ़ा", "मनुष्य", "नकुल", "अंत्य", "क्षत्रिय"),
    SHRAVANA(22, "Shravana", Planet.MOON, "Vishnu", "Deva", "Monkey", "Antya", "Vaishya", "श्रवण", "देव", "वानर", "अंत्य", "वैश्य"),
    DHANISHTA(23, "Dhanishta", Planet.MARS, "Vasus", "Rakshasa", "Lion", "Madhya", "Shudra", "धनिष्ठा", "राक्षस", "सिंह", "मध्य", "शूद्र"),
    SHATABHISHA(24, "Shatabhisha", Planet.RAHU, "Varuna", "Rakshasa", "Horse", "Adi", "Brahmin", "शतभिषा", "राक्षस", "अश्व", "आदि", "ब्राह्मण"),
    PURVA_BHADRAPADA(25, "Purva Bhadrapada", Planet.JUPITER, "Aja Ekapada", "Manushya", "Lion", "Adi", "Brahmin", "पूर्वाभाद्रपद", "मनुष्य", "सिंह", "आदि", "ब्राह्मण"),
    UTTARA_BHADRAPADA(26, "Uttara Bhadrapada", Planet.SATURN, "Ahir Budhnya", "Manushya", "Cow", "Madhya", "Kshatriya", "उत्तराभाद्रपद", "मनुष्य", "गौ", "मध्य", "क्षत्रिय"),
    REVATI(27, "Revati", Planet.MERCURY, "Pushan", "Deva", "Elephant", "Antya", "Shudra", "रेवती", "देव", "गज", "अंत्य", "शूद्र");

    fun getDisplayName(isHindi: Boolean): String = if (isHindi) hindiName else englishName
    fun getDisplayGana(isHindi: Boolean): String = if (isHindi) hindiGana else gana
    fun getDisplayYoni(isHindi: Boolean): String = if (isHindi) hindiYoni else yoni
    fun getDisplayNadi(isHindi: Boolean): String = if (isHindi) hindiNadi else nadi
    fun getDisplayVarna(isHindi: Boolean): String = if (isHindi) hindiVarna else varna

    val sanskritName: String get() = hindiName

    companion object {
        fun fromLongitude(deg: Double): Pair<Nakshatra, Int> {
            val normalized = (deg % 360.0 + 360.0) % 360.0
            val nakshatraSpan = 360.0 / 27.0 // 13° 20' = 13.333333°
            val padaSpan = nakshatraSpan / 4.0 // 3° 20' = 3.333333°

            val nakIndex = floor(normalized / nakshatraSpan).toInt() % 27
            val posInNak = normalized - (nakIndex * nakshatraSpan)
            val pada = (floor(posInNak / padaSpan).toInt() % 4) + 1

            return Pair(entries[nakIndex], pada)
        }
    }
}

data class PlanetaryPosition(
    val planet: Planet,
    val nirayanaLongitude: Double,
    val speed: Double = 1.0,
    val isRetrograde: Boolean = false,
    val sign: ZodiacSign,
    val signDegree: Double,
    val house: Int,
    val nakshatra: Nakshatra,
    val pada: Int,
    val dignity: String,
    val navamshaSign: ZodiacSign
)

data class BirthDetails(
    val name: String = "Self",
    val gender: String = "Male",
    val year: Int = 1995,
    val month: Int = 5,
    val day: Int = 15,
    val hour: Int = 10,
    val minute: Int = 30,
    val placeName: String = "New Delhi, India",
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val timezoneOffsetHours: Double = 5.5
) {
    fun formattedDateTime(): String = String.format("%02d/%02d/%04d %02d:%02d", day, month, year, hour, minute)
}

data class DashaPeriod(
    val lord: Planet,
    val startDate: String,
    val endDate: String,
    val subPeriods: List<DashaPeriod> = emptyList(),
    val isCurrent: Boolean = false
)

data class KundaliChartData(
    val birthDetails: BirthDetails,
    val ayanamsha: Double,
    val lagna: PlanetaryPosition,
    val planets: List<PlanetaryPosition>,
    val houses: Map<Int, List<PlanetaryPosition>>,
    val navamshaHouses: Map<Int, List<PlanetaryPosition>>,
    val moonChartHouses: Map<Int, List<PlanetaryPosition>>,
    val currentMahadasha: Planet,
    val currentAntardasha: Planet,
    val dashaTimeline: List<DashaPeriod>,
    val planetaryJson: String
)
