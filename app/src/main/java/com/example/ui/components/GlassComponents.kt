package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.theme.LocalMindlyColors

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    isHighlighted: Boolean = false,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val glassColors = LocalMindlyColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else if (isHighlighted) 8.dp else 4.dp,
        animationSpec = tween(150),
        label = "glass_elevation"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isPressed || isHighlighted -> glassColors.glassCardBorderGlow
            else -> glassColors.glassCardBorder
        },
        animationSpec = tween(200),
        label = "glass_border"
    )

    val cardModifier = modifier
        .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = if (glassColors.isDark) glassColors.accentGlow.copy(alpha = 0.2f) else Color(0x1A000000),
            spotColor = if (glassColors.isDark) glassColors.accentGlow.copy(alpha = 0.35f) else Color(0x20000000)
        )
        .border(
            border = BorderStroke(1.2.dp, borderColor),
            shape = shape
        )
        .clip(shape)
        .background(glassColors.glassCardBackground)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )

    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
fun MindlyLogo(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    showText: Boolean = true
) {
    val glassColors = LocalMindlyColors.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.35f))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            glassColors.accentGlow,
                            if (glassColors.isDark) Color(0xFF6B21A8) else Color(0xFF1D4ED8)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "MINDLY Emblem",
                tint = Color.White,
                modifier = Modifier.size(size * 0.58f)
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "MINDLY",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = glassColors.textPrimary
            )
        }
    }
}

@Composable
fun MindlyTopBar(
    title: String,
    language: AppLanguage,
    onBack: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    testTag: String = "top_bar"
) {
    val glassColors = LocalMindlyColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("back_button")
                        .clip(CircleShape)
                        .background(glassColors.glassCardBackground)
                        .border(1.dp, glassColors.glassCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = MindlyStrings.get("back", language),
                        tint = glassColors.accentGlow,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = glassColors.textPrimary,
                maxLines = 1
            )
        }

        if (actions != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                actions()
            }
        }
    }
}

enum class NavigationScreen(val route: String) {
    HOME("home"),
    HELP("help"),
    CONTACTS("contacts"),
    SETTINGS("settings")
}

@Composable
fun FloatingBottomDock(
    currentScreen: NavigationScreen,
    onNavigate: (NavigationScreen) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalMindlyColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = if (glassColors.isDark) glassColors.accentGlow.copy(alpha = 0.25f) else Color(0x1F000000),
                    spotColor = if (glassColors.isDark) glassColors.accentGlow.copy(alpha = 0.4f) else Color(0x2E000000)
                )
                .border(
                    width = 1.2.dp,
                    color = glassColors.glassCardBorderGlow.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(32.dp)
                )
                .clip(RoundedCornerShape(32.dp)),
            color = glassColors.glassCardBackground.copy(alpha = 0.94f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockItem(
                    label = MindlyStrings.get("nav_home", language),
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    isSelected = currentScreen == NavigationScreen.HOME,
                    testTag = "nav_home",
                    onClick = { onNavigate(NavigationScreen.HOME) }
                )
                DockItem(
                    label = MindlyStrings.get("nav_help", language),
                    selectedIcon = Icons.Filled.SupportAgent,
                    unselectedIcon = Icons.Outlined.SupportAgent,
                    isSelected = currentScreen == NavigationScreen.HELP,
                    testTag = "nav_help",
                    onClick = { onNavigate(NavigationScreen.HELP) }
                )
                DockItem(
                    label = MindlyStrings.get("nav_contacts", language),
                    selectedIcon = Icons.Filled.People,
                    unselectedIcon = Icons.Outlined.People,
                    isSelected = currentScreen == NavigationScreen.CONTACTS,
                    testTag = "nav_contacts",
                    onClick = { onNavigate(NavigationScreen.CONTACTS) }
                )
                DockItem(
                    label = MindlyStrings.get("nav_settings", language),
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings,
                    isSelected = currentScreen == NavigationScreen.SETTINGS,
                    testTag = "nav_settings",
                    onClick = { onNavigate(NavigationScreen.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun DockItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val glassColors = LocalMindlyColors.current

    val pillBackground by animateColorAsState(
        targetValue = if (isSelected) glassColors.accentContainer else Color.Transparent,
        animationSpec = tween(200),
        label = "pill_bg"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) glassColors.accentGlow else glassColors.textMuted,
        animationSpec = tween(200),
        label = "icon_color"
    )

    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(20.dp))
            .background(pillBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = iconColor
            )
        }
    }
}

@Composable
fun MedicalDisclaimerCard(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalMindlyColors.current

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Medical Disclaimer",
                tint = glassColors.accentGlow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = MindlyStrings.get("medical_disclaimer_short", language),
                style = MaterialTheme.typography.bodySmall,
                color = glassColors.textSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
