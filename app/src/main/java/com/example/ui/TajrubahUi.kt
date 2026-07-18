package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.data.BookingEntity
import com.example.data.ExperienceEntity
import com.example.data.UserProfileEntity
import com.example.data.GeminiClient
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TajrubahApp(viewModel: TajrubahViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val uiNotification = viewModel.uiNotification

    // Listen to ViewModel Notifications (Toasts)
    LaunchedEffect(key1 = true) {
        uiNotification.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    if (currentScreen is Screen.Onboarding) {
        OnboardingScreen(onStart = { viewModel.navigateTo(Screen.Home) })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "تِجربة",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp
                        )
                    },
                    actions = {
                        // Multi-Role Pill Switcher
                        RoleSwitcher(
                            currentRole = currentRole,
                            onRoleSelected = { viewModel.setRole(it) }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                // Adaptive bottom navigation for mobile screen sizes
                TajrubahBottomNavigation(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        is Screen.Home -> TouristHomeView(viewModel = viewModel)
                        is Screen.ExperienceDetail -> ExperienceDetailView(
                            experienceId = screen.experienceId,
                            viewModel = viewModel
                        )
                        is Screen.AiPlanner -> AiPlannerView(viewModel = viewModel)
                        is Screen.AiGuideChat -> AiGuideChatView(viewModel = viewModel)
                        is Screen.Wallet -> WalletProfileView(viewModel = viewModel)
                        is Screen.Bookings -> BookingsHistoryView(viewModel = viewModel)
                        is Screen.Dashboard -> MultiRoleDashboardView(viewModel = viewModel)
                        else -> {}
                    }
                }
            }
        }
    }
}

// --- ONBOARDING / WELCOME SCREEN ---
@Composable
fun OnboardingScreen(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF132A15), Color(0xFF13110E))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Glow backdrop behind App Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(180.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x33C59B27), Color.Transparent)
                            )
                        )
                )
                // Generated Premium Logo Reference
                Image(
                    painter = painterResource(id = R.drawable.img_app_logo),
                    contentDescription = "Tajrubah Logo",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.5.dp, Color(0xFFC59B27), RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "مَـنْـصَـة تِـجْـرَبَـة",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC59B27),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tajrubah Platform",
                fontSize = 18.sp,
                color = Color(0xFFE8DDCB).copy(alpha = 0.7f),
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Glassmorphic intro description card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF27211C).copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFC59B27).copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "المنصة الذكية لاستكشاف التجارب المحلية والبن الخولاني في اليمن",
                        fontSize = 16.sp,
                        color = Color(0xFFF7F3EE),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "شارك في قطف البن، تخييم سقطرى الأسطوري، صياغة الجنابي، وإعداد المأكولات الشعبية مباشرة مع المزارعين والحرفيين المحليين.",
                        fontSize = 13.sp,
                        color = Color(0xFFE8DDCB).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Launch Button
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC59B27)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("launch_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ابدأ الرحلة التراثية",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF13110E)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Explore Icon",
                        tint = Color(0xFF13110E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Academic Graduation Credit
            Text(
                text = "صاحبة الفكرة والابتكار: م. رغد",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFC59B27).copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "مشروع تخرج - منصة تجارب السفر الذكية",
                fontSize = 11.sp,
                color = Color(0xFFE8DDCB).copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// --- DYNAMIC ROLE SWITCHER PILL ---
@Composable
fun RoleSwitcher(
    currentRole: PlatformRole,
    onRoleSelected: (PlatformRole) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 8.dp)
        ) {
            val roleText = when (currentRole) {
                PlatformRole.TOURIST -> "🧔 سائح"
                PlatformRole.PROVIDER -> "🌾 مقدم خدمة"
                PlatformRole.ADMIN -> "⚙️ مسؤول"
            }
            Text(text = roleText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Role Dropdown", tint = MaterialTheme.colorScheme.primary)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text("🧔 سائح يمني / سياحي") },
                onClick = {
                    onRoleSelected(PlatformRole.TOURIST)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("🌾 مقدم خدمة (مزارع/حرفي)") },
                onClick = {
                    onRoleSelected(PlatformRole.PROVIDER)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("⚙️ مسؤول نظام تِجربة") },
                onClick = {
                    onRoleSelected(PlatformRole.ADMIN)
                    expanded = false
                }
            )
        }
    }
}

// --- ADAPTIVE BOTTOM NAVIGATION ---
@Composable
fun TajrubahBottomNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = { Icon(imageVector = Icons.Default.Explore, contentDescription = "Home") },
            label = { Text("استكشاف", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.AiPlanner,
            onClick = { onNavigate(Screen.AiPlanner) },
            icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Planner") },
            label = { Text("مخطط الذكاء", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.AiGuideChat,
            onClick = { onNavigate(Screen.AiGuideChat) },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat") },
            label = { Text("المرشد الذكي", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Bookings,
            onClick = { onNavigate(Screen.Bookings) },
            icon = { Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = "Tickets") },
            label = { Text("تذاكري", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Wallet || currentScreen is Screen.Dashboard,
            onClick = { onNavigate(Screen.Wallet) },
            icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
            label = { Text("المحفظة", fontSize = 11.sp) }
        )
    }
}

// --- TOURIST HOME VIEW ---
@Composable
fun TouristHomeView(viewModel: TajrubahViewModel) {
    val experiences by viewModel.allExperiences.collectAsState()
    val featured by viewModel.featuredExperiences.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }
    var showSurpriseDialog by remember { mutableStateOf(false) }

    val categories = listOf("الكل", "زراعة وثقافة", "طبيعة ومغامرة", "حرف يدوية", "مأكولات شعبية", "بحر وأنشطة")

    val filteredExperiences = experiences.filter { exp ->
        (selectedCategory == "الكل" || exp.category == selectedCategory) &&
                (exp.titleAr.contains(searchQuery, ignoreCase = true) ||
                 exp.titleEn.contains(searchQuery, ignoreCase = true) ||
                 exp.locationAr.contains(searchQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header & Points overview
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "أهلاً بك، ${profile?.name ?: "مستكشف تجربة"}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = profile?.badgeAr ?: "مستكشف مبتدئ",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                // Points Badge
                Row(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Points",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile?.points ?: 0} نقطة",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Search Bar & Surprise trigger
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث عن بن خولاني، سقطرى، مأكولات...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_field"),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Surprise Roulette Wheel Button
                IconButton(
                    onClick = { showSurpriseDialog = true },
                    modifier = Modifier
                        .size(54.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .testTag("surprise_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "تجربة على البركة (Surprise)",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Featured Banners Carousel
        if (featured.isNotEmpty() && searchQuery.isEmpty()) {
            item {
                Column {
                    Text(
                        text = "التجارب الأكثر تميزاً 🔥",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(featured) { exp ->
                            FeaturedExperienceCard(experience = exp, onClick = {
                                viewModel.navigateTo(Screen.ExperienceDetail(exp.id))
                            })
                        }
                    }
                }
            }
        }

        // Category Selection
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(text = category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Main Experiences Feed
        if (filteredExperiences.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SentimentDissatisfied,
                            contentDescription = "No results",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لم نجد أي تجربة مطابقة لـ \"$searchQuery\"",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(filteredExperiences) { exp ->
                ExperienceFeedCard(experience = exp, onClick = {
                    viewModel.navigateTo(Screen.ExperienceDetail(exp.id))
                })
            }
        }

        // End Spacing
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Surprise Trip dialog ("تجربة على البركة")
    if (showSurpriseDialog) {
        SurpriseTripDialog(
            viewModel = viewModel,
            onDismiss = { showSurpriseDialog = false }
        )
    }
}

// --- FEATURED CARD VIEW ---
@Composable
fun FeaturedExperienceCard(
    experience: ExperienceEntity,
    onClick: () -> Unit
) {
    // Map image resource names dynamically
    val imageRes = when (experience.imageResName) {
        "img_banner_coffee" -> R.drawable.img_banner_coffee
        "img_banner_socotra" -> R.drawable.img_banner_socotra
        else -> R.drawable.img_app_logo
    }

    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = experience.titleAr,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Overlay gradient for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                // Category Tag
                Box(
                    modifier = Modifier
                        .background(Color(0xFFC59B27), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = experience.category, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = experience.titleAr,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📍 ${experience.locationAr}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${"%,.0f".format(experience.priceYer)} YER",
                        color = Color(0xFFC59B27),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// --- STANDARD FEED CARD VIEW ---
@Composable
fun ExperienceFeedCard(
    experience: ExperienceEntity,
    onClick: () -> Unit
) {
    val imageRes = when (experience.imageResName) {
        "img_banner_coffee" -> R.drawable.img_banner_coffee
        "img_banner_socotra" -> R.drawable.img_banner_socotra
        else -> R.drawable.img_app_logo
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("experience_card_${experience.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = experience.titleAr,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = experience.category,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFC59B27), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "${experience.rating}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = experience.titleAr,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "📍 ${experience.locationAr} | ⏱ ${experience.durationAr}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${"%,.0f".format(experience.priceYer)} YER",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "تفاصيل الحجز ←",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- EXPERIENCE DETAIL VIEW ---
@Composable
fun ExperienceDetailView(
    experienceId: String,
    viewModel: TajrubahViewModel
) {
    val experience by viewModel.selectedExperience.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    // Add-on states
    var bookGuide by remember { mutableStateOf(false) }
    var bookHotel by remember { mutableStateOf(false) }
    var bookCar by remember { mutableStateOf(false) }
    var selectedCurrency by remember { mutableStateOf("YER") } // YER or USD

    if (experience == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val exp = experience!!

    val imageRes = when (exp.imageResName) {
        "img_banner_coffee" -> R.drawable.img_banner_coffee
        "img_banner_socotra" -> R.drawable.img_banner_socotra
        else -> R.drawable.img_app_logo
    }

    // Dynamic cost calculator
    val baseCost = if (selectedCurrency == "YER") exp.priceYer else exp.priceYer / 600.0
    val guideCost = if (selectedCurrency == "YER") 6000.0 else 10.0
    val hotelCost = if (selectedCurrency == "YER") 18000.0 else 30.0
    val carCost = if (selectedCurrency == "YER") 24000.0 else 40.0

    val totalCost = baseCost +
            (if (bookGuide) guideCost else 0.0) +
            (if (bookHotel) hotelCost else 0.0) +
            (if (bookCar) carCost else 0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Back & Full Image header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = exp.titleAr,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Overlay gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            // Back Button
            IconButton(
                onClick = { viewModel.navigateTo(Screen.Home) },
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Category & Rating
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = exp.category, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Stars", tint = Color(0xFFC59B27), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${exp.rating}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Details Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = exp.titleAr,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = exp.titleEn,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = "📍 ${exp.locationAr}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(text = "⏱ ${exp.durationAr}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Cultural Background
            Text(
                text = "قصة التجربة التراثية 🏺",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = exp.descriptionAr,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                lineHeight = 22.sp,
                textAlign = TextAlign.Justify
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Provider Info
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Agriculture, contentDescription = "Provider", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "مضيف التجربة: ${exp.providerNameAr}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = exp.providerBioAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        Text(text = "موثّق 🛡️", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Smart Booking Configurator
            Text(
                text = "تخصيص باقة الحجز الذكي 🛠️",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Optional Addons
            BookingAddonToggle(
                title = "مرشد محلي مرافق مخصص",
                description = "مرافقة مرشد يمني خبير طوال اليوم لشرح التفاصيل.",
                priceText = if (selectedCurrency == "YER") "+6,000 YER" else "+$10 USD",
                checked = bookGuide,
                onCheckedChange = { bookGuide = it }
            )
            BookingAddonToggle(
                title = "مبيت بيئي في بيت ضيافة تراثي",
                description = "مبيت ليلة واحدة مع وجبة عشاء يمنية أصيلة.",
                priceText = if (selectedCurrency == "YER") "+18,000 YER" else "+$30 USD",
                checked = bookHotel,
                onCheckedChange = { bookHotel = it }
            )
            BookingAddonToggle(
                title = "مواصلات سيارة دفع رباعي 4x4 مخصصة",
                description = "نقل من وإلى الفندق بسيارة لاندكروزر مريحة للطرق الوعرة.",
                priceText = if (selectedCurrency == "YER") "+24,000 YER" else "+$40 USD",
                checked = bookCar,
                onCheckedChange = { bookCar = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Currency Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "عملة الدفع المفضلة:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row {
                    Button(
                        onClick = { selectedCurrency = "YER" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCurrency == "YER") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "ريال يمني YER", fontSize = 11.sp, color = if (selectedCurrency == "YER") Color.Black else MaterialTheme.colorScheme.onSurface)
                    }
                    Button(
                        onClick = { selectedCurrency = "USD" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCurrency == "USD") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "دولار USD", fontSize = 11.sp, color = if (selectedCurrency == "USD") Color.Black else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Pricing & Wallet Check
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "المبلغ الإجمالي المستحق:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Text(
                        text = if (selectedCurrency == "YER") {
                            "${"%,.0f".format(totalCost)} YER"
                        } else {
                            "$${"%.2f".format(totalCost)} USD"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Show Current Wallet Balance
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "رصيدك المتاح:", fontSize = 11.sp)
                    Text(
                        text = if (selectedCurrency == "YER") {
                            "${"%,.0f".format(profile?.walletBalanceYer ?: 0.0)} YER"
                        } else {
                            "$${"%.2f".format(profile?.walletBalanceUsd ?: 0.0)} USD"
                        },
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Booking Action Button
            Button(
                onClick = {
                    viewModel.bookExperience(
                        experience = exp,
                        bookGuide = bookGuide,
                        bookHotel = bookHotel,
                        bookCar = bookCar,
                        paymentCurrency = selectedCurrency
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_booking_button")
            ) {
                Text(text = "تأكيد الحجز والدفع الفوري 💳", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun BookingAddonToggle(
    title: String,
    description: String,
    priceText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(text = priceText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

// --- SURPRISE TRIP SPINNING WHEEL (تجربة على البركة) ---
@Composable
fun SurpriseTripDialog(
    viewModel: TajrubahViewModel,
    onDismiss: () -> Unit
) {
    val surpriseExp by viewModel.surpriseExperience.collectAsState()
    val isSpinning by viewModel.isSurpriseSpinning.collectAsState()
    var budgetInput by remember { mutableStateOf("20000") }

    // Animating wheel rotation
    val infiniteTransition = rememberInfiniteTransition(label = "wheel")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🎯 تجربة على البركة", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = "عليك البركة وعلينا المغامرة! أدخل ميزانيتك القصوى وسنختار لك تجربة يمنية غامضة ومثيرة.", fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = { budgetInput = it },
                    label = { Text("أقصى ميزانية (ريال يمني)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // The Wheel Image / Vector
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Color(0xFF8B5A2B),
                                        Color(0xFFC59B27),
                                        Color(0xFF1E4620),
                                        Color(0xFF8B5A2B)
                                    )
                                ),
                                radius = size.minDimension / 2
                            )
                        }
                        .rotate(if (isSpinning) rotationAngle else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Casino, contentDescription = "Surprise", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isSpinning) {
                    Text(text = "جاري تدوير عجلة التجارب اليمنية... 🌀", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else if (surpriseExp != null) {
                    // Result Card
                    val result = surpriseExp!!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "تم اختيار تجربتك المثالية! 🎉", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = result.titleAr, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
                            Text(text = "📍 ${result.locationAr} | 💰 ${"%,.0f".format(result.priceYer)} YER", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.navigateTo(Screen.ExperienceDetail(result.id))
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("اذهب للتفاصيل واحجز 🎫", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.spinSurpriseTrip(budgetInput.toDoubleOrNull() ?: 20000.0) },
                        modifier = Modifier.weight(1f).testTag("spin_action_button"),
                        enabled = !isSpinning,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("لف العجلة!", color = Color.Black)
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إغلاق")
                    }
                }
            }
        }
    }
}

// --- AI DAILY TRAVEL PLANNER VIEW ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiPlannerView(viewModel: TajrubahViewModel) {
    val planText by viewModel.tripPlanText.collectAsState()
    val isLoading by viewModel.isPlanningLoading.collectAsState()

    var startCity by remember { mutableStateOf("صنعاء") }
    var durationDays by remember { mutableStateOf(3) }
    var budgetInput by remember { mutableStateOf("60000") }

    val cities = listOf("صنعاء", "عدن", "تعز", "المكلا", "جزيرة سقطرى")
    val interestOptions = listOf("التاريخ والآثار", "البن والمدرجات", "المغامرة والهايكنج", "المأكولات التراثية", "البحر والغوص")
    val selectedInterests = remember { mutableStateListOf("البن والمدرجات") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "مرشد السفر ومخطط الرحلات الذكي 🤖🌟",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "نقوم بدمج الذكاء الاصطناعي مع التراث اليمني الحقيقي. حدد تفاصيلك ودع Gemini ينظم لك رحلتك التراثية المتكاملة.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Options Inputs
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Start City Selection
                    Text(text = "مدينة الانطلاق وبداية الرحلة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cities.forEach { city ->
                            FilterChip(
                                selected = startCity == city,
                                onClick = { startCity = city },
                                label = { Text(text = city) }
                            )
                        }
                    }

                    // Duration Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "مدة الرحلة المطلوبة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "$durationDays أيام", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = durationDays.toFloat(),
                        onValueChange = { durationDays = it.toInt() },
                        valueRange = 1f..7f,
                        steps = 5,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )

                    // Budget Field
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        label = { Text("الميزانية التقديرية بالريال اليمني") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Interests Multi-select
                    Text(text = "اهتماماتك التراثية والسياحية:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        interestOptions.forEach { interest ->
                            val contains = selectedInterests.contains(interest)
                            FilterChip(
                                selected = contains,
                                onClick = {
                                    if (contains) selectedInterests.remove(interest)
                                    else selectedInterests.add(interest)
                                },
                                label = { Text(text = interest) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Launch Plan Button
                    Button(
                        onClick = {
                            viewModel.generateTripPlanWithAI(
                                startCity = startCity,
                                durationDays = durationDays,
                                budgetYer = budgetInput.toDoubleOrNull() ?: 60000.0,
                                interests = selectedInterests.toList()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_plan_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Text(text = "توليد البرنامج السياحي بالذكاء الاصطناعي ⚡", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Display results block
        if (isLoading || planText.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "البرنامج السياحي الذكي المقترح 🗺️✨", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
                            if (GeminiClient.getApiKey().isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "وضع محلي", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isLoading && planText.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(text = "جاري تجميع الخطة السياحية وتقدير التكاليف الفندقية والمواصلات...", fontSize = 11.sp, textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            Text(
                                text = planText,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

// --- AI GUIDE CONVERSATIONAL CHAT VIEW ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiGuideChatView(viewModel: TajrubahViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    var inputMessageText by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "ما هو البن الخولاني؟ ☕",
        "تاريخ شجرة دم الأخوين 🌲",
        "كيف تطهى السلتة؟ 🍲",
        "نصيحة للتنقل باليمن 🚙"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "المرشد التراثي الذكي 🧔💬",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = "مساعدك التراثي المدعوم بـ Gemini AI لربطك بتاريخ اليمن العريق", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
            // Clear History Button
            TextButton(onClick = { viewModel.clearChatHistory() }) {
                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "مسح", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
            }
        }

        // Messages Bubble Box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            if (messages.isEmpty()) {
                // Empty state onboarding chat
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "AI Guide",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "مرحباً! أنا مرشدك الثقافي لليمن.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "اسألني عن تاريخ المدرجات الزراعية بحراز، أسرار العسل الدوعني، سحر سقطرى، أو طهي الأكلات الشعبية الشهيرة.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick prompt chips
                    Text(text = "أسئلة شائعة لبدء المحادثة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPrompts.forEach { prompt ->
                            SuggestionChip(
                                onClick = { viewModel.sendChatMessage(prompt.dropLast(2)) },
                                label = { Text(text = prompt, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            } else {
                val scrollState = rememberScrollState()
                // Scroll to bottom automatically on new messages
                LaunchedEffect(messages.size) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    messages.forEach { msg ->
                        ChatBubble(messageSender = msg.sender, messageText = msg.text)
                    }
                    if (isChatLoading) {
                        ChatLoadingBubble()
                    }
                }
            }
        }

        // Input Field Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessageText,
                onValueChange = { inputMessageText = it },
                placeholder = { Text("اسألني عن البن الخولاني، دوعن، سقطرى...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputMessageText.trim().isNotEmpty()) {
                        viewModel.sendChatMessage(inputMessageText)
                        inputMessageText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .testTag("chat_send_button"),
                enabled = !isChatLoading
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(messageSender: String, messageText: String) {
    val isUser = messageSender == "USER"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = messageText,
                    fontSize = 13.sp,
                    color = if (isUser) Color.Black else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ChatLoadingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 2.dp),
            modifier = Modifier.widthIn(max = 200.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "يقرأ تاريخ اليمن الثقافي... ✍️", fontSize = 11.sp)
            }
        }
    }
}

// --- WALLET & USER PROFILE VIEW ---
@Composable
fun WalletProfileView(viewModel: TajrubahViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val bookings by viewModel.allBookings.collectAsState()

    var showTopUpDialog by remember { mutableStateOf(false) }
    var topUpAmount by remember { mutableStateOf("10000") }
    var topUpCurrency by remember { mutableStateOf("YER") }

    if (profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val pr = profile!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Profile details card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = pr.name.take(1), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = pr.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = pr.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "🏆 ${pr.badgeAr}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Credit Card Styled Digital Wallet Panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .testTag("digital_wallet_card"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF8B5A2B), Color(0xFFC59B27), Color(0xFF1E4620))
                            )
                        )
                ) {
                    // Mesh grid overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(text = "محفظة تِجربة الرقمية", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Tajrubah Pay", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(imageVector = Icons.Default.Nfc, contentDescription = "NFC", tint = Color.White)
                        }

                        // Wallet balances
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Column {
                                Text(text = "الرصيد بالريال اليمني", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                                Text(text = "${"%,.0f".format(pr.walletBalanceYer)} YER", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column {
                                Text(text = "الرصيد بالدولار", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                                Text(text = "$${"%.2f".format(pr.walletBalanceUsd)} USD", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(text = "كود الإحالة: ${pr.referralCode}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { showTopUpDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(text = "شحن رصيد +", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Referral Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "برنامج دعوة الأصدقاء 🎁", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "شارك كود الإحالة الخاص بك واحصل على رصيد مكافأة بقيمة 5,000 ريال يمني مع كل حجز يقوم به أصدقاؤك.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.topUpWallet(5000.0, "YER") },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "مشاركة", fontSize = 12.sp)
                    }
                }
            }
        }

        // Shortcut to Provider/Admin dashboard
        item {
            Button(
                onClick = { viewModel.navigateTo(Screen.Dashboard) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dash", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "الولوج إلى لوحة التحكم المتقدمة (لمقدمي الخدمة) 🌾⚙️", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    // Top up Balance Dialog
    if (showTopUpDialog) {
        Dialog(onDismissRequest = { showTopUpDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "شحن محفظة تِجربة", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "قم بشحن رصيد فوري لمحاكاة عمليات الحجز والدفع التراثية.", fontSize = 12.sp, textAlign = TextAlign.Center)

                    OutlinedTextField(
                        value = topUpAmount,
                        onValueChange = { topUpAmount = it },
                        label = { Text("المبلغ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Currency Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { topUpCurrency = "YER" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (topUpCurrency == "YER") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(text = "YER (ريال)", color = if (topUpCurrency == "YER") Color.Black else MaterialTheme.colorScheme.onSurface)
                        }
                        Button(
                            onClick = { topUpCurrency = "USD" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (topUpCurrency == "USD") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(text = "USD (دولار)", color = if (topUpCurrency == "USD") Color.Black else MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val amt = topUpAmount.toDoubleOrNull() ?: 0.0
                                viewModel.topUpWallet(amt, topUpCurrency)
                                showTopUpDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "تأكيد الشحن", color = Color.Black)
                        }
                        OutlinedButton(
                            onClick = { showTopUpDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "إلغاء")
                        }
                    }
                }
            }
        }
    }
}

// --- BOOKINGS HISTORY / ACTIVE TICKETS VIEW ---
@Composable
fun BookingsHistoryView(viewModel: TajrubahViewModel) {
    val bookings by viewModel.allBookings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "تذاكر حجوزاتي التراثية 🎫🔖",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(text = "الوصول السريع لبطاقات الصعود للرحلات والأكواد البرمجية للتحقق من الهوية وصلاحية الحجز.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))

        Spacer(modifier = Modifier.height(12.dp))

        if (bookings.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = "No tickets", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "ليس لديك أي حجوزات نشطة حالياً.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = { viewModel.navigateTo(Screen.Home) }) {
                        Text(text = "تصفح وحجز التجارب الآن ☕🌾", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bookings) { booking ->
                    BoardingPassTicketCard(booking = booking)
                }
            }
        }
    }
}

// --- BOARDING PASS TICKET COMPONENT ---
@Composable
fun BoardingPassTicketCard(booking: BookingEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ticket_${booking.bookingId}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Upper Ticket portion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = booking.category,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (booking.status == "CONFIRMED") Color(0xFF1E4620) else Color.DarkGray,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                // Active padding for comfortable touch
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (booking.status == "CONFIRMED") "مؤكد 🟢" else "مكتمل ✔️",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = booking.experienceTitleAr, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = booking.experienceTitleEn, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "رقم تذكرة الحجز:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(text = booking.bookingId, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "تاريخ وتوقيت الحجز:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(text = booking.bookingDate, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Perforated line (dashed line)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            ) {
                val pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.5f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect,
                    strokeWidth = 2f
                )
            }

            // Lower Ticket (QR Scanner segment)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vector QR Representer
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cellSize = size.width / 8f
                        // Outer positioning corners
                        drawRect(Color.Black, Offset(0f, 0f), androidx.compose.ui.geometry.Size(cellSize * 3, cellSize * 3))
                        drawRect(Color.White, Offset(cellSize, cellSize), androidx.compose.ui.geometry.Size(cellSize, cellSize))

                        drawRect(Color.Black, Offset(size.width - cellSize * 3, 0f), androidx.compose.ui.geometry.Size(cellSize * 3, cellSize * 3))
                        drawRect(Color.White, Offset(size.width - cellSize * 2, cellSize), androidx.compose.ui.geometry.Size(cellSize, cellSize))

                        drawRect(Color.Black, Offset(0f, size.height - cellSize * 3), androidx.compose.ui.geometry.Size(cellSize * 3, cellSize * 3))
                        drawRect(Color.White, Offset(cellSize, size.height - cellSize * 2), androidx.compose.ui.geometry.Size(cellSize, cellSize))

                        // Scattered random pixel bits
                        val randomBits = listOf(
                            Pair(4, 2), Pair(5, 2), Pair(4, 4), Pair(5, 5), Pair(6, 4),
                            Pair(2, 4), Pair(2, 5), Pair(6, 6), Pair(1, 4), Pair(5, 0)
                        )
                        randomBits.forEach { (x, y) ->
                            drawRect(Color.Black, Offset(x * cellSize, y * cellSize), androidx.compose.ui.geometry.Size(cellSize, cellSize))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Added extras summary
                    if (booking.guideName.isNotEmpty() || booking.hotelName.isNotEmpty() || booking.carModel.isNotEmpty()) {
                        Text(text = "الخدمات الإضافية المشمولة:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        val extras = listOfNotNull(
                            if (booking.guideName.isNotEmpty()) "🧔 مرشد محلي" else null,
                            if (booking.hotelName.isNotEmpty()) "🏡 مبيت تراثي" else null,
                            if (booking.carModel.isNotEmpty()) "🚙 لاندكروزر 4x4" else null
                        )
                        Text(text = extras.joinToString(" + "), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(text = "امسح تذكرة الصعود 🔍", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    Text(text = "برجاء إظهار كود التحقق لمقدم الخدمة عند الوصول.", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "القيمة المدفوعة:", fontSize = 10.sp)
                    Text(
                        text = if (booking.currency == "YER") {
                            "${"%,.0f".format(booking.pricePaid)} YER"
                        } else {
                            "$${"%.2f".format(booking.pricePaid)} USD"
                        },
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// --- MULTI-ROLE DASHBOARD VIEW (Service Provider & Admin) ---
@Composable
fun MultiRoleDashboardView(viewModel: TajrubahViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val experiences by viewModel.allExperiences.collectAsState()
    val bookings by viewModel.allBookings.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Provider, 1: Administrator

    // Syncing role from the main pill switcher, but also letting the tabs override
    LaunchedEffect(currentRole) {
        activeTab = if (currentRole == PlatformRole.ADMIN) 1 else 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(selected = activeTab == 0, onClick = {
                activeTab = 0
                viewModel.setRole(PlatformRole.PROVIDER)
            }) {
                Text(text = "🌾 لوحة مقدم الخدمة", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Tab(selected = activeTab == 1, onClick = {
                activeTab = 1
                viewModel.setRole(PlatformRole.ADMIN)
            }) {
                Text(text = "⚙️ لوحة المسؤول (Admin)", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeTab == 0) {
            ProviderDashboardView(experiences = experiences, bookings = bookings, viewModel = viewModel)
        } else {
            AdminDashboardView(experiences = experiences, bookings = bookings, viewModel = viewModel)
        }
    }
}

// --- EXPERIENCE PROVIDER SUBVIEW ---
@Composable
fun ProviderDashboardView(
    experiences: List<ExperienceEntity>,
    bookings: List<BookingEntity>,
    viewModel: TajrubahViewModel
) {
    var showForm by remember { mutableStateOf(false) }

    // Forms fields states
    var titleAr by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("زراعة وثقافة") }
    var locationAr by remember { mutableStateOf("") }
    var locationEn by remember { mutableStateOf("") }
    var priceYer by remember { mutableStateOf("12000") }
    var durationAr by remember { mutableStateOf("يوم كامل") }
    var durationEn by remember { mutableStateOf("Full Day") }
    var descriptionAr by remember { mutableStateOf("") }
    var descriptionEn by remember { mutableStateOf("") }
    var providerBioAr by remember { mutableStateOf("") }

    val categoriesList = listOf("زراعة وثقافة", "طبيعة ومغامرة", "حرف يدوية", "مأكولات شعبية", "بحر وأنشطة")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Earnings Stats Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "مجموع عوائدك المستلمة:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text(text = "125,000 YER", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = { showForm = !showForm },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = if (showForm) "إغلاق النموذج" else "أضف تجربة جديدة +", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (showForm) {
            // New Experience Input Form
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "أضف تجربة تراثية جديدة للمنصة 🌾✍️", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)

                        OutlinedTextField(value = titleAr, onValueChange = { titleAr = it }, label = { Text("عنوان التجربة بالعربية (مثال: حياكة اللحاف التهامي)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = titleEn, onValueChange = { titleEn = it }, label = { Text("عنوان التجربة بالإنجليزية (En Title)") }, modifier = Modifier.fillMaxWidth())

                        // Category Dropdown selector representation
                        Column {
                            Text(text = "الفئة السياحية التراثية:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                categoriesList.forEach { cat ->
                                    FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(text = cat, fontSize = 11.sp) })
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = locationAr, onValueChange = { locationAr = it }, label = { Text("الموقع (عربي)") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = locationEn, onValueChange = { locationEn = it }, label = { Text("الموقع (En)") }, modifier = Modifier.weight(1f))
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = priceYer, onValueChange = { priceYer = it }, label = { Text("السعر (YER)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = durationAr, onValueChange = { durationAr = it }, label = { Text("المدة (مثال: 5 ساعات)") }, modifier = Modifier.weight(1f))
                        }

                        OutlinedTextField(value = descriptionAr, onValueChange = { descriptionAr = it }, label = { Text("الوصف الثقافي والخطوات بالتفصيل (بالعربية)") }, maxLines = 4, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = providerBioAr, onValueChange = { providerBioAr = it }, label = { Text("نبذة قصيرة عنك كصاحب ومقدم هذه الحرفة") }, modifier = Modifier.fillMaxWidth())

                        Button(
                            onClick = {
                                viewModel.addNewExperience(
                                    titleAr = titleAr,
                                    titleEn = titleEn,
                                    category = category,
                                    locationAr = locationAr,
                                    locationEn = locationEn,
                                    priceYer = priceYer.toDoubleOrNull() ?: 12000.0,
                                    durationAr = durationAr,
                                    durationEn = durationEn,
                                    descriptionAr = descriptionAr,
                                    descriptionEn = descriptionEn.ifEmpty { "Cultural traditional experience in Yemen." },
                                    providerNameAr = "مقدم خدمة محلي",
                                    providerNameEn = "Local Provider",
                                    providerBioAr = providerBioAr,
                                    providerBioEn = "Experienced Yemeni heritage provider."
                                )
                                showForm = false
                            },
                            modifier = Modifier.fillMaxWidth().testTag("add_experience_submit"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(text = "نشر ومشاركة التجربة فوراً 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Manage Bookings section
        item {
            Text(text = "الحجوزات الواردة لخدماتك 📥", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        val providerBookings = bookings.filter { it.status == "CONFIRMED" }
        if (providerBookings.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    Text(text = "لا توجد حجوزات نشطة واردة حالياً.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        } else {
            items(providerBookings) { booking ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "كود: ${booking.bookingId}", fontWeight = FontWeight.Bold)
                            Text(text = "${"%,.0f".format(booking.pricePaid)} ${booking.currency}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                        }
                        Text(text = "التجربة المحجوزة: ${booking.experienceTitleAr}", fontSize = 13.sp)
                        Text(text = "التاريخ: ${booking.bookingDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.updateBookingStatus(booking.bookingId, "COMPLETED") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E4620)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "إكمال الزيارة ✔️", color = Color.White, fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.updateBookingStatus(booking.bookingId, "CANCELLED") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "إلغاء الحجز ❌", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- ADMIN SYSTEM CONTROLS VIEW ---
@Composable
fun AdminDashboardView(
    experiences: List<ExperienceEntity>,
    bookings: List<BookingEntity>,
    viewModel: TajrubahViewModel
) {
    val totalRevenue = bookings.filter { it.status != "CANCELLED" }.sumOf {
        if (it.currency == "YER") it.pricePaid else it.pricePaid * 600.0
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Analytics grid counters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "إحصائيات منصة تِجربة الموحدة 📊", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnalyticsCard(title = "التجارب المسجلة", counter = "${experiences.size}", modifier = Modifier.weight(1f))
                    AnalyticsCard(title = "الحجوزات الكلية", counter = "${bookings.size}", modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnalyticsCard(title = "مجموع المداولات بالريال", counter = "${"%,.0f".format(totalRevenue)} YER", modifier = Modifier.weight(1.5f))
                    AnalyticsCard(title = "نسبة رضا السياح", counter = "98%", modifier = Modifier.weight(0.8f))
                }
            }
        }

        // Experiences list for direct Admin management
        item {
            Text(text = "إدارة التجارب التراثية المنشورة 📑", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        if (experiences.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text(text = "لا توجد تجارب معروضة حالياً.")
                }
            }
        } else {
            items(experiences) { exp ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = exp.titleAr, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "الموقع: ${exp.locationAr} | الفئة: ${exp.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                        IconButton(
                            onClick = { viewModel.deleteExperience(exp.id) },
                            modifier = Modifier.background(Color.Red.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(title: String, counter: String, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = counter, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
