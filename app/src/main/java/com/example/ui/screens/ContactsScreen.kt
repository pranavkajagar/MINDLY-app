package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.MindlyViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LocalMindlyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: MindlyViewModel,
    contacts: List<PersonalContactEntity>,
    language: AppLanguage
) {
    val context = LocalContext.current
    val glassColors = LocalMindlyColors.current

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<PersonalContactEntity?>(null) }
    var deletingContact by remember { mutableStateOf<PersonalContactEntity?>(null) }

    fun dialNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${number.replace(" ", "")}")
        }
        context.startActivity(intent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = MindlyStrings.get("my_emergency_contacts", language),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = MindlyStrings.get("contacts_subtitle", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = glassColors.textMuted
                    )
                }

                Button(
                    onClick = {
                        editingContact = null
                        showAddEditDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                    modifier = Modifier.testTag("add_contact_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = MindlyStrings.get("add_contact", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (contacts.isEmpty()) {
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
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = glassColors.accentGlow,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = MindlyStrings.get("no_personal_contacts", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = glassColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add close family members, trusted friends, or your counsellor so you can reach them in one tap.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = glassColors.textMuted
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                editingContact = null
                                showAddEditDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(MindlyStrings.get("add_contact", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("personal_contacts_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(contacts) { contact ->
                        PersonalContactCard(
                            contact = contact,
                            language = language,
                            onCall = { dialNumber(contact.phone) },
                            onEdit = {
                                editingContact = contact
                                showAddEditDialog = true
                            },
                            onDelete = { deletingContact = contact }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        MedicalDisclaimerCard(language = language)
                        Spacer(modifier = Modifier.height(100.dp)) // clearance for dock
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        AddEditContactDialog(
            initialContact = editingContact,
            language = language,
            onDismiss = { showAddEditDialog = false },
            onSave = { name, relationship, phone, email, notes ->
                if (editingContact == null) {
                    viewModel.addPersonalContact(name, relationship, phone, email, notes)
                } else {
                    viewModel.updatePersonalContact(
                        editingContact!!.copy(
                            name = name,
                            relationship = relationship,
                            phone = phone,
                            email = email,
                            notes = notes
                        )
                    )
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
                    text = MindlyStrings.get("delete_contact", language),
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )
            },
            text = {
                Text(
                    text = "${MindlyStrings.get("confirm_delete_contact", language)} (${deletingContact!!.name})",
                    color = glassColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        deletingContact?.let { viewModel.deletePersonalContact(it.id) }
                        deletingContact = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text(MindlyStrings.get("delete", language), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingContact = null }) {
                    Text(MindlyStrings.get("cancel", language), color = glassColors.textMuted)
                }
            },
            containerColor = glassColors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun PersonalContactCard(
    contact: PersonalContactEntity,
    language: AppLanguage,
    onCall: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val glassColors = LocalMindlyColors.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        testTag = "personal_contact_card_${contact.id}"
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(glassColors.accentContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = glassColors.accentGlow,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = glassColors.textPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(glassColors.surface)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = contact.relationship,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = glassColors.accentGlow
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = glassColors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = EmergencyRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (contact.notes.isNotBlank()) {
                Text(
                    text = contact.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = glassColors.textSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = contact.phone,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = glassColors.textPrimary
                )

                Button(
                    onClick = onCall,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(MindlyStrings.get("call", language), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditContactDialog(
    initialContact: PersonalContactEntity?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (name: String, relationship: String, phone: String, email: String, notes: String) -> Unit
) {
    val glassColors = LocalMindlyColors.current

    var name by remember { mutableStateOf(initialContact?.name ?: "") }
    var relationship by remember { mutableStateOf(initialContact?.relationship ?: "Friend") }
    var phone by remember { mutableStateOf(initialContact?.phone ?: "") }
    var email by remember { mutableStateOf(initialContact?.email ?: "") }
    var notes by remember { mutableStateOf(initialContact?.notes ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val relationshipOptions = listOf(
        "Parent", "Guardian", "Friend", "Brother", "Sister",
        "Partner", "Trusted Person", "Doctor / Therapist", "Other"
    )
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = MindlyStrings.get(if (initialContact == null) "add_contact" else "edit_contact", language),
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
                    label = { Text(MindlyStrings.get("contact_name", language)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("contact_name_input")
                )

                // Relationship Exposed Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = relationship,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(MindlyStrings.get("relationship", language)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("contact_relationship_dropdown"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        relationshipOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    relationship = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(MindlyStrings.get("phone_number", language)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("contact_phone_input")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(MindlyStrings.get("email_label", language)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(MindlyStrings.get("notes", language)) },
                    singleLine = false,
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = EmergencyRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank()) {
                        errorText = "Please enter both contact name and phone number."
                    } else {
                        onSave(name, relationship, phone, email, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                modifier = Modifier.testTag("save_contact_btn")
            ) {
                Text(MindlyStrings.get("save", language), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(MindlyStrings.get("cancel", language), color = glassColors.textMuted)
            }
        },
        containerColor = glassColors.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
