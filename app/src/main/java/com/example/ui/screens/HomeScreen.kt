package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AwarenessCategory
import com.example.data.model.AwarenessRepository
import com.example.data.model.MoodType
import com.example.data.model.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.MindlyViewModel
import com.example.ui.SubScreen
import com.example.ui.components.GlassCard
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.components.MindlyLogo
import com.example.ui.components.NavigationScreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LocalMindlyColors
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: MindlyViewModel,
    user: UserEntity,
    language: AppLanguage,
    onNavigateToTab: (NavigationScreen) -> Unit,
    onOpenSubScreen: (SubScreen) -> Unit
) {
    val context = LocalContext.current
    val glassColors = LocalMindlyColors.current
    val scrollState = rememberScrollState()

    // Determine Greeting by Hour
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingKey = when (hour) {
        in 4..11 -> "greeting_morning"
        in 12..16 -> "greeting_afternoon"
        else -> "greeting_evening"
    }

    // Mood Check-in state
    var selectedMoodLevel by remember { mutableStateOf<Int?>(null) }
    var moodNote by remember { mutableStateOf("") }
    var moodSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp) // extra padding for bottom dock
    ) {
        // Top Bar: Logo & Avatar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MindlyLogo(size = 32.dp)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(glassColors.surface)
                    .border(1.dp, glassColors.glassCardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = user.role.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Greeting
        Text(
            text = "${MindlyStrings.get(greetingKey, language)}, ${user.name}",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = glassColors.textPrimary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Prominent Support Section: "Need Help Right Now?"
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            isHighlighted = true,
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmergencyRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = EmergencyRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = MindlyStrings.get("need_help_title", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = glassColors.textPrimary
                        )
                        Text(
                            text = MindlyStrings.get("need_help_desc", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = glassColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onOpenSubScreen(SubScreen.EmergencyHelp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("home_emergency_help_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmergencyRed,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = MindlyStrings.get("btn_emergency_help", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { onNavigateToTab(NavigationScreen.HELP) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("home_find_support_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = glassColors.accentGlow,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = MindlyStrings.get("btn_find_support", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Access Cards (3 Cards)
        Text(
            text = MindlyStrings.get("quick_access", language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = glassColors.textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                title = MindlyStrings.get("qa_help_title", language),
                subtitle = MindlyStrings.get("qa_help_desc", language),
                icon = Icons.Default.SupportAgent,
                testTag = "qa_help_card",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(NavigationScreen.HELP) }
            )

            QuickAccessCard(
                title = MindlyStrings.get("qa_contacts_title", language),
                subtitle = MindlyStrings.get("qa_contacts_desc", language),
                icon = Icons.Default.People,
                testTag = "qa_contacts_card",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(NavigationScreen.CONTACTS) }
            )

            QuickAccessCard(
                title = MindlyStrings.get("qa_mood_title", language),
                subtitle = MindlyStrings.get("qa_mood_desc", language),
                icon = Icons.Default.Mood,
                testTag = "qa_mood_card",
                modifier = Modifier.weight(1f),
                onClick = { onOpenSubScreen(SubScreen.MoodHistory) }
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Daily Mood Check-In Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = MindlyStrings.get("daily_checkin", language),
                            style = MaterialTheme.typography.labelMedium,
                            color = glassColors.accentGlow,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = MindlyStrings.get("how_are_you_feeling", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = glassColors.textPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = { onOpenSubScreen(SubScreen.MoodHistory) },
                        modifier = Modifier.testTag("view_mood_history_btn"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = glassColors.accentGlow
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = MindlyStrings.get("view_mood_history", language),
                            fontSize = 11.sp,
                            color = glassColors.accentGlow
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5 Mood Emoji Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MoodType.entries.forEach { mood ->
                        val isSelected = selectedMoodLevel == mood.level
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .testTag("mood_option_${mood.level}")
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) glassColors.accentContainer else glassColors.surface)
                                .border(
                                    1.2.dp,
                                    if (isSelected) glassColors.accentGlow else glassColors.glassCardBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedMoodLevel = mood.level }
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                        ) {
                            Text(text = mood.emoji, fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = MindlyStrings.get(mood.key, language),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) glassColors.accentGlow else glassColors.textSecondary
                            )
                        }
                    }
                }

                if (selectedMoodLevel != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = moodNote,
                        onValueChange = { moodNote = it },
                        placeholder = { Text(MindlyStrings.get("mood_note_hint", language), fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mood_note_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = false,
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = glassColors.accentGlow,
                            unfocusedBorderColor = glassColors.glassCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.logMood(selectedMoodLevel!!, moodNote) {
                                moodSubmitted = true
                                moodNote = ""
                                Toast.makeText(
                                    context,
                                    MindlyStrings.get("mood_logged_success", language),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("submit_mood_checkin_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow)
                    ) {
                        Text(
                            text = MindlyStrings.get("log_mood", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Daily Wellbeing Tools Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = MindlyStrings.get("wellbeing_tools", language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = glassColors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ToolCard(
                title = MindlyStrings.get("tool_breathing", language),
                subtitle = MindlyStrings.get("tool_breathing_desc", language),
                icon = Icons.Default.Air,
                accentColor = glassColors.accentGlow,
                testTag = "tool_breathing_card",
                modifier = Modifier.weight(1f),
                onClick = { onOpenSubScreen(SubScreen.BreathingExercise) }
            )

            ToolCard(
                title = MindlyStrings.get("tool_grounding", language),
                subtitle = MindlyStrings.get("tool_grounding_desc", language),
                icon = Icons.Default.SelfImprovement,
                accentColor = Color(0xFF10B981),
                testTag = "tool_grounding_card",
                modifier = Modifier.weight(1f),
                onClick = { onOpenSubScreen(SubScreen.GroundingExercise) }
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Mental Health Awareness Categories Section
        Column {
            Text(
                text = MindlyStrings.get("mental_wellbeing", language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = glassColors.textPrimary
            )
            Text(
                text = MindlyStrings.get("awareness_subtitle", language),
                style = MaterialTheme.typography.bodySmall,
                color = glassColors.textMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Carousel / Grid of 8 categories
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(AwarenessRepository.categories) { cat ->
                    AwarenessCategoryCard(
                        category = cat,
                        language = language,
                        onClick = { onOpenSubScreen(SubScreen.AwarenessDetail(cat)) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Wellbeing Tip of the Day
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(glassColors.accentContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = glassColors.accentGlow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = MindlyStrings.get("wellbeing_tip_title", language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = glassColors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "\"Taking a 5-minute break to deliberately exhale longer than you inhale activates the parasympathetic nervous system, naturally reducing physical tension and restoring cognitive clarity.\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = glassColors.textSecondary,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Medical Safety Disclaimer Card
        MedicalDisclaimerCard(language = language)
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val glassColors = LocalMindlyColors.current

    GlassCard(
        modifier = modifier.height(125.dp),
        onClick = onClick,
        testTag = testTag
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(glassColors.accentContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = glassColors.accentGlow,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = glassColors.textMuted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val glassColors = LocalMindlyColors.current

    GlassCard(
        modifier = modifier.height(130.dp),
        onClick = onClick,
        testTag = testTag
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = glassColors.textMuted,
                    lineHeight = 14.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun AwarenessCategoryCard(
    category: AwarenessCategory,
    language: AppLanguage,
    onClick: () -> Unit
) {
    val glassColors = LocalMindlyColors.current

    val icon = when (category.iconName) {
        "bolt" -> Icons.Default.Bolt
        "waves" -> Icons.Default.Waves
        "cloud" -> Icons.Default.Cloud
        "moon" -> Icons.Default.Nightlight
        "shield_alert" -> Icons.Default.Shield
        "user_heart" -> Icons.Default.PersonOutline
        "school" -> Icons.Default.School
        else -> Icons.Default.AutoAwesome
    }

    GlassCard(
        modifier = Modifier
            .width(160.dp)
            .height(150.dp),
        onClick = onClick,
        testTag = "awareness_card_${category.id}"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(glassColors.accentContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = glassColors.accentGlow,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = MindlyStrings.get(category.titleKey, language),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category.subtitle,
                    fontSize = 10.sp,
                    color = glassColors.textMuted,
                    maxLines = 2,
                    lineHeight = 13.sp
                )
            }
        }
    }
}
