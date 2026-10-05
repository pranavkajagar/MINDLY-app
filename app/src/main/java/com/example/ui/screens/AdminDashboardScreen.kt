package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProfessionalContactEntity
import com.example.data.model.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.AdminStats
import com.example.ui.MindlyViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.MindlyTopBar
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LocalMindlyColors
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: MindlyViewModel,
    stats: AdminStats,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val glassColors = LocalMindlyColors.current
    val adminHelplines by viewModel.adminHelplines.collectAsState()
    val adminUsers by viewModel.adminUsers.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Helplines, 1: Real-time Users

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<ProfessionalContactEntity?>(null) }
    var deletingContact by remember { mutableStateOf<ProfessionalContactEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.background)
    ) {
        MindlyTopBar(
            title = MindlyStrings.get("admin_title", language),
            language = language,
            onBack = onBack,
            actions = {
                if (activeTab == 0) {
                    Button(
                        onClick = {
                            editingContact = null
                            showAddEditDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                        modifier = Modifier.testTag("admin_add_contact_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Helpline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            testTag = "admin_dashboard_top_bar"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .testTag("admin_contacts_list"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Overview Stats Grid - Driven by Real-Time Firestore Listeners
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = MindlyStrings.get("admin_overview", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = glassColors.textPrimary
                    )

                    // Real-time Central Firestore sync badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SuccessGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Central Firestore Live",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = MindlyStrings.get("stat_total_users", language),
                        value = stats.totalUsers.toString(),
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = MindlyStrings.get("stat_published_contacts", language),
                        value = stats.publishedHelplines.toString(),
                        icon = Icons.Default.SupportAgent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = MindlyStrings.get("stat_personal_contacts", language),
                        value = stats.totalPersonalContacts.toString(),
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = MindlyStrings.get("stat_mood_entries", language),
                        value = stats.totalMoodLogs.toString(),
                        icon = Icons.Default.Mood,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section Tabs: Helplines vs Registered Users
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(glassColors.surface)
                        .border(1.dp, glassColors.glassCardBorder, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 0) glassColors.accentGlow else Color.Transparent)
                            .clickable { activeTab = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Helplines (${adminHelplines.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 0) Color.White else glassColors.textSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 1) glassColors.accentGlow else Color.Transparent)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Real-Time Users (${adminUsers.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 1) Color.White else glassColors.textSecondary
                        )
                    }
                }
            }

            if (activeTab == 0) {
                // Helplines list
                if (adminHelplines.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No helplines in directory.",
                                color = glassColors.textMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(adminHelplines) { contact ->
                        AdminHelplineCard(
                            contact = contact,
                            language = language,
                            onTogglePublish = { viewModel.togglePublishStatus(contact.id, contact.isPublished, contact.docId) },
                            onEdit = {
                                editingContact = contact
                                showAddEditDialog = true
                            },
                            onDelete = { deletingContact = contact }
                        )
                    }
                }
            } else {
                // Real-time Users list from Central Firestore
                if (adminUsers.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = glassColors.accentGlow,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Listening for user registrations on Firestore...",
                                    color = glassColors.textMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(adminUsers) { user ->
                        AdminUserCard(user = user)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }

    // Add / Edit Professional Contact Dialog
    if (showAddEditDialog) {
        AddEditProfessionalDialog(
            initialContact = editingContact,
            language = language,
            onDismiss = { showAddEditDialog = false },
            onSave = { contact ->
                if (editingContact == null) {
                    viewModel.addProfessionalContact(contact)
                } else {
                    viewModel.updateProfessionalContact(contact)
                }
                showAddEditDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingContact != null) {
        AlertDialog(
            onDismissRequest = { deletingContact = null },
            title = {
                Text(
                    text = "Delete Helpline",
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete \"${deletingContact!!.name}\" from the directory?",
                    color = glassColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        deletingContact?.let { viewModel.deleteProfessionalContact(it.id, it.docId) }
                        deletingContact = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingContact = null }) {
                    Text("Cancel", color = glassColors.textMuted)
                }
            },
            containerColor = glassColors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalMindlyColors.current

    GlassCard(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(glassColors.accentContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = glassColors.accentGlow,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = glassColors.textPrimary
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = glassColors.textMuted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun AdminUserCard(user: UserEntity) {
    val glassColors = LocalMindlyColors.current
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val dateStr = remember(user.createdAt) { dateFormat.format(Date(user.createdAt)) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (user.role == "admin") glassColors.accentGlow.copy(alpha = 0.2f) else glassColors.accentContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (user.role == "admin") glassColors.accentGlow else glassColors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = glassColors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (user.role == "admin") glassColors.accentGlow else glassColors.surface)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = user.role.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user.role == "admin") Color.White else glassColors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = user.email,
                        fontSize = 11.sp,
                        color = glassColors.textMuted
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Joined: $dateStr • Lang: ${user.language.uppercase()}",
                        fontSize = 10.sp,
                        color = glassColors.textSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SuccessGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Live",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
            }
        }
    }
}

@Composable
private fun AdminHelplineCard(
    contact: ProfessionalContactEntity,
    language: AppLanguage,
    onTogglePublish: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val glassColors = LocalMindlyColors.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        testTag = "admin_contact_${contact.id}"
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        text = "${contact.professionalType} • ${contact.organization}",
                        style = MaterialTheme.typography.bodySmall,
                        color = glassColors.textMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (contact.isPublished) SuccessGreen.copy(alpha = 0.15f)
                                else glassColors.surface
                            )
                            .border(
                                1.dp,
                                if (contact.isPublished) SuccessGreen.copy(alpha = 0.4f)
                                else glassColors.glassCardBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (contact.isPublished) "Published" else "Draft",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (contact.isPublished) SuccessGreen else glassColors.textMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = glassColors.accentGlow,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = EmergencyRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = contact.description,
                style = MaterialTheme.typography.bodySmall,
                color = glassColors.textSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Phone: ${contact.phone}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (contact.isPublished) "Public" else "Hidden",
                        fontSize = 12.sp,
                        color = glassColors.textMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = contact.isPublished,
                        onCheckedChange = { onTogglePublish() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SuccessGreen
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditProfessionalDialog(
    initialContact: ProfessionalContactEntity?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (ProfessionalContactEntity) -> Unit
) {
    val glassColors = LocalMindlyColors.current

    var name by remember { mutableStateOf(initialContact?.name ?: "") }
    var professionalType by remember { mutableStateOf(initialContact?.professionalType ?: "Mental Health Helpline") }
    var organization by remember { mutableStateOf(initialContact?.organization ?: "") }
    var phone by remember { mutableStateOf(initialContact?.phone ?: "") }
    var email by remember { mutableStateOf(initialContact?.email ?: "") }
    var location by remember { mutableStateOf(initialContact?.location ?: "Pan India") }
    var availableHours by remember { mutableStateOf(initialContact?.availableHours ?: "24/7 (Toll Free)") }
    var languages by remember { mutableStateOf(initialContact?.languages ?: "English, Hindi, Kannada") }
    var description by remember { mutableStateOf(initialContact?.description ?: "") }
    var problemCategories by remember { mutableStateOf(initialContact?.problemCategories ?: "crisis, anxiety, depression") }
    var isEmergency by remember { mutableStateOf(initialContact?.isEmergency ?: true) }
    var isPublished by remember { mutableStateOf(initialContact?.isPublished ?: true) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val typeOptions = listOf(
        "Mental Health Helpline",
        "Hospital / Specialized Clinic",
        "Support Organization",
        "Emergency Service",
        "Psychologist",
        "Psychiatrist",
        "Counsellor"
    )
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialContact == null) "Add Professional Helpline" else "Edit Helpline",
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name / Service Name *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_input_name")
                )

                // Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = professionalType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Service Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        typeOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    professionalType = opt
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = organization,
                    onValueChange = { organization = it },
                    label = { Text("Organization / Institution *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_input_org")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number / Helpline *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_input_phone")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / Coverage") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = availableHours,
                    onValueChange = { availableHours = it },
                    label = { Text("Operating Hours") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = languages,
                    onValueChange = { languages = it },
                    label = { Text("Languages Supported") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = problemCategories,
                    onValueChange = { problemCategories = it },
                    label = { Text("Problem Tags (comma separated)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Services Offered") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isEmergency,
                        onCheckedChange = { isEmergency = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Emergency / Crisis 24/7 service", fontSize = 13.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish to Public Directory immediately", fontSize = 13.sp)
                }

                if (errorText != null) {
                    Text(text = errorText!!, color = EmergencyRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank() || organization.isBlank()) {
                        errorText = "Please fill in all required fields (Name, Organization, Phone)."
                    } else {
                        val contact = (initialContact ?: ProfessionalContactEntity(
                            name = name.trim(),
                            professionalType = professionalType,
                            organization = organization.trim(),
                            phone = phone.trim()
                        )).copy(
                            name = name.trim(),
                            professionalType = professionalType,
                            organization = organization.trim(),
                            phone = phone.trim(),
                            email = email.trim(),
                            location = location.trim(),
                            availableHours = availableHours.trim(),
                            languages = languages.trim(),
                            description = description.trim(),
                            problemCategories = problemCategories.trim(),
                            isEmergency = isEmergency,
                            isPublished = isPublished,
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(contact)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                modifier = Modifier.testTag("admin_save_contact_btn")
            ) {
                Text("Save to Central Directory", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = glassColors.textMuted)
            }
        },
        containerColor = glassColors.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
