package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppLanguage
import com.example.i18n.MindlyStrings
import com.example.ui.MindlyViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.MindlyLogo
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LocalMindlyColors

@Composable
fun AuthScreen(
    viewModel: MindlyViewModel,
    language: AppLanguage,
    isLoading: Boolean,
    errorMessageKey: String?
) {
    val glassColors = LocalMindlyColors.current
    var isRegistering by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(language) }

    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotSuccessMsg by remember { mutableStateOf(false) }

    // Secret Admin Access via 7 taps on MINDLY logo
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var showPinDialog by remember { mutableStateOf(false) }
    var secretTapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Logo with secret 7-tap trigger
            MindlyLogo(
                size = 42.dp,
                modifier = Modifier
                    .testTag("mindly_logo_header")
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {
                        val now = System.currentTimeMillis()
                        if (now - lastTapTime > 2500L) {
                            secretTapCount = 1
                        } else {
                            secretTapCount += 1
                        }
                        lastTapTime = now

                        if (secretTapCount == 7) {
                            secretTapCount = 0
                            pinInput = ""
                            pinError = null
                            showPinDialog = true
                        }
                    }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = MindlyStrings.get("tagline", selectedLanguage),
                style = MaterialTheme.typography.bodySmall,
                color = glassColors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Main Auth Glass Card
            GlassCard(
                modifier = Modifier
                    .widthIn(max = 440.dp)
                    .fillMaxWidth(),
                isHighlighted = true
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = MindlyStrings.get(
                            if (isRegistering) "register_title" else "login_title",
                            selectedLanguage
                        ),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = glassColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = MindlyStrings.get(
                            if (isRegistering) "register_subtitle" else "login_subtitle",
                            selectedLanguage
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = glassColors.textMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Language Selector Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(glassColors.surface)
                            .border(1.dp, glassColors.glassCardBorder, RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) glassColors.accentContainer else Color.Transparent)
                                    .clickable {
                                        selectedLanguage = lang
                                        viewModel.setLanguage(lang)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = lang.nativeName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) glassColors.accentGlow else glassColors.textSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Registration extra field: Full Name
                    AnimatedVisibility(visible = isRegistering) {
                        Column {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text(MindlyStrings.get("full_name_label", selectedLanguage)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = glassColors.textMuted
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("full_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = glassColors.accentGlow,
                                    unfocusedBorderColor = glassColors.glassCardBorder
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(MindlyStrings.get("email_label", selectedLanguage)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = glassColors.textMuted
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = glassColors.accentGlow,
                            unfocusedBorderColor = glassColors.glassCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(MindlyStrings.get("password_label", selectedLanguage)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = glassColors.textMuted
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = glassColors.textMuted
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = glassColors.accentGlow,
                            unfocusedBorderColor = glassColors.glassCardBorder
                        )
                    )

                    // Confirm Password Field (for register)
                    AnimatedVisibility(visible = isRegistering) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text(MindlyStrings.get("confirm_password_label", selectedLanguage)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = glassColors.textMuted
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                        Icon(
                                            imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility",
                                            tint = glassColors.textMuted
                                        )
                                    }
                                },
                                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("confirm_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = glassColors.accentGlow,
                                    unfocusedBorderColor = glassColors.glassCardBorder
                                )
                            )
                        }
                    }

                    // Error Message
                    if (errorMessageKey != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = MindlyStrings.get(errorMessageKey, selectedLanguage),
                            color = EmergencyRed,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Forgot Password Link (for login)
                    if (!isRegistering) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    forgotEmail = email
                                    forgotSuccessMsg = false
                                    showForgotDialog = true
                                },
                                modifier = Modifier.testTag("forgot_password_button")
                            ) {
                                Text(
                                    text = MindlyStrings.get("forgot_password", selectedLanguage),
                                    fontSize = 12.sp,
                                    color = glassColors.accentGlow
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            if (isRegistering) {
                                viewModel.register(fullName, email, password, confirmPassword, selectedLanguage)
                            } else {
                                viewModel.login(email, password, selectedLanguage)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = glassColors.accentGlow,
                            contentColor = Color.White
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = MindlyStrings.get(
                                    if (isRegistering) "register_btn" else "login_btn",
                                    selectedLanguage
                                ),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Toggle between Login & Register
                    TextButton(
                        onClick = {
                            isRegistering = !isRegistering
                        },
                        modifier = Modifier.testTag("auth_toggle_mode_button")
                    ) {
                        Text(
                            text = MindlyStrings.get(
                                if (isRegistering) "have_account" else "no_account",
                                selectedLanguage
                            ),
                            fontSize = 13.sp,
                            color = glassColors.accentGlow
                        )
                    }
                }
            }
        }
    }

    // Secret Admin Access PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                pinInput = ""
                pinError = null
            },
            title = {
                Text(
                    text = "Enter the PIN",
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 8) {
                                pinInput = it
                                pinError = null
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("secret_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = glassColors.accentGlow,
                            unfocusedBorderColor = glassColors.glassCardBorder
                        )
                    )

                    if (pinError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = pinError!!,
                            color = EmergencyRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.testTag("secret_pin_error")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.verifyAndLoginAdminPin(pinInput)
                        if (success) {
                            showPinDialog = false
                            pinInput = ""
                            pinError = null
                        } else {
                            pinError = "Incorrect PIN"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow),
                    modifier = Modifier.testTag("secret_pin_submit")
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPinDialog = false
                        pinInput = ""
                        pinError = null
                    }
                ) {
                    Text(MindlyStrings.get("cancel", selectedLanguage), color = glassColors.textMuted)
                }
            },
            containerColor = glassColors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Forgot Password Dialog
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = {
                Text(
                    text = MindlyStrings.get("forgot_pw_title", selectedLanguage),
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = MindlyStrings.get("forgot_pw_desc", selectedLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = glassColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text(MindlyStrings.get("email_label", selectedLanguage)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (forgotSuccessMsg) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = MindlyStrings.get("reset_link_sent", selectedLanguage),
                            color = glassColors.accentGlow,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotEmail.isNotBlank()) {
                            forgotSuccessMsg = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentGlow)
                ) {
                    Text(MindlyStrings.get("send", selectedLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text(MindlyStrings.get("cancel", selectedLanguage), color = glassColors.textMuted)
                }
            },
            containerColor = glassColors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
