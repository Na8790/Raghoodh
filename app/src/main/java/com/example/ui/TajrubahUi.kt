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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
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
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .frostedGlassBackground(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "تِجربة",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp,
                            modifier = Modifier.clickable { viewModel.navigateTo(Screen.Home) }
                        )
                    },
                    actions = {
                        // Book button for Graduation documentation
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.ProjectDocs) },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "كتاب مشروع التخرج الشامل",
                                tint = Color(0xFFC59B27), // Gold
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        // Multi-Role Pill Switcher
                        RoleSwitcher(
                            currentRole = currentRole,
                            onRoleSelected = { viewModel.setRole(it) }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
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
                        is Screen.ProjectDocs -> ProjectDocumentationCenter(viewModel = viewModel)
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
            .frostedGlassBackground()
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
                    containerColor = Color(0x1BFFFFFF)
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0x33FFFFFF)),
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
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color(0xFFC59B27),
        selectedTextColor = Color(0xFFC59B27),
        indicatorColor = Color(0x33C59B27),
        unselectedIconColor = Color(0xFF94A3B8),
        unselectedTextColor = Color(0xFF94A3B8)
    )

    NavigationBar(
        containerColor = Color(0x3D080B14), // Glass dark obsidian
        tonalElevation = 0.dp,
        modifier = Modifier.border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x33FFFFFF), Color.Transparent)
            ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        )
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = { Icon(imageVector = Icons.Default.Explore, contentDescription = "Home") },
            label = { Text("استكشاف", fontSize = 11.sp) },
            colors = navItemColors
        )
        NavigationBarItem(
            selected = currentScreen is Screen.AiPlanner,
            onClick = { onNavigate(Screen.AiPlanner) },
            icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Planner") },
            label = { Text("مخطط الذكاء", fontSize = 11.sp) },
            colors = navItemColors
        )
        NavigationBarItem(
            selected = currentScreen is Screen.AiGuideChat,
            onClick = { onNavigate(Screen.AiGuideChat) },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat") },
            label = { Text("المرشد الذكي", fontSize = 11.sp) },
            colors = navItemColors
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Bookings,
            onClick = { onNavigate(Screen.Bookings) },
            icon = { Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = "Tickets") },
            label = { Text("تذاكري", fontSize = 11.sp) },
            colors = navItemColors
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Wallet || currentScreen is Screen.Dashboard,
            onClick = { onNavigate(Screen.Wallet) },
            icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
            label = { Text("المحفظة", fontSize = 11.sp) },
            colors = navItemColors
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

        // Graduation Project Documentation launcher banner
        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.ProjectDocs) },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x1BFFFFFF)
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0x3DFFFFFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("grad_docs_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFC59B27).copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Graduation Symbol",
                                tint = Color(0xFFC59B27),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مركز التوثيق والأبحاث الأكاديمية",
                                color = Color(0xFFC59B27),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اضغط هنا لتصفح كتاب التوثيق لمشروع التخرج كاملاً، والمخططات الهندسية (UML/DFD)، ومحاكاة الأنظمة الفرعية الذكية!",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Navigate to Docs",
                        tint = Color(0xFFC59B27),
                        modifier = Modifier.size(24.dp)
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
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x1AFFFFFF),
                        unfocusedContainerColor = Color(0x0FFFFFFF),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0x22FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFE2E8F0)
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
                        tint = Color.Black,
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
                        label = { Text(text = category, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0x14FFFFFF),
                            labelColor = Color(0xFFE2E8F0)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == category,
                            selectedBorderColor = Color.Transparent,
                            borderColor = Color(0x1AFFFFFF),
                            selectedBorderWidth = 0.dp,
                            borderWidth = 1.dp
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
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
        colors = CardDefaults.cardColors(containerColor = Color(0x11FFFFFF)),
        elevation = CardDefaults.cardElevation(0.dp)
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
            .background(Color(0x13FFFFFF), RoundedCornerShape(12.dp))
            .border(1.dp, if (checked) MaterialTheme.colorScheme.primary else Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(12.dp),
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
        border = BorderStroke(1.dp, Color(0x33FFFFFF)),
        colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Upper Ticket portion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x14FFFFFF),
                                Color.Transparent
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

// --- FROSTED GLASS MESH GRADIENT MODIFIER ---
fun Modifier.frostedGlassBackground(): Modifier = this.drawBehind {
    val width = size.width
    val height = size.height

    // 1. Draw solid deep obsidian dark base
    drawRect(color = Color(0xFF080B14))

    // 2. Draw Glow Backdrops (Mesh Gradient Blobs)
    // Top-left Amber/Orange glow blob (80dp-like radius)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFD97706).copy(alpha = 0.22f), Color.Transparent),
            center = Offset(-width * 0.15f, -height * 0.05f),
            radius = width * 0.8f
        ),
        center = Offset(-width * 0.15f, -height * 0.05f),
        radius = width * 0.8f
    )

    // Center-left/middle Purple glow blob
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF9333EA).copy(alpha = 0.15f), Color.Transparent),
            center = Offset(width * 0.1f, height * 0.45f),
            radius = width * 0.55f
        ),
        center = Offset(width * 0.1f, height * 0.45f),
        radius = width * 0.55f
    )

    // Bottom-right Teal glow blob (100dp-like radius)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF0D9488).copy(alpha = 0.18f), Color.Transparent),
            center = Offset(width * 1.15f, height * 0.8f),
            radius = width * 0.9f
        ),
        center = Offset(width * 1.15f, height * 0.8f),
        radius = width * 0.9f
    )
}

// =========================================================================
// 🎓 GRADUATION PROJECT DOCUMENTATION & ADVANCED SUBSYSTEMS CENTER
// =========================================================================
@Composable
fun ProjectDocumentationCenter(viewModel: TajrubahViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val experiences by viewModel.allExperiences.collectAsState()
    val bookings by viewModel.allBookings.collectAsState()
    
    var activeMainTab by remember { mutableStateOf(0) } // 0: Book, 1: UML Diagrams, 2: Smart Simulators
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Navigation Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.Home) },
                modifier = Modifier.background(Color(0x1AFFFFFF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "المركز الأكاديمي والتوثيق 🎓",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = "كتاب توثيق تخرج منصة تِجربة ومحاكي الأنظمة",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Graduation Hero Banner (Engineer Raghad)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x1BFFFFFF)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFC59B27).copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Academic Cap",
                    tint = Color(0xFFC59B27),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "مشروع تخرج البكالوريوس في هندسة البرمجيات",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFFC59B27),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "تطوير منصة تِجربة (Tajrubah) التراثية الرقمية لليمن",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "المصممة المنفذة: م. رغد | الجمهورية اليمنية",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .background(Color(0x2BFFFFFF), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF22C55E), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "نظام نشط | كاشف الاحتيال محمي بنسبة 100%",
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Main Tab Selection Row
        ScrollableTabRow(
            selectedTabIndex = activeMainTab,
            containerColor = Color.Transparent,
            contentColor = Color(0xFFC59B27),
            edgePadding = 0.dp,
            divider = {}
        ) {
            Tab(
                selected = activeMainTab == 0,
                onClick = { activeMainTab = 0 }
            ) {
                Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Book, contentDescription = "Book", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "كتاب التوثيق الأكاديمي", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Tab(
                selected = activeMainTab == 1,
                onClick = { activeMainTab = 1 }
            ) {
                Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Assessment, contentDescription = "Diagrams", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "المخططات والرسومات (UML)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Tab(
                selected = activeMainTab == 2,
                onClick = { activeMainTab = 2 }
            ) {
                Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = "Simulators", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "محاكاة الأنظمة الذكية", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Tab Content
        when (activeMainTab) {
            0 -> DocumentationBookView()
            1 -> SystemDiagramsView()
            2 -> SmartSubsystemsSimulatorView(viewModel = viewModel)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// =========================================================================
// SUB-VIEW: DOCUMENTATION BOOK CHAPTERS (ACCORDION STYLE)
// =========================================================================
@Composable
fun DocumentationBookView() {
    var expandedChapter by remember { mutableStateOf<Int?>(0) }
    
    val chapters = listOf(
        ChapterData(
            title = "الأبواب التمهيدية والملخص التنفيذي",
            icon = Icons.Default.School,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "📄 غلاف المشروع الرسمي", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "العنوان: تصميم وتطوير المنصة التراثية الرقمية لليمن 'تِجربة' (Tajrubah) باستخدام هندسة الهواتف المحمولة والذكاء الاصطناعي.\nالجهة: كلية الهندسة وتقنية المعلومات.\nالمنفذة: م. رغد.\nالعام الأكاديمي: 2026م.", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                    
                    Text(text = "📜 الإهداء وشكر التقدير", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "نهدي هذا العمل إلى أهلنا في اليمن المتمسكين بتراثهم وتاريخهم؛ إلى مزارعي البن في حراز، ونحالي دوعن، وصائغي الجنابي بباب اليمن، وصيادي صيرة بـعدن الأبية، وحماة أشجار دم الأخوين بسقطرى.\nكما نتقدم بالشكر للدكاترة والمشرفين الأكاديميين الأجلاء الذين دعموا مسيرتنا العلمية.", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                    
                    Text(text = "الملخص باللغة العربية (Abstract - AR)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "تعد السياحة الثقافية والتراثية محركاً اقتصادياً هاماً، لكن التراث اليمني الفريد يعاني من العزلة والغياب الرقمي. يهدف هذا المشروع إلى بناء منصة 'تِجربة' وهي تطبيق محمول تفاعلي يهدف لربط السياح بتجارب ثقافية حية مباشرة مع الحرفيين والمزارعين المحترفين في اليمن. يدعم النظام تقنيات التخزين المحلي SQLite (Room) لضمان العمل غير المتصل بالإنترنت، ونظام الدفع المتعدد ثنائي العملة (ريال يمني/دولار)، ونظام جدولة الرحلات الذكي بالذكاء الاصطناعي عبر Gemini، ونظام مكافآت ونقاط تفاعلي لتعزيز الهوية والولاء الوطني.", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                    
                    Text(text = "English Abstract", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "The 'Tajrubah' platform is a mobile application dedicated to preserving and digitizing Yemen's rich intangible cultural heritage. By connecting travelers directly with authentic local experience providers—such as Khawlani coffee harvesters, Socotra eco-guides, Janbiyah artisans, and Sira fishermen—the app builds an ethical micro-economy in Yemen. Implementing a dual-currency digital wallet, an offline-resilient local Room database, a smart generative AI trip advisor using Gemini, and a gamified referral system, Tajrubah provides a production-grade software model for cultural preservation and sustainable tourism.", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                }
            }
        ),
        ChapterData(
            title = "الفصل الأول: مشكلة الدراسة، الأهداف والجدوى الخماسية",
            icon = Icons.Default.TrendingUp,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "⚠️ مشكلة الدراسة (Problem Statement)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "1. غياب قنوات التسويق والاتصال الرقمي لصالح المجتمعات الحرفية والزراعية النائية في اليمن.\n2. صعوبة تنظيم وتأكيد الرحلات الثقافية الموثوقة مع الحرفيين المحليين.\n3. ضعف البنية المالية السياحية التقليدية والحاجة لنظام تتبع محفظة إلكتروني مرن يدعم الريال اليمني والدولار بمرونة تامة.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

                    Text(text = "🎯 أهداف المشروع العامة والتفصيلية", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "• الهدف العام: تمكين الاقتصاد التراثي اليمني رقمياً ونشر ثقافة السياحة البيئية والمجتمعية الحقيقية.\n• الأهداف التفصيلية:\n- إنشاء واجهة تفاعلية زجاجية (Frosted Glass UX) تبرز جمال الهوية البصرية لليمن.\n- دمج محاكي دردشة ذكي كمرشد ثقافي معزز بالذكاء الاصطناعي.\n- تطبيق محاكي حماية ضد الحجوزات الوهمية لتأمين إيرادات مقدمي الخدمة.\n- تصميم هيكلية برمجية قابلة للتوسع لإضافة فنادق، مرشدين، وسيارات في اليمن.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

                    Text(text = "📈 دراسة الجدوى الخماسية (5D Feasibility Study)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "1. الجدوى الاقتصادية: توليد دخل مستدام للسياحة المحلية والحد من الفقر عبر عمولة بسيطة (10%) تُستقطع فقط من قيمة الحجوزات المؤكدة.\n2. الجدوى التقنية: ملاءمة لغة Kotlin وJetpack Compose مع نظام قواعد بيانات Room الداخلي، مما يوفر سرعة استجابة هائلة وجودة عالية.\n3. الجدوى التشغيلية: تصميم واجهات عربية مبسطة وبسيطة جداً لا تتطلب من المزارع أو الحرفي سوى بضع نقرات لإدارة حجوزاته.\n4. الجدوى القانونية: الامتثال لقوانين النشر وحماية الخصوصية للمستخدمين والتوافق مع التراخيص السياحية المحلية في اليمن.\n5. الجدوى الزمنية: تخطيط تسلسلي للمشروع خلال 16 أسبوعاً من التحليل والترميز والاختبار وضمان الجودة بنجاح.", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                }
            }
        ),
        ChapterData(
            title = "الفصل الثاني: تحليل المتطلبات، قاموس البيانات والجدول الزمني",
            icon = Icons.Default.Assessment,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "📌 المتطلبات الوظيفية (Functional Requirements)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "• نظام استكشاف وحجز التجارب التراثية لليمن.\n• نظام الدفع ثنائي العملة (ريال يمني/دولار) ومحفظة قابلة للشحن.\n• منشئ مسار الرحلة الذكي (AI Trip Planner) المعتمد على الميزانية والمدينة والاهتمامات.\n• محاكي دردشة المرشد الثقافي الذكي بالذكاء الاصطناعي.\n• بوابات الخدمة المتخصصة (مقدم التجربة، المسؤول، شركات السيارات، الفنادق، المرشدين المحليين).\n• نظام الأرباح والعمولات كاشف الاحتيال وحماية الحجز.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

                    Text(text = "⚙️ المتطلبات غير الوظيفية (Non-Functional Requirements)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "• الأمان والخصوصية: تشفير كامل لبيانات الحجز وتأمين كود التحقق QR Code.\n• الاستجابة والمرونة: تصميم متجاوب لجميع الشاشات ودعم استمرارية العمل بدون شبكة عبر Room كاش للبيانات.\n• سهولة الاستخدام: واجهة داكنة مريحة للعين ليلاً مدمجة بهوية يمنية أصيلة.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

                    Text(text = "📘 قاموس البيانات (Data Dictionary)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "يحتوي النظام على الجداول الأساسية التالية في قاعدة البيانات المحلية SQLite (Room):\n\n1. جدول التجارب (experiences):\n- id (TEXT, Key) | titleAr/titleEn (TEXT) | priceYer (DOUBLE) | category (TEXT)\n\n2. جدول الحجوزات (bookings):\n- bookingId (TEXT, Key) | experienceId (TEXT) | pricePaid (DOUBLE) | currency (TEXT) | status (TEXT)\n\n3. جدول الملف الشخصي (user_profile):\n- userId (TEXT, Key) | name (TEXT) | walletBalanceYer/Usd (DOUBLE) | points (INT) | badgeAr (TEXT)", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                }
            }
        ),
        ChapterData(
            title = "الفصل الثالث: بنية النظام، دليل التشغيل ودراسة السوق",
            icon = Icons.Default.Share,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "🏰 بنية النظام متعددة الطبقات (Multi-Tier Architecture)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "• الطبقة الأولى (Presentation Layer): واجهة المستخدم المصممة بـ Jetpack Compose ذات المظهر البلوري وتأثيرات Frosted Glass.\n• الطبقة الثانية (Business Logic / ViewModel): تنظيم حالات التطبيق والربط مع محرك الذكاء الاصطناعي ونظام معالجة الدفع والتحقق.\n• الطبقة الثالثة (Data Access Layer - Repository): تنسيق تبادل البيانات وجلبها من التخزين المحلي أو جلبها خارجياً من API ومزامنتها.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

                    Text(text = "🔒 بروتوكولات الأمان والحماية", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "تتبع المنصة بروتوكولات حماية قوية تتضمن توثيق الهوية عبر أكواد الـ OTP الذكية ثنائية المعامل، وتأمين وحماية بوابات الـ REST API من هجمات الإغراق، بالإضافة للتحقق الأمني من سلامة الحجوزات عبر خوارزميات كشف الحجوزات الوهمية لتفادي احتيال الروبوتات.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

                    Text(text = "📈 دراسة السوق ونموذج العمل التجاري (BMC)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
                    Text(text = "• الشريحة المستهدفة: السياح المحليين الباحثين عن تجارب فريدة، والمهتمين بالتعرف على ثقافة اليمن العميقة.\n• مصادر الإيرادات: استقطاع عمولة 10% من الحجوزات المدفوعة للمزارعين والحرفيين لضمان تغطية تكاليف الخادم والتحديثات.\n• الشركاء الرئيسيون: نقابات مزارعي البن اليمني، جمعيات الحرف اليدوية بباب اليمن، منظمي الرحلات البيئية المحلية.", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                }
            }
        ),
        ChapterData(
            title = "الفصل الخامس: مخطط السياق (Context Diagram)",
            icon = Icons.Default.Language,
            content = { ChapterFiveContent() }
        ),
        ChapterData(
            title = "الفصل السادس: مخطط حالات الاستخدام (Use Case)",
            icon = Icons.Default.Groups,
            content = { ChapterSixContent() }
        ),
        ChapterData(
            title = "الفصل السابع: مخططات تدفق البيانات (DFD Levels)",
            icon = Icons.Default.Layers,
            content = { ChapterSevenContent() }
        ),
        ChapterData(
            title = "الفصل الثامن: مخطط الكيانات والعلاقات (ERD)",
            icon = Icons.Default.Dns,
            content = { ChapterEightContent() }
        ),
        ChapterData(
            title = "الفصل التاسع: مخطط الفئات (Class Diagram)",
            icon = Icons.Default.AccountTree,
            content = { ChapterNineContent() }
        ),
        ChapterData(
            title = "الفصل الحادي والعشرون: تصميم واجهات الـ API (REST API Design)",
            icon = Icons.Default.Code,
            content = { ChapterTwentyOneContent() }
        ),
        ChapterData(
            title = "ملحق حالات الاستخدام التفصيلية (UC-007 إلى UC-020)",
            icon = Icons.Default.AssignmentTurnedIn,
            content = { UseCasesContent() }
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        chapters.forEachIndexed { index, chapter ->
            val isExpanded = expandedChapter == index
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isExpanded) Color(0xFFC59B27) else Color(0x1AFFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedChapter = if (isExpanded) null else index }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = chapter.icon,
                                contentDescription = chapter.title,
                                tint = if (isExpanded) Color(0xFFC59B27) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = chapter.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isExpanded) Color(0xFFC59B27) else Color.White
                            )
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = if (isExpanded) Color(0xFFC59B27) else Color(0xFF94A3B8)
                        )
                    }

                    if (isExpanded) {
                        Divider(color = Color(0x1AFFFFFF), thickness = 1.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            chapter.content()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterFiveContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "🌐 5.1 مقدمة مخطط السياق (Context Diagram)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "يُعد مخطط السياق أولى مراحل تحليل النظام، ويستخدم لإظهار حدود النظام والعلاقات بين المنصة والجهات الخارجية (External Entities) دون الدخول في تفاصيل العمليات الداخلية.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

        Text(text = "🏢 5.2 النظام الرئيسي (Tajrubah Platform)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 13.sp)
        Text(text = "يمثل النواة المركزية لإدارة المستخدمين، الحجوزات، الدفع الإلكتروني، الذكاء الاصطناعي، الإشعارات، والتقارير.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

        Text(text = "👥 5.3 الكيانات الخارجية (External Entities)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 13.sp)
        Text(text = "• المستخدم (Traveler): حجز، دفع، تصفح، تقييم.\n• مقدم التجربة (Experience Provider): إدارة التجارب وقبول الطلبات.\n• الفنادق (Hotels): إدارة الغرف وحالة الإشغال والأسعار.\n• شركات تأجير السيارات: إدارة السيارات وعقود التأجير.\n• المرشد السياحي: تنظيم الجولات والتواصل.\n• مدير النظام (Admin): مراقبة النظام والتقارير والعمولات.\n• بوابة الدفع / خدمات الرسائل (SMS & Email) / الخرائط (Google Maps) / محرك الذكاء الاصطناعي.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

        Text(text = "🔄 5.4 تدفقات البيانات (Data Flows)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 13.sp)
        Text(text = "• المستخدم ➜ يدخل التسجيل والحجز والدفع ➜ النظام\n• النظام ➜ يعيد نتائج البحث وتأكيد الحجز والتذاكر QR ➜ المستخدم\n• مقدم الخدمة ➜ يرسل بيانات التجارب والأسعار ➜ النظام\n• النظام ➜ يرسل الحجوزات والأرباح والتقارير ➜ مقدم الخدمة", fontSize = 12.sp, color = Color(0xFFE2E8F0))
    }
}

@Composable
fun ChapterSixContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "🎭 6.1 الممثلون في النظام (Actors)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "• A1: المستخدم (Traveler) - يبحث، يحجز، يدفع، ويقيم.\n• A2: مقدم التجربة (Experience Provider) - ينشئ التجارب، يحدد المواعيد، يتابع الأرباح.\n• A3: الفندق (Hotel) - يدير الغرف والأسعار والتوفر.\n• A4: شركة تأجير السيارات - تضيف المركبات، تحدد الأسعار وعقود الإيجار.\n• A5: المرشد السياحي - ينظم الجولات الزمنية ويستقبل الحجوزات.\n• A6: مدير النظام (Admin) - يدير العمليات، العمولات، الصلاحيات، والنسخ الاحتياطي.\n• A7: الأنظمة الخارجية - بوابة الدفع، الخرائط، SMS/OTP.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

        Text(text = "⚙️ 6.2 حالات الاستخدام الأساسية للمستخدم (Traveler Use Cases)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 13.sp)
        Text(text = "1. إنشاء حساب وتسجيل الدخول وتحديث الملف.\n2. البحث والاستكشاف (تجارب، فنادق، سيارات، مرشدين).\n3. حجز وتأكيد الخدمة والدفع الإلكتروني.\n4. استخدام مساعد الذكاء الاصطناعي (AI Advisor) لإنشاء رحلة ذكية.\n5. إنشاء رحلة مفاجئة وشراء هدية سياحية وتواصل عبر الدردشة.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

        Text(text = "🤝 6.3 العلاقات البرمجية (Relationships)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 13.sp)
        Text(text = "• Association: لربط الممثلين بحالات استخدامهم.\n• Include: حجز تجربة يتضمن التحقق من التوفر والدفع الإلكتروني.\n• Extend: تقييم الخدمة يمتد اختيارياً بعد اكتمال الحجز بنجاح.\n• Generalization: يرث جميع مقدمي الخدمات (تجارب، فنادق، سيارات، مرشدين) الخصائص من كيان أب عام يسمى (Service Provider).", fontSize = 12.sp, color = Color(0xFFE2E8F0))
    }
}

@Composable
fun ChapterSevenContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "📊 7.1 مستويات تدفق البيانات (DFD Levels)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        
        Text(text = "• Level 0 (مخطط تدفق البيانات العام):", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 13.sp)
        Text(text = "يوضح تدفق البيانات من الكيانات الخارجية إلى العمليات الرئيسية التسعة: (P1: إدارة الحسابات، P2: إدارة التجارب، P3: البحث، P4: إدارة الحجوزات، P5: إدارة المدفوعات، P6: الذكاء الاصطناعي، P7: الإشعارات، P8: التقارير، P9: إدارة النظام) وتخزينها في 12 مخزن بيانات (D1 إلى D12).", fontSize = 11.sp, color = Color(0xFFE2E8F0))

        Text(text = "• Level 1 (تفصيل العمليات الفرعية):", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 13.sp)
        Text(text = "يفصل كل عملية رئيسية؛ مثلاً إدارة الحجوزات P4 تنقسم إلى: (P4.1 إنشاء الحجز، P4.2 فحص التوفر، P4.3 تأكيد الحجز، P4.4 إلغاء الحجز، P4.5 إصدار الفاتورة).", fontSize = 11.sp, color = Color(0xFFE2E8F0))

        Text(text = "• Level 2 (التفصيل الإجرائي الدقيق):", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 13.sp)
        Text(text = "يركز على العمليات الحرجة كالمعاملات المالية والدفع الآمن (P5.1.1 إلى P5.1.10) حيث يتم تنفيذ العمليات بنظام (Database Transactions) لضمان سلامة العمليات المالية وتجنب تضارب حجز الغرف أو المقاعد.", fontSize = 11.sp, color = Color(0xFFE2E8F0))
    }
}

@Composable
fun ChapterEightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "🗄️ 8.1 هيكل قاعدة البيانات المركزي (Logical ERD)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "تم تصميم وتطوير قاعدة البيانات بالتوافق مع شروط التطبيع الثالث (3NF) لضمان تقليص التكرار وتحسين الأداء وسرعة الاستعلام. تحتوي قاعدة البيانات على 30 جدولاً رئيسياً ومساعداً:", fontSize = 12.sp, color = Color(0xFFE2E8F0))

        Text(text = "🔑 8.2 الجداول الرئيسية في النظام", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 13.sp)
        Text(text = "1. Users: يحفظ بيانات المسافرين والمديرين ومقدمي الخدمات.\n2. Service_Providers: يحتوي على تفاصيل الأنشطة والتراخيص ونسب العمولات.\n3. Experiences: تفاصيل ومواقع وأسعار الأنشطة التراثية.\n4. Bookings & Payments: سجلات المعاملات المالية والحجوزات وحالاتها.\n5. Hotels & Rooms / Cars / Tour_Guides: جداول تتبع الغرف والتوفر والمواصفات.\n6. Reviews / Favorites / Notifications / Conversations & Messages: تتبع تفاعل المستخدم والمحادثات الفورية الآمنة.", fontSize = 12.sp, color = Color(0xFFE2E8F0))

        Text(text = "🔗 8.3 العلاقات والتكامل المرجعي (Referential Integrity)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 13.sp)
        Text(text = "• علاقة واحد لمتعدد (1..N): مثل المستخدم يستطيع إنشاء عدة حجوزات، ومقدم الخدمة يملك عدة تجارب، والفندق يحتوي على عدة غرف.\n• علاقة متعدد لمتعدد (M..N): مثل تصنيف التجارب والدرجات الوظيفية، ويتم فكها بجداول وسيطة (User_Roles, Experience_Categories).\n• الأمان: استخدام معرّفات فريدة UUID لجميع الحقول كمفاتيح أساسية، وتفعيل الحذف المؤقت (Soft Delete) للمحافظة على البيانات التاريخية والتقارير المالية.", fontSize = 12.sp, color = Color(0xFFE2E8F0))
    }
}

@Composable
fun UseCasesContent() {
    var selectedUc by remember { mutableStateOf(0) }
    
    val ucs = listOf(
        "UC-007: حجز تجربة",
        "UC-008: حجز فندق",
        "UC-009: حجز سيارة",
        "UC-011: الدفع الإلكتروني",
        "UC-012: نظام التقييمات",
        "UC-013: الإشعارات الذكية",
        "UC-014: الدردشة المباشرة",
        "UC-015: مساعد AI للسفر",
        "UC-016: الرحلات المفاجئة",
        "UC-017: الهدايا السياحية",
        "UC-019: الأرباح والعمولات",
        "UC-020: التحليلات والتقارير"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "📌 تفاصيل حالات الاستخدام الأكاديمية (Academic Use Cases Specification)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "اختر حالة الاستخدام لاستعراض السيناريوهات وقواعد العمل المتبعة برمجياً:", fontSize = 11.sp, color = Color(0xFF94A3B8))

        // Horizontal scrollable tags for Use Cases
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ucs.size) { index ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedUc == index) Color(0xFFC59B27) else Color(0x13FFFFFF))
                        .clickable { selectedUc = index }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = ucs[index], fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedUc == index) Color.Black else Color.White)
                }
            }
        }

        Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))

        // Render selected use case details in a structured layout
        when (selectedUc) {
            0 -> UcDetailLayout(
                id = "UC-007", name = "حجز تجربة (Book Experience)",
                goal = "تمكين المستخدم من حجز أي تجربة سياحية/تراثية يمنية بطريقة إلكترونية آمنة.",
                actors = "المستخدم (سائح)، مقدم التجربة، بوابة الدفع، قاعدة البيانات.",
                pre = "وجود حساب فعال، تسجيل دخول، توفر مقاعد شاغرة بالتجربة وموعد متاح.",
                inputs = "معرف التجربة، التاريخ والوقت، عدد الأشخاص، بيانات الدفع.",
                flow = "1. يدخل صفحة تفاصيل التجربة ويضغط 'احجز الآن'.\n2. يحدد التاريخ والوقت وعدد المشاركين.\n3. يحسب النظام السعر الإجمالي ويعرض ملخص الفاتورة.\n4. يختار وسيلة الدفع وينفذ العملية بنجاح.\n5. يخصم النظام المقاعد وينشئ سجل الحجز وفاتورة إلكترونية مع رمز QR للحجز.\n6. يرسل إشعارات تأكيد لجميع الأطراف.",
                alt = "A1: عدم توفر مقاعد ➜ يعرض مواعيد بديلة.\nA2: فشل الدفع ➜ يعرض تنبيه ويطلب إعادة المحاولة.\nA3: رفض الحجز من المزود ➜ استرداد المبلغ بالكامل للمحفظة.",
                post = "إنشاء حجز جديد، خصم السعة المتوفرة، إصدار الفاتورة وتوليد رمز الـ QR Code الخاص بالحجز.",
                rules = "BR-030: لا يسمح بتجاوز السعة المحددة للتجربة.\nBR-031: لا يتم تأكيد الحجز إلا بعد نجاح الدفع.\nBR-033: يتاح تقييم التجربة فقط بعد اكتمالها بنجاح."
            )
            1 -> UcDetailLayout(
                id = "UC-008", name = "حجز فندق (Book Hotel)",
                goal = "تمكين المستخدم من البحث عن الفنادق وغرف الضيافة التراثية في اليمن وحجزها.",
                actors = "المستخدم (سائح)، إدارة الفندق، بوابة الدفع، نظام الخرائط.",
                pre = "وجود حساب فعال، وتوافر غرفة شاغرة بالتاريخ المحدد.",
                inputs = "اسم الفندق، نوع الغرفة، تاريخ الوصول والمغادرة، عدد الليالي والنزلاء.",
                flow = "1. يبحث عن الفنادق في الوجهة ويختار فندقاً.\n2. يعرض النظام تفاصيل الفندق والغرف والأسعار والتقييمات.\n3. يختار الغرفة ويدخل التواريخ ويحسب النظام التكلفة الإجمالية.\n4. يدفع القيمة ويقوم النظام بتحديث حالة الغرفة وإرسال التأكيد الفوري.",
                alt = "A1: الغرفة غير متاحة ➜ يقترح فنادق تراثية بديلة.\nA2: إلغاء الحجز ➜ يتحقق من سياسة الإلغاء ويخصم رسوم الإلغاء إن وجدت.",
                post = "تحديث حالة الغرفة إلى مأهولة، إنشاء سجل مالي وفاتورة وإخطار الفندق.",
                rules = "BR-036: يمنع حجز غرف غير متاحة.\nBR-037: لا يمكن أن يكون المغادرة قبل تاريخ الوصول.\nBR-039: يمنع تجاوز الحد الأقصى لعدد النزلاء في الغرفة الواحد."
            )
            2 -> UcDetailLayout(
                id = "UC-009", name = "حجز سيارة (Book Rental Car)",
                goal = "تمكين المستخدم من استئجار سيارات الدفع الرباعي والسيارات السياحية مع/بدون سائق.",
                actors = "المستخدم، شركة تأجير السيارات، نظام الخرائط (GPS)، بوابة الدفع.",
                pre = "وجود حساب، رخصة قيادة سارية ومقبولة (إذا كان الحجز بدون سائق).",
                inputs = "المدينة، تاريخ ووقت الاستلام والتسليم, نوع السيارة، موقع التوصيل، اختيار سائق.",
                flow = "1. تصفح السيارات المتاحة بالمنطقة واختيار المركبة.\n2. تحديد خدمات إضافية (سائق، مقعد أطفال، إنترنت متنقل، تأمين شامل).\n3. مراجعة العقد الإلكتروني ودفع المبلغ.\n4. توليد تذكرة QR لاستلام السيارة وإخطار الشركة لتوثيق الحالة بالصور.",
                alt = "A2: عدم وجود رخصة ➜ يمنع الحجز بدون سائق ويطلب رفع صورة رخصة سارية المفعول.",
                post = "إنشاء سجل إيجار وعقد إلكتروني موثق، وتغيير حالة السيارة لغير متاحة.",
                rules = "BR-042: يجب التحقق من صلاحية رخصة القيادة قبل تسليم السيارة.\nBR-045: توقيع العقد الإلكتروني إلزامي لتفادي النزاعات القانونية."
            )
            3 -> UcDetailLayout(
                id = "UC-011", name = "الدفع الإلكتروني (Electronic Payment)",
                goal = "إتمام العمليات المالية للحجوزات بطريقة آمنة بضمان معايير PCI DSS وحساب الضمان (Escrow).",
                actors = "المستخدم، بوابة الدفع، البنوك والمحافظ اليمنية (فلوسك، كاش)، محفظة تِجربة.",
                pre = "وجود حجز معلق وصالح، وتوفر رصيد كافٍ أو بطاقة دفع مقبولة.",
                inputs = "رقم الحجز، القيمة، طريقة الدفع، كود خصم للتفعيل.",
                flow = "1. يعرض النظام ملخص الطلب والرسوم والضرائب.\n2. يختار المستخدم وسيلة الدفع (بطاقة، محفظة إلكترونية، رصيد المحفظة).\n3. تتم معالجة العملية واحتجاز المبلغ في حساب الضمان (Escrow) لحماية الأطراف.\n4. يصدر النظام فاتورة إلكترونية مفصلة ويحدث حالة الحجز إلى Paid.",
                alt = "A3: استخدام محفظة تِجربة ➜ يخصم الرصيد مباشرة دون الرجوع لبوابة الدفع الخارجية.",
                post = "تغيير حالة الحجز، تحديث السجل المالي غير القابل للتعديل، تحويل نسبة العمولة وإضافة نقاط المكافأة.",
                rules = "BR-055: يمنع تكرار دفع الحجز الناجح.\nBR-058: تشفير كامل للبيانات وتطبيق معايير PCI DSS لعدم تخزين أرقام البطاقات الحساسة."
            )
            4 -> UcDetailLayout(
                id = "UC-012", name = "نظام التقييمات والتعليقات (Ratings & Reviews)",
                goal = "تمكين المستخدم من تقييم جودة الخدمة لتعزيز الثقة وتحسين توصيات الذكاء الاصطناعي.",
                actors = "المستخدم، مقدم الخدمة، محرك الذكاء الاصطناعي لتصفية التعليقات.",
                pre = "أن تكون حالة الحجز مكتملة (Completed) ومرتبطة بحساب المستخدم.",
                inputs = "عدد النجوم (1-5)، تعليق نصي، صور ومقاطع فيديو اختيارية.",
                flow = "1. يرسل النظام إشعاراً لطلب التقييم بعد انتهاء الرحلة.\n2. يحدد المستخدم تقييم جوانب الخدمة (النظافة، السعر، التعامل).\n3. يكتب التعليق ويرفق الصور ويرسل البيانات ليتم فحصها ونشرها وتحديث متوسط التقييم.",
                alt = "A2: تعديل التقييم ➜ يسمح للمستخدم بتعديل تقييمه خلال 48 ساعة فقط من نشره.",
                post = "حفظ التقييم الموثق، تحديث متوسط التقييم العام للخدمة ومزودها لترتيب نتائج البحث.",
                rules = "BR-062: لا يسمح بتقييم أي خدمة إلا بعد اكتمالها فعلياً.\nBR-066: تعرض فقط تقييمات العملاء الذين أتموا الحجز فعلاً (Verified Booking)."
            )
            5 -> UcDetailLayout(
                id = "UC-013", name = "نظام الإشعارات الذكية (Smart Notifications)",
                goal = "إبقاء أطراف المنصة على اطلاع دائم بجميع الأحداث الهامة وقنوات التواصل.",
                actors = "المستخدم، نظام الإشعارات الفورية (Push)، خادم البريد، نظام SMS/OTP.",
                pre = "حدوث فعل يستوجب الإشعار، وتفعيل المستخدم لتلقي الإشعارات بجهازه.",
                inputs = "معرف المستلم، محتوى الرسالة، الأولوية وقناة الإرسال المفضلة.",
                flow = "1. حدوث حدث (تأكيد حجز، نجاح دفع، رسالة دعم فني).\n2. يحدد النظام نوع الإشعار والمستلم وقناة الإرسال.\n3. يصيغ محتوى مخصصاً ومحفزاً ويرسله فوراً مع حفظ نسخة بمركز الإشعارات.",
                alt = "A1: عطل بالإنترنت لدى السائح ➜ يحفظ الإشعار بالطابور المحلي ليعرض عند الاتصال.",
                post = "وصول التنبيه لجهاز المستخدم وتحديث مركز الإشعارات الخاص به.",
                rules = "BR-070: حفظ تاريخ الإشعارات لمدة 90 يوماً على الأقل.\nBR-072: يجب وصول إشعارات التحقق OTP خلال مدة لا تتجاوز 30 ثانية لضمان الفاعلية."
            )
            6 -> UcDetailLayout(
                id = "UC-014", name = "الدردشة المباشرة (Real-Time Chat)",
                goal = "تسهيل التواصل الفوري والآمن بين السياح ومزودي الخدمات لمناقشة تفاصيل الرحلة.",
                actors = "المستخدم، مقدم الخدمة (مرشد، فندق...)، قاعدة البيانات ونظام الإشعارات.",
                pre = "تسجيل دخول فعال، عدم وجود حظر بين الطرفين.",
                inputs = "معرف المرسل والمستقبل، نص الرسالة، المرفقات والصور، الموقع الجغرافي.",
                flow = "1. يفتح صفحة المحادثة من تفاصيل الخدمة أو الحجز.\n2. يكتب الرسالة ويرفق الصور أو يرسل موقعه الجغرافي.\n3. يشفر النظام البيانات ويرسلها فوراً مع إشعار فوري للمستلم لتحديث الشاشة.",
                alt = "A1: انقطاع الشبكة ➜ يحفظ الرسائل محلياً ويعيد إرسالها فور عودة الاتصال تلقائياً.",
                post = "حفظ سجل الرسائل بأمان، وتحديث مؤشر القراءة والاستلام فوراً.",
                rules = "BR-075: تشفير الرسائل أثناء النقل ببروتوكولات آمنة.\nBR-076: يمنع بدء محادثة مع أي مقدم خدمة موقوف أو محظور قضائياً."
            )
            7 -> UcDetailLayout(
                id = "UC-015", name = "مساعد الذكاء الاصطناعي (AI Travel Assistant)",
                goal = "توليد برامج سياحية متكاملة ومخصصة بناءً على ميزانية واهتمامات وتواريخ رحلة المستخدم.",
                actors = "المستخدم، محرك الذكاء الاصطناعي (Gemini API)، قاعدة البيانات والخرائط.",
                pre = "توفر اتصال بالإنترنت، ووجود بيانات كافية عن الخدمات باليمن بالسيستم.",
                inputs = "المدينة، تاريخ ومدة الرحلة، الميزانية، نوع الرحلة (عائلية، شبابية)، الاهتمامات.",
                flow = "1. يفتح واجهة المساعد ويجيب عن أسئلة التفضيلات والميزانية.\n2. يقوم محرك الذكاء الاصطناعي بتحليل البيانات والبحث عن الخدمات المطابقة باليمن.\n3. ينشئ خطة يومية مقترحة (فنادق، تجارب، سيارات، مطاعم) مع حساب التكلفة التقديرية.\n4. يعرض الخطة مع إمكانية تعديلها أو حفظها بملفه الشخصي.",
                alt = "A1: الميزانية منخفضة ➜ يقترح خطة اقتصادية شاملة تجارب مجانية وإقامة شعبية تراثية.",
                post = "حفظ خطة السفر وحساب التكلفة وتوفير خيار تحويل الخطة إلى حجوزات فورية بنقرة واحدة.",
                rules = "BR-082: يجب ألا تتجاوز التكلفة المقترحة الميزانية المحددة للمستخدم.\nBR-083: تعتمد التوصيات على التقييمات الأعلى، القرب الجغرافي وتوفر المواعيد."
            )
            8 -> UcDetailLayout(
                id = "UC-016", name = "الرحلات المفاجئة (Surprise Trips)",
                goal = "تقديم تجربة سفر استثنائية مشوقة ومغامرة حيث يقوم النظام بتنظيم الرحلة دون كشف تفاصيلها.",
                actors = "المستخدم، محرك الذكاء الاصطناعي، نظام الحجوزات والدفع والإشعارات.",
                pre = "حساب فعال، قبول السائح لشروط المغامرة وإتمام عملية الدفع مسبقاً.",
                inputs = "مدينة الانطلاق، التواريخ، عدد الأشخاص، الميزانية ومستوى المغامرة المطلوب.",
                flow = "1. يحدد ميزانيته ومستواه المفضل للمغامرة (طبيعة، بحر، جبال).\n2. يصمم النظام جدولاً سرياً مخصصاً ويقترح سعراً نهائياً شاملاً.\n3. يدفع المستخدم القيمة ويقوم النظام بحجز كل شيء بأمان مع حجب الوجهة.\n4. يكشف النظام تفاصيل الرحلة ونقطة الانطلاق قبل الموعد بـ 24 ساعة فقط لإشعال الشغف.",
                alt = "A2: تجاوز الميزانية ➜ يقوم النظام بتقليص الخدمات الفاخرة ليتناسب الحجز مع الميزانية.",
                post = "إنشاء رحلة مفاجئة وتأكيد الحجوزات مع مقدمي الخدمات مع إبقاء البيانات مخفية عن السائح.",
                rules = "BR-089: حظر كشف تفاصيل الوجهة ونوعية الحجوزات قبل وقت الكشف المتفق عليه بالمواصفات."
            )
            9 -> UcDetailLayout(
                id = "UC-017", name = "نظام الهدايا السياحية (Travel Gifts)",
                goal = "تمكين المستخدم من شراء بطاقات هدايا وتجارب سياحية وتقديمها للأصدقاء والأهل.",
                actors = "المستخدم (المهدي)، مستلم الهدية، بوابة الدفع، نظام الإشعارات والبطاقات.",
                pre = "وجود حساب، اختيار خدمة قابلة للإهداء وإتمام عملية الدفع.",
                inputs = "نوع الخدمة أو المبلغ، اسم البريد/رقم المستلم، رسالة التهنئة، موعد الإرسال.",
                flow = "1. يختار تجربة بن حراز أو حجز فندق سقطرى كهدية ويضغط 'إهداء'.\n2. يدخل بيانات المستلم ويكتب رسالة تهنئة ويحدد موعد الإرسال.\n3. يتم الدفع، ويولد النظام بطاقة رقمية مميزة مع كود استرداد فريد (Gift/QR Code).\n4. يتم إرسال البطاقة بالموعد المجدد ويقوم المستفيد بتفعيلها والتمتع بالرحلة.",
                alt = "A2: إرسال مجدول ➜ تحفظ البطاقة وتدقق تلقائياً لإرسالها بالوقت المحدد مستقبلاً.",
                post = "توليد رمز استرداد غير قابل للتكرار، إصدار الفاتورة وإخطار المستلم بهديته الثقافية.",
                rules = "BR-094: لكل هدية كود استرداد فريد مستخدم لمرة واحدة فقط لمنع التكرار والاحتيال."
            )
            10 -> UcDetailLayout(
                id = "UC-019", name = "إدارة الأرباح والعمولات (Revenue & Commission)",
                goal = "حساب مستحقات مقدمي الخدمات وعمولات المنصة تلقائياً وبأعلى درجات الدقة المالية.",
                actors = "مدير النظام، مقدم الخدمة، المحاسب المالي للتطبيق وقاعدة البيانات.",
                pre = "اكتمال الحجز والدفع بنجاح، ومرور فترة حماية المستهلك المعتمدة.",
                inputs = "رقم الحجز، السعر الإجمالي، نسبة عمولة المنصة (10%).",
                flow = "1. عند نجاح الحجز، يسجل النظام الحركة المالية.\n2. يحسب عمولة المنصة (النسبة المعتمدة بـ 10% مثلاً) والضرائب.\n3. يحتجز النظام صافي الربح بمحفظة المزود كمعلق حتى اكتمال الخدمة.\n4. عند التأكيد، يفعل الرصيد للسحب ويستطيع المزود طلب التحويل البنكي أو النقدي.",
                alt = "A1: إلغاء الحجز ➜ إلغاء العمولات المعلقة وإرجاع الأموال للمشتري حسب شروط الإلغاء.",
                post = "تحديث فوري لتقارير إيرادات المنصة وصافي رصيد المحافظ لشركاء التراث باليمن.",
                rules = "BR-101: تحديد نسب العمولات بمرونة من لوحة تحكم المسؤول حسب فئات الخدمات.\nBR-103: تسجيل الحركات بسجل تدقيق (Audit Log) مشفر ومؤمن بالكامل غير قابل للتعديل."
            )
            11 -> UcDetailLayout(
                id = "UC-020", name = "التحليلات ولوحات التحكم (Analytics & Dashboards)",
                goal = "توفير إحصائيات فورية ومؤشرات أداء شاملة (KPIs) لمتخذي القرار ومزودي الخدمات.",
                actors = "مدير النظام، مقدم الخدمة، قاعدة البيانات ومحرك تحليل البيانات.",
                pre = "تسجيل دخول بصلاحيات إدارة أو تزويد خدمة، وتوفر بيانات تاريخية بالمنصة.",
                inputs = "النطاق الزمني، تصنيف الخدمات، نوع المحافظة المطلوب دراستها باليمن.",
                flow = "1. يدخل المسؤول لوحة التحكم ويحدد التواريخ وفلاتر التحليل.\n2. يجمع النظام الإحصائيات (المستخدمين الجدد، حجم الحجوزات، إجمالي المدفوعات، نسب الإلغاء).\n3. يعالج البيانات ويعرضها برسوم بيانية تفاعلية سهلة الاستيعاب مع خيارات التصدير.",
                alt = "A2: تصدير التقارير ➜ توفير خيارات تحميل فوري لكشوفات الحساب بصيغ PDF وExcel وCSV.",
                post = "تحديث لوحة المؤشرات الفورية وتوثيق عملية سحب أو استخراج التقارير بسجل الحماية.",
                rules = "BR-108: حظر استعراض أي تقارير أو مؤشرات خارج صلاحية المستخدم المحددة بالنظام."
            )
        }
    }
}

@Composable
fun UcDetailLayout(
    id: String,
    name: String,
    goal: String,
    actors: String,
    pre: String,
    inputs: String,
    flow: String,
    alt: String,
    post: String,
    rules: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🆔 $id", color = Color(0xFFC59B27), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Divider(color = Color(0x11FFFFFF))

        UcField("🎯 الهدف الرئيسي (Goal)", goal)
        UcField("👥 الممثلون (Actors)", actors)
        UcField("🔒 الشروط المسبقة (Preconditions)", pre)
        UcField("📥 المدخلات (Inputs)", inputs)
        UcField("📝 السيناريو الأساسي (Main Flow)", flow)
        UcField("🔄 السيناريوهات البديلة (Alternative)", alt)
        UcField("🏁 النتائج النهائية (Postconditions)", post)
        UcField("⚖️ قواعد العمل البرمجية (Business Rules)", rules)
    }
}

@Composable
fun UcField(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 11.sp)
        Text(text = desc, fontSize = 10.sp, color = Color(0xFFE2E8F0), lineHeight = 14.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
    }
}

data class ChapterData(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val content: @Composable () -> Unit
)

// =========================================================================
// SUB-VIEW: INTERACTIVE SYSTEM DIAGRAMS (UML/DFD DRAWINGS)
// =========================================================================
@Composable
fun SystemDiagramsView() {
    var selectedDiagramTab by remember { mutableStateOf(0) } // 0: Use Case, 1: DFD, 2: ERD, 3: Architecture, 4: Gantt
    
    val diagramTabs = listOf("مخطط Use Case", "تدفق البيانات DFD", "قاعدة البيانات ERD", "بنية النظام Layered", "الجدول Gantt")
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ScrollableTabRow(
            selectedTabIndex = selectedDiagramTab,
            containerColor = Color(0x13FFFFFF),
            contentColor = Color(0xFFC59B27),
            edgePadding = 0.dp,
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
            divider = {}
        ) {
            diagramTabs.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = selectedDiagramTab == index,
                    onClick = { selectedDiagramTab = index }
                ) {
                    Text(
                        text = tabTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                        color = if (selectedDiagramTab == index) Color(0xFFC59B27) else Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Render selected diagram inside a beautiful visual canvas/frame
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (selectedDiagramTab) {
                    0 -> UseCaseDiagramVisual()
                    1 -> DfdLevelZeroVisual()
                    2 -> ErdVisual()
                    3 -> SystemArchitectureVisual()
                    4 -> GanttChartVisual()
                }
            }
        }
    }
}

@Composable
fun UseCaseDiagramVisual() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "UML Use Case Diagram (مخطط حالات الاستخدام)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "يوضح المخطط التالي العلاقة التفاعلية بين المستخدمين (سياح/مقدمي خدمة) والنظام:", fontSize = 11.sp, color = Color(0xFF94A3B8))
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Interactive representation with Compose Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Actor 1
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color(0x1BFFFFFF), RoundedCornerShape(12.dp))
                    .padding(10.dp)
                    .width(80.dp)
            ) {
                Icon(imageVector = Icons.Default.Group, contentDescription = "Tourist", tint = Color(0xFFC59B27), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "سائح يمني", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }

            // Connection arrows and Cases column
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                UseCaseBubble("استكشاف وحجز تجارب البن/سقطرى")
                UseCaseBubble("تخطيط رحلة مخصصة بالذكاء")
                UseCaseBubble("تقييم ومراجعة تجربة تراثية")
                UseCaseBubble("صرف وشحن المحفظة الرقمية")
            }
        }
        
        Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Actor 2
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color(0x1BFFFFFF), RoundedCornerShape(12.dp))
                    .padding(10.dp)
                    .width(80.dp)
            ) {
                Icon(imageVector = Icons.Default.Group, contentDescription = "Provider", tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "مقدم خدمة", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }

            // Connection arrows and Cases column
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                UseCaseBubble("نشر وإدارة تفاصيل تجربتي")
                UseCaseBubble("تأكيد / تتبع حجوزات السياح")
                UseCaseBubble("استقبال مستحقات الدفع والعمولات")
            }
        }
    }
}

@Composable
fun UseCaseBubble(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
            .background(Color(0x0AFFFFFF), RoundedCornerShape(16.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Color(0xFFE2E8F0), fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
fun DfdLevelZeroVisual() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Data Flow Diagram - DFD Level 0 (مخطط تدفق البيانات)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "يوضح المخطط كيفية تدفق البيانات والعمليات بين الأطراف الخارجية وعملية المعالجة المركزية للتطبيق:", fontSize = 11.sp, color = Color(0xFF94A3B8))

        Spacer(modifier = Modifier.height(10.dp))

        // DFD Zero Visual flow
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Source External Entity
            Column(
                modifier = Modifier
                    .border(1.dp, Color(0xFFC59B27).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .background(Color(0x13FFFFFF))
                    .padding(8.dp)
                    .width(70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "سائح", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(text = "User", color = Color(0xFF94A3B8), fontSize = 9.sp)
            }

            // Flow Indicator
            Text(text = "➜\nطلب الحجز\n⬅\nالتذكرة QR", color = Color(0xFFC59B27), fontSize = 8.sp, textAlign = TextAlign.Center, lineHeight = 10.sp)

            // Central Process Box
            Column(
                modifier = Modifier
                    .border(2.dp, Color(0xFF38BDF8), CircleShape)
                    .background(Color(0x2BFFFFFF))
                    .padding(12.dp)
                    .size(90.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "0.0\nمنصة تِجربة", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
                Text(text = "Central Engine", color = Color(0xFF38BDF8), fontSize = 8.sp)
            }

            // Flow Indicator
            Text(text = "⬅\nتفاصيل الحجز\n➜\nالأرباح والعمولة", color = Color(0xFF38BDF8), fontSize = 8.sp, textAlign = TextAlign.Center, lineHeight = 10.sp)

            // Destination External Entity
            Column(
                modifier = Modifier
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .background(Color(0x13FFFFFF))
                    .padding(8.dp)
                    .width(70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "مقدم خدمة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(text = "Provider", color = Color(0xFF94A3B8), fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun ErdVisual() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Entity-Relationship (ER) Diagram (تصميم الكيانات والعلاقات)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "هيكل الجداول الأربعة الرئيسية في قاعدة بيانات Room لـتِجربة مع توضيح المفاتيح الرئيسية والأجنبية والعلاقات:", fontSize = 11.sp, color = Color(0xFF94A3B8))

        Spacer(modifier = Modifier.height(10.dp))

        // ER Diagram visual blocks
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Table 1
            ErdTableBlock(
                tableName = "user_profile (الملف الشخصي للسائح)",
                borderColor = Color(0xFFC59B27),
                fields = listOf(
                    "🔑 userId : TEXT (PK) - المعرف",
                    "• name : TEXT - الاسم",
                    "• walletBalanceYer : DOUBLE - رصيد الريال",
                    "• walletBalanceUsd : DOUBLE - رصيد الدولار",
                    "• points : INT - النقاط المكتسبة",
                    "• badgeAr : TEXT - الشارة الثقافية"
                )
            )

            // Relationship Indicator
            Text(text = "⬇ علاقة واحد لمتعدد (1 .. *) - يحجز", color = Color(0xFFC59B27), fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterHorizontally), fontWeight = FontWeight.Bold)

            // Table 2
            ErdTableBlock(
                tableName = "bookings (الحجوزات والتذاكر الرقمية)",
                borderColor = Color(0xFF38BDF8),
                fields = listOf(
                    "🔑 bookingId : TEXT (PK) - كود الحجز",
                    "🔗 experienceId : TEXT (FK) - كود التجربة",
                    "• pricePaid : DOUBLE - السعر المدفوع",
                    "• currency : TEXT - العملة المستخدمة",
                    "• status : TEXT - حالة الحجز (CONFIRMED)",
                    "• qrCodeData : TEXT - كود التحقق السريع"
                )
            )

            // Relationship Indicator
            Text(text = "⬆ علاقة متعدد لواحد (* .. 1) - ينتمي إلى", color = Color(0xFF38BDF8), fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterHorizontally), fontWeight = FontWeight.Bold)

            // Table 3
            ErdTableBlock(
                tableName = "experiences (التجارب التراثية لليمن)",
                borderColor = Color(0xFF34D399),
                fields = listOf(
                    "🔑 id : TEXT (PK) - معرف التجربة",
                    "• titleAr / titleEn : TEXT - عنوان التجربة",
                    "• category : TEXT - الفئة (زراعة، حرف...)",
                    "• priceYer : DOUBLE - السعر بالريال",
                    "• rating : DOUBLE - التقييم العام"
                )
            )
        }
    }
}

@Composable
fun ErdTableBlock(tableName: String, borderColor: Color, fields: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(Color(0x0AFFFFFF))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(borderColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                .padding(6.dp)
        ) {
            Text(text = tableName, fontWeight = FontWeight.Bold, color = borderColor, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        fields.forEach { field ->
            Text(text = field, fontSize = 10.sp, color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}

@Composable
fun SystemArchitectureVisual() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "System Multi-Tier Architecture (بنية وهيكل النظام)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "الهندسة المعمارية ثلاثية الطبقات المتبعة لضمان مرونة الأداء والتحكم:", fontSize = 11.sp, color = Color(0xFF94A3B8))

        Spacer(modifier = Modifier.height(10.dp))

        // Vertical layers
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ArchitectureLayerBlock("طبقة العرض والواجهات (Presentation) - Jetpack Compose M3", Color(0xFFC59B27), "تهتم برسم الشاشات ذات المظهر الزجاجي البلوري وتحديثها ديناميكياً مع تفاعلات المستخدم وسياق التصفح.")
            ArchitectureLayerBlock("طبقة منطق العمل والتحكم (ViewModel State Engine)", Color(0xFF38BDF8), "الوسيط المسؤول عن حفظ حالات البيانات وتجهيز طلبات الذكاء الاصطناعي ومعالجة الرصيد ثنائي العملة.")
            ArchitectureLayerBlock("طبقة إدارة البيانات والمستودع (Repository / Local Room & AI)", Color(0xFF34D399), "إدارة العمليات لقاعدة بيانات SQLite وتكامل استدعاءات Gemini API وتحديث الحجوزات.")
        }
    }
}

@Composable
fun ArchitectureLayerBlock(title: String, color: Color, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.05f))
            .padding(12.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, color = color, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = description, fontSize = 10.sp, color = Color(0xFFE2E8F0), lineHeight = 14.sp)
    }
}

@Composable
fun GanttChartVisual() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Gantt Progress Chart (الجدول الزمني لمراحل التخرج)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "معدل الإنجاز والتوزيع الزمني للمشروع خلال 16 أسبوعاً بنسبة اكتمال 100%:", fontSize = 11.sp, color = Color(0xFF94A3B8))

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            GanttBar("تحليل المتطلبات وهندسة البرمجيات (أسبوع 1-3)", 1f, Color(0xFFC59B27))
            GanttBar("تصميم قاعدة البيانات وهندسة العلاقات (أسبوع 4-6)", 1f, Color(0xFF38BDF8))
            GanttBar("تطوير منطق العمل وربط الـ ViewModel (أسبوع 7-10)", 1f, Color(0xFF34D399))
            GanttBar("تطوير محاكيات الأنظمة الفرعية الذكية (أسبوع 11-13)", 1f, Color(0xFFA855F7))
            GanttBar("الاختبارات الشاملة (UAT & Testing) والنشر (أسبوع 14-16)", 1f, Color(0xFFE11D48))
        }
    }
}

@Composable
fun GanttBar(phaseName: String, progress: Float, barColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = phaseName, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "${(progress * 100).toInt()}% مكتمل", fontSize = 10.sp, color = barColor, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0x1AFFFFFF), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .background(barColor, RoundedCornerShape(4.dp))
            )
        }
    }
}

// =========================================================================
// SUB-VIEW: INTERACTIVE ADVANCED SMART SUBSYSTEMS SIMULATORS
// =========================================================================
@Composable
fun SmartSubsystemsSimulatorView(viewModel: TajrubahViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    
    var activeSubsystemTab by remember { mutableStateOf(0) } // 0: Rewards/Referral, 1: Fraud Detection, 2: Provider Portals, 3: AR
    
    val subTabs = listOf("🪙 النقاط والإحالات", "🛡️ كشف الاحتيال", "🌾 بوابات مقدمي الخدمات", "🔮 محاكي الواقع المعزز")
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ScrollableTabRow(
            selectedTabIndex = activeSubsystemTab,
            containerColor = Color(0x13FFFFFF),
            contentColor = Color(0xFFC59B27),
            edgePadding = 0.dp,
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
            divider = {}
        ) {
            subTabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeSubsystemTab == index,
                    onClick = { activeSubsystemTab = index }
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                        color = if (activeSubsystemTab == index) Color(0xFFC59B27) else Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Render subsystem simulator screen
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (activeSubsystemTab) {
                    0 -> RewardsAndReferralsSimulator(profile = profile, viewModel = viewModel)
                    1 -> FraudBookingInterceptionSimulator()
                    2 -> PortalsSimulationCenter()
                    3 -> ArParallaxMockSimulator()
                }
            }
        }
    }
}

@Composable
fun RewardsAndReferralsSimulator(profile: com.example.data.UserProfileEntity?, viewModel: TajrubahViewModel) {
    var referralInput by remember { mutableStateOf("") }
    var pointsLocal by remember { mutableStateOf(profile?.points ?: 1250) }
    var badgeLocal by remember { mutableStateOf(profile?.badgeAr ?: "سفير ثقافي ذهبي") }
    var successMsg by remember { mutableStateOf("") }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "🪙 نظام المكافآت والإحالات والتسويق التراثي", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "صُمم هذا النظام لتحفيز المستخدمين على مشاركة تراث بلدهم وجلب سياح جدد، مع كسب نقاط ترفع رتبتهم الثقافية في اليمن.", fontSize = 11.sp, color = Color(0xFF94A3B8))

        // User stats display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x0AFFFFFF), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "رصيد النقاط الحالي", fontSize = 10.sp, color = Color(0xFF94A3B8))
                Text(text = "$pointsLocal نقطة ثقافية", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC59B27))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "رتبة السفير الثقافي", fontSize = 10.sp, color = Color(0xFF94A3B8))
                Text(text = badgeLocal, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Divider(color = Color(0x1AFFFFFF))

        // Share referral code block
        Text(text = "🔗 كود الإحالة الخاص بك لمشاركته مع الأصدقاء:", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                .background(Color(0x0AFFFFFF))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = profile?.referralCode ?: "RAGHAD-TAJRUBAH", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 14.sp)
            Row(modifier = Modifier.clickable {
                successMsg = "تم نسخ كود إحالتك بنجاح لمشاركته في تليجرام/واتساب!"
            }, verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Copy", tint = Color(0xFFC59B27), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "نسخ ومشاركة", color = Color(0xFFC59B27), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Apply dynamic mock referral code
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "🎁 هل تلقيت دعوة؟ أدخل كود صديقك للحصول على نقاط:", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = referralInput,
                onValueChange = { referralInput = it },
                placeholder = { Text("مثال: YEMEN-HERITAGE", fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFC59B27),
                    unfocusedBorderColor = Color(0x22FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            Button(
                onClick = {
                    if (referralInput.trim().uppercase() == "YEMEN-HERITAGE") {
                        pointsLocal += 300
                        successMsg = "مبروك! تم تفعيل الإحالة بنجاح وحصلت على +300 نقطة تراثية مجانية!"
                        referralInput = ""
                        // Trigger dynamic badge promotion locally for simulation
                        if (pointsLocal >= 1500) {
                            badgeLocal = "سفير التراث اليمني"
                        }
                    } else if (referralInput.trim().isEmpty()) {
                        successMsg = "يرجى كتابة كود صالح."
                    } else {
                        successMsg = "كود غير صالح أو منتهى الصلاحية. جرب: YEMEN-HERITAGE"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC59B27)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "تفعيل الكود", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        if (successMsg.isNotEmpty()) {
            Text(
                text = successMsg,
                color = if (successMsg.contains("مبروك")) Color(0xFF22C55E) else Color(0xFFF43F5E),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun FraudBookingInterceptionSimulator() {
    var isShieldActive by remember { mutableStateOf(true) }
    var isAttackSimulated by remember { mutableStateOf(false) }
    val logs = remember { mutableStateListOf(
        "🛡️ [نظام الدرع]: جدار الحماية الذكي قيد التشغيل والترقب في اليمن.",
        "✓ [فحص الهوية]: لم يتم رصد أي محاولات إغراق أو هجمات روبوتية حالياً."
    )}

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🛡️ درع الحماية الذكي وكاشف الحجوزات الوهمية", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
            // Active Shield toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (isShieldActive) "نشط" else "معطل", fontSize = 10.sp, color = if (isShieldActive) Color(0xFF22C55E) else Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = isShieldActive,
                    onCheckedChange = { isShieldActive = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF22C55E),
                        checkedTrackColor = Color(0xFF22C55E).copy(alpha = 0.2f)
                    )
                )
            }
        }
        Text(text = "يحلل هذا الدرع فواصل الحجز الزمنية وتوثيق هويات السياح لمنع الروبوتات من شل وحجز مزارع البن أو فنادق سقطرى دون دفع حقيقي.", fontSize = 11.sp, color = Color(0xFF94A3B8))

        // Interception logs terminal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF33FFFFFF), RoundedCornerShape(12.dp))
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            logs.forEach { log ->
                Text(
                    text = log,
                    fontSize = 9.sp,
                    color = if (log.contains("خطر") || log.contains("حظر")) Color(0xFFF43F5E) else if (log.contains("حجب")) Color(0xFFF59E0B) else Color(0xFF34D399),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        // Simulate attack button
        Button(
            onClick = {
                if (!isShieldActive) {
                    logs.add("🚨 [خطر داهم]: تم رصد محاولة حجز وهمي مكثف (100 طلب/ثانية) من خوادم مجهولة!")
                    logs.add("🚨 [كارثة]: النظام معطل! تم تجميد غرف فندق سقطرى وتجربة البن بالكامل بحجوزات معلقة وهمية!")
                } else {
                    logs.add("⚔️ [رصد هجوم]: روبوت مبرمج حاول حجز تجربة بن حراز 50 مرة متتالية خلال 0.2 ثانية.")
                    logs.add("🛡️ [حظر وتأمين]: تم اكتشاف السلوك المشبوه، طلب كود الـ OTP للتحقق، وحظر عنوان الـ IP المهاجم بنجاح!")
                    logs.add("✓ [حالة آمنة]: إفشال محاولة الاحتيال وتأمين مزارع العم علي الحرازي.")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (isShieldActive) Color(0xFF10B981) else Color(0xFFEF4444)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "محاكاة هجوم حجز وهمي (Bot spam attack)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
fun PortalsSimulationCenter() {
    var selectedPortal by remember { mutableStateOf(0) } // 0: Cars, 1: Hotels, 2: Guides, 3: Technical Support
    
    val portals = listOf("🚗 شركات السيارات", "🏨 الفنادق المحلية", "🗺️ المرشدين المحليين", "🛠️ الدعم الفني")
    
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "🌾 بوابات إدارة وتفاعل أصحاب المصلحة في اليمن", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "بوابات متخصصة ومستقلة لإشراك شركات السيارات، والفنادق التراثية، والدعم التقني في منصة تِجربة:", fontSize = 11.sp, color = Color(0xFF94A3B8))

        // Sub tab
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            portals.forEachIndexed { index, name ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedPortal == index) Color(0xFFC59B27) else Color(0x13FFFFFF))
                        .clickable { selectedPortal = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = name.substring(2), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (selectedPortal == index) Color.Black else Color.White)
                }
            }
        }

        Divider(color = Color(0x1AFFFFFF))

        // Render Portal simulation
        when (selectedPortal) {
            0 -> CarRentalPortalVisual()
            1 -> HotelPortalVisual()
            2 -> LocalGuidesPortalVisual()
            3 -> SupportDeskPortalVisual()
        }
    }
}

@Composable
fun CarRentalPortalVisual() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "لوحة تحكم شركات السيارات (تويوتا لاندكروزر 4x4)", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 12.sp)
        Text(text = "تتيح لشركات تأجير سيارات الدفع الرباعي تتبع الرحلات الجبلية الوعرة لحراز وسقطرى:", fontSize = 11.sp, color = Color(0xFFE2E8F0))

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CarStatusCard("لاندكروزر #8222", "في رحلة (حراز)", "نشط", Color(0xFF22C55E), modifier = Modifier.weight(1f))
            CarStatusCard("لاندكروزر #4312", "في انتظار سائح", "جاهز", Color(0xFF38BDF8), modifier = Modifier.weight(1f))
            CarStatusCard("نيسان باترول #10", "صيانة دورية (عدن)", "معطل", Color(0xFFF43F5E), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun CarStatusCard(name: String, route: String, status: String, statusColor: Color, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
        border = BorderStroke(0.5.dp, Color(0x22FFFFFF)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
            Text(text = route, fontSize = 9.sp, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun HotelPortalVisual() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "لوحة تحكم الفنادق ودور الضيافة التراثية", fontWeight = FontWeight.Bold, color = Color(0xFF34D399), fontSize = 12.sp)
        Text(text = "تمكن دور الضيافة التقليدية البيئية من تتبع إشغال غرف السياح وتنسيق وجبات الإفطار التراثية:", fontSize = 11.sp, color = Color(0xFFE2E8F0))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
            border = BorderStroke(0.5.dp, Color(0x22FFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "اسم المنشأة: بيت الضيافة التراثي - حراز", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                    Text(text = "معدل الإشغال: 80%", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Text(text = "• الغرفة 102: مأهولة (السائح أحمد حامد) - حجز مؤكد", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                Text(text = "• الغرفة 105: مأهولة (م. رغد) - حجز مؤكد", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                Text(text = "• الغرفة 108: شاغرة - جاهزة للاستقبال", fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
fun LocalGuidesPortalVisual() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "لوحة تحكم المرشدين المحليين الأخصائيين", fontWeight = FontWeight.Bold, color = Color(0xFFA855F7), fontSize = 12.sp)
        Text(text = "بوابة مخصصة للمرشدين مثل 'مصلح الحرازي' لمشاهدة مسار السير البري وتفاصيل الرحلة:", fontSize = 11.sp, color = Color(0xFFE2E8F0))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
            border = BorderStroke(0.5.dp, Color(0x22FFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Map, contentDescription = "Map", tint = Color(0xFFA855F7), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "المرشد النشط: مصلح الحرازي", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                    Text(text = "الرحلة الحالية: قطف البن الخولاني التراثي", fontSize = 10.sp, color = Color(0xFFE2E8F0))
                    Text(text = "موقع التجمع: عقبة حراز، 09:00 صباحاً", fontSize = 9.sp, color = Color(0xFF94A3B8))
                }
            }
        }
    }
}

@Composable
fun SupportDeskPortalVisual() {
    var ticketText by remember { mutableStateOf("") }
    var resultMsg by remember { mutableStateOf("") }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "لوحة تحكم الدعم الفني ومعالجة البلاغات", fontWeight = FontWeight.Bold, color = Color(0xFFE11D48), fontSize = 12.sp)
        Text(text = "تواصل مباشر مع الدعم الفني لمنصة تِجربة لمعالجة أي مشاكل دفع أو تعديل في حجز الرحلات الثقافية:", fontSize = 11.sp, color = Color(0xFFE2E8F0))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = ticketText,
                onValueChange = { ticketText = it },
                placeholder = { Text("اكتب مشكلتك هنا لإرسال بلاغ فوري...", fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE11D48),
                    unfocusedBorderColor = Color(0x22FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Button(
                onClick = {
                    if (ticketText.trim().isNotEmpty()) {
                        resultMsg = "تم فتح تذكرة دعم فني برقم #${(10000..99999).random()}! سيقوم مهندسونا بالتواصل معك فوراً."
                        ticketText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "إرسال", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        if (resultMsg.isNotEmpty()) {
            Text(text = resultMsg, color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ArParallaxMockSimulator() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "🔮 محاكي الواقع المعزز واستعراض المعالم (AR Tour Preview)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 14.sp)
        Text(text = "يتيح هذا النظام للسياح تصفح معالم اليمن التراثية (مثل باب اليمن، قلعة صيرة) بتقنية الواقع المعزز ثلاثي الأبعاد مباشرة قبل الحجز. حرك إصبعك على البطاقة أدناه لمحاكاة التأثير البصري ثلاثي الأبعاد ثلاثي الأبعاد:", fontSize = 11.sp, color = Color(0xFF94A3B8))

        Spacer(modifier = Modifier.height(4.dp))

        // Simulated AR parallax card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color(0x0FFFFFFF), RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFFC59B27).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x / 10f).coerceIn(-30f, 30f)
                        offsetY = (offsetY + dragAmount.y / 10f).coerceIn(-20f, 20f)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Layer 1 (Background Glow)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = (offsetX * 0.4f).dp, y = (offsetY * 0.4f).dp)
                    .background(Color(0xFFC59B27).copy(alpha = 0.15f), CircleShape)
            )

            // Layer 2 (Middle Gate outline / text)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(x = (offsetX * 0.8f).dp, y = (offsetY * 0.8f).dp)
                    .border(2.dp, Color(0xFFC59B27), RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(14.dp)
            ) {
                Text(text = "🏛️ باب اليمن الأثري (باب صنعاء)", fontWeight = FontWeight.Bold, color = Color(0xFFC59B27), fontSize = 12.sp)
                Text(text = "صنعاء القديمة - تراث عالمي لليونسكو", fontSize = 9.sp, color = Color(0xFFE2E8F0))
            }

            // Layer 3 (Foreground Floating camera / scan overlay)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(text = "📷 نمط استعراض AR نشط (AR 3D Preview Active)", color = Color(0xFF34D399), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(text = "حرك إصبعك لتغيير المنظور", color = Color(0xFF94A3B8), fontSize = 9.sp)
            }
        }
    }
}

// =========================================================================
// CHAPTER 9: CLASS DIAGRAM & ACADEMIC UML SPECIFICATION
// =========================================================================
@Composable
fun ChapterNineContent() {
    var selectedSubTab by remember { mutableStateOf(0) }
    val subTabs = listOf(
        "البنية الأساسية",
        "الحجوزات والمالية",
        "الذكاء الاصطناعي",
        "الإدارة والتحكم",
        "العلاقات والمبادئ"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "📊 مخطط الفئات (Class Diagram) لمنصة تِجربة",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC59B27),
            fontSize = 14.sp
        )
        Text(
            text = "تصميم الفئات الكائنية (Object-Oriented Structure) المعتمد في منصة تِجربة وفق مبادئ SOLID و Clean Architecture لضمان سهولة التطوير والصيانة وقابلية التوسع.",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(subTabs.size) { index ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedSubTab == index) Color(0xFFC59B27) else Color(0x13FFFFFF))
                        .clickable { selectedSubTab = index }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = subTabs[index],
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedSubTab == index) Color.Black else Color.White
                    )
                }
            }
        }

        Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))

        when (selectedSubTab) {
            0 -> CoreClassesTab()
            1 -> BookingPaymentTab()
            2 -> AIRecommendationTab()
            3 -> AdminDashboardTab()
            4 -> RelationsOopTab()
        }
    }
}

@Composable
fun ClassDiagramBlock(
    className: String,
    isInterface: Boolean = false,
    attributes: List<String>,
    methods: List<String>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
        border = BorderStroke(1.dp, if (isInterface) Color(0xFFA855F7).copy(alpha = 0.5f) else Color(0xFFC59B27).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isInterface) Color(0xFFA855F7).copy(alpha = 0.15f) else Color(0xFFC59B27).copy(alpha = 0.15f))
                    .padding(10.dp)
            ) {
                Column {
                    if (isInterface) {
                        Text(
                            text = "<<Interface>>",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA855F7),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = className,
                            fontWeight = FontWeight.Bold,
                            color = if (isInterface) Color(0xFFA855F7) else Color(0xFFC59B27),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Attributes section
            Column(modifier = Modifier.padding(10.dp)) {
                if (attributes.isNotEmpty()) {
                    Text(
                        text = "Attributes (الخصائص):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color(0xFF38BDF8)
                    )
                    attributes.forEach { attr ->
                        Text(
                            text = attr,
                            fontSize = 9.sp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(start = 6.dp, top = 1.dp, bottom = 1.dp)
                        )
                    }
                }
                
                if (attributes.isNotEmpty() && methods.isNotEmpty()) {
                    Divider(
                        color = Color(0x1AFFFFFF),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                if (methods.isNotEmpty()) {
                    Text(
                        text = "Methods (العمليات):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color(0xFF34D399)
                    )
                    methods.forEach { method ->
                        Text(
                            text = method,
                            fontSize = 9.sp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(start = 6.dp, top = 1.dp, bottom = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveClassList(classes: List<Pair<String, Pair<List<String>, List<String>>>>, isInterface: Boolean = false) {
    var expandedClassIndex by remember { mutableStateOf<Int?>(null) }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        classes.forEachIndexed { index, item ->
            val isExpanded = expandedClassIndex == index
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x0AFFFFFF)),
                border = BorderStroke(1.dp, if (isExpanded) Color(0xFFC59B27) else Color(0x11FFFFFF)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedClassIndex = if (isExpanded) null else index }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isInterface) "⚙️ Interface: " else "📦 Class: ",
                                fontSize = 10.sp,
                                color = if (isInterface) Color(0xFFA855F7) else Color(0xFFC59B27),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.first,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = if (isExpanded) "إغلاق ❌" else "عرض التفاصيل 🔍",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    if (isExpanded) {
                        Divider(color = Color(0x1AFFFFFF))
                        Box(modifier = Modifier.padding(10.dp)) {
                            ClassDiagramBlock(
                                className = item.first,
                                isInterface = isInterface,
                                attributes = item.second.first,
                                methods = item.second.second
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoreClassesTab() {
    val coreClasses = listOf(
        "User" to (listOf(
            "+ userId : UUID",
            "+ fullName : String",
            "+ email : String",
            "+ phone : String",
            "+ passwordHash : String",
            "+ profileImage : String",
            "+ gender : Enum",
            "+ birthDate : Date",
            "+ language : String",
            "+ status : UserStatus",
            "+ createdAt : DateTime",
            "+ updatedAt : DateTime"
        ) to listOf(
            "+ register()",
            "+ login()",
            "+ logout()",
            "+ updateProfile()",
            "+ changePassword()",
            "+ verifyOTP()"
        )),
        "Role" to (listOf(
            "+ roleId : UUID",
            "+ roleName : String",
            "+ description : String"
        ) to listOf(
            "+ assignRole()",
            "+ removeRole()"
        )),
        "Permission" to (listOf(
            "+ permissionId : UUID",
            "+ permissionName : String",
            "+ module : String"
        ) to listOf(
            "+ grantPermission()",
            "+ revokePermission()"
        )),
        "Experience" to (listOf(
            "+ experienceId : UUID",
            "+ title : String",
            "+ description : String",
            "+ location : Geography",
            "+ price : Decimal",
            "+ duration : Integer",
            "+ capacity : Integer",
            "+ status : Enum",
            "+ averageRating : Float"
        ) to listOf(
            "+ publish()",
            "+ update()",
            "+ delete()",
            "+ calculateRating()",
            "+ checkAvailability()"
        )),
        "Category" to (listOf(
            "+ categoryId : UUID",
            "+ name : String",
            "+ icon : String"
        ) to listOf(
            "+ createCategory()",
            "+ updateCategory()"
        )),
        "Booking" to (listOf(
            "+ bookingId : UUID",
            "+ bookingDate : Date",
            "+ status : Enum",
            "+ totalPrice : Decimal",
            "+ paymentStatus : Enum",
            "+ qrCode : String"
        ) to listOf(
            "+ createBooking()",
            "+ cancelBooking()",
            "+ confirmBooking()",
            "+ completeBooking()"
        )),
        "Payment" to (listOf(
            "+ paymentId : UUID",
            "+ amount : Decimal",
            "+ paymentMethod : Enum",
            "+ transactionId : String",
            "+ paymentStatus : Enum",
            "+ paidAt : DateTime"
        ) to listOf(
            "+ processPayment()",
            "+ refund()",
            "+ generateReceipt()"
        )),
        "Review" to (listOf(
            "+ reviewId : UUID",
            "+ rating : Integer",
            "+ comment : String",
            "+ createdAt : DateTime"
        ) to listOf(
            "+ addReview()",
            "+ editReview()",
            "+ deleteReview()"
        ))
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "🏰 9.2 الفئات الأساسية في منصة تِجربة (Core Classes)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC59B27),
            fontSize = 13.sp
        )
        Text(
            text = "انقر على أي فئة (Class) لاستعراض خصائصها وعملياتها بالتفصيل البرمجي والنمذجة الكائنية:",
            fontSize = 11.sp,
            color = Color(0xFFE2E8F0)
        )
        InteractiveClassList(classes = coreClasses)
    }
}

@Composable
fun BookingPaymentTab() {
    val bookingPaymentClasses = listOf(
        "Booking" to (listOf(
            "+ bookingId : UUID",
            "+ userId : UUID",
            "+ serviceId : UUID",
            "+ serviceType : Enum",
            "+ bookingDate : Date",
            "+ startDate : DateTime",
            "+ endDate : DateTime",
            "+ numberOfPeople : Integer",
            "+ totalAmount : Decimal",
            "+ bookingStatus : Enum",
            "+ createdAt : DateTime"
        ) to listOf(
            "+ createBooking()",
            "+ validateBooking()",
            "+ confirmBooking()",
            "+ cancelBooking()",
            "+ modifyBooking()",
            "+ calculateTotal()",
            "+ updateStatus()"
        )),
        "BookingItem" to (listOf(
            "+ itemId : UUID",
            "+ bookingId : UUID",
            "+ serviceId : UUID",
            "+ quantity : Integer",
            "+ price : Decimal"
        ) to listOf(
            "+ addItem()",
            "+ removeItem()",
            "+ calculatePrice()"
        )),
        "AvailabilityChecker" to (listOf(
            "+ serviceId : UUID",
            "+ requestedDate : Date",
            "+ capacity : Integer"
        ) to listOf(
            "+ checkAvailability()",
            "+ reserveSlot()",
            "+ releaseSlot()"
        )),
        "Payment" to (listOf(
            "+ paymentId : UUID",
            "+ bookingId : UUID",
            "+ amount : Decimal",
            "+ currency : String",
            "+ paymentMethod : Enum",
            "+ transactionId : String",
            "+ status : PaymentStatus",
            "+ paymentDate : DateTime"
        ) to listOf(
            "+ initiatePayment()",
            "+ verifyPayment()",
            "+ completePayment()",
            "+ refundPayment()"
        )),
        "Invoice" to (listOf(
            "+ invoiceId : UUID",
            "+ bookingId : UUID",
            "+ invoiceNumber : String",
            "+ amount : Decimal",
            "+ tax : Decimal",
            "+ createdDate : DateTime"
        ) to listOf(
            "+ generateInvoice()",
            "+ downloadPDF()",
            "+ sendInvoice()"
        )),
        "Commission" to (listOf(
            "+ commissionId : UUID",
            "+ providerId : UUID",
            "+ bookingId : UUID",
            "+ percentage : Float",
            "+ amount : Decimal"
        ) to listOf(
            "+ calculateCommission()",
            "+ applyCommission()"
        )),
        "Wallet" to (listOf(
            "+ walletId : UUID",
            "+ providerId : UUID",
            "+ balance : Decimal",
            "+ currency : String"
        ) to listOf(
            "+ addBalance()",
            "+ withdraw()",
            "+ getBalance()"
        )),
        "Withdrawal" to (listOf(
            "+ withdrawalId : UUID",
            "+ walletId : UUID",
            "+ amount : Decimal",
            "+ status : Enum",
            "+ requestDate : Date"
        ) to listOf(
            "+ requestWithdrawal()",
            "+ approveWithdrawal()",
            "+ rejectWithdrawal()"
        ))
    )

    val paymentInterfaces = listOf(
        "PaymentGateway" to (emptyList<String>() to listOf(
            "+ pay()",
            "+ verifyTransaction()",
            "+ refund()"
        ))
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "💰 9.11 وحدة الحجوزات والمدفوعات (Booking & Payment Module)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC59B27),
            fontSize = 13.sp
        )
        Text(
            text = "تمثل هذه الفئات دورة المعاملات المالية والحجوزات الآمنة، بدءاً من حجز الخدمة، مروراً بالدفع وتحديث المحافظ وحساب العمولات:",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )
        
        Text(
            text = "🔌 الواجهات (Interfaces):",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFA855F7),
            fontSize = 11.sp
        )
        InteractiveClassList(classes = paymentInterfaces, isInterface = true)
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "📦 فئات الوحدة (Classes):",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC59B27),
            fontSize = 11.sp
        )
        InteractiveClassList(classes = bookingPaymentClasses)
        
        Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))
        
        Text(
            text = "🔄 سيناريو العمل المالي (Conceptual Workflow):",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF38BDF8),
            fontSize = 12.sp
        )
        Text(
            text = "1. السائح يختار تجربة ➜ يقوم الـ AvailabilityChecker بالتحقق من توفر الموعد والسعة.\n2. يتم إنشاء الـ Booking في حالة معلقة.\n3. ينتقل السائح لصفحة الدفع ➜ معالجة العملية عبر الـ PaymentGateway.\n4. عند نجاح الدفع ➜ تحديث حالة الحجز (Paid)، توليد الفاتورة إلكترونياً (Invoice)، وتوليد رمز الحجز QR Code.\n5. يتم حساب عمولة المنصة تلقائياً (Commission) بنسبة 10%، وإضافة صافي الربح لمحفظة مقدم الخدمة (Wallet).",
            fontSize = 11.sp,
            color = Color(0xFFE2E8F0),
            lineHeight = 15.sp
        )
    }
}

@Composable
fun AIRecommendationTab() {
    val aiClasses = listOf(
        "AI Assistant" to (listOf(
            "+ assistantId : UUID",
            "+ userId : UUID",
            "+ conversationId : UUID",
            "+ language : String",
            "+ status : Enum"
        ) to listOf(
            "+ startConversation()",
            "+ analyzeRequest()",
            "+ generateResponse()",
            "+ suggestTrip()",
            "+ answerQuestions()"
        )),
        "SmartTripGenerator" to (listOf(
            "+ tripGeneratorId : UUID",
            "+ budget : Decimal",
            "+ duration : Integer",
            "+ destination : String",
            "+ travelersCount : Integer"
        ) to listOf(
            "+ createTripPlan()",
            "+ calculateBudget()",
            "+ selectExperiences()",
            "+ selectHotels()",
            "+ selectTransportation()",
            "+ optimizeTrip()"
        )),
        "RecommendationEngine" to (listOf(
            "+ recommendationId : UUID",
            "+ userId : UUID",
            "+ algorithmType : String",
            "+ confidenceScore : Float"
        ) to listOf(
            "+ generateRecommendations()",
            "+ rankResults()",
            "+ personalizeResults()",
            "+ updateModel()"
        )),
        "UserProfileAnalyzer" to (listOf(
            "+ analysisId : UUID",
            "+ userId : UUID",
            "+ interests : JSON",
            "+ preferences : JSON",
            "+ behaviorScore : Float"
        ) to listOf(
            "+ analyzeBehavior()",
            "+ extractPreferences()",
            "+ updateProfile()",
            "+ predictNeeds()"
        )),
        "AI Model" to (listOf(
            "+ modelId : UUID",
            "+ modelName : String",
            "+ version : String",
            "+ accuracy : Float",
            "+ status : Enum"
        ) to listOf(
            "+ trainModel()",
            "+ testModel()",
            "+ predict()",
            "+ improveAccuracy()"
        )),
        "RecommendationResult" to (listOf(
            "+ resultId : UUID",
            "+ userId : UUID",
            "+ itemType : Enum",
            "+ itemId : UUID",
            "+ score : Float",
            "+ createdAt : DateTime"
        ) to listOf(
            "+ saveResult()",
            "+ updateScore()",
            "+ removeResult()"
        )),
        "UserPreference" to (listOf(
            "+ preferenceId : UUID",
            "+ userId : UUID",
            "+ preferredActivities : JSON",
            "+ favoriteLocations : JSON",
            "+ budgetRange : Decimal"
        ) to listOf(
            "+ addPreference()",
            "+ updatePreference()",
            "+ removePreference()"
        )),
        "TripHistory" to (listOf(
            "+ historyId : UUID",
            "+ userId : UUID",
            "+ tripId : UUID",
            "+ rating : Integer",
            "+ completedAt : DateTime"
        ) to listOf(
            "+ saveHistory()",
            "+ analyzeHistory()",
            "+ getPreviousTrips()"
        )),
        "AI Chat Message" to (listOf(
            "+ messageId : UUID",
            "+ conversationId : UUID",
            "+ senderType : Enum",
            "+ content : Text",
            "+ createdAt : DateTime"
        ) to listOf(
            "+ sendMessage()",
            "+ receiveMessage()",
            "+ storeMessage()"
        ))
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "🤖 9.17 وحدة الذكاء الاصطناعي والتوصيات (AI & Recommendation Module)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC59B27),
            fontSize = 13.sp
        )
        Text(
            text = "تعد هذه الوحدة قلب الابتكار في منصة تِجربة، حيث تقوم بتحليل بيانات وتفضيلات السياح واقتراح خطط سفر ذكية ومخصصة بالكامل تبرز التراث اليمني الأصيل:",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )
        
        InteractiveClassList(classes = aiClasses)
        
        Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))
        
        Text(
            text = "💡 خوارزمية إنشاء الرحلة الذكية (AI Conceptual Workflow):",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF34D399),
            fontSize = 12.sp
        )
        Text(
            text = "عند طلب السائح رحلة محددة (مثل: رحلة لمدة 5 أيام بميزانية 500 دولار):\n1. يقوم الـ AI Assistant باستقبال الطلب وفهمه لغوياً.\n2. يحلل الـ UserProfileAnalyzer اهتمامات السائح وسلوكه.\n3. يقوم الـ SmartTripGenerator بفرز وتوزيع الميزانية وتوليد خطة متكاملة تشمل الأنشطة (Experiences)، الغرف (Hotels) والمواصلات المتاحة.\n4. يرتب الـ RecommendationEngine النتائج ويحسب السعر الإجمالي للخطة.",
            fontSize = 11.sp,
            color = Color(0xFFE2E8F0),
            lineHeight = 15.sp
        )
    }
}

@Composable
fun AdminDashboardTab() {
    val adminClasses = listOf(
        "Admin" to (listOf(
            "+ adminId : UUID",
            "+ userId : UUID",
            "+ department : String",
            "+ accessLevel : Enum",
            "+ status : Enum"
        ) to listOf(
            "+ loginDashboard()",
            "+ manageUsers()",
            "+ manageProviders()",
            "+ manageSystem()",
            "+ generateReports()",
            "+ monitorSystem()"
        )),
        "AdminDashboard" to (listOf(
            "+ dashboardId : UUID",
            "+ title : String",
            "+ lastUpdate : DateTime",
            "+ statistics : JSON"
        ) to listOf(
            "+ loadStatistics()",
            "+ displayKPIs()",
            "+ exportReport()",
            "+ refreshData()"
        )),
        "UserManagement" to (listOf(
            "+ managementId : UUID",
            "+ totalUsers : Integer",
            "+ activeUsers : Integer",
            "+ blockedUsers : Integer"
        ) to listOf(
            "+ createUser()",
            "+ updateUser()",
            "+ deleteUser()",
            "+ blockUser()",
            "+ activateUser()",
            "+ verifyUser()"
        )),
        "ProviderManagement" to (listOf(
            "+ providerManagementId : UUID",
            "+ verificationStatus : Enum",
            "+ approvalDate : Date"
        ) to listOf(
            "+ approveProvider()",
            "+ rejectProvider()",
            "+ suspendProvider()",
            "+ verifyDocuments()",
            "+ viewProviderPerformance()"
        )),
        "ContentManagement" to (listOf(
            "+ contentId : UUID",
            "+ contentType : Enum",
            "+ status : Enum"
        ) to listOf(
            "+ addContent()",
            "+ editContent()",
            "+ deleteContent()",
            "+ approveContent()"
        )),
        "ReportManager" to (listOf(
            "+ reportId : UUID",
            "+ reportType : Enum",
            "+ createdDate : DateTime"
        ) to listOf(
            "+ generateReport()",
            "+ exportPDF()",
            "+ exportExcel()",
            "+ scheduleReport()"
        )),
        "AnalyticsDashboard" to (listOf(
            "+ analyticsId : UUID",
            "+ visitorsCount : Integer",
            "+ bookingsCount : Integer",
            "+ revenue : Decimal",
            "+ conversionRate : Float"
        ) to listOf(
            "+ calculateKPIs()",
            "+ analyzeUsers()",
            "+ analyzeRevenue()",
            "+ predictGrowth()"
        )),
        "SubscriptionManager" to (listOf(
            "+ subscriptionId : UUID",
            "+ planName : String",
            "+ price : Decimal",
            "+ duration : Integer"
        ) to listOf(
            "+ createPlan()",
            "+ updatePlan()",
            "+ cancelSubscription()",
            "+ renewSubscription()"
        )),
        "CommissionManager" to (listOf(
            "+ commissionId : UUID",
            "+ percentage : Float",
            "+ totalCommission : Decimal"
        ) to listOf(
            "+ calculateCommission()",
            "+ updateCommissionRate()",
            "+ generateCommissionReport()"
        )),
        "AdvertisementManager" to (listOf(
            "+ advertisementId : UUID",
            "+ title : String",
            "+ startDate : Date",
            "+ endDate : Date",
            "+ budget : Decimal"
        ) to listOf(
            "+ createAdvertisement()",
            "+ approveAdvertisement()",
            "+ stopAdvertisement()",
            "+ trackPerformance()"
        )),
        "AuditLog" to (listOf(
            "+ logId : UUID",
            "+ userId : UUID",
            "+ action : String",
            "+ ipAddress : String",
            "+ createdAt : DateTime"
        ) to listOf(
            "+ saveLog()",
            "+ searchLogs()",
            "+ exportLogs()"
        )),
        "SystemSettings" to (listOf(
            "+ settingId : UUID",
            "+ key : String",
            "+ value : String"
        ) to listOf(
            "+ updateSetting()",
            "+ getSetting()",
            "+ resetSetting()"
        ))
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "🛠️ 9.24 لوحات التحكم والإدارة والرقابة (Admin Dashboard & Management)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC59B27),
            fontSize = 13.sp
        )
        Text(
            text = "فئات مخصصة لإدارة العمليات اليومية والمالية لمنصة تِجربة، ومراقبة أداء مقدمي الخدمات والمستخدمين عبر لوحات تحكم ذكية ومؤمنة تماماً:",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )
        InteractiveClassList(classes = adminClasses)
        
        Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))
        
        Text(
            text = "🔒 الصلاحيات والأمان (RBAC Model):",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE11D48),
            fontSize = 12.sp
        )
        Text(
            text = "يعتمد النظام على نموذج التحكم بالوصول المستند للأدوار (Role Based Access Control):\n• Administrator (المدير العام): تحكم كامل بجميع الإعدادات والتحليلات والتقارير.\n• Finance Manager (المدير المالي): إدارة المحافظ والعمولات ومطالبات السحب والاشتراكات الفعالة.\n• Support Staff (الدعم الفني): متابعة البلاغات والتذاكر والشكاوى الواردة من المستخدمين.\n• Content Manager (مدير المحتوى): مراجعة وقبول ونشر تفاصيل الأنشطة والتجارب التراثية.",
            fontSize = 11.sp,
            color = Color(0xFFE2E8F0),
            lineHeight = 15.sp
        )
    }
}

@Composable
fun RelationsOopTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "🔗 9.3 العلاقات بين الكيانات والوراثة والتجميع (UML Relationships)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC59B27),
            fontSize = 13.sp
        )
        Text(
            text = "هنا نوضح كيف تترابط فئات النظام كبرمجة كائنية التوجه (OOP) وكيف تم نمذجتها برمجياً وتصميمياً:",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        // Inheritance Block
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🧬 9.4 الوراثة (Inheritance):",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp
                )
                Text(
                    text = "فئة أساسية لمقدم الخدمة (ServiceProvider) تشمل الخصائص المشتركة:\n- providerId | commercialName | license | wallet | rating | status\n\nويرث منها الفئات المتخصصة التالية مع إضافة خصائصها الخاصة:\n- ExperienceProvider (مقدم التجارب)\n- Hotel (الفنادق ودور الضيافة)\n- CarCompany (شركات تأجير السيارات)\n- TourGuide (المرشدين السياحيين)",
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Composition Block
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
            border = BorderStroke(1.dp, Color(0xFFF43F5E).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "♦ 9.5 التركيب القوي (Composition):",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF43F5E),
                    fontSize = 12.sp
                )
                Text(
                    text = "علاقة ارتباط قوية جداً تم تفعيلها برمجياً لضمان التناسق الإتلافي المرجعي:\n- مثال: Hotel ♦──── Room\n- منطق العمل: في حال تم إيقاف أو حذف الفندق من لوحة الإدارة يتم تلقائياً حذف جميع الغرف المرتبطة به في قاعدة البيانات لمنع وجود بيانات يتيمة.",
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Aggregation Block
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
            border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "○ 9.6 التجميع المرن (Aggregation):",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    fontSize = 12.sp
                )
                Text(
                    text = "علاقة ارتباط مرنة بحيث يحتفظ كل عنصر بكيانه المستقل:\n- مثال: Trip ○──── Experience\n- منطق العمل: الرحلة الذكية (Trip) تحتوي وتجمع عدة تجارب (Experience)، ولكن في حال قام السائح بحذف الرحلة من ملفه، تظل التجارب محفوظة في النظام وقابلة للاستكشاف والحجز بشكل مستقل.",
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Dependency Block
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
            border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "⚙️ 9.7 الاعتماد والواجهات (Dependency & Interfaces):",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA855F7),
                    fontSize = 12.sp
                )
                Text(
                    text = "• الاعتماد (Dependency): يعتمد كائن الحجز (Booking) على الخدمات المساعدة لتنفيذ وظائفه:\n- PaymentService (لتنفيذ بوابات الدفع)\n- NotificationService (لإرسال كود الـ OTP والـ QR Code والتنبيهات)\n- AIRecommendationService (لاقتراح رحلات وتفاصيل دقيقة)\n\n• الواجهات (Interfaces): صُممت الفئات المشتركة كواجهات لتسهيل التطوير واستبدال مزودي الخدمات:\n- IPaymentGateway (Stripe / PayPal وبوابات الدفع اليمنية لاحقاً)\n- INotificationService (Email / SMS / WebPush Notifications)\n- IMapService (Google Maps / OpenStreetMap)",
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// =========================================================================
// CHAPTER 21: REST API DESIGN & INTERACTIVE CONSOLE
// =========================================================================
@Composable
fun ChapterTwentyOneContent() {
    var selectedEndpoint by remember { mutableStateOf(0) }
    var apiResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val endpoints = listOf(
        "POST /auth/login" to ("""
        {
          "email": "student@tajrubah.edu",
          "password": "hashed_password_123"
        }
        """.trimIndent() to """
        {
          "status": "success",
          "code": 200,
          "message": "تم تسجيل الدخول بنجاح",
          "data": {
            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "user": {
              "id": "e30129bc-d931-4b11-b930-b9df72a2e4c2",
              "name": "م. رغد",
              "email": "student@tajrubah.edu",
              "role": "Traveler"
            }
          }
        }
        """.trimIndent()),

        "GET /experiences" to ("" to """
        {
          "status": "success",
          "code": 200,
          "data": [
            {
              "id": "exp-coffee-01",
              "titleAr": "قطف البن الخولاني التراثي في مدرجات حراز",
              "priceYer": 25000,
              "currency": "YER",
              "capacity": 15,
              "rating": 4.9
            },
            {
              "id": "exp-socotra-02",
              "titleAr": "جولة السير البيئية لوادي ديرهور بـسقطرى",
              "priceYer": 75000,
              "currency": "YER",
              "capacity": 8,
              "rating": 5.0
            }
          ]
        }
        """.trimIndent()),

        "POST /bookings/create" to ("""
        {
          "experienceId": "exp-coffee-01",
          "startDate": "2026-07-25T09:00:00Z",
          "numberOfPeople": 2,
          "currency": "YER"
        }
        """.trimIndent() to """
        {
          "status": "success",
          "code": 201,
          "message": "تم إنشاء الحجز بنجاح وهو بانتظار الدفع",
          "data": {
            "bookingId": "bk-8820491-yz",
            "experienceId": "exp-coffee-01",
            "totalPrice": 50000,
            "currency": "YER",
            "bookingStatus": "PENDING_PAYMENT",
            "qrCodeData": "tajrubah://booking/bk-8820491-yz"
          }
        }
        """.trimIndent()),

        "POST /payments/pay" to ("""
        {
          "bookingId": "bk-8820491-yz",
          "paymentMethod": "MOCK_WALLET",
          "amount": 50000,
          "currency": "YER"
        }
        """.trimIndent() to """
        {
          "status": "success",
          "code": 200,
          "message": "تمت معالجة الدفع بنجاح واقتطاع المبلغ من حساب الضمان",
          "data": {
            "paymentId": "pay-9204912-ab",
            "bookingId": "bk-8820491-yz",
            "amountPaid": 50000,
            "currency": "YER",
            "status": "PAID",
            "transactionId": "tx_yem_wallet_00129",
            "paidAt": "2026-07-20T21:54:10Z"
          }
        }
        """.trimIndent()),

        "POST /ai/trip-plan" to ("""
        {
          "city": "صنعاء القديمة وحراز",
          "durationDays": 3,
          "budgetUsd": 300,
          "travelersCount": 2,
          "interests": ["مزارع البن", "عمارة تراثية", "حرف يدوية"]
        }
        """.trimIndent() to """
        {
          "status": "success",
          "code": 200,
          "data": {
            "assistantPlan": "برنامج سياحي ذكي مخصص لصنعاء وحراز لمدة 3 أيام لعدد 2 أشخاص بميزانية 300 دولار",
            "days": [
              {
                "day": 1,
                "activities": [
                  "صباحاً: جولة تصفح AR لباب اليمن ومصانع الجنابي القديمة",
                  "مساءً: تذوق شاي القشر والقهوة الصنعانية ببيت الفن التراثي"
                ]
              },
              {
                "day": 2,
                "activities": [
                  "صباحاً: السفر لـحراز عبر سيارة لاندكروزر دفع رباعي",
                  "ظهراً: تجربة قطف حبوب البن الخولاني التراثي مع مزارعي حراز"
                ]
              },
              {
                "day": 3,
                "activities": [
                  "صباحاً: زيارة حصن الهجرة الأثري المعلق فوق السحاب",
                  "مساءً: العودة لصنعاء واستخراج الهدايا وتذكرة الـ QR للمشاركين"
                ]
              }
            ],
            "totalCostEstimationUsd": 280
          }
        }
        """.trimIndent())
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "🔌 تصميم واجهة برمجة التطبيقات (REST API Design)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC59B27),
            fontSize = 14.sp
        )
        Text(
            text = "توفر منصة تِجربة واجهات برمجة تطبيقات (APIs) آمنة للغاية لتسهيل الترابط والتكامل السريع والآمن بين منصة تِجربة وكافة الأنظمة الفرعية والجهات الخارجية:",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        // API Specs details
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
            border = BorderStroke(1.dp, Color(0xFFC59B27).copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "🌐 مواصفات بروتوكول الـ REST API:", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 12.sp)
                Text(text = "• Base URL: https://api.tajrubah.com/v1", fontSize = 10.sp, color = Color.White)
                Text(text = "• Security: Bearer JWT Token + SSL Encryption", fontSize = 10.sp, color = Color.White)
                Text(text = "• Data Format: application/json", fontSize = 10.sp, color = Color.White)
                Text(text = "• Rate Limiting: 60 requests/minute per client IP", fontSize = 10.sp, color = Color.White)
            }
        }

        Divider(color = Color(0x1AFFFFFF))

        Text(
            text = "🕹️ وحدة محاكاة واختبار الـ API (Interactive API Console):",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC59B27),
            fontSize = 13.sp
        )
        Text(
            text = "اختر أحد الـ API Endpoints لاستعراض محددات ومخرجات الطلب وتجربة المعالجة التفاعلية الفورية:",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        // LazyRow of API buttons
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(endpoints.size) { index ->
                val parts = endpoints[index].first.split(" ")
                val method = parts[0]
                val path = parts[1]
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedEndpoint == index) Color(0xFFC59B27) else Color(0x13FFFFFF))
                        .clickable {
                            selectedEndpoint = index
                            apiResponse = ""
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(if (method == "POST") Color(0xFF10B981) else Color(0xFF38BDF8), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(text = method, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = path, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (selectedEndpoint == index) Color.Black else Color.White)
                    }
                }
            }
        }

        // Show Payload if any
        val selectedItem = endpoints[selectedEndpoint]
        val payload = selectedItem.second.first
        val expectedResponse = selectedItem.second.second

        if (payload.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "📥 Request Body (JSON payload):", fontSize = 10.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(text = payload, fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = Color(0xFF34D399))
                }
            }
        }

        // Action Button
        Button(
            onClick = {
                isLoading = true
                apiResponse = ""
                coroutineScope.launch {
                    kotlinx.coroutines.delay(600)
                    isLoading = false
                    apiResponse = expectedResponse
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC59B27)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isLoading) "جاري إرسال الطلب ومعالجة الـ API..." else "إرسال طلب تجريبي (Send Test Request)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }

        // Response view
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFC59B27))
            }
        }

        if (apiResponse.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "📤 Response (HTTP 200 OK):", fontSize = 10.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                    Text(text = "Format: JSON", fontSize = 9.sp, color = Color(0xFF94A3B8))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(text = apiResponse, fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = Color(0xFFE2E8F0))
                }
            }
        }
    }
}


