package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.MindlyViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LocalMindlyColors

@Composable
fun SettingsScreen(
    viewModel: MindlyViewModel,
    user: UserEntity,
    language: AppLanguage,
    isDarkMode: Boolean,
    onOpenAdminDashboard: () -> Unit
) {
    val context = LocalContext.current
    val glassColors = LocalMindlyColors.current
    val scrollState = rememberScrollState()

    var showPasswordDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var pwError by remember { mutableStateOf<String?>(null) }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }

    var showClearDataDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.background)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .verticalScroll(scrollState)
    ) {
        // Title
        Text(
            text = MindlyStrings.get("settings_title", language),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = glassColors.textPrimary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Admin Dashboard Banner (if role == "admin")
        if (user.role == "admin") {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isHighlighted = true,
                onClick = onOpenAdminDashboard,
                testTag = "admin_dashboard_tile"
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(glassColors.accentContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = glassColors.accentGlow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = MindlyStrings.get("admin_dashboard_link", language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = "Manage helplines, directory contacts & stats",
                                fontSize = 11.sp,
                                color = glassColors.textSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(glassColors.accentGlow)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Appearance Section
        Text(
            text = MindlyStrings.get("appearance", language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = glassColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Brightness4,
                            contentDescription = null,
                            tint = glassColors.accentGlow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isDarkMode) MindlyStrings.get("dark_mode", language) else MindlyStrings.get("light_mode", language),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = MindlyStrings.get("theme_desc", language),
                                fontSize = 11.sp,
                                color = glassColors.textMuted
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) },
                        modifier = Modifier.testTag("theme_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = glassColors.accentGlow
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Language Section
        Text(
            text = MindlyStrings.get("language_section", language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = glassColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLanguage.entries.forEach { langOption ->
                    val isSelected = language == langOption
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) glassColors.accentContainer else glassColors.surface)
                            .border(1.dp, if (isSelected) glassColors.accentGlow else glassColors.glassCardBorder, RoundedCornerShape(12.dp))
                            .clickable { viewModel.setLanguage(langOption) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("language_option_${langOption.code}"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = langOption.displayName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) glassColors.accentGlow else glassColors.textPrimary
                            )
                            Text(
                                text = langOption.nativeName,
                                fontSize = 12.sp,
                                color = glassColors.textMuted
                            )
                        }

                        if (isSelected) {
                            Text("✓", color = glassColors.accentGlow, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Account Section
        Text(
            text = MindlyStrings.get("account_section", language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = glassColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(glassColors.accentContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = glassColors.accentGlow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = glassColors.textPrimary
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = glassColors.textMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(glassColors.surface)
                            .border(1.dp, glassColors.glassCardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (user.role == "admin") "Admin" else "User",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = glassColors.accentGlow
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        editedName = user.name
                        showEditNameDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_display_name_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Display Name", color = glassColors.textPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        oldPassword = ""
                        newPassword = ""
                        pwError = null
                        showPasswordDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("change_password_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(MindlyStrings.get("change_password", language), color = glassColors.textPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("logout_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = glassColors.surface,
                        contentColor = EmergencyRed
                    )
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(MindlyStrings.get("logout", language), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Privacy & Local Data Section
        Text(
            text = MindlyStrings.get("privacy_section", language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = glassColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = MindlyStrings.get("privacy_desc", language),
                    style = MaterialTheme.typography.bodySmall,
                    color = glassColors.textSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { showClearDataDialog = true },
                    modifier = Modifier.testTag("clear_data_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = EmergencyRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = MindlyStrings.get("delete_my_data", language),
                        color = EmergencyRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // dock clearance
    }

    // Change Password Dialog
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = {
                Text(
                    text = MindlyStrings.get("change_password", language),
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Current Password") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("current_pw_input")
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password (min 6 chars)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("new_pw_input")
                    )

                    if (pwError != null) {
                        Text(text = pwError!!, color = EmergencyRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.changePassword(oldPassword, newPassword) { success, msg ->
                            if (success) {
                                showPasswordDialog = false
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            } else {
                                pwError = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow)
                ) {
                    Text(MindlyStrings.get("save", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text(MindlyStrings.get("cancel", language), color = glassColors.textMuted)
                }
            },
            containerColor = glassColors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = {
                Text(
                    text = MindlyStrings.get("delete_my_data", language),
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )
            },
            text = {
                Text(
                    text = MindlyStrings.get("confirm_delete_data", language),
                    color = glassColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearUserData()
                        showClearDataDialog = false
                        Toast.makeText(context, "Local data cleared.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Clear Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text(MindlyStrings.get("cancel", language), color = glassColors.textMuted)
                }
            },
            containerColor = glassColors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text(
                    text = "Change Display Name",
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "This name will appear in your greeting on the Home screen.",
                        style = MaterialTheme.typography.bodySmall,
                        color = glassColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedName.isNotBlank()) {
                            viewModel.updateUserName(editedName)
                            showEditNameDialog = false
                            Toast.makeText(context, "Display name updated!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel", color = glassColors.textMuted)
                }
            },
            containerColor = glassColors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
