package com.example.astrosage.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KundaliDao {
    @Query("SELECT * FROM birth_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<BirthProfileEntity>>

    @Query("SELECT * FROM birth_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): BirthProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: BirthProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: BirthProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: BirthProfileEntity)

    @Query("DELETE FROM birth_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Long)
}
