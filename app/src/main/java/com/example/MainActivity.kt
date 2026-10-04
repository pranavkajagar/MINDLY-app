package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.AdminStats
import com.example.ui.MindlyViewModel
import com.example.ui.SubScreen
import com.example.ui.components.FloatingBottomDock
import com.example.ui.components.NavigationScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.AwarenessDetailScreen
import com.example.ui.screens.BreathingExerciseScreen
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.EmergencyHelpScreen
import com.example.ui.screens.GroundingExerciseScreen
import com.example.ui.screens.HelpDirectoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoodHistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.LocalMindlyColors
import com.example.ui.theme.MindlyTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MindlyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MindlyTheme(darkTheme = isDarkMode) {
                MindlyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MindlyApp(viewModel: MindlyViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val language by viewModel.language.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val subScreen by viewModel.subScreen.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val personalContacts by viewModel.personalContacts.collectAsState()
    val moodHistory by viewModel.moodHistory.collectAsState()
    val adminStats by viewModel.adminStats.collectAsState()

    val glassColors = LocalMindlyColors.current

    if (currentUser == null) {
        AuthScreen(
            viewModel = viewModel,
            language = language,
            isLoading = isAuthLoading,
            errorMessageKey = authError
        )
    } else {
        // Handle Back button for secondary sub-screens
        val isSubScreenActive = subScreen !is SubScreen.None
        BackHandler(enabled = isSubScreenActive) {
            viewModel.navigateBack()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(glassColors.background)
                .statusBarsPadding()
        ) {
            // Main Content Area with subtle fade transitions
            AnimatedContent(
                targetState = Pair(subScreen, currentTab),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "mindly_content_transition"
            ) { (currentSub, activeTab) ->
                when (currentSub) {
                    is SubScreen.AwarenessDetail -> {
                        AwarenessDetailScreen(
                            category = currentSub.category,
                            language = language,
                            onBack = { viewModel.navigateBack() },
                            onOpenHelp = {
                                viewModel.navigateBack()
                                viewModel.navigateToTab(NavigationScreen.HELP)
                            },
                            onOpenEmergency = {
                                viewModel.navigateBack()
                                viewModel.pushSubScreen(SubScreen.EmergencyHelp)
                            }
                        )
                    }
                    is SubScreen.BreathingExercise -> {
                        BreathingExerciseScreen(
                            language = language,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is SubScreen.GroundingExercise -> {
                        GroundingExerciseScreen(
                            language = language,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is SubScreen.MoodHistory -> {
                        MoodHistoryScreen(
                            moods = moodHistory,
                            language = language,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is SubScreen.EmergencyHelp -> {
                        EmergencyHelpScreen(
                            personalContacts = personalContacts,
                            language = language,
                            onBack = { viewModel.navigateBack() },
                            onOpenHelpDirectory = {
                                viewModel.navigateBack()
                                viewModel.navigateToTab(NavigationScreen.HELP)
                            }
                        )
                    }
                    is SubScreen.AdminDashboard -> {
                        if (currentUser?.role == "admin") {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                stats = adminStats,
                                language = language,
                                onBack = { viewModel.navigateBack() }
                            )
                        } else {
                            viewModel.navigateBack()
                        }
                    }
                    SubScreen.None -> {
                        // Main Bottom Dock Tabs
                        when (activeTab) {
                            NavigationScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                user = currentUser!!,
                                language = language,
                                onNavigateToTab = { viewModel.navigateToTab(it) },
                                onOpenSubScreen = { viewModel.pushSubScreen(it) }
                            )
                            NavigationScreen.HELP -> HelpDirectoryScreen(
                                viewModel = viewModel,
                                language = language
                            )
                            NavigationScreen.CONTACTS -> ContactsScreen(
                                viewModel = viewModel,
                                contacts = personalContacts,
                                language = language
                            )
                            NavigationScreen.SETTINGS -> SettingsScreen(
                                viewModel = viewModel,
                                user = currentUser!!,
                                language = language,
                                isDarkMode = isDarkMode,
                                onOpenAdminDashboard = { viewModel.pushSubScreen(SubScreen.AdminDashboard) }
                            )
                        }
                    }
                }
            }

            // Fixed Floating 3D Glass Bottom Dock (Visible on main tabs)
            if (!isSubScreenActive) {
                FloatingBottomDock(
                    currentScreen = currentTab,
                    onNavigate = { viewModel.navigateToTab(it) },
                    language = language,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
