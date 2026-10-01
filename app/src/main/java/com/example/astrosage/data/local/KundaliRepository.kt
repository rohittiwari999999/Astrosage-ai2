package com.example.astrosage.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class KundaliRepository(private val kundaliDao: KundaliDao) {

    val allProfiles: Flow<List<BirthProfileEntity>> = kundaliDao.getAllProfiles()

    suspend fun getProfileById(id: Long): BirthProfileEntity? {
        return kundaliDao.getProfileById(id)
    }

    suspend fun saveProfile(profile: BirthProfileEntity): Long {
        return kundaliDao.insertProfile(profile)
    }

    suspend fun deleteProfile(profile: BirthProfileEntity) {
        kundaliDao.deleteProfile(profile)
    }

    suspend fun initDefaultProfileIfNeeded() {
        val existing = kundaliDao.getAllProfiles().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val starterProfile = BirthProfileEntity(
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
                relation = "Self"
            )
            kundaliDao.insertProfile(starterProfile)

            val spouseProfile = BirthProfileEntity(
                name = "Priya Patel",
                gender = "Female",
                year = 1997,
                month = 9,
                day = 22,
                hour = 14,
                minute = 15,
                placeName = "Mumbai, India",
                latitude = 19.0760,
                longitude = 72.8777,
                relation = "Partner"
            )
            kundaliDao.insertProfile(spouseProfile)
        }
    }
}
