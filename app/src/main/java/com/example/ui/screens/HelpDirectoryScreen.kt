package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProblemCategory
import com.example.data.model.ProblemDirectory
import com.example.data.model.ProfessionalContactEntity
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.MindlyViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LocalMindlyColors
import com.example.ui.theme.SuccessGreen

@Composable
fun HelpDirectoryScreen(
    viewModel: MindlyViewModel,
    language: AppLanguage
) {
    val context = LocalContext.current
    val glassColors = LocalMindlyColors.current

    val helplines by viewModel.filteredHelplines.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val selectedProblem by viewModel.selectedProblemFilter.collectAsState()
    val emergencyOnly by viewModel.emergencyOnlyFilter.collectAsState()

    val professionalTypes = listOf(
        "All",
        "Mental Health Helpline",
        "Hospital / Specialized Clinic",
        "Support Organization",
        "Emergency Service",
        "Psychologist",
        "Psychiatrist"
    )

    fun dialNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${number.replace(" ", "").replace("-", "")}")
        }
        context.startActivity(intent)
    }

    fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        context.startActivity(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.background)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        // Top Title & Controlled Verification Shield Logo
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isTabletOrDesktop = this.maxWidth >= 600.dp
            val isSmallMobile = this.maxWidth < 360.dp
            val titleSize = if (isTabletOrDesktop) 26.sp else 22.sp
            val subtitleSize = if (isTabletOrDesktop) 14.sp else 12.sp
            val badgeHeight = if (isTabletOrDesktop) 38.dp else 32.dp
            val badgeIconSize = if (isTabletOrDesktop) 18.dp else 15.dp
            val badgeFontSize = if (isTabletOrDesktop) 12.sp else 11.sp

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = MindlyStrings.get("qa_help_title", language),
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Official Government & Verified Helplines Directory",
                        fontSize = subtitleSize,
                        color = glassColors.textMuted,
                        maxLines = if (isSmallMobile) 2 else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Controlled verification/shield badge (strictly 32px on mobile, 38px on desktop)
                Box(
                    modifier = Modifier
                        .height(badgeHeight)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = if (isTabletOrDesktop) 10.dp else 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified Directory",
                            tint = SuccessGreen,
                            modifier = Modifier.size(badgeIconSize)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Verified Only",
                            fontSize = badgeFontSize,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("help_directory_list"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. FIRST: CATEGORIES OF PROBLEMS (User's primary requirement!)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Problem Category",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = glassColors.textPrimary
                        )

                        if (selectedProblem != "All") {
                            TextButton(
                                onClick = { viewModel.clearProblemFilter() },
                                modifier = Modifier.testTag("clear_problem_filter_btn")
                            ) {
                                Text(
                                    text = "Show All Numbers",
                                    fontSize = 12.sp,
                                    color = glassColors.accentGlow,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal scrolling problem cards
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        item {
                            ProblemCard(
                                title = "All Problems",
                                subtitle = "View all verified helplines & clinics",
                                icon = Icons.Default.SupportAgent,
                                isSelected = selectedProblem == "All",
                                onClick = { viewModel.clearProblemFilter() }
                            )
                        }

                        items(ProblemDirectory.categories) { prob ->
                            val icon = when (prob.iconName) {
                                "crisis" -> Icons.Default.Emergency
                                "anxiety" -> Icons.Default.Waves
                                "depression" -> Icons.Default.Cloud
                                "student" -> Icons.Default.School
                                "addiction" -> Icons.Default.LocalHospital
                                "relationship" -> Icons.Default.People
                                "sleep" -> Icons.Default.Nightlight
                                else -> Icons.Default.Shield
                            }

                            ProblemCard(
                                title = prob.title,
                                subtitle = prob.subtitle,
                                icon = icon,
                                isSelected = selectedProblem == prob.tag,
                                onClick = { viewModel.setProblemFilter(prob.tag) }
                            )
                        }
                    }
                }
            }

            // 2. FEATURED GOVERNMENT NUMBERS BAR (Instant 1-tap call)
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = glassColors.accentGlow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Government 24/7 Toll-Free Helplines",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = glassColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        item {
                            GovtQuickDialPill(
                                name = "Tele-MANAS",
                                number = "14416",
                                badge = "Govt of India 24/7",
                                onCall = { dialNumber("14416") }
                            )
                        }
                        item {
                            GovtQuickDialPill(
                                name = "KIRAN Helpline",
                                number = "1800-599-0019",
                                badge = "National Toll-Free",
                                onCall = { dialNumber("18005990019") }
                            )
                        }
                        item {
                            GovtQuickDialPill(
                                name = "Emergency Rescue",
                                number = "112",
                                badge = "All India 24/7",
                                onCall = { dialNumber("112") }
                            )
                        }
                        item {
                            GovtQuickDialPill(
                                name = "Drug De-Addiction",
                                number = "1800-11-0031",
                                badge = "Govt De-Addiction",
                                onCall = { dialNumber("1800110031") }
                            )
                        }
                        item {
                            GovtQuickDialPill(
                                name = "NIMHANS Clinic",
                                number = "080-26995000",
                                badge = "Govt Apex Institute",
                                onCall = { dialNumber("08026995000") }
                            )
                        }
                    }
                }
            }

            // Active Filter Notice Banner
            if (selectedProblem != "All") {
                item {
                    val activeCategory = ProblemDirectory.categories.find { it.tag == selectedProblem }
                    val categoryTitle = activeCategory?.title ?: selectedProblem

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        isHighlighted = true,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = glassColors.accentGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Showing contacts for: $categoryTitle (${helplines.size})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = glassColors.textPrimary
                                )
                            }

                            IconButton(
                                onClick = { viewModel.clearProblemFilter() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = glassColors.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar & Filter Options
            item {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        placeholder = {
                            Text(
                                text = "Search helpline name, hospital, phone number...",
                                fontSize = 13.sp,
                                color = glassColors.textMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = glassColors.accentGlow
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = glassColors.textMuted
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("help_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = glassColors.accentGlow,
                            unfocusedBorderColor = glassColors.glassCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary type filter chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(professionalTypes) { type ->
                            val isSelected = selectedCategory == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectedCategoryFilter.value = type },
                                label = {
                                    Text(
                                        text = if (type == "All") "All Types" else type,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = glassColors.accentContainer,
                                    selectedLabelColor = glassColors.accentGlow,
                                    containerColor = glassColors.surface,
                                    labelColor = glassColors.textSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = glassColors.glassCardBorder,
                                    selectedBorderColor = glassColors.accentGlow,
                                    enabled = true,
                                    selected = isSelected
                                )
                            )
                        }
                    }

                    // Emergency Only Filter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = MindlyStrings.get("filter_emergency_only", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = glassColors.textSecondary
                        )
                        Switch(
                            checked = emergencyOnly,
                            onCheckedChange = { viewModel.emergencyOnlyFilter.value = it },
                            modifier = Modifier.testTag("emergency_only_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmergencyRed
                            )
                        )
                    }
                }
            }

            // Verified Contacts List
            if (helplines.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(glassColors.accentContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = null,
                                    tint = glassColors.accentGlow,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No verified contacts match this criteria.",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = glassColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.clearProblemFilter()
                                    viewModel.selectedCategoryFilter.value = "All"
                                    viewModel.searchQuery.value = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reset All Filters")
                            }
                        }
                    }
                }
            } else {
                items(helplines) { helpline ->
                    VerifiedHelplineCard(
                        contact = helpline,
                        onCall = { dialNumber(helpline.phone) },
                        onEmail = { sendEmail(helpline.email) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                MedicalDisclaimerCard(language = language)
                Spacer(modifier = Modifier.height(100.dp)) // clearance for dock
            }
        }
    }
}

@Composable
private fun GovtQuickDialPill(
    name: String,
    number: String,
    badge: String,
    onCall: () -> Unit
) {
    val glassColors = LocalMindlyColors.current

    GlassCard(
        modifier = Modifier
            .width(200.dp)
            .height(115.dp),
        isHighlighted = true,
        onClick = onCall,
        testTag = "govt_dial_${name.replace(" ", "_")}"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SuccessGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SuccessGreen
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(glassColors.accentGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary,
                    maxLines = 1
                )
                Text(
                    text = number,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = glassColors.accentGlow
                )
            }
        }
    }
}

@Composable
private fun ProblemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val glassColors = LocalMindlyColors.current

    GlassCard(
        modifier = Modifier
            .width(180.dp)
            .height(130.dp),
        isHighlighted = isSelected,
        onClick = onClick,
        testTag = "problem_card_${title.replace(" ", "_")}"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) glassColors.accentGlow else glassColors.accentContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else glassColors.accentGlow,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(glassColors.accentGlow)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) glassColors.accentGlow else glassColors.textPrimary,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = glassColors.textMuted,
                    maxLines = 2,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
private fun VerifiedHelplineCard(
    contact: ProfessionalContactEntity,
    onCall: () -> Unit,
    onEmail: () -> Unit
) {
    val glassColors = LocalMindlyColors.current
    val isGovernment = contact.organization.contains("Govt", ignoreCase = true) ||
        contact.organization.contains("Ministry", ignoreCase = true) ||
        contact.phone in listOf("14416", "1800-599-0019", "112", "1800-11-0031", "080-26995000", "080-26995393")

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        isHighlighted = isGovernment,
        testTag = "helpline_card_${contact.id}"
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Name & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = contact.organization,
                        style = MaterialTheme.typography.bodySmall,
                        color = glassColors.accentGlow,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isGovernment) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(glassColors.accentContainer)
                                .border(1.dp, glassColors.accentGlow.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Govt Official",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = glassColors.accentGlow
                            )
                        }
                    }

                    // Verified Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SuccessGreen.copy(alpha = 0.15f))
                            .border(1.dp, SuccessGreen.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "✓ Verified",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }

                    if (contact.isEmergency) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmergencyRed.copy(alpha = 0.18f))
                                .border(1.dp, EmergencyRed.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "24/7",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmergencyRed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = contact.description,
                style = MaterialTheme.typography.bodySmall,
                color = glassColors.textSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Details: Hours & Languages
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = glassColors.textMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = contact.availableHours,
                    fontSize = 11.sp,
                    color = glassColors.textMuted
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = null,
                    tint = glassColors.textMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = contact.languages,
                    fontSize = 11.sp,
                    color = glassColors.textMuted,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PROMINENT PHONE NUMBER & CALL BUTTON ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(glassColors.surface)
                    .border(1.dp, glassColors.glassCardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(glassColors.accentContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = glassColors.accentGlow,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = contact.phone,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = glassColors.textPrimary
                        )
                        Text(
                            text = if (isGovernment) "Govt Toll-Free / Free Support" else "Direct Helpline",
                            fontSize = 10.sp,
                            color = if (isGovernment) SuccessGreen else glassColors.textMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (contact.email.isNotBlank()) {
                        IconButton(
                            onClick = onEmail,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(glassColors.glassCardBackground)
                                .border(1.dp, glassColors.glassCardBorder, RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = glassColors.accentGlow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onCall,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isGovernment) SuccessGreen else glassColors.accentGlow,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
