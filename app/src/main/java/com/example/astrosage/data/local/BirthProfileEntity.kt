package com.example.astrosage.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.astrosage.core.astronomy.BirthDetails

@Entity(tableName = "birth_profiles")
data class BirthProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    val relation: String = "Self",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toBirthDetails(): BirthDetails {
        return BirthDetails(
            name = name,
            gender = gender,
            year = year,
            month = month,
            day = day,
            hour = hour,
            minute = minute,
            placeName = placeName,
            latitude = latitude,
            longitude = longitude,
            timezoneOffsetHours = 5.5
        )
    }

    companion object {
        fun fromBirthDetails(details: BirthDetails, relation: String = "Self"): BirthProfileEntity {
            return BirthProfileEntity(
                name = details.name,
                gender = details.gender,
                year = details.year,
                month = details.month,
                day = details.day,
                hour = details.hour,
                minute = details.minute,
                placeName = details.placeName,
                latitude = details.latitude,
                longitude = details.longitude,
                relation = relation
            )
        }
    }
}
