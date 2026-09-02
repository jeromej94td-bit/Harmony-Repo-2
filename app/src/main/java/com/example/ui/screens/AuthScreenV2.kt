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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SupabaseConfig
import com.example.ui.auth.GoogleSignInOutcome
import com.example.ui.auth.performHarmonyGoogleSignIn
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email as EmailProvider
import kotlinx.coroutines.launch

private val v2Pink = Color(0xFFFF3F8E)
private val v2Violet = Color(0xFFA555FF)
private val v2Blue = Color(0xFF4FAAFF)
private val v2Background = Color(0xFF07030E)

/**
 * Fresh authentication entry point used by HarmonyEntryActivity.
 *
 * This intentionally does not delegate to the legacy AuthScreen. The separate
 * source file prevents a stale Google AI Studio copy of AuthScreen.kt from
 * keeping the old password-reset-link UI alive.
 */
@Composable
fun AuthScreenV2(
    onAuthSuccess: () -> Unit,
    onDemoRequested: () -> Unit = onAuthSuccess
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var recoveryMode by remember { mutableStateOf(false) }
    var recoveryCode by remember { mutableStateOf("") }
    var recoveryNewPassword by remember { mutableStateOf("") }
    var recoveryConfirmPassword by remember { mutableStateOf("") }
    var recoveryPasswordVisible by remember { mutableStateOf(false) }
    var recoveryDone by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val accent = remember { Brush.horizontalGradient(listOf(v2Pink, v2Violet, v2Blue)) }

    LaunchedEffect(Unit) {
        val existingSession = runCatching {
            SupabaseConfig.client.auth.currentSessionOrNull()
        }.getOrNull()
        if (existingSession != null) onAuthSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A0525), v2Background, Color.Black)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 42.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HARMONY",
                style = TextStyle(
                    fontSize = 31.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    brush = accent
                )
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (recoveryMode) "PASSWORT" else "ZUSAMMEN VERBUNDEN",
                color = Color.White.copy(alpha = 0.62f),
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(34.dp))

            if (recoveryMode) {
                if (recoveryDone) {
                    Text(
                        "Passwort geändert",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Dein neues Passwort wurde gespeichert. Du kannst dich jetzt damit anmelden.",
                        color = Color(0xFF9AF0BC),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(24.dp))
                    V2PrimaryButton("Zur Anmeldung", accent) {
                        recoveryMode = false
                        recoveryDone = false
                        recoveryCode = ""
                        recoveryNewPassword = ""
                        recoveryConfirmPassword = ""
                        password = ""
                        errorMessage = null
                        successMessage = null
                    }
                } else {
                    Text(
                        "Passwort zurücksetzen",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Gib den Passwort-Code aus deiner E-Mail ein und lege direkt dein neues Passwort fest.",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(24.dp))

                    V2AuthField(
                        value = recoveryCode,
                        onValueChange = {
                            recoveryCode = it.filter(Char::isDigit).take(8)
                            errorMessage = null
                        },
                        placeholder = "Passwort-Code",
                        leadingIcon = {
                            Icon(Icons.Filled.Email, null, tint = Color(0xFFD4B8F5))
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    V2AuthField(
                        value = recoveryNewPassword,
                        onValueChange = {
                            recoveryNewPassword = it
                            errorMessage = null
                        },
                        placeholder = "Neues Passwort",
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, null, tint = Color(0xFFD4B8F5))
                        },
                        trailingIcon = {
                            IconButton(onClick = { recoveryPasswordVisible = !recoveryPasswordVisible }) {
                                Icon(Icons.Filled.Visibility, null, tint = Color(0xFFD4B8F5))
                            }
                        },
                        visualTransformation = if (recoveryPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    V2AuthField(
                        value = recoveryConfirmPassword,
                        onValueChange = {
                            recoveryConfirmPassword = it
                            errorMessage = null
                        },
                        placeholder = "Passwort wiederholen",
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, null, tint = Color(0xFFD4B8F5))
                        },
                        visualTransformation = if (recoveryPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    )

                    V2Status(errorMessage, successMessage, isLoading)
                    Spacer(Modifier.height(18.dp))
                    V2PrimaryButton("Passwort speichern", accent, enabled = !isLoading) {
                        scope.launch {
                            val currentEmail = email.trim()
                            when {
                                recoveryCode.length < 6 -> {
                                    errorMessage = "Bitte gib den vollständigen Passwort-Code ein."
                                    return@launch
                                }
                                recoveryNewPassword.length < 6 -> {
                                    errorMessage = "Das neue Passwort muss mindestens 6 Zeichen lang sein."
                                    return@launch
                                }
                                recoveryNewPassword != recoveryConfirmPassword -> {
                                    errorMessage = "Die beiden Passwörter stimmen nicht überein."
                                    return@launch
                                }
                            }

                            isLoading = true
                            errorMessage = null
                            successMessage = null
                            val result = runCatching {
                                SupabaseConfig.client.auth.verifyEmailOtp(
                                    type = OtpType.Email.RECOVERY,
                                    email = currentEmail,
                                    token = recoveryCode
                                )
                                SupabaseConfig.client.auth.updateUser {
                                    password = recoveryNewPassword
                                }
                                runCatching { SupabaseConfig.client.auth.signOut() }
                            }
                            isLoading = false
                            if (result.isSuccess) {
                                recoveryDone = true
                                recoveryCode = ""
                                recoveryNewPassword = ""
                                recoveryConfirmPassword = ""
                            } else {
                                errorMessage = result.exceptionOrNull()?.message
                                    ?: "Der Passwort-Code ist ungültig oder abgelaufen."
                            }
                        }
                    }

                    TextButton(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                successMessage = null
                                val result = runCatching {
                                    SupabaseConfig.client.auth.resetPasswordForEmail(
                                        email = email.trim(),
                                        redirectUrl = SupabaseConfig.PASSWORD_RECOVERY_REDIRECT_URL
                                    )
                                }
                                isLoading = false
                                if (result.isSuccess) {
                                    successMessage = "Ein neuer Passwort-Code wurde gesendet."
                                } else {
                                    errorMessage = result.exceptionOrNull()?.message
                                        ?: "Der Code konnte nicht erneut gesendet werden."
                                }
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Text("Code erneut senden", color = Color(0xFF9FCEFF))
                    }
                    TextButton(
                        onClick = {
                            recoveryMode = false
                            recoveryCode = ""
                            recoveryNewPassword = ""
                            recoveryConfirmPassword = ""
                            errorMessage = null
                            successMessage = null
                        },
                        enabled = !isLoading
                    ) {
                        Text("Zurück zur Anmeldung", color = Color(0xFFD6A9FF))
                    }
                }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            successMessage = null
                            val result = runCatching { performHarmonyGoogleSignIn(context) }
                            isLoading = false
                            if (result.isSuccess) {
                                when (result.getOrThrow()) {
                                    GoogleSignInOutcome.SESSION_CREATED -> onAuthSuccess()
                                    GoogleSignInOutcome.OAUTH_REDIRECT_STARTED -> {
                                        successMessage = "Google-Anmeldung wurde geöffnet. Bitte schließe sie dort ab."
                                    }
                                }
                            } else {
                                errorMessage = result.exceptionOrNull()?.message
                                    ?: "Google-Anmeldung konnte nicht gestartet werden."
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF171321)
                    ),
                    shape = RoundedCornerShape(17.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text("G", color = Color(0xFF4285F4), fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(14.dp))
                    Text("Mit Google anmelden", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = 0.18f))
                    Text("oder", color = Color.White.copy(alpha = 0.55f), modifier = Modifier.padding(horizontal = 14.dp))
                    HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = 0.18f))
                }
                Spacer(Modifier.height(20.dp))

                V2AuthField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    placeholder = "E-Mail",
                    leadingIcon = { Icon(Icons.Filled.Email, null, tint = Color(0xFFD4B8F5)) }
                )
                Spacer(Modifier.height(12.dp))
                V2AuthField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = "Passwort",
                    leadingIcon = { Icon(Icons.Filled.Lock, null, tint = Color(0xFFD4B8F5)) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(Icons.Filled.Visibility, null, tint = Color(0xFFD4B8F5))
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )

                V2Status(errorMessage, successMessage, isLoading)
                Spacer(Modifier.height(20.dp))
                V2PrimaryButton("Anmelden", accent, enabled = !isLoading) {
                    scope.launch {
                        val currentEmail = email.trim()
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
                            errorMessage = "Bitte gib eine gültige E-Mail-Adresse ein."
                            return@launch
                        }
                        if (password.isEmpty()) {
                            errorMessage = "Bitte gib dein Passwort ein."
                            return@launch
                        }
                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        val result = runCatching {
                            SupabaseConfig.client.auth.signInWith(EmailProvider) {
                                this.email = currentEmail
                                this.password = password
                            }
                        }
                        isLoading = false
                        if (result.isSuccess) onAuthSuccess()
                        else errorMessage = result.exceptionOrNull()?.message ?: "Fehler beim Anmelden"
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val currentEmail = email.trim()
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
                                errorMessage = "Bitte gib eine gültige E-Mail-Adresse ein."
                                return@launch
                            }
                            if (password.length < 6) {
                                errorMessage = "Das Passwort muss mindestens 6 Zeichen lang sein."
                                return@launch
                            }
                            isLoading = true
                            errorMessage = null
                            successMessage = null
                            val result = runCatching {
                                SupabaseConfig.client.auth.signUpWith(EmailProvider) {
                                    this.email = currentEmail
                                    this.password = password
                                }
                            }
                            isLoading = false
                            if (result.isSuccess) {
                                val session = runCatching { SupabaseConfig.client.auth.currentSessionOrNull() }.getOrNull()
                                if (session != null) onAuthSuccess()
                                else successMessage = "Registrierung erfolgreich. Bitte bestätige deine E-Mail-Adresse."
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Fehler bei der Registrierung"
                            }
                        }
                    },
                    enabled = !isLoading,
                    border = null,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.5.dp, accent, RoundedCornerShape(17.dp)),
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Text("Registrieren", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = onDemoRequested, enabled = !isLoading) {
                    Text("App im Demo-Modus testen", color = Color(0xFF9FCEFF), fontWeight = FontWeight.SemiBold)
                }

                Text(
                    text = "Passwort vergessen?",
                    color = Color(0xFFD6A9FF),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 24.dp)
                        .clickable(enabled = !isLoading) {
                            val currentEmail = email.trim()
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
                                errorMessage = "Bitte gib eine gültige E-Mail-Adresse ein."
                                successMessage = null
                            } else {
                                // Important: change the UI first. The user sees the code mask immediately.
                                recoveryMode = true
                                recoveryDone = false
                                recoveryCode = ""
                                recoveryNewPassword = ""
                                recoveryConfirmPassword = ""
                                errorMessage = null
                                successMessage = "Passwort-Code wird gesendet..."
                                isLoading = true

                                scope.launch {
                                    val result = runCatching {
                                        SupabaseConfig.client.auth.resetPasswordForEmail(
                                            email = currentEmail,
                                            redirectUrl = SupabaseConfig.PASSWORD_RECOVERY_REDIRECT_URL
                                        )
                                    }
                                    isLoading = false
                                    if (result.isSuccess) {
                                        successMessage = "Passwort-Code wurde gesendet. Bitte prüfe dein Postfach."
                                    } else {
                                        successMessage = null
                                        errorMessage = result.exceptionOrNull()?.message
                                            ?: "Passwort-Code konnte nicht gesendet werden."
                                    }
                                }
                            }
                        }
                )
            }
        }
    }
}

@Composable
private fun V2Status(error: String?, success: String?, loading: Boolean) {
    if (error != null) {
        Text(error, color = Color(0xFFFF8EAA), fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
    }
    if (success != null) {
        Text(success, color = Color(0xFF8FF0BA), fontSize = 13.sp, modifier = Modifier.padding(top = 10.dp))
    }
    if (loading) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            color = v2Pink,
            trackColor = Color.White.copy(alpha = 0.12f)
        )
    }
}

@Composable
private fun V2PrimaryButton(
    label: String,
    gradient: Brush,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(17.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(gradient, RoundedCornerShape(17.dp))
    ) {
        Text(label, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun V2AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit),
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFB4A9C1)) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = v2Pink,
            unfocusedBorderColor = Color(0xFF70448B),
            focusedContainerColor = Color(0xC0180A2A),
            unfocusedContainerColor = Color(0xB3140A24),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = v2Pink
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(60.dp)
    )
}
