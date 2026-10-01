package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.astrosage.core.astronomy.BirthDetails
import com.example.astrosage.core.astronomy.EphemerisEngine
import com.example.astrosage.core.astronomy.KundaliMatchingEngine
import com.example.astrosage.data.local.AstroDatabase
import com.example.astrosage.data.local.BirthProfileEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AstroSage AI", appName)
  }

  @Test
  fun `verify astronomical ephemeris calculation`() {
    val birthDetails = BirthDetails(
      name = "Test Native",
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
    val chart = EphemerisEngine.calculateKundali(birthDetails)
    assertNotNull(chart)
    assertTrue("Ayanamsha should be around 23.8°", chart.ayanamsha in 23.0..24.5)
    assertEquals("Should have 9 primary planets", 9, chart.planets.size)
    assertNotNull("Lagna position should be calculated", chart.lagna)
    assertTrue("Planetary JSON should not be blank", chart.planetaryJson.isNotBlank())
  }

  @Test
  fun `verify kundali matching engine`() {
    val boy = BirthDetails(name = "Boy", year = 1995, month = 5, day = 15, hour = 10, minute = 30)
    val girl = BirthDetails(name = "Girl", year = 1997, month = 9, day = 22, hour = 14, minute = 15)
    val boyChart = EphemerisEngine.calculateKundali(boy)
    val girlChart = EphemerisEngine.calculateKundali(girl)
    val match = KundaliMatchingEngine.matchKundali(boyChart, girlChart)
    assertTrue("Total score must be between 0 and 36", match.totalScore in 0.0..36.0)
    assertNotNull(match.recommendation)
  }

  @Test
  fun `verify room database entity and dao`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AstroDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.kundaliDao()

    val profile = BirthProfileEntity(
      name = "Vikram Aditya",
      gender = "Male",
      year = 1990,
      month = 8,
      day = 20,
      hour = 6,
      minute = 45,
      placeName = "Ujjain, India",
      latitude = 23.1765,
      longitude = 75.7885
    )
    val insertedId = dao.insertProfile(profile)
    assertTrue(insertedId > 0)

    val profiles = dao.getAllProfiles().first()
    assertEquals(1, profiles.size)
    assertEquals("Vikram Aditya", profiles[0].name)
    assertEquals("Ujjain, India", profiles[0].placeName)

    db.close()
  }

  @Test
  fun `verify astrosage ai assistant response generation`() = runBlocking {
    val birthDetails = BirthDetails(
      name = "Test Native",
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
    val chart = EphemerisEngine.calculateKundali(birthDetails)
    val answer = com.example.astrosage.core.ai.AstroSageAiService.askAstrologer(chart, emptyList(), "What is my career outlook?")
    assertNotNull(answer)
    assertTrue("Answer should not be blank", answer.isNotBlank())
    assertTrue("Answer should provide astrologer guidance", answer.contains("Career") || answer.contains("Acharya") || answer.contains("Lagna"))
  }
}
