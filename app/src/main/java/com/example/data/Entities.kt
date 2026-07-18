package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "experiences")
data class ExperienceEntity(
    @PrimaryKey val id: String,
    val titleAr: String,
    val titleEn: String,
    val category: String, // Agriculture, Crafts, Culinary, Nature, Marine
    val locationAr: String,
    val locationEn: String,
    val priceYer: Double,
    val rating: Double,
    val durationAr: String,
    val durationEn: String,
    val imageResName: String, // e.g. "img_banner_coffee", "img_banner_socotra"
    val descriptionAr: String,
    val descriptionEn: String,
    val providerNameAr: String,
    val providerNameEn: String,
    val providerBioAr: String,
    val providerBioEn: String,
    val isFeatured: Boolean = false
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val bookingId: String,
    val experienceId: String,
    val experienceTitleAr: String,
    val experienceTitleEn: String,
    val bookingDate: String,
    val category: String,
    val pricePaid: Double,
    val currency: String, // YER or USD
    val qrCodeData: String,
    val status: String, // PENDING, CONFIRMED, COMPLETED, CANCELLED
    val guideName: String, // Optional local guide booked
    val hotelName: String, // Optional eco-lodge/hotel booked
    val carModel: String // Optional transport vehicle booked
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String = "me",
    val name: String,
    val email: String,
    val walletBalanceYer: Double,
    val walletBalanceUsd: Double,
    val points: Int,
    val badgeAr: String,
    val badgeEn: String,
    val referralCode: String
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // USER or AI
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
