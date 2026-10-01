package com.example.astrosage.core.astronomy

data class AshtakootResult(
    val varnaScore: Double,
    val varnaMax: Double = 1.0,
    val vashyaScore: Double,
    val vashyaMax: Double = 2.0,
    val taraScore: Double,
    val taraMax: Double = 3.0,
    val yoniScore: Double,
    val yoniMax: Double = 4.0,
    val maitriScore: Double,
    val maitriMax: Double = 5.0,
    val ganaScore: Double,
    val ganaMax: Double = 6.0,
    val bhakootScore: Double,
    val bhakootMax: Double = 7.0,
    val nadiScore: Double,
    val nadiMax: Double = 8.0,
    val totalScore: Double,
    val boyManglik: Boolean,
    val girlManglik: Boolean,
    val mangalDoshaStatus: String,
    val recommendation: String,
    val detailedSummary: String
)

object KundaliMatchingEngine {

    fun matchKundali(boyChart: KundaliChartData, girlChart: KundaliChartData): AshtakootResult {
        val boyMoon = boyChart.planets.first { it.planet == Planet.MOON }
        val girlMoon = girlChart.planets.first { it.planet == Planet.MOON }

        val boySign = boyMoon.sign
        val girlSign = girlMoon.sign
        val boyNak = boyMoon.nakshatra
        val girlNak = girlMoon.nakshatra

        // 1. Varna (1 pt)
        val varnaScore = calculateVarna(boySign, girlSign)

        // 2. Vashya (2 pts)
        val vashyaScore = calculateVashya(boySign, girlSign)

        // 3. Tara (3 pts)
        val taraScore = calculateTara(boyNak, girlNak)

        // 4. Yoni (4 pts)
        val yoniScore = calculateYoni(boyNak, girlNak)

        // 5. Graha Maitri (5 pts)
        val maitriScore = calculateGrahaMaitri(boySign.ruler, girlSign.ruler)

        // 6. Gana (6 pts)
        val ganaScore = calculateGana(boyNak.gana, girlNak.gana)

        // 7. Bhakoot (7 pts)
        val bhakootScore = calculateBhakoot(boySign, girlSign)

        // 8. Nadi (8 pts)
        val nadiScore = calculateNadi(boyNak.nadi, girlNak.nadi)

        val total = varnaScore + vashyaScore + taraScore + yoniScore + maitriScore + ganaScore + bhakootScore + nadiScore

        // Mangal Dosha check (Mars in houses 1, 2, 4, 7, 8, 12 from Lagna)
        val boyMars = boyChart.planets.first { it.planet == Planet.MARS }
        val girlMars = girlChart.planets.first { it.planet == Planet.MARS }
        val mangalHouses = listOf(1, 2, 4, 7, 8, 12)
        val boyManglik = boyMars.house in mangalHouses
        val girlManglik = girlMars.house in mangalHouses

        val mangalStatus = when {
            boyManglik && girlManglik -> "Both are Manglik (Dosha Cancelled / Shamana)"
            !boyManglik && !girlManglik -> "Neither is Manglik (Free from Mangal Dosha)"
            boyManglik && !girlManglik -> "Groom is Manglik (Kuja Dosha Remedies Suggested)"
            else -> "Bride is Manglik (Kuja Dosha Remedies Suggested)"
        }

        val recommendation = when {
            total >= 28 -> "Excellent Match (Uttam Guna Milan) - Highly Auspicious"
            total >= 21 -> "Good Match (Madhyam Guna Milan) - Favorable Union"
            total >= 18 -> "Average Match (Samanya) - Marriage Acceptable with Remedies"
            else -> "Below Threshold (Doshas Present) - Astrological Remedies Required"
        }

        val summary = "Guna Milan Score: ${String.format("%.1f", total)}/36. " +
                "Nadi compatibility is ${if (nadiScore > 0) "harmonious" else "afflicted with Nadi Dosha"}. " +
                "Bhakoot is ${if (bhakootScore > 0) "auspicious" else "neutralized"}. $mangalStatus."

        return AshtakootResult(
            varnaScore = varnaScore,
            vashyaScore = vashyaScore,
            taraScore = taraScore,
            yoniScore = yoniScore,
            maitriScore = maitriScore,
            ganaScore = ganaScore,
            bhakootScore = bhakootScore,
            nadiScore = nadiScore,
            totalScore = total,
            boyManglik = boyManglik,
            girlManglik = girlManglik,
            mangalDoshaStatus = mangalStatus,
            recommendation = recommendation,
            detailedSummary = summary
        )
    }

    private fun getVarnaRank(sign: ZodiacSign): Int {
        return when (sign) {
            ZodiacSign.CANCER, ZodiacSign.SCORPIO, ZodiacSign.PISCES -> 4 // Brahmin
            ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SAGITTARIUS -> 3 // Kshatriya
            ZodiacSign.TAURUS, ZodiacSign.VIRGO, ZodiacSign.CAPRICORN -> 2 // Vaishya
            ZodiacSign.GEMINI, ZodiacSign.LIBRA, ZodiacSign.AQUARIUS -> 1 // Shudra
        }
    }

    private fun calculateVarna(boySign: ZodiacSign, girlSign: ZodiacSign): Double {
        val bRank = getVarnaRank(boySign)
        val gRank = getVarnaRank(girlSign)
        return if (bRank >= gRank) 1.0 else 0.0
    }

    private fun getVashyaCategory(sign: ZodiacSign): String {
        return when (sign) {
            ZodiacSign.ARIES, ZodiacSign.TAURUS -> "Chatushpada"
            ZodiacSign.GEMINI, ZodiacSign.VIRGO, ZodiacSign.LIBRA, ZodiacSign.AQUARIUS -> "Manava"
            ZodiacSign.CANCER, ZodiacSign.PISCES, ZodiacSign.CAPRICORN -> "Jalachara"
            ZodiacSign.LEO -> "Vanchara"
            ZodiacSign.SCORPIO -> "Keeta"
            ZodiacSign.SAGITTARIUS -> "Manava"
        }
    }

    private fun calculateVashya(boySign: ZodiacSign, girlSign: ZodiacSign): Double {
        val bV = getVashyaCategory(boySign)
        val gV = getVashyaCategory(girlSign)
        if (bV == gV) return 2.0
        if (bV == "Vanchara" || gV == "Vanchara") return 0.5
        return 1.0
    }

    private fun calculateTara(boyNak: Nakshatra, girlNak: Nakshatra): Double {
        val countGtoB = ((boyNak.number - girlNak.number + 27) % 9) + 1
        val countBtoG = ((girlNak.number - boyNak.number + 27) % 9) + 1

        val badTaras = listOf(1, 3, 5, 7) // Janma, Vipat, Pratyak, Vadha
        val gToBGood = countGtoB !in badTaras
        val bToGGood = countBtoG !in badTaras

        return when {
            gToBGood && bToGGood -> 3.0
            gToBGood || bToGGood -> 1.5
            else -> 0.0
        }
    }

    private fun calculateYoni(boyNak: Nakshatra, girlNak: Nakshatra): Double {
        val bAnimal = boyNak.yoni
        val gAnimal = girlNak.yoni
        if (bAnimal == gAnimal) return 4.0

        val enemyPairs = listOf(
            setOf("Horse", "Buffalo"),
            setOf("Elephant", "Lion"),
            setOf("Sheep", "Monkey"),
            setOf("Serpent", "Mongoose"),
            setOf("Dog", "Deer"),
            setOf("Cat", "Rat"),
            setOf("Cow", "Tiger")
        )

        val pair = setOf(bAnimal, gAnimal)
        if (pair in enemyPairs) return 0.0

        val friendlyPairs = listOf(
            setOf("Horse", "Elephant"),
            setOf("Serpent", "Cat"),
            setOf("Goat", "Monkey"),
            setOf("Cow", "Deer")
        )
        if (pair in friendlyPairs) return 3.0
        return 2.0
    }

    private fun calculateGrahaMaitri(lord1: Planet, lord2: Planet): Double {
        if (lord1 == lord2) return 5.0

        val friendMap = mapOf(
            Planet.SUN to listOf(Planet.MOON, Planet.MARS, Planet.JUPITER),
            Planet.MOON to listOf(Planet.SUN, Planet.MERCURY),
            Planet.MARS to listOf(Planet.SUN, Planet.MOON, Planet.JUPITER),
            Planet.MERCURY to listOf(Planet.SUN, Planet.VENUS),
            Planet.JUPITER to listOf(Planet.SUN, Planet.MOON, Planet.MARS),
            Planet.VENUS to listOf(Planet.MERCURY, Planet.SATURN),
            Planet.SATURN to listOf(Planet.MERCURY, Planet.VENUS)
        )

        val lord1Friends = friendMap[lord1] ?: emptyList()
        val lord2Friends = friendMap[lord2] ?: emptyList()

        val l1SeesL2Friend = lord2 in lord1Friends
        val l2SeesL1Friend = lord1 in lord2Friends

        return when {
            l1SeesL2Friend && l2SeesL1Friend -> 5.0
            l1SeesL2Friend || l2SeesL1Friend -> 4.0
            else -> 1.0
        }
    }

    private fun calculateGana(bGana: String, gGana: String): Double {
        if (bGana == gGana) return 6.0
        if (bGana == "Deva" && gGana == "Manushya") return 6.0
        if (bGana == "Manushya" && gGana == "Deva") return 5.0
        if (bGana == "Rakshasa" || gGana == "Rakshasa") return 0.0
        return 1.0
    }

    private fun calculateBhakoot(boySign: ZodiacSign, girlSign: ZodiacSign): Double {
        val diff = Math.abs(boySign.number - girlSign.number)
        val distance = if (diff > 6) 12 - diff else diff
        // 2/12, 6/8, 9/5 causes Bhakoot Dosha
        return when (distance) {
            1 -> 0.0 // 2/12
            5 -> 0.0 // 6/8
            4 -> 0.0 // 9/5
            else -> 7.0
        }
    }

    private fun calculateNadi(bNadi: String, gNadi: String): Double {
        return if (bNadi != gNadi) 8.0 else 0.0
    }
}
