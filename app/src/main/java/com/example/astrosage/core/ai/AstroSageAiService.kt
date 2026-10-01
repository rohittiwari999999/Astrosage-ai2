package com.example.astrosage.core.ai

import com.example.BuildConfig
import com.example.astrosage.core.astronomy.KundaliChartData
import com.example.astrosage.core.astronomy.Planet
import com.example.astrosage.core.astronomy.ZodiacSign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class StrengthsAndWeaknesses(
    val positivePlanets: List<String> = emptyList(),
    val afflictedPlanets: List<String> = emptyList()
)

data class AstrologicalRemedies(
    val gemstone: String = "",
    val mantra: String = "",
    val rudraksha: String = ""
)

data class KundaliAiReading(
    val lagnaAnalysis: String,
    val currentDashaPrediction: String,
    val strengthsAndWeaknesses: StrengthsAndWeaknesses,
    val remedies: AstrologicalRemedies,
    val isAiGenerated: Boolean = true
)

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "acharya"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

object AstroSageAiService {

    private const val PRIMARY_MODEL = "gemini-3.1-flash-lite-preview"
    private const val SECONDARY_MODEL = "gemini-3.8-flash"
    private const val TERTIARY_MODEL = "gemini-3.5-flash"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun getEffectiveApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") {
                key
            } else {
                System.getenv("GEMINI_API_KEY") ?: ""
            }
        } catch (e: Exception) {
            System.getenv("GEMINI_API_KEY") ?: ""
        }
    }

    private fun callGeminiApi(requestJson: JSONObject, apiKey: String): String? {
        val models = listOf(PRIMARY_MODEL, SECONDARY_MODEL, TERTIARY_MODEL)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        for (model in models) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBodyStr = response.body?.string() ?: ""

                if (response.isSuccessful && responseBodyStr.isNotBlank()) {
                    val rootObj = JSONObject(responseBodyStr)
                    val candidates = rootObj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.getJSONObject("content")
                        val parts = content.getJSONArray("parts")
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotBlank()) {
                            return text
                        }
                    }
                }
            } catch (e: Exception) {
                // Try next fallback model
            }
        }
        return null
    }

    private const val SYSTEM_INSTRUCTION = """You are an expert Vedic Astrologer following Parashari and KP astrology principles.
Analyze the provided planetary position JSON data (Planets, Signs, Houses, Dasha, Aspects) 
and return detailed, highly accurate predictions.

Constraints:
1. Do not recalculate degrees; strictly use provided coordinates.
2. Return strictly valid JSON adhering to the specified schema.
3. Keep tone respectful, traditional yet practical and advisory."""

    suspend fun generatePrediction(planetaryJson: String, chartData: KundaliChartData): KundaliAiReading =
        withContext(Dispatchers.IO) {
            val apiKey = getEffectiveApiKey()

            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // If API key is not yet set in Secrets panel, generate comprehensive Vedic rule-based reading
                return@withContext generateSynthesizedVedicReading(chartData)
            }

            try {
                val promptText = """Analyze this Vedic Kundali planetary chart data and generate a comprehensive astrological reading with remediation suggestions.
Chart JSON:
$planetaryJson

Return STRICTLY a JSON object with this exact schema:
{
  "lagna_analysis": "Detailed analysis of Ascendant, character, vitality, life path, and planetary aspects on 1st house",
  "current_dasha_prediction": "Detailed prediction for the current active Mahadasha and Antardasha period, favorable timing and warnings",
  "strengths_and_weaknesses": {
    "positive_planets": ["List of strong, exalted, or well-placed planets with house and effect"],
    "afflicted_planets": ["List of afflicted, combust, debilitated, or malefic planets with remedial attention"]
  },
  "remedies": {
    "gemstone": "Recommended primary gemstone with finger and metal",
    "mantra": "Prescribed Vedic or Beej mantra with repetition count",
    "rudraksha": "Recommended Mukhi Rudraksha with benefits"
  }
}"""

                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArray = JSONArray().apply {
                                put(JSONObject().put("text", promptText))
                            }
                            put("parts", partsArray)
                        }
                        put(contentObj)
                    }
                    put("contents", contentsArray)

                    val sysContent = JSONObject().apply {
                        val sysParts = JSONArray().apply {
                            put(JSONObject().put("text", SYSTEM_INSTRUCTION))
                        }
                        put("parts", sysParts)
                    }
                    put("systemInstruction", sysContent)

                    val genConfig = JSONObject().apply {
                        put("temperature", 0.4)
                        put("responseMimeType", "application/json")
                    }
                    put("generationConfig", genConfig)
                }

                val rawText = callGeminiApi(requestJson, apiKey)
                if (rawText != null) {
                    parseAiJson(rawText, chartData)
                } else {
                    generateSynthesizedVedicReading(chartData)
                }
            } catch (e: Exception) {
                generateSynthesizedVedicReading(chartData)
            }
        }

    suspend fun askAstrologer(
        chartData: KundaliChartData,
        chatHistory: List<ChatMessage>,
        question: String,
        isHindi: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateRuleBasedChatAnswer(chartData, question, isHindi)
        }

        try {
            val hindiConstraint = if (isHindi) {
                """
                CRITICAL MANDATORY INSTRUCTION:
                The user's app language is set to HINDI.
                You MUST answer EXCLUSIVELY and 100% in pure HINDI (Devanagari script).
                Do NOT use any English words or Roman script transliteration.
                All names of rashis, grahas, bhavas, nakshatras, dashas, and remedies must be in pure Hindi only.
                """.trimIndent()
            } else ""

            val systemInstruction = """You are Acharya AI, a revered and compassionate Vedic Astrologer rooted in Parashari, Jaimini, and KP systems.
You have the user's exact astronomical birth chart coordinates:
${chartData.planetaryJson}

$hindiConstraint

Answer the user's specific question with deep astrological insight, naming the relevant Houses, Planet positions, Current Mahadasha (${chartData.currentMahadasha.sanskritName}), and Antardasha (${chartData.currentAntardasha.sanskritName}).
Be encouraging, traditional yet practical, and suggest actionable Vedic remedies (dana, mantra, lifestyle alignments). Keep answers structured and engaging."""

            val contentsArray = JSONArray()

            // Filter history to ensure alternating user/model turns without duplicate trailing question
            val historyWithoutCurrent = if (chatHistory.lastOrNull()?.text == question) {
                chatHistory.dropLast(1)
            } else {
                chatHistory
            }

            var expectedRole = "user"
            for (msg in historyWithoutCurrent.takeLast(6)) {
                val role = if (msg.sender == "user") "user" else "model"
                if (role == expectedRole) {
                    val cObj = JSONObject().apply {
                        put("role", role)
                        val pArray = JSONArray().apply {
                            put(JSONObject().put("text", msg.text))
                        }
                        put("parts", pArray)
                    }
                    contentsArray.put(cObj)
                    expectedRole = if (role == "user") "model" else "user"
                }
            }

            // Current question as the final user turn
            val currentMsg = JSONObject().apply {
                put("role", "user")
                val pArray = JSONArray().apply {
                    put(JSONObject().put("text", question))
                }
                put("parts", pArray)
            }
            contentsArray.put(currentMsg)

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                val sysContent = JSONObject().apply {
                    val sysParts = JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    }
                    put("parts", sysParts)
                }
                put("systemInstruction", sysContent)
                val genConfig = JSONObject().apply {
                    put("temperature", 0.6)
                }
                put("generationConfig", genConfig)
            }

            val answer = callGeminiApi(requestJson, apiKey)
            if (!answer.isNullOrBlank()) {
                answer
            } else {
                generateRuleBasedChatAnswer(chartData, question, isHindi)
            }
        } catch (e: Exception) {
            generateRuleBasedChatAnswer(chartData, question, isHindi)
        }
    }

    private fun parseAiJson(rawText: String, chartData: KundaliChartData): KundaliAiReading {
        return try {
            val cleanJson = rawText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleanJson)
            val lagnaAnalysis = json.optString("lagna_analysis", "")
            val dashaPrediction = json.optString("current_dasha_prediction", "")

            val snw = json.optJSONObject("strengths_and_weaknesses")
            val posList = mutableListOf<String>()
            val affList = mutableListOf<String>()

            snw?.optJSONArray("positive_planets")?.let { arr ->
                for (i in 0 until arr.length()) posList.add(arr.getString(i))
            }
            snw?.optJSONArray("afflicted_planets")?.let { arr ->
                for (i in 0 until arr.length()) affList.add(arr.getString(i))
            }

            val rem = json.optJSONObject("remedies")
            val gemstone = rem?.optString("gemstone", "Yellow Sapphire or Pearl based on ascendant lord") ?: ""
            val mantra = rem?.optString("mantra", "Om Namah Shivaya 108 times daily") ?: ""
            val rudraksha = rem?.optString("rudraksha", "5 Mukhi Rudraksha") ?: ""

            KundaliAiReading(
                lagnaAnalysis = lagnaAnalysis,
                currentDashaPrediction = dashaPrediction,
                strengthsAndWeaknesses = StrengthsAndWeaknesses(posList, affList),
                remedies = AstrologicalRemedies(gemstone, mantra, rudraksha),
                isAiGenerated = true
            )
        } catch (e: Exception) {
            generateSynthesizedVedicReading(chartData)
        }
    }

    fun generateSynthesizedVedicReading(chartData: KundaliChartData): KundaliAiReading {
        val lagna = chartData.lagna
        val sun = chartData.planets.first { it.planet == Planet.SUN }
        val moon = chartData.planets.first { it.planet == Planet.MOON }
        val jupiter = chartData.planets.first { it.planet == Planet.JUPITER }
        val mars = chartData.planets.first { it.planet == Planet.MARS }

        val lagnaAnalysis = "Your Ascendant (Lagna) is ${lagna.sign.sanskritName} (${lagna.sign.englishName}) at ${String.format("%.2f", lagna.signDegree)}° in ${lagna.nakshatra.englishName} Nakshatra (Pada ${lagna.pada}). " +
                "The Lagna Lord is ${lagna.sign.ruler.sanskritName}. ${lagna.sign.englishName} Lagna bestows leadership instincts, vitality, resilience, and a purposeful presence. " +
                "Moon placed in ${moon.sign.sanskritName} gives you a reflective, intuitive mindset with sharp cognitive faculties. " +
                "Sun in ${sun.sign.sanskritName} highlights personal authority, dignity, and career ambition."

        val dashaPrediction = "You are currently running the Mahadasha of ${chartData.currentMahadasha.sanskritName} (${chartData.currentMahadasha.englishName}) " +
                "with ${chartData.currentAntardasha.sanskritName} Antardasha. " +
                "This dasha period activates karmic themes related to the houses ruled by ${chartData.currentMahadasha.sanskritName}. " +
                "Focus on career consolidation, disciplined daily routines, and spiritual grounding to harness this planetary energy to its highest potential."

        val positivePlanets = mutableListOf<String>()
        val afflictedPlanets = mutableListOf<String>()

        for (p in chartData.planets) {
            when {
                p.dignity.contains("Exalted") || p.dignity.contains("Own") || p.dignity.contains("Moolatrikona") -> {
                    positivePlanets.add("${p.planet.sanskritName} (${p.planet.englishName}) in House ${p.house} (${p.sign.sanskritName}) - ${p.dignity}")
                }
                p.dignity.contains("Debilitated") || p.dignity.contains("Enemy") || p.isRetrograde -> {
                    afflictedPlanets.add("${p.planet.sanskritName} (${p.planet.englishName}) in House ${p.house} (${p.sign.sanskritName}) - ${p.dignity}${if (p.isRetrograde) ", Vakri (Retrograde)" else ""}")
                }
                else -> {
                    positivePlanets.add("${p.planet.sanskritName} in House ${p.house} - Well aligned")
                }
            }
        }

        val (gem, mantra, rudra) = when (lagna.sign.ruler) {
            Planet.MARS -> Triple("Red Coral (Moonga) in copper or gold on ring finger", "Om Kram Kreem Kroum Sah Bhaumaya Namah (108 times)", "3 Mukhi Rudraksha")
            Planet.VENUS -> Triple("Diamond or White Sapphire (Safed Pukhraj) in silver", "Om Shum Shukraya Namah (108 times on Friday)", "6 Mukhi Rudraksha")
            Planet.MERCURY -> Triple("Emerald (Panna) in gold or bronze on little finger", "Om Bum Budhaya Namah (108 times on Wednesday)", "4 Mukhi Rudraksha")
            Planet.MOON -> Triple("Natural Pearl (Moti) in silver on little finger", "Om Shram Shreem Shroum Sah Chandraya Namah", "2 Mukhi Rudraksha")
            Planet.SUN -> Triple("Ruby (Manikya) in gold on ring finger", "Om Hram Hreem Hroum Sah Suryaya Namah (108 times at sunrise)", "1 Mukhi or 12 Mukhi Rudraksha")
            Planet.JUPITER -> Triple("Yellow Sapphire (Pukhraj) in gold on index finger", "Om Gram Greem Groum Sah Gurave Namah (108 times)", "5 Mukhi Rudraksha")
            Planet.SATURN -> Triple("Blue Sapphire (Neelam) or Amethyst in silver/iron on middle finger", "Om Sham Shanaishcharaya Namah (108 times on Saturday evening)", "7 Mukhi or 14 Mukhi Rudraksha")
            else -> Triple("Yellow Sapphire in gold", "Om Namah Shivaya (108 times daily)", "5 Mukhi Rudraksha")
        }

        return KundaliAiReading(
            lagnaAnalysis = lagnaAnalysis,
            currentDashaPrediction = dashaPrediction,
            strengthsAndWeaknesses = StrengthsAndWeaknesses(
                positivePlanets = positivePlanets.take(4),
                afflictedPlanets = afflictedPlanets.take(4)
            ),
            remedies = AstrologicalRemedies(
                gemstone = gem,
                mantra = mantra,
                rudraksha = rudra
            ),
            isAiGenerated = false
        )
    }

    private fun generateRuleBasedChatAnswer(chartData: KundaliChartData, question: String, isHindi: Boolean = false): String {
        val qLower = question.lowercase()
        val isHindiQuery = qLower.contains("kya") || qLower.contains("kaise") || qLower.contains("shadi") ||
                qLower.contains("vivah") || qLower.contains("naukri") || qLower.contains("paisa") ||
                qLower.contains("dhan") || qLower.contains("upay") || qLower.contains("swasthya") ||
                qLower.contains("batao") || qLower.contains("hoga") || qLower.contains("lagna") ||
                qLower.contains("kismat") || qLower.contains("graha") || qLower.contains("kar") ||
                qLower.contains("क") || qLower.contains("म") || qLower.contains("य")
        val forceHindi = isHindi || isHindiQuery

        val lagnaLord = chartData.lagna.sign.ruler
        val moon = chartData.planets.first { it.planet == Planet.MOON }
        val sun = chartData.planets.first { it.planet == Planet.SUN }
        val mars = chartData.planets.first { it.planet == Planet.MARS }
        val jupiter = chartData.planets.first { it.planet == Planet.JUPITER }
        val saturn = chartData.planets.first { it.planet == Planet.SATURN }
        val venus = chartData.planets.first { it.planet == Planet.VENUS }

        return when {
            // Career / Job / Business
            qLower.contains("career") || qLower.contains("job") || qLower.contains("business") ||
                    qLower.contains("work") || qLower.contains("naukri") || qLower.contains("vyapar") ||
                    qLower.contains("करियर") || qLower.contains("नौकरी") || qLower.contains("व्यवसाय") -> {
                if (forceHindi) {
                    """
                    💼 **आचार्य AI आजीविका व आजीविका विश्लेषण**:
                    • **दशम भाव (कर्म भाव)**: आपके ${chartData.lagna.sign.sanskritName} लग्न के अनुसार सूर्य देव ${sun.sign.sanskritName} राशि (भाव ${sun.house}) में तथा शनि देव ${saturn.sign.sanskritName} राशि में स्थित हैं।
                    • **सक्रिय महादशा**: वर्तमान में आपकी ${chartData.currentMahadasha.sanskritName} महादशा एवं ${chartData.currentAntardasha.sanskritName} अंतर्दशा चल रही है, जो कार्यक्षेत्र में सतत प्रयास से शुभ परिणाम देगी।
                    • **ज्योतिषीय मार्गदर्शन**: प्रशासनिक, प्रबंधकीय अथवा तकनीकी क्षेत्र में आपकी सफलता के प्रबल योग हैं।
                    • **अचूक वैदिक उपाय**: नित्य प्रातःकाल तांबे के पात्र से भगवान सूर्य को अर्घ्य दें और 'आदित्य हृदय स्तोत्र' का श्रद्धापूर्वक पाठ करें।
                    """.trimIndent()
                } else {
                    """
                    💼 **Acharya AI Career & Profession Analysis**:
                    • **10th House (Karma Bhava)**: In your ${chartData.lagna.sign.englishName} Ascendant chart, Sun occupies ${sun.sign.englishName} (House ${sun.house}) and Saturn is in ${saturn.sign.englishName} (House ${saturn.house}).
                    • **Active Dasha Influence**: You are currently operating under ${chartData.currentMahadasha.sanskritName} Mahadasha with ${chartData.currentAntardasha.sanskritName} Antardasha, supporting steady professional recognition.
                    • **Strategic Guidance**: Roles involving strategy, administration, or advisory bring substantial success.
                    • **Vedic Remedy**: Offer water to Surya Dev every morning at sunrise in a copper vessel and chant Aditya Hridaya Stotram.
                    """.trimIndent()
                }
            }

            // Marriage / Love / Relationships
            qLower.contains("marriage") || qLower.contains("relationship") || qLower.contains("love") ||
                    qLower.contains("spouse") || qLower.contains("partner") || qLower.contains("shadi") || qLower.contains("vivah") ||
                    qLower.contains("विवाह") || qLower.contains("शादी") || qLower.contains("जीवनसाथी") -> {
                val isManglik = mars.house in listOf(1, 2, 4, 7, 8, 12)
                if (forceHindi) {
                    val mangalStr = if (isManglik) "मंगल देव भाव ${mars.house} में स्थित हैं (आंशिक मांगलिक प्रभाव), अतः जीवनसाथी के साथ सामंजस्य व संवाद बनाए रखें।" else "आपकी जन्मपत्रिका में कोई गंभीर मंगल दोष नहीं है।"
                    """
                    💍 **आचार्य AI विवाह एवं दांपत्य विश्लेषण**:
                    • **सप्तम भाव (कलत्र भाव)**: आपका विवाह भाव तथा प्रेम के कारक शुक्र देव ${venus.sign.sanskritName} (भाव ${venus.house}) में विराजमान हैं।
                    • **मांगलिक स्थिति**: $mangalStr
                    • **विवाह समय संकेत**: वर्तमान ${chartData.currentMahadasha.sanskritName} महादशा शुभ संबंधों के अवसर प्रदान कर रही है।
                    • **अचूक वैदिक उपाय**: प्रत्येक शुक्रवार मां महालक्ष्मी को खीर अथवा श्वेत मिष्ठान्न का भोग अर्पित करें और 'ॐ शुं शुक्राय नमः' का 108 बार जप करें।
                    """.trimIndent()
                } else {
                    val mangalStr = if (isManglik) "Mars is stationed in House ${mars.house}, advising mutual patience and open communication." else "Your natal chart is free from severe Kuja (Mangal) Dosha."
                    """
                    💍 **Acharya AI Marriage & Relationship Insights**:
                    • **7th House (Kalatra Bhava)**: Your marriage sector and natural karaka Venus are positioned in ${venus.sign.englishName} (House ${venus.house}).
                    • **Manglik Assessment**: $mangalStr
                    • **Timing Horizon**: Your active ${chartData.currentMahadasha.sanskritName} Mahadasha activates auspicious relationship connections.
                    • **Vedic Remedy**: Worship Goddess Mahalakshmi on Fridays and chant 'Om Shum Shukraya Namah' 108 times for harmonious bliss.
                    """.trimIndent()
                }
            }

            // Wealth / Money / Finance
            qLower.contains("money") || qLower.contains("wealth") || qLower.contains("finance") ||
                    qLower.contains("rich") || qLower.contains("paisa") || qLower.contains("dhan") || qLower.contains("income") ||
                    qLower.contains("धन") || qLower.contains("पैसा") || qLower.contains("लक्ष्मी") -> {
                if (forceHindi) {
                    """
                    💰 **आचार्य AI धन व आर्थिक समृद्धि विश्लेषण**:
                    • **द्वितीय एवं एकादश भाव**: धन के नैसर्गिक कारक देवगुरु बृहस्पति ${jupiter.sign.sanskritName} (भाव ${jupiter.house}) में स्थित हैं।
                    • **दशा प्रभाव**: ${chartData.currentMahadasha.sanskritName}-${chartData.currentAntardasha.sanskritName} काल में संचित धन और निवेश में दीर्घकालिक स्थिरता आएगी।
                    • **आर्थिक सलाह**: सट्टेबाजी या जल्दबाजी में जोखिम लेने से बचें; अनुशासित निवेश करें।
                    • **अचूक वैदिक उपाय**: गुरुवार को गाय को भीगी चने की दाल व गुड़ खिलाएं और घर के उत्तर-पूर्व (ईशान) कोण को स्वच्छ रखें।
                    """.trimIndent()
                } else {
                    """
                    💰 **Acharya AI Wealth & Financial Guidance**:
                    • **2nd (Dhana) & 11th (Labha) Houses**: Guru (Jupiter), the universal karaka of prosperity, resides in ${jupiter.sign.englishName} (House ${jupiter.house}).
                    • **Dasha Flow**: Your ongoing ${chartData.currentMahadasha.sanskritName}-${chartData.currentAntardasha.sanskritName} cycle favors consistent compounding and asset acquisition.
                    • **Vedic Remedy**: Feed cows with soaked lentils and jaggery on Thursdays, and keep the North-East zone of your home luminous and clean.
                    """.trimIndent()
                }
            }

            // Health / Doshas / Shani / Sade Sati
            qLower.contains("health") || qLower.contains("sade sati") || qLower.contains("disease") ||
                    qLower.contains("swasthya") || qLower.contains("dosha") || qLower.contains("shani") || qLower.contains("upay") ||
                    qLower.contains("स्वास्थ्य") || qLower.contains("दोष") || qLower.contains("शनि") || qLower.contains("उपाय") -> {
                if (forceHindi) {
                    """
                    🛡️ **आचार्य AI स्वास्थ्य, दोष एवं वैदिक शांति उपाय**:
                    • **लग्न व चंद्र बल**: लग्न स्वामी ${lagnaLord.sanskritName} तथा चंद्र देव ${moon.sign.sanskritName} राशि में स्थित होकर आपके शारीरिक व मानसिक स्वास्थ्य को नियंत्रित करते हैं।
                    • **शनि प्रभाव**: शनि देव ${saturn.sign.sanskritName} में स्थित हैं। नियमित दिनचर्या, ध्यान और योग आवश्यक है।
                    • **दोष निवारण**: यदि किसी ग्रह की प्रतिकूलता अनुभव हो तो नियमित शिव उपासना सर्वोत्तम कवच है।
                    • **अचूक वैदिक उपाय**: नित्य 'महामृत्युंजय मंत्र' का 11 बार जप करें तथा शनिवार को पीपल के वृक्ष के नीचे तिल के तेल का दीपक प्रज्वलित करें।
                    """.trimIndent()
                } else {
                    """
                    🛡️ **Acharya AI Health, Dosha & Remedial Insights**:
                    • **Vitality Core**: Your Ascendant Lord ${lagnaLord.sanskritName} and Moon in ${moon.sign.englishName} anchor physical stamina and mental equanimity.
                    • **Saturn's Lesson**: Saturn in ${saturn.sign.englishName} advises disciplined rest, hydration, and regular meditative routines.
                    • **Vedic Remedy**: Chant the Maha Mrityunjaya Mantra 11 times daily, and light a sesame oil lamp under a Peepal tree on Saturday evenings.
                    """.trimIndent()
                }
            }

            // Gemstones / Lucky points
            qLower.contains("gemstone") || qLower.contains("ratna") || qLower.contains("lucky") || qLower.contains("stone") ||
                    qLower.contains("रत्न") || qLower.contains("रूबी") || qLower.contains("पन्ना") || qLower.contains("पुखराज") -> {
                val (gemNameHi, metalHi, fingerHi) = when (lagnaLord) {
                    Planet.MARS -> Triple("लाल मूंगा", "तांबा अथवा सोना", "अनामिका उंगली")
                    Planet.VENUS -> Triple("हीरा अथवा श्वेत पुखराज", "चांदी अथवा प्लैटिनम", "मध्यमा अथवा कनिष्ठिका उंगली")
                    Planet.MERCURY -> Triple("पन्ना", "सोना अथवा कांस्य", "कनिष्ठिका उंगली")
                    Planet.MOON -> Triple("सच्चा मोती", "चांदी", "कनिष्ठिका उंगली")
                    Planet.SUN -> Triple("माणिक्य", "सोना अथवा तांबा", "अनामिका उंगली")
                    Planet.JUPITER -> Triple("पीला पुखराज", "सोना", "तर्जनी उंगली")
                    Planet.SATURN -> Triple("नीलम अथवा कटैला", "चांदी अथवा पंचधातु", "मध्यमा उंगली")
                    else -> Triple("पीला पुखराज", "सोना", "तर्जनी उंगली")
                }
                val (gemName, metal, finger) = when (lagnaLord) {
                    Planet.MARS -> Triple("Red Coral (Moonga)", "Copper or Gold", "Ring finger")
                    Planet.VENUS -> Triple("Diamond / White Sapphire", "Silver or Platinum", "Middle or Little finger")
                    Planet.MERCURY -> Triple("Emerald (Panna)", "Gold or Bronze", "Little finger")
                    Planet.MOON -> Triple("Natural Pearl (Moti)", "Silver", "Little finger")
                    Planet.SUN -> Triple("Ruby (Manikya)", "Gold or Copper", "Ring finger")
                    Planet.JUPITER -> Triple("Yellow Sapphire (Pukhraj)", "Gold", "Index finger")
                    Planet.SATURN -> Triple("Blue Sapphire (Neelam)", "Silver or Panchadhatu", "Middle finger")
                    else -> Triple("Yellow Sapphire (Pukhraj)", "Gold", "Index finger")
                }
                if (forceHindi) {
                    """
                    💎 **आचार्य AI अनुकूल रत्न एवं शुभ तत्त्व**:
                    • **लग्न स्वामी**: आपके लग्नेश ${lagnaLord.sanskritName} हैं।
                    • **प्रमुख जीवन रत्न**: $gemNameHi
                    • **धातु व उंगली**: इसे $metalHi में $fingerHi में शुभ मुहूर्त में धारण करना श्रेयस्कर है।
                    • **शुभ तत्त्व**: आपकी लग्न राशि का तत्त्व ${chartData.lagna.sign.element} है, जिसके अनुसार शुभ आचरण आपके भाग्य को बल प्रदान करता है।
                    """.trimIndent()
                } else {
                    """
                    💎 **Acharya AI Auspicious Gemstone & Lucky Tokens**:
                    • **Ascendant Lord**: Governed by ${lagnaLord.englishName} (${lagnaLord.sanskritName}).
                    • **Primary Life Gemstone**: $gemName
                    • **Metal & Finger**: To be set in $metal and worn on the $finger following sacred energization.
                    • **Auspicious Direction**: Aligns positively with your ruling element (${chartData.lagna.sign.element}).
                    """.trimIndent()
                }
            }

            // General / Default
            else -> {
                if (forceHindi) {
                    """
                    🕉️ **आचार्य AI कुंडली परामर्श**:
                    • **आपकी जन्म कुंडली**: ${chartData.lagna.sign.sanskritName} लग्न, ${moon.sign.sanskritName} चंद्र राशि, एवं नक्षत्र ${moon.nakshatra.sanskritName} (चरण ${moon.pada})।
                    • **वर्तमान समय चक्र**: आप इस समय ${chartData.currentMahadasha.sanskritName} महादशा एवं ${chartData.currentAntardasha.sanskritName} अंतर्दशा से गुजर रहे हैं।
                    • **आचार्य का संदेश**: अपने लग्नेश ${lagnaLord.sanskritName} को बलवान बनाएं। किसी भी नए कार्य को शुभ तिथि व नक्षत्र में आरंभ करें।
                    • **संकेत**: आप करियर, विवाह, धन, स्वास्थ्य या वैदिक उपायों में से किसी भी विषय पर विस्तार से पूछ सकते हैं!
                    """.trimIndent()
                } else {
                    """
                    🕉️ **Acharya AI Vedic Horoscope Consultation**:
                    • **Birth Signature**: ${chartData.lagna.sign.englishName} Ascendant, Moon in ${moon.sign.englishName} (${moon.nakshatra.englishName}), under active ${chartData.currentMahadasha.sanskritName} Mahadasha.
                    • **Planetary Alignment**: Your chart possesses notable strengths centered around your ruling lord ${lagnaLord.sanskritName}.
                    • **Guidance**: Align major decisions with favorable transit phases and strengthen your Lagna Lord.
                    • **Tip**: Feel free to ask specific questions regarding Career, Marriage, Wealth, Health, or Vedic Remedies!
                    """.trimIndent()
                }
            }
        }
    }
}
