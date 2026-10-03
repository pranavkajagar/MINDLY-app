package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.components.GlassCard
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.components.MindlyTopBar
import com.example.ui.theme.LocalMindlyColors
import kotlinx.coroutines.delay

enum class BreathingPhase(val key: String, val durationMs: Long, val targetScale: Float) {
    BREATHE_IN("breathe_in", 4000L, 1.35f),
    HOLD_IN("hold", 4000L, 1.35f),
    BREATHE_OUT("breathe_out", 4000L, 0.85f),
    HOLD_OUT("hold", 4000L, 0.85f)
}

@Composable
fun BreathingExerciseScreen(
    language: AppLanguage,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val glassColors = LocalMindlyColors.current

    var isRunning by remember { mutableStateOf(true) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.BREATHE_IN) }
    var cyclesCompleted by remember { mutableIntStateOf(0) }
    var secondsRemaining by remember { mutableIntStateOf(4) }

    // Haptic helper
    fun triggerVibration() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
    }

    // Breathing Timer Loop
    LaunchedEffect(isRunning, currentPhase) {
        if (!isRunning) return@LaunchedEffect

        secondsRemaining = 4
        triggerVibration()

        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
        }

        currentPhase = when (currentPhase) {
            BreathingPhase.BREATHE_IN -> BreathingPhase.HOLD_IN
            BreathingPhase.HOLD_IN -> BreathingPhase.BREATHE_OUT
            BreathingPhase.BREATHE_OUT -> BreathingPhase.HOLD_OUT
            BreathingPhase.HOLD_OUT -> {
                cyclesCompleted += 1
                BreathingPhase.BREATHE_IN
            }
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isRunning) currentPhase.targetScale else 1.0f,
        animationSpec = tween(
            durationMillis = if (currentPhase == BreathingPhase.HOLD_IN || currentPhase == BreathingPhase.HOLD_OUT) 200 else 4000,
            easing = FastOutSlowInEasing
        ),
        label = "breathing_circle_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.background)
    ) {
        MindlyTopBar(
            title = MindlyStrings.get("tool_breathing", language),
            language = language,
            onBack = onBack,
            testTag = "breathing_top_bar"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header stats
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(glassColors.surface)
                    .border(1.dp, glassColors.glassCardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = glassColors.accentGlow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${MindlyStrings.get("breath_cycle", language)}: $cyclesCompleted",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary
                )
            }

            // Visual Animated Breathing Circle
            Box(
                modifier = Modifier
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background ripple glow
                Canvas(
                    modifier = Modifier
                        .size(260.dp * animatedScale)
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glassColors.accentGlow.copy(alpha = 0.35f),
                                glassColors.accentGlow.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
                    drawCircle(
                        color = glassColors.accentGlow.copy(alpha = 0.4f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Inner Circle with phase text
                Box(
                    modifier = Modifier
                        .size(160.dp * animatedScale.coerceAtLeast(0.9f))
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    glassColors.accentGlow,
                                    if (glassColors.isDark) Color(0xFF6B21A8) else Color(0xFF0369A1)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = MindlyStrings.get(currentPhase.key, language),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${secondsRemaining}s",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Controls
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { isRunning = !isRunning },
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("breathing_toggle_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRunning) "Pause" else "Resume",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            currentPhase = BreathingPhase.BREATHE_IN
                            cyclesCompleted = 0
                            secondsRemaining = 4
                        },
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("breathing_reset_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = glassColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                MedicalDisclaimerCard(language = language)
            }
        }
    }
}

@Composable
fun GroundingExerciseScreen(
    language: AppLanguage,
    onBack: () -> Unit
) {
    val glassColors = LocalMindlyColors.current
    val scrollState = rememberScrollState()

    val checkedItems = remember { mutableStateListOf<Int>() }

    val steps = listOf(
        Pair(5, MindlyStrings.get("g_see", language)),
        Pair(4, MindlyStrings.get("g_touch", language)),
        Pair(3, MindlyStrings.get("g_hear", language)),
        Pair(2, MindlyStrings.get("g_smell", language)),
        Pair(1, MindlyStrings.get("g_taste", language))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.background)
    ) {
        MindlyTopBar(
            title = MindlyStrings.get("grounding_title", language),
            language = language,
            onBack = onBack,
            testTag = "grounding_top_bar"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
                .padding(bottom = 90.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = glassColors.accentGlow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "5-4-3-2-1 Technique",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = glassColors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = MindlyStrings.get("grounding_desc", language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = glassColors.textSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            steps.forEachIndexed { index, (count, instruction) ->
                val isChecked = checkedItems.contains(index)

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    isHighlighted = isChecked,
                    onClick = {
                        if (isChecked) {
                            checkedItems.remove(index)
                        } else {
                            checkedItems.add(index)
                        }
                    },
                    testTag = "grounding_step_$index"
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isChecked) Color(0xFF10B981) else glassColors.accentContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = count.toString(),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = if (isChecked) Color.White else glassColors.accentGlow
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = instruction,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isChecked) glassColors.textPrimary else glassColors.textSecondary
                            )
                        }

                        Icon(
                            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isChecked) Color(0xFF10B981) else glassColors.textMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (checkedItems.size == steps.size) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    isHighlighted = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✨ Great Job!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = glassColors.accentGlow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You've successfully anchored yourself back in the physical present. Notice the calm in your breathing.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = glassColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedButton(
                onClick = { checkedItems.clear() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("grounding_reset_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset Checkpoints", color = glassColors.textSecondary)
            }

            Spacer(modifier = Modifier.height(20.dp))
            MedicalDisclaimerCard(language = language)
        }
    }
}
