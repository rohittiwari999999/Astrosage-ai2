package com.example.astrosage.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.astrosage.core.AstroReportItem
import com.example.astrosage.core.ai.AstroSageAiService
import com.example.astrosage.core.ai.ChatMessage
import com.example.astrosage.core.ai.KundaliAiReading
import com.example.astrosage.core.astronomy.*
import com.example.astrosage.data.local.AstroDatabase
import com.example.astrosage.data.local.BirthProfileEntity
import com.example.astrosage.data.local.KundaliRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KundaliRepository
    val profiles: StateFlow<List<BirthProfileEntity>>

    // Language toggle: Hindi (true) vs English (false)
    private val _isHindi = MutableStateFlow(true)
    val isHindi: StateFlow<Boolean> = _isHindi.asStateFlow()

    private val _currentChart = MutableStateFlow<KundaliChartData?>(null)
    val currentChart: StateFlow<KundaliChartData?> = _currentChart.asStateFlow()

    private val _currentAiReading = MutableStateFlow<KundaliAiReading?>(null)
    val currentAiReading: StateFlow<KundaliAiReading?> = _currentAiReading.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatThinking = MutableStateFlow(false)
    val isChatThinking: StateFlow<Boolean> = _isChatThinking.asStateFlow()

    // Kundali Matching State
    private val _matchingResult = MutableStateFlow<AshtakootResult?>(null)
    val matchingResult: StateFlow<AshtakootResult?> = _matchingResult.asStateFlow()

    // Panchang State
    private val _panchangData = MutableStateFlow(
        PanchangEngine.calculatePanchang(Calendar.getInstance(), 28.6139, 77.2090)
    )
    val panchangData: StateFlow<PanchangData> = _panchangData.asStateFlow()

    // Active Selected Report dialog
    private val _selectedReport = MutableStateFlow<AstroReportItem?>(null)
    val selectedReport: StateFlow<AstroReportItem?> = _selectedReport.asStateFlow()

    init {
        val db = AstroDatabase.getDatabase(application)
        repository = KundaliRepository(db.kundaliDao())
        profiles = repository.allProfiles.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initDefaultProfileIfNeeded()
            // Load starter profile
            val starter = BirthDetails(
                name = "Aarav Sharma",
                gender = "Male",
                year = 1995,
                month = 5,
                day = 15,
                hour = 10,
                minute = 30,
                placeName = "New Delhi, India",
                latitude = 28.6139,
                longitude = 77.2090,
                timezoneOffsetHours = 5.5
            )
            calculateChart(starter)
        }
    }

    fun toggleLanguage() {
        _isHindi.value = !_isHindi.value
        val chart = _currentChart.value
        if (chart != null && _chatMessages.value.size <= 1) {
            initChatWelcome(chart)
        }
    }

    fun setLanguage(hindi: Boolean) {
        _isHindi.value = hindi
        val chart = _currentChart.value
        if (chart != null && _chatMessages.value.size <= 1) {
            initChatWelcome(chart)
        }
    }

    fun openReport(report: AstroReportItem) {
        _selectedReport.value = report
    }

    fun closeReport() {
        _selectedReport.value = null
    }

    fun calculateChart(birthDetails: BirthDetails) {
        val chart = EphemerisEngine.calculateKundali(birthDetails)
        _currentChart.value = chart
        initChatWelcome(chart)
        // Generate AI Prediction
        fetchAiPrediction(chart)
    }

    fun initChatWelcome(chart: KundaliChartData) {
        val moon = chart.planets.find { it.planet == com.example.astrosage.core.astronomy.Planet.MOON } ?: chart.lagna
        val welcomeText = if (_isHindi.value) {
            "नमस्ते ${chart.birthDetails.name} जी! 🙏 मैं आपका आचार्य AI एस्ट्रो सहायक हूँ। आपकी जन्म कुंडली (${chart.lagna.sign.sanskritName} लग्न, ${moon.sign.sanskritName} चंद्र राशि, ${chart.currentMahadasha.sanskritName} महादशा) का पूर्ण अध्ययन कर लिया गया है। आप मुझसे करियर, विवाह, धन, स्वास्थ्य, या वैदिक ग्रह उपायों से जुड़ा कोई भी प्रश्न पूछ सकते हैं।"
        } else {
            "Namaste ${chart.birthDetails.name}! 🙏 I am your Acharya AI Astro Assistant. I have analyzed your natal chart (${chart.lagna.sign.englishName} Ascendant, ${moon.sign.englishName} Moon, under active ${chart.currentMahadasha.sanskritName} Mahadasha). How may I guide you today? Feel free to ask about your career, marriage, finances, health, or astrological remedies."
        }
        _chatMessages.value = listOf(
            ChatMessage(
                id = "welcome_msg",
                sender = "acharya",
                text = welcomeText
            )
        )
    }

    fun clearChat() {
        val chart = _currentChart.value ?: return
        initChatWelcome(chart)
    }

    fun selectProfile(profile: BirthProfileEntity) {
        calculateChart(profile.toBirthDetails())
    }

    fun saveNewProfile(birthDetails: BirthDetails, relation: String = "Self") {
        viewModelScope.launch {
            val entity = BirthProfileEntity.fromBirthDetails(birthDetails, relation)
            repository.saveProfile(entity)
            calculateChart(birthDetails)
        }
    }

    fun deleteProfile(profile: BirthProfileEntity) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }

    fun fetchAiPrediction(chart: KundaliChartData? = _currentChart.value) {
        val targetChart = chart ?: return
        viewModelScope.launch {
            _isAiLoading.value = true
            try {
                val reading = AstroSageAiService.generatePrediction(targetChart.planetaryJson, targetChart)
                _currentAiReading.value = reading
            } catch (e: Exception) {
                _currentAiReading.value = AstroSageAiService.generateSynthesizedVedicReading(targetChart)
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun sendChatMessage(question: String) {
        val chart = _currentChart.value ?: return
        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "user",
            text = question
        )
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isChatThinking.value = true
            val reply = AstroSageAiService.askAstrologer(chart, _chatMessages.value, question, isHindi = _isHindi.value)
            val acharyaMsg = ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                sender = "acharya",
                text = reply
            )
            _chatMessages.value = _chatMessages.value + acharyaMsg
            _isChatThinking.value = false
        }
    }

    fun performMatching(boyDetails: BirthDetails, girlDetails: BirthDetails) {
        val boyChart = EphemerisEngine.calculateKundali(boyDetails)
        val girlChart = EphemerisEngine.calculateKundali(girlDetails)
        val result = KundaliMatchingEngine.matchKundali(boyChart, girlChart)
        _matchingResult.value = result
    }

    fun refreshPanchangForDate(calendar: Calendar, lat: Double = 28.6139, lon: Double = 77.2090) {
        _panchangData.value = PanchangEngine.calculatePanchang(calendar, lat, lon)
    }
}
