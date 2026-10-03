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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.AdminStats
import com.example.ui.MindlyViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.MindlyTopBar
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LocalMindlyColors
import com.example.ui.theme.SuccessGreen

@Composable
fun AdminDashboardScreen(
    viewModel: MindlyViewModel,
    stats: AdminStats,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val glassColors = LocalMindlyColors.current
    val adminHelplines by viewModel.adminHelplines.collectAsState()

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
            // Overview Stats Grid
            item {
                Text(
                    text = MindlyStrings.get("admin_overview", language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary
                )
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

                Text(
                    text = MindlyStrings.get("admin_contacts_list", language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary
                )
            }

            // Helplines list
            items(adminHelplines) { contact ->
                AdminHelplineCard(
                    contact = contact,
                    language = language,
                    onTogglePublish = { viewModel.togglePublishStatus(contact.id, contact.isPublished) },
                    onEdit = {
                        editingContact = contact
                        showAddEditDialog = true
                    },
                    onDelete = { deletingContact = contact }
                )
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
                        deletingContact?.let { viewModel.deleteProfessionalContact(it.id) }
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (contact.isPublished) SuccessGreen.copy(alpha = 0.2f) else glassColors.surface)
                            .border(
                                1.dp,
                                if (contact.isPublished) SuccessGreen.copy(alpha = 0.4f) else glassColors.glassCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (contact.isPublished) "Published" else "Unpublished",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (contact.isPublished) SuccessGreen else glassColors.textMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${contact.phone} • ${contact.availableHours}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = glassColors.accentGlow
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Admin Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Publish/Unpublish toggle button
                OutlinedButton(
                    onClick = onTogglePublish,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = if (contact.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (contact.isPublished) glassColors.textMuted else SuccessGreen
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (contact.isPublished) "Unpublish" else "Publish",
                        fontSize = 12.sp,
                        color = glassColors.textPrimary
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = glassColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = EmergencyRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
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
    var availableHours by remember { mutableStateOf(initialContact?.availableHours ?: "24/7 (Toll-Free)") }
    var languages by remember { mutableStateOf(initialContact?.languages ?: "English, Hindi, Kannada") }
    var description by remember { mutableStateOf(initialContact?.description ?: "") }
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
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialContact == null) "Add Professional Helpline" else "Edit Helpline Details",
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
                    label = { Text("Helpline / Professional Name *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_name_input")
                )

                // Professional Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = professionalType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        typeOptions.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    professionalType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = organization,
                    onValueChange = { organization = it },
                    label = { Text("Organization / Hospital *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_org_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number / Helpline *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_phone_input")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Contact Email (Optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (e.g. Pan India, Bengaluru)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = availableHours,
                    onValueChange = { availableHours = it },
                    label = { Text("Available Hours (e.g. 24/7)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = languages,
                    onValueChange = { languages = it },
                    label = { Text("Supported Languages") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Service Description") },
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Checkboxes for Emergency & Published
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isEmergency = !isEmergency },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isEmergency,
                        onCheckedChange = { isEmergency = it },
                        colors = CheckboxDefaults.colors(checkedColor = EmergencyRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mark as 24/7 Emergency Crisis Service", fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPublished = !isPublished },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        colors = CheckboxDefaults.colors(checkedColor = glassColors.accentGlow)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish to Public Directory Immediately", fontSize = 12.sp)
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
                        errorText = "Name, organization, and phone number are required."
                    } else {
                        val entity = ProfessionalContactEntity(
                            id = initialContact?.id ?: 0,
                            name = name.trim(),
                            professionalType = professionalType,
                            organization = organization.trim(),
                            phone = phone.trim(),
                            email = email.trim(),
                            location = location.trim(),
                            availableHours = availableHours.trim(),
                            languages = languages.trim(),
                            description = description.trim(),
                            isEmergency = isEmergency,
                            isPublished = isPublished,
                            createdAt = initialContact?.createdAt ?: System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(entity)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                modifier = Modifier.testTag("admin_save_contact_btn")
            ) {
                Text("Save Helpline", fontWeight = FontWeight.Bold)
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
