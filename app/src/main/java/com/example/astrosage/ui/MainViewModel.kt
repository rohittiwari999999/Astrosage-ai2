package com.example.astrosage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astrosage.core.astronomy.EphemerisEngine
import com.example.astrosage.core.astronomy.PanchangData
import com.example.astrosage.core.astronomy.PanchangEngine
import com.example.astrosage.core.astronomy.PlanetPosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class CityLocation(
    val nameHi: String,
    val nameEn: String,
    val latitude: Double,
    val longitude: Double
)

class MainViewModel : ViewModel() {

    val popularCities = listOf(
        CityLocation("नई दिल्ली (New Delhi)", "New Delhi", 28.6139, 77.2090),
        CityLocation("वाराणसी / काशी (Varanasi)", "Varanasi", 25.3176, 82.9739),
        CityLocation("उज्जैन (Ujjain - महाकाल)", "Ujjain", 23.1765, 75.7885),
        CityLocation("हरिद्वार (Haridwar)", "Haridwar", 29.9457, 78.1642),
        CityLocation("मुम्बई (Mumbai)", "Mumbai", 19.0760, 72.8777),
        CityLocation("कोलकाता (Kolkata)", "Kolkata", 22.5726, 88.3639),
        CityLocation("बेंगलुरु (Bengaluru)", "Bengaluru", 12.9716, 77.5946),
        CityLocation("जयपुर (Jaipur)", "Jaipur", 26.9124, 75.7873)
    )

    private val _selectedCity = MutableStateFlow(popularCities[0])
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _currentCalendar = MutableStateFlow(Calendar.getInstance())
    val currentCalendar: StateFlow<Calendar> = _currentCalendar.asStateFlow()

    private val _panchangState = MutableStateFlow(
        PanchangEngine.calculatePanchang(
            calendar = Calendar.getInstance(),
            cityName = popularCities[0].nameHi,
            latitude = popularCities[0].latitude,
            longitude = popularCities[0].longitude
        )
    )
    val panchangState: StateFlow<PanchangData> = _panchangState.asStateFlow()

    private val _planetsState = MutableStateFlow<List<PlanetPosition>>(emptyList())
    val planetsState: StateFlow<List<PlanetPosition>> = _planetsState.asStateFlow()

    init {
        recalculate()
    }

    fun setCity(city: CityLocation) {
        _selectedCity.value = city
        recalculate()
    }

    fun changeDateOffset(dayOffset: Int) {
        val cal = _currentCalendar.value.clone() as Calendar
        cal.add(Calendar.DAY_OF_MONTH, dayOffset)
        _currentCalendar.value = cal
        recalculate()
    }

    fun setCalendarDate(cal: Calendar) {
        _currentCalendar.value = cal
        recalculate()
    }

    fun refreshPanchangForDate(calendar: Calendar) {
        _currentCalendar.value = calendar
        recalculate()
    }

    fun recalculate() {
        viewModelScope.launch {
            val cal = _currentCalendar.value
            val city = _selectedCity.value
            val data = PanchangEngine.calculatePanchang(
                calendar = cal,
                cityName = city.nameHi,
                latitude = city.latitude,
                longitude = city.longitude
            )
            _panchangState.value = data

            val jd = EphemerisEngine.toJulianDay(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                12, 0, 0
            )
            _planetsState.value = EphemerisEngine.calculatePlanets(jd)
        }
    }
}
