package com.example.astrosage.core

data class ServiceItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val subtitleEn: String,
    val subtitleHi: String,
    val iconEmoji: String,
    val badgeColorHex: Long = 0xFFE65100
)

data class AstroReportItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val descEn: String,
    val descHi: String,
    val tagEn: String,
    val tagHi: String,
    val iconEmoji: String,
    val accentColorHex: Long
)

object AstroData {
    val quickServices = listOf(
        ServiceItem("kundali", "Kundali", "कुंडली", "Birth Chart D1/D9", "जन्म पत्रिका", "📜", 0xFFE65100),
        ServiceItem("matching", "Kundli Milan", "कुंडली मिलान", "36 Guna Matching", "36 गुण मिलान", "💑", 0xFFD81B60),
        ServiceItem("panchang", "Aaj Ka Panchang", "दैनिक पंचांग", "Tithi & Muhurat", "तिथि व मुहूर्त", "📅", 0xFF0288D1),
        ServiceItem("horoscope", "Rashifal", "दैनिक राशिफल", "12 Zodiac Forecast", "12 राशि भविष्य", "♈", 0xFF7B1FA2),
        ServiceItem("ai_astrologer", "Ask AI", "एआई ज्योतिषी", "Gemini Vedic Chat", "वैदिक परामर्श", "🤖", 0xFFFF8F00),
        ServiceItem("sade_sati", "Sade Sati", "साढ़े साती", "Shani Impact & Dates", "शनि साढ़े साती", "🪐", 0xFF455A64),
        ServiceItem("dasha", "Dasha Phal", "दशा फल", "Vimshottari Timeline", "विंशोत्तरी दशा", "⏳", 0xFF388E3C),
        ServiceItem("lal_kitab", "Lal Kitab", "लाल किताब", "Remedies & Debts", "अचूक उपाय व ऋण", "📕", 0xFFC2185B),
        ServiceItem("gemstones", "Gemstones", "रत्न सलाह", "Lucky Stones", "भाग्यशाली रत्न", "💎", 0xFF0097A7),
        ServiceItem("varshphal", "Varshphal", "वर्षफल", "Annual Solar Return", "वार्षिक भविष्यफल", "🔮", 0xFF689F38),
        ServiceItem("numerology", "Numerology", "अंकशास्त्र", "Radix & Destiny", "मूलांक व भाग्यांक", "🔢", 0xFFEF6C00),
        ServiceItem("remedies", "Remedies", "वैदिक उपाय", "Mantra & Yantra", "शांति व निवारण", "🪔", 0xFFF57C00)
    )

    val astrologyReports = listOf(
        AstroReportItem(
            id = "life_report",
            titleEn = "Life Report (Jeevan Phal)",
            titleHi = "संपूर्ण जीवन रिपोर्ट",
            descEn = "Comprehensive analysis of Ascendant, major yogas, longevity, career & destiny.",
            descHi = "लग्न, प्रमुख योग, आयु, भाग्य एवं संपूर्ण जीवन का विस्तृत वैदिक विश्लेषण।",
            tagEn = "POPULAR",
            tagHi = "लोकप्रिय",
            iconEmoji = "🌟",
            accentColorHex = 0xFFFFB300
        ),
        AstroReportItem(
            id = "monthly_horoscope",
            titleEn = "Monthly Horoscope",
            titleHi = "मासिक राशिफल 2026",
            descEn = "Detailed monthly transits of Jupiter, Saturn, Rahu-Ketu and planetary impacts.",
            descHi = "गुरु, शनि, राहु-केतु के मासिक गोचर का आपकी राशि पर विस्तृत प्रभाव।",
            tagEn = "FREE",
            tagHi = "मुफ्त",
            iconEmoji = "📅",
            accentColorHex = 0xFF29B6F6
        ),
        AstroReportItem(
            id = "sade_sati_report",
            titleEn = "Shani Sade Sati Report",
            titleHi = "शनि साढ़े साती रिपोर्ट",
            descEn = "Rising, peak, and setting phases of Saturn with effective remedial measures.",
            descHi = "शनि की साढ़े साती के तीनों चरण, ढैय्या एवं अचूक शांति उपाय।",
            tagEn = "DETAILED",
            tagHi = "विस्तृत",
            iconEmoji = "⚡",
            accentColorHex = 0xFF7E57C2
        ),
        AstroReportItem(
            id = "career_report",
            titleEn = "Career & Job Prospects",
            titleHi = "करियर व नौकरी रिपोर्ट",
            descEn = "10th House analysis, suitable profession, promotion timings, and business yoga.",
            descHi = "दशम भाव, आजीविका के शुभ क्षेत्र, पदोन्नति समय व व्यापारिक योग।",
            tagEn = "PROMISES",
            tagHi = "सफलता",
            iconEmoji = "💼",
            accentColorHex = 0xFF26A69A
        ),
        AstroReportItem(
            id = "marriage_report",
            titleEn = "Marriage & Love Life",
            titleHi = "विवाह व दांपत्य सुख",
            descEn = "7th house analysis, spouse characteristics, marriage timing, and Manglik check.",
            descHi = "सप्तम भाव, जीवनसाथी का स्वरूप, विवाह का समय एवं मांगलिक विश्लेषण।",
            tagEn = "ACCURATE",
            tagHi = "सटीक",
            iconEmoji = "💍",
            accentColorHex = 0xFFEC407A
        ),
        AstroReportItem(
            id = "health_report",
            titleEn = "Health & Vitality Report",
            titleHi = "स्वास्थ्य व आरोग्य रिपोर्ट",
            descEn = "6th house & Lagna vitality, dietary recommendations, and Ayurvedic balance.",
            descHi = "षष्ठ भाव, लग्न बल, संभावित स्वास्थ्य समस्याएं एवं आयुर्वेदिक संतुलन।",
            tagEn = "WELLNESS",
            tagHi = "आरोग्य",
            iconEmoji = "🩺",
            accentColorHex = 0xFF66BB6A
        ),
        AstroReportItem(
            id = "wealth_report",
            titleEn = "Wealth & Dhana Yogas",
            titleHi = "धन व आर्थिक समृद्धि योग",
            descEn = "2nd & 11th houses analysis, Laxmi yoga, investment fortune, and savings.",
            descHi = "द्वितीय व एकादश भाव, लक्ष्मी योग, संपत्ति व धन संचय के शुभ योग।",
            tagEn = "PROSPERITY",
            tagHi = "समृद्धि",
            iconEmoji = "💰",
            accentColorHex = 0xFFFFCA28
        ),
        AstroReportItem(
            id = "remedies_report",
            titleEn = "Vedic Remedies & Mantras",
            titleHi = "वैदिक उपाय व रत्न सलाह",
            descEn = "Customized gemstones, Beej mantras, Rudraksha, and charitable remedies.",
            descHi = "कुंडली अनुसार अनुकूल रत्न, बीज मंत्र, रुद्राक्ष एवं विशेष दान उपाय।",
            tagEn = "REMEDIES",
            tagHi = "निवारण",
            iconEmoji = "🕉️",
            accentColorHex = 0xFFFF7043
        )
    )
}
