package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MoodCheckinEntity
import com.example.data.model.MoodType
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.components.GlassCard
import com.example.ui.components.MindlyTopBar
import com.example.ui.theme.LocalMindlyColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MoodHistoryScreen(
    moods: List<MoodCheckinEntity>,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val glassColors = LocalMindlyColors.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.background)
    ) {
        MindlyTopBar(
            title = MindlyStrings.get("view_mood_history", language),
            language = language,
            onBack = onBack,
            testTag = "mood_history_top_bar"
        )

        if (moods.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(glassColors.accentContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mood,
                            contentDescription = null,
                            tint = glassColors.accentGlow,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Mood Entries Yet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = glassColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Log how you're feeling on the Home page to start tracking your emotional journey.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = glassColors.textMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .testTag("mood_history_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your Recorded Check-Ins (${moods.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = glassColors.textMuted
                    )
                }

                items(moods) { checkin ->
                    val mood = MoodType.fromLevel(checkin.moodLevel)

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "mood_item_${checkin.id}"
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(glassColors.accentContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = mood.emoji, fontSize = 24.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = MindlyStrings.get(mood.key, language),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = glassColors.textPrimary
                                    )
                                    Text(
                                        text = dateFormat.format(Date(checkin.timestamp)),
                                        fontSize = 11.sp,
                                        color = glassColors.textMuted
                                    )
                                }

                                if (checkin.note.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = checkin.note,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = glassColors.textSecondary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
