package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PersonalContactEntity
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.components.GlassCard
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.components.MindlyTopBar
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LocalMindlyColors

@Composable
fun EmergencyHelpScreen(
    personalContacts: List<PersonalContactEntity>,
    language: AppLanguage,
    onBack: () -> Unit,
    onOpenHelpDirectory: () -> Unit
) {
    val context = LocalContext.current
    val glassColors = LocalMindlyColors.current
    val scrollState = rememberScrollState()

    fun dialNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${number.replace(" ", "")}")
        }
        context.startActivity(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.background)
    ) {
        MindlyTopBar(
            title = MindlyStrings.get("emergency_page_title", language),
            language = language,
            onBack = onBack,
            testTag = "emergency_help_top_bar"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 90.dp)
        ) {
            // Critical Red Warning Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isHighlighted = true,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmergencyRed.copy(alpha = 0.12f))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmergencyRed.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = EmergencyRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "CRITICAL SAFETY NOTICE",
                            fontWeight = FontWeight.ExtraBold,
                            color = EmergencyRed,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = MindlyStrings.get("emergency_warning", language),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = glassColors.textPrimary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Immediate National Emergency Dial Button
                    Button(
                        onClick = { dialNumber("112") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dial_112_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmergencyRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Emergency, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Call 112 (National Emergency)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Verified National Mental Health Crisis Lines
            Text(
                text = "Verified 24/7 Mental Health Helplines",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = glassColors.textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tele-MANAS
            CrisisHelplineCard(
                title = MindlyStrings.get("tele_manas_title", language),
                phone = "14416",
                altPhone = "1800-891-4416",
                description = MindlyStrings.get("tele_manas_desc", language),
                badge = "Govt of India • 24/7 Toll-Free",
                testTag = "call_telemanas_btn",
                onCall = { dialNumber("14416") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // KIRAN
            CrisisHelplineCard(
                title = MindlyStrings.get("kiran_helpline_title", language),
                phone = "1800-599-0019",
                altPhone = null,
                description = MindlyStrings.get("kiran_helpline_desc", language),
                badge = "Toll-Free • 13 Languages",
                testTag = "call_kiran_btn",
                onCall = { dialNumber("18005990019") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Vandrevala Foundation
            CrisisHelplineCard(
                title = "Vandrevala Foundation Helpline",
                phone = "+91 9999 666 555",
                altPhone = null,
                description = "Free, confidential 24-hour psychological crisis intervention and counseling.",
                badge = "24/7 Crisis Response",
                testTag = "call_vandrevala_btn",
                onCall = { dialNumber("+919999666555") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Personal Emergency Contacts Quick Call
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = MindlyStrings.get("urgent_personal_contacts", language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (personalContacts.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = MindlyStrings.get("no_personal_contacts", language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = glassColors.textMuted
                        )
                    }
                }
            } else {
                personalContacts.forEach { contact ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        testTag = "emergency_contact_${contact.id}"
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = glassColors.textPrimary
                                )
                                Text(
                                    text = "${contact.relationship} • ${contact.phone}",
                                    fontSize = 12.sp,
                                    color = glassColors.textSecondary
                                )
                            }

                            Button(
                                onClick = { dialNumber(contact.phone) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Link to full directory
            OutlinedButton(
                onClick = onOpenHelpDirectory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("open_directory_from_emergency"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Browse All Helplines in Directory", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))
            MedicalDisclaimerCard(language = language)
        }
    }
}

@Composable
private fun CrisisHelplineCard(
    title: String,
    phone: String,
    altPhone: String?,
    description: String,
    badge: String,
    testTag: String,
    onCall: () -> Unit
) {
    val glassColors = LocalMindlyColors.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(glassColors.accentContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.accentGlow
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = glassColors.textSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = glassColors.accentGlow
                    )
                    if (altPhone != null) {
                        Text(
                            text = "Alt: $altPhone",
                            fontSize = 11.sp,
                            color = glassColors.textMuted
                        )
                    }
                }

                Button(
                    onClick = onCall,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                    modifier = Modifier.testTag(testTag)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
