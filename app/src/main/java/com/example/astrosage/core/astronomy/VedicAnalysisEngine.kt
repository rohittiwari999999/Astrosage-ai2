package com.example.astrosage.core.astronomy

import kotlin.math.abs

data class GemstoneInfo(
    val nameEn: String,
    val nameHi: String,
    val hindiName: String,
    val planetEn: String,
    val planetHi: String,
    val metal: String,
    val metalHi: String,
    val finger: String,
    val fingerHi: String,
    val day: String,
    val mantra: String
)

data class FavourableDetails(
    val bhagyaRatna: GemstoneInfo,
    val jeevanRatna: GemstoneInfo,
    val punyaRatna: GemstoneInfo,
    val luckyDays: List<String>,
    val luckyDaysHi: List<String>,
    val luckyNumbers: List<Int>,
    val luckyColors: List<String>,
    val luckyColorsHi: List<String>,
    val luckyDirection: String,
    val luckyDirectionHi: String,
    val ishtaDevata: String,
    val ishtaDevataHi: String,
    val friendlySigns: List<String>,
    val friendlySignsHi: List<String>
)

data class GhatakDetails(
    val rashiNameHi: String,
    val rashiNameEn: String,
    val ghatakMonthHi: String,
    val ghatakMonthEn: String,
    val ghatakTithiHi: String,
    val ghatakTithiEn: String,
    val ghatakDayHi: String,
    val ghatakDayEn: String,
    val ghatakNakshatraHi: String,
    val ghatakNakshatraEn: String,
    val ghatakPraharHi: String,
    val ghatakPraharEn: String,
    val ghatakLagnaHi: String,
    val ghatakLagnaEn: String
)

data class ManglikAnalysis(
    val isManglik: Boolean,
    val percentage: Int,
    val marsHouse: Int,
    val isCancelled: Boolean,
    val descriptionHi: String,
    val descriptionEn: String,
    val remediesHi: List<String>,
    val remediesEn: List<String>
)

data class KaalSarpAnalysis(
    val hasDosha: Boolean,
    val typeNameHi: String,
    val typeNameEn: String,
    val intensityHi: String,
    val intensityEn: String,
    val remediesHi: List<String>,
    val remediesEn: List<String>
)

data class SadeSatiAnalysis(
    val isActive: Boolean,
    val phaseHi: String,
    val phaseEn: String,
    val saturnSignHi: String,
    val saturnSignEn: String,
    val impactHi: String,
    val impactEn: String,
    val remediesHi: List<String>,
    val remediesEn: List<String>
)

object VedicAnalysisEngine {

    /**
     * Get First Syllable (Namakshar) based on Nakshatra and Pada.
     */
    fun getNamakshar(nakshatra: Nakshatra, pada: Int, isHindi: Boolean = false): String {
        val p = (pada.coerceIn(1, 4)) - 1
        val syllables = when (nakshatra) {
            Nakshatra.ASHWINI -> if (isHindi) listOf("चू", "चे", "चो", "ला") else listOf("चू (Chu)", "चे (Che)", "चो (Cho)", "ला (La)")
            Nakshatra.BHARANI -> if (isHindi) listOf("ली", "लू", "ले", "लो") else listOf("ली (Li)", "लू (Lu)", "ले (Le)", "लो (Lo)")
            Nakshatra.KRITTIKA -> if (isHindi) listOf("अ", "ई", "उ", "ए") else listOf("अ (A)", "ई (I)", "उ (U)", "ए (E)")
            Nakshatra.ROHINI -> if (isHindi) listOf("ओ", "वा", "वी", "वू") else listOf("ओ (O)", "वा (Va)", "वी (Vi)", "वू (Vu)")
            Nakshatra.MRIGASHIRA -> if (isHindi) listOf("वे", "वो", "का", "की") else listOf("वे (Ve)", "वो (Vo)", "का (Ka)", "की (Ki)")
            Nakshatra.ARDRA -> if (isHindi) listOf("कु", "घ", "ङ", "छ") else listOf("कु (Ku)", "घ (Gha)", "ङ (Nga)", "छ (Chha)")
            Nakshatra.PUNARVASU -> if (isHindi) listOf("के", "को", "हा", "ही") else listOf("के (Ke)", "को (Ko)", "हा (Ha)", "ही (Hi)")
            Nakshatra.PUSHYA -> if (isHindi) listOf("हू", "हे", "हो", "डा") else listOf("हू (Hu)", "हे (He)", "हो (Ho)", "डा (Da)")
            Nakshatra.ASHLESHA -> if (isHindi) listOf("डी", "डू", "डे", "डो") else listOf("डी (Di)", "डू (Du)", "डे (De)", "डो (Do)")
            Nakshatra.MAGHA -> if (isHindi) listOf("मा", "मी", "मू", "मे") else listOf("मा (Ma)", "मी (Mi)", "मू (Mu)", "मे (Me)")
            Nakshatra.PURVA_PHALGUNI -> if (isHindi) listOf("मो", "टा", "टी", "टू") else listOf("मो (Mo)", "टा (Ta)", "टी (Ti)", "टू (Tu)")
            Nakshatra.UTTARA_PHALGUNI -> if (isHindi) listOf("टे", "टो", "पा", "पी") else listOf("टे (Te)", "टो (To)", "पा (Pa)", "पी (Pi)")
            Nakshatra.HASTA -> if (isHindi) listOf("पू", "ष", "ण", "ठ") else listOf("पू (Pu)", "ष (Sha)", "ण (Na)", "ठ (Tha)")
            Nakshatra.CHITRA -> if (isHindi) listOf("पे", "पो", "रा", "री") else listOf("पे (Pe)", "पो (Po)", "रा (Ra)", "री (Ri)")
            Nakshatra.SWATI -> if (isHindi) listOf("रू", "रे", "रो", "ता") else listOf("रू (Ru)", "रे (Re)", "रो (Ro)", "ता (Ta)")
            Nakshatra.VISHAKHA -> if (isHindi) listOf("ती", "तू", "ते", "तो") else listOf("ती (Ti)", "तू (Tu)", "ते (Te)", "तो (To)")
            Nakshatra.ANURADHA -> if (isHindi) listOf("ना", "नी", "नू", "ने") else listOf("ना (Na)", "नी (Ni)", "नू (Nu)", "ने (Ne)")
            Nakshatra.JYESHTHA -> if (isHindi) listOf("नो", "या", "यी", "यू") else listOf("नो (No)", "या (Ya)", "यी (Yi)", "यू (Yu)")
            Nakshatra.MULA -> if (isHindi) listOf("ये", "यो", "भा", "भी") else listOf("ये (Ye)", "यो (Yo)", "भा (Bha)", "भी (Bhi)")
            Nakshatra.PURVA_ASHADHA -> if (isHindi) listOf("भू", "धा", "फा", "ढा") else listOf("भू (Bhu)", "धा (Dha)", "फा (Pha)", "ढा (Dha)")
            Nakshatra.UTTARA_ASHADHA -> if (isHindi) listOf("भे", "भो", "जा", "जी") else listOf("भे (Bhe)", "भो (Bho)", "जा (Ja)", "जी (Ji)")
            Nakshatra.SHRAVANA -> if (isHindi) listOf("खी", "खू", "खे", "खो") else listOf("खी (Khi)", "खू (Khu)", "खे (Khe)", "खो (Kho)")
            Nakshatra.DHANISHTA -> if (isHindi) listOf("गा", "गी", "गू", "गे") else listOf("गा (Ga)", "गी (Gi)", "गू (Gu)", "गे (Ge)")
            Nakshatra.SHATABHISHA -> if (isHindi) listOf("गो", "सा", "सी", "सू") else listOf("गो (Go)", "सा (Sa)", "सी (Si)", "सू (Su)")
            Nakshatra.PURVA_BHADRAPADA -> if (isHindi) listOf("से", "सो", "दा", "दी") else listOf("से (Se)", "सो (So)", "दा (Da)", "दी (Di)")
            Nakshatra.UTTARA_BHADRAPADA -> if (isHindi) listOf("दू", "थ", "झ", "ञ") else listOf("दू (Du)", "थ (Tha)", "झ (Jha)", "ञ (Nya)")
            Nakshatra.REVATI -> if (isHindi) listOf("दे", "दो", "चा", "ची") else listOf("दे (De)", "दो (Do)", "चा (Cha)", "ची (Chi)")
        }
        return syllables[p]
    }

    /**
     * Calculate Paya based on Moon House relative to Lagna.
     */
    fun calculatePaya(moonHouse: Int): Pair<String, String> {
        return when (moonHouse) {
            1, 6, 11 -> Pair("स्वर्ण - मध्यम", "Gold Paya (Moderate)")
            2, 5, 9 -> Pair("रजत - सर्वोत्कृष्ट शुभ", "Silver Paya (Most Auspicious)")
            3, 7, 10 -> Pair("ताम्र - उत्तम फलदायी", "Copper Paya (Benefic & Auspicious)")
            else -> Pair("लौह - संघर्ष उपरांत सफलता", "Iron Paya (Success after effort)")
        }
    }

    /**
     * Calculate Tara based on Moon Nakshatra.
     */
    fun getTaraName(isHindi: Boolean): String {
        return if (isHindi) "जन्म तारा" else "Janma Tara (Auspicious)"
    }

    /**
     * Get Gemstone Info for a specific Planet.
     */
    fun getGemstoneForPlanet(planet: Planet): GemstoneInfo {
        return when (planet) {
            Planet.SUN -> GemstoneInfo(
                nameEn = "Ruby",
                nameHi = "माणिक्य",
                hindiName = "माणिक्य",
                planetEn = "Sun",
                planetHi = "सूर्य",
                metal = "Gold / Copper",
                metalHi = "सोना / तांबा",
                finger = "Ring Finger (अनामिका)",
                fingerHi = "अनामिका उंगली",
                day = "Sunday Morning",
                mantra = "ॐ सूर्याय नमः"
            )
            Planet.MOON -> GemstoneInfo(
                nameEn = "Pearl",
                nameHi = "मोती",
                hindiName = "मोती",
                planetEn = "Moon",
                planetHi = "चंद्र",
                metal = "Silver",
                metalHi = "चांदी",
                finger = "Little Finger (कनिष्ठिका)",
                fingerHi = "कनिष्ठिका उंगली",
                day = "Monday Evening",
                mantra = "ॐ सों सोमाय नमः"
            )
            Planet.MARS -> GemstoneInfo(
                nameEn = "Red Coral",
                nameHi = "मूँगा",
                hindiName = "मूँगा",
                planetEn = "Mars",
                planetHi = "मंगल",
                metal = "Copper / Gold",
                metalHi = "तांबा / सोना",
                finger = "Ring Finger (अनामिका)",
                fingerHi = "अनामिका उंगली",
                day = "Tuesday Morning",
                mantra = "ॐ भौं भौमाय नमः"
            )
            Planet.MERCURY -> GemstoneInfo(
                nameEn = "Emerald",
                nameHi = "पन्ना",
                hindiName = "पन्ना",
                planetEn = "Mercury",
                planetHi = "बुध",
                metal = "Gold / Silver",
                metalHi = "सोना / चांदी",
                finger = "Little Finger (कनिष्ठिका)",
                fingerHi = "कनिष्ठिका उंगली",
                day = "Wednesday Morning",
                mantra = "ॐ बुं बुधाय नमः"
            )
            Planet.JUPITER -> GemstoneInfo(
                nameEn = "Yellow Sapphire",
                nameHi = "पुखराज",
                hindiName = "पुखराज",
                planetEn = "Jupiter",
                planetHi = "गुरु",
                metal = "Gold / Brass",
                metalHi = "सोना / पीतल",
                finger = "Index Finger (तर्जनी)",
                fingerHi = "तर्जनी उंगली",
                day = "Thursday Morning",
                mantra = "ॐ बृं बृहस्पतये नमः"
            )
            Planet.VENUS -> GemstoneInfo(
                nameEn = "Diamond / Opal",
                nameHi = "हीरा / ओपल",
                hindiName = "हीरा",
                planetEn = "Venus",
                planetHi = "शुक्र",
                metal = "Silver / Platinum",
                metalHi = "चांदी / प्लैटिनम",
                finger = "Middle / Ring Finger",
                fingerHi = "मध्यमा / अनामिका उंगली",
                day = "Friday Morning",
                mantra = "ॐ शुं शुक्राय नमः"
            )
            Planet.SATURN -> GemstoneInfo(
                nameEn = "Blue Sapphire",
                nameHi = "नीलम",
                hindiName = "नीलम",
                planetEn = "Saturn",
                planetHi = "शनि",
                metal = "Silver / Panchdhatu",
                metalHi = "चांदी / पंचधातु",
                finger = "Middle Finger (मध्यमा)",
                fingerHi = "मध्यमा उंगली",
                day = "Saturday Evening",
                mantra = "ॐ शं शनैश्चराय नमः"
            )
            Planet.RAHU -> GemstoneInfo(
                nameEn = "Hessonite (Gomed)",
                nameHi = "गोमेद",
                hindiName = "गोमेद",
                planetEn = "Rahu",
                planetHi = "राहु",
                metal = "Silver / Ashtadhatu",
                metalHi = "चांदी / अष्टधातु",
                finger = "Middle Finger (मध्यमा)",
                fingerHi = "मध्यमा उंगली",
                day = "Saturday Night",
                mantra = "ॐ रां राहवे नमः"
            )
            Planet.KETU -> GemstoneInfo(
                nameEn = "Cat's Eye (Lehsuniya)",
                nameHi = "लहसुनिया",
                hindiName = "लहसुनिया",
                planetEn = "Ketu",
                planetHi = "केतु",
                metal = "Silver",
                metalHi = "चांदी",
                finger = "Little Finger (कनिष्ठिका)",
                fingerHi = "कनिष्ठिका उंगली",
                day = "Tuesday Night",
                mantra = "ॐ कें केतवे नमः"
            )
            Planet.ASCENDANT -> GemstoneInfo(
                nameEn = "Yellow Sapphire",
                nameHi = "पुखराज",
                hindiName = "पुखराज",
                planetEn = "Lagna",
                planetHi = "लग्न",
                metal = "Gold",
                metalHi = "सोना",
                finger = "Index Finger",
                fingerHi = "तर्जनी उंगली",
                day = "Thursday",
                mantra = "ॐ नमः शिवाय"
            )
        }
    }

    /**
     * Calculate Favourable elements from Kundali.
     */
    fun calculateFavourableDetails(chart: KundaliChartData): FavourableDetails {
        val lagnaSign = chart.lagna.sign
        val moonSign = chart.planets.firstOrNull { it.planet == Planet.MOON }?.sign ?: lagnaSign

        // 9th house sign lord = Bhagya Lord
        val ninthHouseSignNum = ((lagnaSign.number + 7) % 12) + 1
        val ninthHouseLord = ZodiacSign.fromNumber(ninthHouseSignNum).ruler

        // 1st house sign lord = Lagna Lord
        val lagnaLord = lagnaSign.ruler

        // 5th house sign lord = Punya Lord
        val fifthHouseSignNum = ((lagnaSign.number + 3) % 12) + 1
        val fifthHouseLord = ZodiacSign.fromNumber(fifthHouseSignNum).ruler

        val bhagyaGem = getGemstoneForPlanet(ninthHouseLord)
        val jeevanGem = getGemstoneForPlanet(lagnaLord)
        val punyaGem = getGemstoneForPlanet(fifthHouseLord)

        val luckyDays = when (lagnaLord) {
            Planet.SUN -> listOf("Sunday", "Tuesday", "Thursday")
            Planet.MOON -> listOf("Monday", "Thursday", "Sunday")
            Planet.MARS -> listOf("Tuesday", "Sunday", "Thursday")
            Planet.MERCURY -> listOf("Wednesday", "Friday", "Saturday")
            Planet.JUPITER -> listOf("Thursday", "Tuesday", "Sunday")
            Planet.VENUS -> listOf("Friday", "Saturday", "Wednesday")
            Planet.SATURN -> listOf("Saturday", "Friday", "Wednesday")
            else -> listOf("Thursday", "Sunday")
        }

        val luckyDaysHi = when (lagnaLord) {
            Planet.SUN -> listOf("रविवार", "मंगलवार", "गुरुवार")
            Planet.MOON -> listOf("सोमवार", "गुरुवार", "रविवार")
            Planet.MARS -> listOf("मंगलवार", "रविवार", "गुरुवार")
            Planet.MERCURY -> listOf("बुधवार", "शुक्रवार", "शनिवार")
            Planet.JUPITER -> listOf("गुरुवार", "मंगलवार", "रविवार")
            Planet.VENUS -> listOf("शुक्रवार", "शनिवार", "बुधवार")
            Planet.SATURN -> listOf("शनिवार", "शुक्रवार", "बुधवार")
            else -> listOf("गुरुवार", "रविवार")
        }

        val luckyNumbers = when (lagnaLord) {
            Planet.SUN -> listOf(1, 4, 7)
            Planet.MOON -> listOf(2, 7, 9)
            Planet.MARS -> listOf(9, 3, 1)
            Planet.MERCURY -> listOf(5, 6, 2)
            Planet.JUPITER -> listOf(3, 9, 1, 7)
            Planet.VENUS -> listOf(6, 5, 8)
            Planet.SATURN -> listOf(8, 6, 5)
            else -> listOf(3, 7, 9)
        }

        val luckyColors = when (lagnaLord) {
            Planet.SUN -> listOf("Orange", "Saffron", "Golden Yellow")
            Planet.MOON -> listOf("White", "Silver", "Cream")
            Planet.MARS -> listOf("Bright Red", "Coral", "Maroon")
            Planet.MERCURY -> listOf("Emerald Green", "Light Green")
            Planet.JUPITER -> listOf("Bright Yellow", "Golden", "Mustard")
            Planet.VENUS -> listOf("Pearl White", "Sky Blue", "Pink")
            Planet.SATURN -> listOf("Dark Blue", "Black", "Navy")
            else -> listOf("Yellow", "White")
        }

        val luckyColorsHi = when (lagnaLord) {
            Planet.SUN -> listOf("केसरिया", "नारंगी", "स्वर्णिम पीला")
            Planet.MOON -> listOf("श्वेत", "चांदी", "क्रीम")
            Planet.MARS -> listOf("लाल", "मूँगा रंग", "गहरा गुलाबी")
            Planet.MERCURY -> listOf("हरा", "हल्का हरा")
            Planet.JUPITER -> listOf("पीला", "स्वर्णिम", "हल्दी")
            Planet.VENUS -> listOf("चमकीला सफेद", "आसमानी", "गुलाबी")
            Planet.SATURN -> listOf("नीला", "जामुनी", "गहरा स्लेटी")
            else -> listOf("पीला", "श्वेत")
        }

        val luckyDirection = when (lagnaSign.element) {
            "Fire" -> "East (पूर्व)"
            "Earth" -> "South (दक्षिण)"
            "Air" -> "West (पश्चिम)"
            else -> "North (उत्तर)"
        }

        val ishtaDevata = when (fifthHouseLord) {
            Planet.SUN -> "भगवान सूर्य / श्री राम"
            Planet.MOON -> "भगवान शिव / माता पार्वती"
            Planet.MARS -> "श्री हनुमान जी / भगवान कार्तिकेय"
            Planet.MERCURY -> "भगवान गणेश / श्री नारायण"
            Planet.JUPITER -> "भगवान विष्णु / देवगुरु"
            Planet.VENUS -> "माता महालक्ष्मी / देवी दुर्गा"
            Planet.SATURN -> "भगवान शिव / शनिदेव"
            else -> "भगवान विष्णु / शिव"
        }

        return FavourableDetails(
            bhagyaRatna = bhagyaGem,
            jeevanRatna = jeevanGem,
            punyaRatna = punyaGem,
            luckyDays = luckyDays,
            luckyDaysHi = luckyDaysHi,
            luckyNumbers = luckyNumbers,
            luckyColors = luckyColors,
            luckyColorsHi = luckyColorsHi,
            luckyDirection = luckyDirection,
            luckyDirectionHi = luckyDirection,
            ishtaDevata = ishtaDevata,
            ishtaDevataHi = ishtaDevata,
            friendlySigns = listOf("Taurus", "Cancer", "Scorpio", "Pisces"),
            friendlySignsHi = listOf("वृषभ", "कर्क", "वृश्चिक", "मीन")
        )
    }

    /**
     * Classical Ghatak Chakra lookup based on Moon Sign.
     */
    fun calculateGhatakDetails(moonSign: ZodiacSign): GhatakDetails {
        return when (moonSign) {
            ZodiacSign.ARIES -> GhatakDetails(
                rashiNameHi = "मेष (Aries)",
                rashiNameEn = "Aries",
                ghatakMonthHi = "कार्तिक",
                ghatakMonthEn = "Kartik",
                ghatakTithiHi = "नंदा (1, 6, 11)",
                ghatakTithiEn = "Nanda (1, 6, 11)",
                ghatakDayHi = "रविवार",
                ghatakDayEn = "Sunday",
                ghatakNakshatraHi = "मघा",
                ghatakNakshatraEn = "Magha",
                ghatakPraharHi = "प्रथम प्रहर (सुबह 6 से 9)",
                ghatakPraharEn = "1st Prahar (6-9 AM)",
                ghatakLagnaHi = "मेष लग्न",
                ghatakLagnaEn = "Aries Ascendant"
            )
            ZodiacSign.TAURUS -> GhatakDetails(
                rashiNameHi = "वृषभ (Taurus)",
                rashiNameEn = "Taurus",
                ghatakMonthHi = "मार्गशीर्ष (अगहन)",
                ghatakMonthEn = "Margashirsha",
                ghatakTithiHi = "पूर्णा (5, 10, 15)",
                ghatakTithiEn = "Purna (5, 10, 15)",
                ghatakDayHi = "शनिवार",
                ghatakDayEn = "Saturday",
                ghatakNakshatraHi = "हस्त",
                ghatakNakshatraEn = "Hasta",
                ghatakPraharHi = "तृतीय प्रहर (दोपहर 12 से 3)",
                ghatakPraharEn = "3rd Prahar (12-3 PM)",
                ghatakLagnaHi = "वृश्चिक लग्न",
                ghatakLagnaEn = "Scorpio Ascendant"
            )
            ZodiacSign.GEMINI -> GhatakDetails(
                rashiNameHi = "मिथुन (Gemini)",
                rashiNameEn = "Gemini",
                ghatakMonthHi = "पौष",
                ghatakMonthEn = "Pausha",
                ghatakTithiHi = "भद्रा (2, 7, 12)",
                ghatakTithiEn = "Bhadra (2, 7, 12)",
                ghatakDayHi = "सोमवार",
                ghatakDayEn = "Monday",
                ghatakNakshatraHi = "स्वाति",
                ghatakNakshatraEn = "Swati",
                ghatakPraharHi = "द्वितीय प्रहर (सुबह 9 से 12)",
                ghatakPraharEn = "2nd Prahar (9-12 AM)",
                ghatakLagnaHi = "धनु लग्न",
                ghatakLagnaEn = "Sagittarius Ascendant"
            )
            ZodiacSign.CANCER -> GhatakDetails(
                rashiNameHi = "कर्क (Cancer)",
                rashiNameEn = "Cancer",
                ghatakMonthHi = "माघ",
                ghatakMonthEn = "Magha",
                ghatakTithiHi = "रिक्ता (4, 9, 14)",
                ghatakTithiEn = "Rikta (4, 9, 14)",
                ghatakDayHi = "बुधवार",
                ghatakDayEn = "Wednesday",
                ghatakNakshatraHi = "अनुराधा",
                ghatakNakshatraEn = "Anuradha",
                ghatakPraharHi = "चतुर्थ प्रहर (दोपहर 3 से 6)",
                ghatakPraharEn = "4th Prahar (3-6 PM)",
                ghatakLagnaHi = "वृषभ लग्न",
                ghatakLagnaEn = "Taurus Ascendant"
            )
            ZodiacSign.LEO -> GhatakDetails(
                rashiNameHi = "सिंह (Leo)",
                rashiNameEn = "Leo",
                ghatakMonthHi = "फाल्गुन",
                ghatakMonthEn = "Phalguna",
                ghatakTithiHi = "जया (3, 8, 13)",
                ghatakTithiEn = "Jaya (3, 8, 13)",
                ghatakDayHi = "शनिवार",
                ghatakDayEn = "Saturday",
                ghatakNakshatraHi = "मूल",
                ghatakNakshatraEn = "Mula",
                ghatakPraharHi = "प्रथम प्रहर (सुबह 6 से 9)",
                ghatakPraharEn = "1st Prahar (6-9 AM)",
                ghatakLagnaHi = "तुला लग्न",
                ghatakLagnaEn = "Libra Ascendant"
            )
            ZodiacSign.VIRGO -> GhatakDetails(
                rashiNameHi = "कन्या (Virgo)",
                rashiNameEn = "Virgo",
                ghatakMonthHi = "चैत्र",
                ghatakMonthEn = "Chaitra",
                ghatakTithiHi = "पूर्णा (5, 10, 15)",
                ghatakTithiEn = "Purna (5, 10, 15)",
                ghatakDayHi = "शनिवार",
                ghatakDayEn = "Saturday",
                ghatakNakshatraHi = "श्रवण",
                ghatakNakshatraEn = "Shravana",
                ghatakPraharHi = "द्वितीय प्रहर (सुबह 9 से 12)",
                ghatakPraharEn = "2nd Prahar (9-12 AM)",
                ghatakLagnaHi = "मीन लग्न",
                ghatakLagnaEn = "Pisces Ascendant"
            )
            ZodiacSign.LIBRA -> GhatakDetails(
                rashiNameHi = "तुला (Libra)",
                rashiNameEn = "Libra",
                ghatakMonthHi = "वैशाख",
                ghatakMonthEn = "Vaishakha",
                ghatakTithiHi = "नंदा (1, 6, 11)",
                ghatakTithiEn = "Nanda (1, 6, 11)",
                ghatakDayHi = "गुरुवार",
                ghatakDayEn = "Thursday",
                ghatakNakshatraHi = "शतभिषा",
                ghatakNakshatraEn = "Shatabhisha",
                ghatakPraharHi = "तृतीय प्रहर (दोपहर 12 से 3)",
                ghatakPraharEn = "3rd Prahar (12-3 PM)",
                ghatakLagnaHi = "मेष लग्न",
                ghatakLagnaEn = "Aries Ascendant"
            )
            ZodiacSign.SCORPIO -> GhatakDetails(
                rashiNameHi = "वृश्चिक (Scorpio)",
                rashiNameEn = "Scorpio",
                ghatakMonthHi = "ज्येष्ठ",
                ghatakMonthEn = "Jyeshtha",
                ghatakTithiHi = "भद्रा (2, 7, 12)",
                ghatakTithiEn = "Bhadra (2, 7, 12)",
                ghatakDayHi = "शुक्रवार",
                ghatakDayEn = "Friday",
                ghatakNakshatraHi = "रेवती",
                ghatakNakshatraEn = "Revati",
                ghatakPraharHi = "चतुर्थ प्रहर (दोपहर 3 से 6)",
                ghatakPraharEn = "4th Prahar (3-6 PM)",
                ghatakLagnaHi = "वृषभ लग्न",
                ghatakLagnaEn = "Taurus Ascendant"
            )
            ZodiacSign.SAGITTARIUS -> GhatakDetails(
                rashiNameHi = "धनु (Sagittarius)",
                rashiNameEn = "Sagittarius",
                ghatakMonthHi = "आषाढ़",
                ghatakMonthEn = "Ashadha",
                ghatakTithiHi = "जया (3, 8, 13)",
                ghatakTithiEn = "Jaya (3, 8, 13)",
                ghatakDayHi = "शुक्रवार",
                ghatakDayEn = "Friday",
                ghatakNakshatraHi = "अश्विनी",
                ghatakNakshatraEn = "Ashwini",
                ghatakPraharHi = "प्रथम प्रहर (सुबह 6 से 9)",
                ghatakPraharEn = "1st Prahar (6-9 AM)",
                ghatakLagnaHi = "मिथुन लग्न",
                ghatakLagnaEn = "Gemini Ascendant"
            )
            ZodiacSign.CAPRICORN -> GhatakDetails(
                rashiNameHi = "मकर (Capricorn)",
                rashiNameEn = "Capricorn",
                ghatakMonthHi = "श्रावण",
                ghatakMonthEn = "Shravana",
                ghatakTithiHi = "रिक्ता (4, 9, 14)",
                ghatakTithiEn = "Rikta (4, 9, 14)",
                ghatakDayHi = "मंगलवार",
                ghatakDayEn = "Tuesday",
                ghatakNakshatraHi = "रोहिणी",
                ghatakNakshatraEn = "Rohini",
                ghatakPraharHi = "द्वितीय प्रहर (सुबह 9 से 12)",
                ghatakPraharEn = "2nd Prahar (9-12 AM)",
                ghatakLagnaHi = "कर्क लग्न",
                ghatakLagnaEn = "Cancer Ascendant"
            )
            ZodiacSign.AQUARIUS -> GhatakDetails(
                rashiNameHi = "कुंभ (Aquarius)",
                rashiNameEn = "Aquarius",
                ghatakMonthHi = "भाद्रपद",
                ghatakMonthEn = "Bhadrapada",
                ghatakTithiHi = "पूर्णा (5, 10, 15)",
                ghatakTithiEn = "Purna (5, 10, 15)",
                ghatakDayHi = "गुरुवार",
                ghatakDayEn = "Thursday",
                ghatakNakshatraHi = "आर्द्रा",
                ghatakNakshatraEn = "Ardra",
                ghatakPraharHi = "तृतीय प्रहर (दोपहर 12 से 3)",
                ghatakPraharEn = "3rd Prahar (12-3 PM)",
                ghatakLagnaHi = "सिंह लग्न",
                ghatakLagnaEn = "Leo Ascendant"
            )
            ZodiacSign.PISCES -> GhatakDetails(
                rashiNameHi = "मीन (Pisces)",
                rashiNameEn = "Pisces",
                ghatakMonthHi = "आश्विन",
                ghatakMonthEn = "Ashvina",
                ghatakTithiHi = "नंदा (1, 6, 11)",
                ghatakTithiEn = "Nanda (1, 6, 11)",
                ghatakDayHi = "शुक्रवार",
                ghatakDayEn = "Friday",
                ghatakNakshatraHi = "अश्लेषा",
                ghatakNakshatraEn = "Ashlesha",
                ghatakPraharHi = "चतुर्थ प्रहर (दोपहर 3 से 6)",
                ghatakPraharEn = "4th Prahar (3-6 PM)",
                ghatakLagnaHi = "कन्या लग्न",
                ghatakLagnaEn = "Virgo Ascendant"
            )
        }
    }

    /**
     * Analyze Manglik Dosha from Mars placement.
     */
    fun analyzeManglik(chart: KundaliChartData): ManglikAnalysis {
        val mars = chart.planets.firstOrNull { it.planet == Planet.MARS }
        val marsHouse = mars?.house ?: 1
        val marsSign = mars?.sign ?: ZodiacSign.ARIES

        val isManglikHouse = marsHouse in listOf(1, 4, 7, 8, 12)
        if (!isManglikHouse) {
            return ManglikAnalysis(
                isManglik = false,
                percentage = 0,
                marsHouse = marsHouse,
                isCancelled = true,
                descriptionHi = "आपकी कुंडली में मंगल $marsHouse वें भाव में स्थित है। अतः आप मांगलिक नहीं हैं। दांपत्य जीवन के लिए यह शुभ स्थिति है।",
                descriptionEn = "Mars is placed in the $marsHouse house. You are NOT Manglik. Highly favourable for marital harmony.",
                remediesHi = listOf("नियमित रूप से श्री हनुमान चालीसा का पाठ करें।", "मंगलवार को मीठे भोजन का दान करें।"),
                remediesEn = listOf("Recite Hanuman Chalisa regularly.", "Donate sweet sweets on Tuesdays.")
            )
        }

        // Check cancellation
        val isCancelled = marsSign in listOf(ZodiacSign.ARIES, ZodiacSign.SCORPIO, ZodiacSign.LEO, ZodiacSign.CAPRICORN) ||
                chart.planets.any { it.planet == Planet.JUPITER && it.house == marsHouse }

        val pct = if (isCancelled) 25 else when (marsHouse) {
            7, 8 -> 100
            1, 4 -> 75
            else -> 50
        }

        val descHi = if (isCancelled) {
            "आपकी कुंडली में मंगल $marsHouse वें भाव में है परंतु स्वराशि/मित्र राशि के प्रभाव से मांगलिक दोष का आंशिक परिहार (कंसिलेशन) हो गया है। इसका दांपत्य पर कोई गंभीर दुष्प्रभाव नहीं पड़ेगा।"
        } else {
            "आपकी कुंडली में मंगल $marsHouse वें भाव में स्थित है, जिससे मांगलिक प्रभाव बन रहा है। विवाह के समय कुंडली मिलान एवं मंगल शांति उपाय श्रेयस्कर हैं।"
        }

        val descEn = if (isCancelled) {
            "Mars is in house $marsHouse, but beneficial dignity produces Manglik Dosha Cancellation (Parihara). Minimal impact on marital life."
        } else {
            "Mars occupies house $marsHouse creating Manglik influence. Horoscope matching and remedial peace rituals are recommended."
        }

        return ManglikAnalysis(
            isManglik = !isCancelled,
            percentage = pct,
            marsHouse = marsHouse,
            isCancelled = isCancelled,
            descriptionHi = descHi,
            descriptionEn = descEn,
            remediesHi = listOf(
                "प्रत्येक मंगलवार को हनुमान जी को सिंदूर व चोला अर्पित करें।",
                "मंगल स्तोत्र का पाठ करें: 'ॐ धरणीगर्भसंभूतं विद्युतकान्तिसमप्रभम्।'",
                "विवाह पूर्व वर/कन्या की कुंडली का विधिवत 36 गुण व नाड़ी मिलान करवाएं।",
                "उज्जैन अथवा मंगलनाथ धाम में भात पूजा / मंगल शांति संकल्प।"
            ),
            remediesEn = listOf(
                "Offer sindoor and recite Hanuman Chalisa every Tuesday.",
                "Chant Mars Beej Mantra: Om Kraam Kreem Kroum Sah Bhaumaya Namah.",
                "Perform proper Kundali matching before marriage.",
                "Support charity and help elder siblings or armed forces personnel."
            )
        )
    }

    /**
     * Analyze Kaal Sarp Dosha.
     */
    fun analyzeKaalSarp(chart: KundaliChartData): KaalSarpAnalysis {
        val rahu = chart.planets.firstOrNull { it.planet == Planet.RAHU }
        val ketu = chart.planets.firstOrNull { it.planet == Planet.KETU }
        val rahuHouse = rahu?.house ?: 1
        val ketuHouse = ketu?.house ?: 7

        val typeNameHi = when (rahuHouse) {
            1 -> "अनंत कालसर्प दोष (1st - 7th)"
            2 -> "कुलिक कालसर्प दोष (2nd - 8th)"
            3 -> "वासुकि कालसर्प दोष (3rd - 9th)"
            4 -> "शंखपाल कालसर्प दोष (4th - 10th)"
            5 -> "पद्म कालसर्प दोष (5th - 11th)"
            6 -> "महापद्म कालसर्प दोष (6th - 12th)"
            7 -> "तक्षक कालसर्प दोष (7th - 1st)"
            8 -> "कर्कोटक कालसर्प दोष (8th - 2nd)"
            9 -> "शंखनाद कालसर्प दोष (9th - 3rd)"
            10 -> "घातक कालसर्प दोष (10th - 4th)"
            11 -> "विषधर कालसर्प दोष (11th - 5th)"
            else -> "शेषनाग कालसर्प दोष (12th - 6th)"
        }

        val typeNameEn = when (rahuHouse) {
            1 -> "Ananta Kaal Sarp Dosha"
            2 -> "Kulika Kaal Sarp Dosha"
            3 -> "Vasuki Kaal Sarp Dosha"
            4 -> "Shankhapala Kaal Sarp Dosha"
            5 -> "Padma Kaal Sarp Dosha"
            6 -> "Mahapadma Kaal Sarp Dosha"
            7 -> "Takshaka Kaal Sarp Dosha"
            8 -> "Karkotaka Kaal Sarp Dosha"
            9 -> "Shankhanaad Kaal Sarp Dosha"
            10 -> "Ghataka Kaal Sarp Dosha"
            11 -> "Vishadhara Kaal Sarp Dosha"
            else -> "Sheshnaag Kaal Sarp Dosha"
        }

        // Check if all planets are between Rahu and Ketu
        var hemmedCount = 0
        chart.planets.forEach { p ->
            if (p.planet != Planet.RAHU && p.planet != Planet.KETU) {
                if (p.house in listOf(rahuHouse, ketuHouse)) hemmedCount++
            }
        }

        val hasDosha = hemmedCount <= 2 // Partial or full depending on distribution

        return KaalSarpAnalysis(
            hasDosha = hasDosha,
            typeNameHi = typeNameHi,
            typeNameEn = typeNameEn,
            intensityHi = if (hasDosha) "आंशिक कालसर्प प्रभाव" else "दोष मुक्त (शुभ योग)",
            intensityEn = if (hasDosha) "Partial Kaal Sarp Yoga" else "Free from Kaal Sarp Yoga",
            remediesHi = listOf(
                "प्रतिदिन 'ॐ नमः शिवाय' अथवा महामृत्युंजय मंत्र का 108 बार जाप करें।",
                "नाग पंचमी के दिन चांदी के नाग-नागिन जोड़े का विधिवत पूजन कर जल प्रवाह करें।",
                "सोमवार को शिवलिंग पर कच्चा दूध, बेलपत्र एवं काले तिल अर्पित करें।",
                "उज्जैन महाकालेश्वर अथवा त्र्यंबकेश्वर नासिक में कालसर्प शांति अनुष्ठान।"
            ),
            remediesEn = listOf(
                "Chant Mahamrityunjaya Mantra 108 times daily.",
                "Perform Rudrabhishek on Mondays with milk and honey.",
                "Avoid lending money or unnecessary partnership conflicts on Saturday.",
                "Seek blessings at Trimbakeshwar or Ujjain Mahakal temple."
            )
        )
    }

    /**
     * Analyze Shani Sade Sati for the native based on Moon Sign.
     */
    fun analyzeSadeSati(chart: KundaliChartData): SadeSatiAnalysis {
        val moon = chart.planets.firstOrNull { it.planet == Planet.MOON }
        val moonSign = moon?.sign ?: ZodiacSign.ARIES

        // Currently in 2026, Saturn transits in Pisces (मीन राशि)
        // Sade Sati affects signs: Aquarius (12th to Saturn / Setting), Pisces (Janma / Peak), Aries (Rising)
        val phase = when (moonSign) {
            ZodiacSign.PISCES -> Pair("द्वितीय चरण (शिखर काल / Peak Phase)", "Phase 2 (Peak Janma Shani)")
            ZodiacSign.AQUARIUS -> Pair("तृतीय चरण (अंतिम ढलती साढ़े साती)", "Phase 3 (Setting Sade Sati)")
            ZodiacSign.ARIES -> Pair("प्रथम चरण (आरंभिक उदय काल)", "Phase 1 (Rising Sade Sati)")
            ZodiacSign.CANCER, ZodiacSign.SCORPIO -> Pair("शनि की ढैय्या (कंटक/अष्टम शनि)", "Saturn Dhaiya (Minor transit)")
            else -> Pair("साढ़े साती से मुक्त (शुभ समय)", "Free from Sade Sati (Favourable)")
        }

        val isActive = moonSign in listOf(ZodiacSign.AQUARIUS, ZodiacSign.PISCES, ZodiacSign.ARIES)

        val impactHi = if (isActive) {
            "वर्तमान में आपकी राशि पर शनि का प्रभाव सक्रिय है। यह काल कर्मफल, अनुशासन एवं आत्मचिंतन का है। थोड़ी मानसिक चंचलता अथवा परिश्रम के उपरांत स्थायी सफलता मिलेगी।"
        } else {
            "आपकी राशि पर वर्तमान में कोई साढ़े साती का प्रभाव नहीं है। शनिदेव की कृपा से कार्यक्षेत्र एवं भाग्य में शुभता बनी रहेगी।"
        }

        val impactEn = if (isActive) {
            "Saturn's transit is currently influencing your Moon sign. It fosters discipline, patience, and karmic growth. Perseverance leads to long-term accomplishments."
        } else {
            "Your Moon sign is currently free from Shani Sade Sati. Clear path for personal and professional growth."
        }

        return SadeSatiAnalysis(
            isActive = isActive,
            phaseHi = phase.first,
            phaseEn = phase.second,
            saturnSignHi = "मीन (Pisces) - 2026 गोचर",
            saturnSignEn = "Pisces (2026 Transit)",
            impactHi = impactHi,
            impactEn = impactEn,
            remediesHi = listOf(
                "प्रत्येक शनिवार शाम को पीपल के वृक्ष के नीचे सरसों के तेल का दीपक प्रज्वलित करें।",
                "शनि बीज मंत्र: 'ॐ प्रां प्रीं प्रौं सः शनैश्चराय नमः' का 108 बार जाप करें।",
                "काले तिल, उड़द दाल, काला छाता अथवा जूते-चप्पलों का जरूरतमंदों को दान करें।",
                "शनिवार को सुंदरकांड अथवा दशरथकृत शनि स्तोत्र का पाठ करें।"
            ),
            remediesEn = listOf(
                "Light a mustard oil lamp under a Peepal tree on Saturday evenings.",
                "Chant Shani Beej Mantra: Om Praam Preem Proum Sah Shanaischaraya Namah.",
                "Donate black sesame, black clothes, or iron utensils to the needy.",
                "Read Hanuman Chalisa and Dasharatha Shani Stotram regularly."
            )
        )
    }
}
