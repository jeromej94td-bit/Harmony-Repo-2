package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email as EmailProvider
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }

    LaunchedEffect(Unit) {
        val existingSession = runCatching {
            SupabaseConfig.client.auth.currentSessionOrNull()
        }.getOrNull()
        if (existingSession != null) {
            onAuthSuccess()
        }
    }

    val darkCosmicBg = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F041C), Color(0xFF000000))
    )
    
    val accentGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF3366), Color(0xFF6633FF))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkCosmicBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x44FF3366), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "HARMONY",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp,
                    brush = accentGradient
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        val res = kotlin.runCatching {
                            // 1. Build Google ID Request
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(SupabaseConfig.GOOGLE_WEB_CLIENT_ID)
                                .setAutoSelectEnabled(false)
                                .build()

                            // 2. Create Credential Request
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            // 3. Request credential from user
                            val activity = context.findActivity() ?: throw Exception("Activity Context nicht gefunden")
                            val result = credentialManager.getCredential(
                                context = activity,
                                request = request
                            )

                            // 4. Handle credential selection and pass token to Supabase
                            val credential = result.credential
                            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val finalIdToken = googleIdTokenCredential.idToken
                                
                                SupabaseConfig.client.auth.signInWith(IDToken) {
                                    idToken = finalIdToken
                                    provider = Google
                                }
                            } else {
                                throw Exception("Unerwarteter Anmeldetyp: ${credential.type}")
                            }
                        }
                        isLoading = false
                        if (res.isSuccess) {
                            onAuthSuccess()
                        } else {
                            val exception = res.exceptionOrNull()
                            Log.e("AuthScreen", "Google Login Error", exception)
                            if (exception is GetCredentialException) {
                                errorMessage = "Google-Login abgebrochen oder fehlgeschlagen: ${exception.message}"
                            } else {
                                errorMessage = exception?.message ?: "Fehler beim Google-Login"
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("G  Mit Google anmelden", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                Text(" oder ", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("E-Mail", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6633FF),
                    unfocusedBorderColor = Color(0xFF2A1B3D),
                    focusedContainerColor = Color(0xFF160B24),
                    unfocusedContainerColor = Color(0xFF160B24),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Passwort", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(Icons.Filled.Visibility, contentDescription = null, tint = Color.Gray)
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6633FF),
                    unfocusedBorderColor = Color(0xFF2A1B3D),
                    focusedContainerColor = Color(0xFF160B24),
                    unfocusedContainerColor = Color(0xFF160B24),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (successMessage != null) {
                Text(
                    text = successMessage!!,
                    color = Color(0xFF4CAF50),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), color = Color(0xFFFF3366))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val currentEmail = email.trim()
                        val currentPassword = password
                        
                        if (currentEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
                            errorMessage = "Bitte gib eine gültige E-Mail-Adresse ein."
                            return@launch
                        }
                        if (currentPassword.isEmpty()) {
                            errorMessage = "Bitte gib dein Passwort ein."
                            return@launch
                        }

                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        val res = kotlin.runCatching {
                            SupabaseConfig.client.auth.signInWith(EmailProvider) {
                                this.email = currentEmail
                                this.password = currentPassword
                            }
                        }
                        isLoading = false
                        if (res.isSuccess) onAuthSuccess()
                        else errorMessage = res.exceptionOrNull()?.message ?: "Fehler beim Anmelden"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(accentGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Anmelden", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        val currentEmail = email.trim()
                        val currentPassword = password
                        
                        if (currentEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
                            errorMessage = "Bitte gib eine gültige E-Mail-Adresse für die Registrierung ein."
                            return@launch
                        }
                        if (currentPassword.length < 6) {
                            errorMessage = "Das Passwort muss mindestens 6 Zeichen lang sein."
                            return@launch
                        }

                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        val res = kotlin.runCatching {
                            SupabaseConfig.client.auth.signUpWith(EmailProvider) {
                                this.email = currentEmail
                                this.password = currentPassword
                            }
                        }
                        isLoading = false
                        if (res.isSuccess) {
                            val session = kotlin.runCatching { SupabaseConfig.client.auth.currentSessionOrNull() }.getOrNull()
                            if (session != null) {
                                onAuthSuccess()
                            } else {
                                successMessage = "Registrierung erfolgreich! Bitte überprüfe dein Postfach und bestätige deine E-Mail-Adresse über den Link."
                            }
                        } else {
                            errorMessage = res.exceptionOrNull()?.message ?: "Fehler bei der Registrierung"
                        }
                    }
                },
                border = null,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(2.dp, accentGradient, RoundedCornerShape(12.dp))
            ) {
                Text("Registrieren", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    onAuthSuccess()
                }
            ) {
                Text("App im Demo-Modus testen", color = Color(0xFF99CCFF), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Passwort vergessen?",
                color = Color(0xFFCC99FF),
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* TODO: Forgot Password */ }
            )
        }
    }
}
