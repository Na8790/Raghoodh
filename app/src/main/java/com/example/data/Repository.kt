package com.example.data

import kotlinx.coroutines.flow.Flow

class TajrubahRepository(private val database: AppDatabase) {
    private val experienceDao = database.experienceDao()
    private val bookingDao = database.bookingDao()
    private val userProfileDao = database.userProfileDao()
    private val chatMessageDao = database.chatMessageDao()

    // Experiences
    val allExperiences: Flow<List<ExperienceEntity>> = experienceDao.getAllExperiences()
    val featuredExperiences: Flow<List<ExperienceEntity>> = experienceDao.getFeaturedExperiences()

    suspend fun getExperienceById(id: String): ExperienceEntity? {
        return experienceDao.getExperienceById(id)
    }

    suspend fun insertExperience(experience: ExperienceEntity) {
        experienceDao.insertExperience(experience)
    }

    suspend fun deleteExperience(id: String) {
        experienceDao.deleteExperience(id)
    }

    // Bookings
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    suspend fun insertBooking(booking: BookingEntity) {
        bookingDao.insertBooking(booking)
    }

    suspend fun updateBookingStatus(bookingId: String, status: String) {
        bookingDao.updateBookingStatus(bookingId, status)
    }

    // User Profile
    val userProfileFlow: Flow<UserProfileEntity?> = userProfileDao.getUserProfileFlow()

    suspend fun getUserProfile(): UserProfileEntity? {
        return userProfileDao.getUserProfile()
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        userProfileDao.insertProfile(profile)
    }

    suspend fun updateWalletAndPoints(balanceYer: Double, balanceUsd: Double, points: Int) {
        userProfileDao.updateWalletAndPoints(balanceYer, balanceUsd, points)
    }

    // Chat History
    val chatMessagesFlow: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()

    suspend fun insertChatMessage(message: ChatMessageEntity) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun clearChatHistory() {
        chatMessageDao.clearHistory()
    }
}
