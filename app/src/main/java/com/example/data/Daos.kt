package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperienceDao {
    @Query("SELECT * FROM experiences")
    fun getAllExperiences(): Flow<List<ExperienceEntity>>

    @Query("SELECT * FROM experiences WHERE id = :id")
    suspend fun getExperienceById(id: String): ExperienceEntity?

    @Query("SELECT * FROM experiences WHERE isFeatured = 1")
    fun getFeaturedExperiences(): Flow<List<ExperienceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(experiences: List<ExperienceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperience(experience: ExperienceEntity)

    @Query("DELETE FROM experiences WHERE id = :id")
    suspend fun deleteExperience(id: String)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY bookingDate DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE bookingId = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, status: String)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE userId = 'me'")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE userId = 'me'")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET walletBalanceYer = :balanceYer, walletBalanceUsd = :balanceUsd, points = :points WHERE userId = 'me'")
    suspend fun updateWalletAndPoints(balanceYer: Double, balanceUsd: Double, points: Int)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}
