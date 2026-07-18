package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

sealed class Screen {
    object Onboarding : Screen()
    object Home : Screen()
    data class ExperienceDetail(val experienceId: String) : Screen()
    object AiPlanner : Screen()
    object AiGuideChat : Screen()
    object Wallet : Screen()
    object Bookings : Screen()
    object Dashboard : Screen()
}

enum class PlatformRole {
    TOURIST,
    PROVIDER,
    ADMIN
}

class TajrubahViewModel(
    application: Application,
    private val repository: TajrubahRepository
) : AndroidViewModel(application) {

    // --- State Observables ---
    val allExperiences: StateFlow<List<ExperienceEntity>> = repository.allExperiences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredExperiences: StateFlow<List<ExperienceEntity>> = repository.featuredExperiences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessagesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- UI Navigation State ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Onboarding)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentRole = MutableStateFlow(PlatformRole.TOURIST)
    val currentRole: StateFlow<PlatformRole> = _currentRole.asStateFlow()

    // --- Selected Detail State ---
    private val _selectedExperience = MutableStateFlow<ExperienceEntity?>(null)
    val selectedExperience: StateFlow<ExperienceEntity?> = _selectedExperience.asStateFlow()

    // --- AI Trip Planner State ---
    private val _tripPlanText = MutableStateFlow("")
    val tripPlanText: StateFlow<String> = _tripPlanText.asStateFlow()

    private val _isPlanningLoading = MutableStateFlow(false)
    val isPlanningLoading: StateFlow<Boolean> = _isPlanningLoading.asStateFlow()

    // --- AI Chatbot Guide State ---
    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // --- Surprise Trip Roulette State ---
    private val _surpriseExperience = MutableStateFlow<ExperienceEntity?>(null)
    val surpriseExperience: StateFlow<ExperienceEntity?> = _surpriseExperience.asStateFlow()

    private val _isSurpriseSpinning = MutableStateFlow(false)
    val isSurpriseSpinning: StateFlow<Boolean> = _isSurpriseSpinning.asStateFlow()

    // --- Toast / Snackbar Notification State ---
    private val _uiNotification = MutableSharedFlow<String>()
    val uiNotification: SharedFlow<String> = _uiNotification.asSharedFlow()

    // --- Navigation Helper ---
    fun navigateTo(screen: Screen) {
        if (screen is Screen.ExperienceDetail) {
            viewModelScope.launch {
                _selectedExperience.value = repository.getExperienceById(screen.experienceId)
            }
        }
        _currentScreen.value = screen
    }

    fun setRole(role: PlatformRole) {
        _currentRole.value = role
    }

    // --- Wallet & Real Booking Operations ---
    fun bookExperience(
        experience: ExperienceEntity,
        bookGuide: Boolean,
        bookHotel: Boolean,
        bookCar: Boolean,
        paymentCurrency: String // YER or USD
    ) {
        viewModelScope.launch {
            val profile = repository.getUserProfile() ?: return@launch

            // Calculate total price based on optional services
            var totalCost = if (paymentCurrency == "YER") {
                experience.priceYer
            } else {
                experience.priceYer / 600.0 // 1 USD = 600 YER (traditional Yemen standard exchange rate)
            }

            val guideName = if (bookGuide) "مصلح الحرازي" else ""
            val hotelName = if (bookHotel) "بيت الضيافة التراثي" else ""
            val carModel = if (bookCar) "تويوتا لاندكروزر 4x4" else ""

            if (bookGuide) totalCost += if (paymentCurrency == "YER") 6000.0 else 10.0
            if (bookHotel) totalCost += if (paymentCurrency == "YER") 18000.0 else 30.0
            if (bookCar) totalCost += if (paymentCurrency == "YER") 24000.0 else 40.0

            // Check if user has sufficient funds
            val hasFunds = if (paymentCurrency == "YER") {
                profile.walletBalanceYer >= totalCost
            } else {
                profile.walletBalanceUsd >= totalCost
            }

            if (!hasFunds) {
                _uiNotification.emit("عذراً، رصيدك في المحفظة غير كافٍ لإتمام هذا الحجز.")
                return@launch
            }

            // Deduct funds and award points (100 base points + 50 points per extra service)
            val pointsEarned = 100 + (if (bookGuide) 50 else 0) + (if (bookHotel) 50 else 0) + (if (bookCar) 50 else 0)
            val newPoints = profile.points + pointsEarned

            val (newYer, newUsd) = if (paymentCurrency == "YER") {
                Pair(profile.walletBalanceYer - totalCost, profile.walletBalanceUsd)
            } else {
                Pair(profile.walletBalanceYer, profile.walletBalanceUsd - totalCost)
            }

            // Update user profile in local database
            repository.updateWalletAndPoints(newYer, newUsd, newPoints)

            // Dynamic badge promotion
            val newBadgeAr = when {
                newPoints >= 2500 -> "سفير التراث اليمني"
                newPoints >= 1500 -> "سفير ثقافي ذهبي"
                newPoints >= 500 -> "مستكشف محلي نشط"
                else -> "مستكشف مبتدئ"
            }
            val newBadgeEn = when {
                newPoints >= 2500 -> "Yemeni Heritage Ambassador"
                newPoints >= 1500 -> "Gold Cultural Ambassador"
                newPoints >= 500 -> "Active Local Explorer"
                else -> "Novice Explorer"
            }
            repository.saveUserProfile(
                profile.copy(
                    walletBalanceYer = newYer,
                    walletBalanceUsd = newUsd,
                    points = newPoints,
                    badgeAr = newBadgeAr,
                    badgeEn = newBadgeEn
                )
            )

            // Record Booking in Room database
            val bookingId = "TJB-" + UUID.randomUUID().toString().take(6).uppercase()
            val bookingDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            val booking = BookingEntity(
                bookingId = bookingId,
                experienceId = experience.id,
                experienceTitleAr = experience.titleAr,
                experienceTitleEn = experience.titleEn,
                bookingDate = bookingDate,
                category = experience.category,
                pricePaid = totalCost,
                currency = paymentCurrency,
                qrCodeData = "Tajrubah-Verification:$bookingId:${experience.id}:$newPoints",
                status = "CONFIRMED",
                guideName = guideName,
                hotelName = hotelName,
                carModel = carModel
            )
            repository.insertBooking(booking)

            _uiNotification.emit("تم تأكيد حجزك بنجاح! كود الحجز: $bookingId. حصلت على +$pointsEarned نقطة ثقافية.")
            _currentScreen.value = Screen.Bookings
        }
    }

    // --- AI Organizers / Planners ---
    fun generateTripPlanWithAI(
        startCity: String,
        durationDays: Int,
        budgetYer: Double,
        interests: List<String>
    ) {
        viewModelScope.launch {
            _isPlanningLoading.value = true
            _tripPlanText.value = ""
            try {
                val plan = withContext(Dispatchers.IO) {
                    GeminiClient.generateTripPlan(startCity, durationDays, budgetYer, interests)
                }
                _tripPlanText.value = plan
            } catch (e: Exception) {
                _tripPlanText.value = "حدث خطأ أثناء توليد الخطة: ${e.message}"
            } finally {
                _isPlanningLoading.value = false
            }
        }
    }

    // --- AI Cultural Guide Chatbot ---
    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return
        viewModelScope.launch {
            // Save User Message
            val userMsg = ChatMessageEntity(sender = "USER", text = text)
            repository.insertChatMessage(userMsg)

            _isChatLoading.value = true
            try {
                // Get chat history for conversational memory
                val history = chatMessages.value
                val aiResponseText = withContext(Dispatchers.IO) {
                    GeminiClient.chatWithGuide(history, text)
                }

                // Save AI Response
                val aiMsg = ChatMessageEntity(sender = "AI", text = aiResponseText)
                repository.insertChatMessage(aiMsg)
            } catch (e: Exception) {
                val errMsg = ChatMessageEntity(sender = "AI", text = "عذراً يا صديقي، واجهت مشكلة في الاتصال بالشبكة حالياً.")
                repository.insertChatMessage(errMsg)
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
            _uiNotification.emit("تم مسح سجل المحادثة مع المرشد الذكي.")
        }
    }

    // --- Provider Portal: Add New Experience ---
    fun addNewExperience(
        titleAr: String,
        titleEn: String,
        category: String,
        locationAr: String,
        locationEn: String,
        priceYer: Double,
        durationAr: String,
        durationEn: String,
        descriptionAr: String,
        descriptionEn: String,
        providerNameAr: String,
        providerNameEn: String,
        providerBioAr: String,
        providerBioEn: String
    ) {
        viewModelScope.launch {
            val newId = "exp_custom_" + UUID.randomUUID().toString().take(6)
            val customExp = ExperienceEntity(
                id = newId,
                titleAr = titleAr,
                titleEn = titleEn,
                category = category,
                locationAr = locationAr,
                locationEn = locationEn,
                priceYer = priceYer,
                rating = 5.0,
                durationAr = durationAr,
                durationEn = durationEn,
                imageResName = "img_app_logo", // default logo
                descriptionAr = descriptionAr,
                descriptionEn = descriptionEn,
                providerNameAr = providerNameAr,
                providerNameEn = providerNameEn,
                providerBioAr = providerBioAr,
                providerBioEn = providerBioEn,
                isFeatured = false
            )
            repository.insertExperience(customExp)
            _uiNotification.emit("تمت إضافة تجربتك التراثية بنجاح ومشاركتها مع جميع السياح!")
            _currentScreen.value = Screen.Home
        }
    }

    // --- Delete / Cancel Experience ---
    fun deleteExperience(id: String) {
        viewModelScope.launch {
            repository.deleteExperience(id)
            _uiNotification.emit("تم حذف التجربة التراثية بنجاح.")
        }
    }

    // --- Admin Control: Complete Booking status ---
    fun updateBookingStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
            _uiNotification.emit("تم تحديث حالة الحجز $bookingId إلى $status.")
        }
    }

    // --- Surprise Trip Spinning Wheel ("تجربة على البركة") ---
    fun spinSurpriseTrip(maxBudgetYer: Double) {
        viewModelScope.launch {
            _isSurpriseSpinning.value = true
            _surpriseExperience.value = null
            
            // Artificial delay to play a beautiful spinning animation in UI
            kotlinx.coroutines.delay(2000)

            val experiences = allExperiences.value.filter { it.priceYer <= maxBudgetYer }
            if (experiences.isEmpty()) {
                _uiNotification.emit("عذراً، لم نجد أي تجربة تراثية تطابق هذه الميزانية حالياً.")
                _surpriseExperience.value = null
            } else {
                val selected = experiences.random()
                _surpriseExperience.value = selected
                _uiNotification.emit("يا سلام! اخترنا لك تجربة مميزة: ${selected.titleAr}")
            }
            _isSurpriseSpinning.value = false
        }
    }

    // --- Top-up Wallet (YER or USD) ---
    fun topUpWallet(amount: Double, currency: String) {
        viewModelScope.launch {
            val profile = repository.getUserProfile() ?: return@launch
            val (newYer, newUsd) = if (currency == "YER") {
                Pair(profile.walletBalanceYer + amount, profile.walletBalanceUsd)
            } else {
                Pair(profile.walletBalanceYer, profile.walletBalanceUsd + amount)
            }
            repository.updateWalletAndPoints(newYer, newUsd, profile.points)
            _uiNotification.emit("تم شحن محفظتك بنجاح بقيمة $amount $currency!")
        }
    }
}

// --- ViewModel Factory ---
class TajrubahViewModelFactory(
    private val application: Application,
    private val repository: TajrubahRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TajrubahViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TajrubahViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
