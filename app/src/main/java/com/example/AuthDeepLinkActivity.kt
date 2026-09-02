package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SupabaseConfig
import com.example.ui.theme.HarmonyTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import kotlinx.coroutines.launch

class AuthDeepLinkActivity : ComponentActivity() {

    private var sessionReady by mutableStateOf(false)
    private var deepLinkError by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val linkType = authLinkParameter(intent, "type")
        val callbackUri = intent.data
        deepLinkError = authLinkParameter(intent, "error_description")
            ?: authLinkParameter(intent, "error")

        if (
            callbackUri == null ||
            callbackUri.scheme != SupabaseConfig.AUTH_DEEP_LINK_SCHEME ||
            callbackUri.host != SupabaseConfig.AUTH_DEEP_LINK_HOST
        ) {
            deepLinkError = "Dieser Link ist ungültig oder gehört nicht zu Harmony."
        } else if (deepLinkError == null) {
            SupabaseConfig.client.handleDeeplinks(intent) {
                runOnUiThread {
                    sessionReady = true
                    if (linkType != "recovery") {
                        navigateToMainApp()
                    }
                }
            }
        }

        setContent {
            HarmonyTheme(darkTheme = true) {
                if (linkType == "recovery") {
                    PasswordRecoveryContent(
                        sessionReady = sessionReady,
                        deepLinkError = deepLinkError,
                        onReturnToLogin = ::navigateToLogin
                    )
                } else {
                    AuthCallbackContent(
                        deepLinkError = deepLinkError,
                        onReturnToLogin = ::navigateToLogin
                    )
                }
            }
        }
    }

    private fun navigateToMainApp() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        finish()
    }

    private fun navigateToLogin() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        finish()
    }
}

private fun authLinkParameter(intent: Intent, name: String): String? {
    val uri = intent.data ?: return null
    uri.getQueryParameter(name)?.let { return it }
    val fragment = uri.fragment ?: return null
    return runCatching {
        Uri.parse("https://harmony.local/?$fragment").getQueryParameter(name)
    }.getOrNull()
}

@Composable
private fun PasswordRecoveryContent(
    sessionReady: Boolean,
    deepLinkError: String?,
    onReturnToLogin: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AuthCallbackBackground {
        Text(
            text = "HARMONY",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 5.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Neues Passwort festlegen",
            color = Color(0xFFFFB5D2),
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        when {
            deepLinkError != null -> {
                StatusCard(
                    title = "Reset-Link nicht gültig",
                    message = deepLinkError
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onReturnToLogin, modifier = Modifier.fillMaxWidth()) {
                    Text("Zurück zur Anmeldung")
                }
            }

            isDone -> {
                StatusCard(
                    title = "Passwort geändert",
                    message = "Dein neues Passwort wurde gespeichert. Du kannst dich jetzt damit anmelden."
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onReturnToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA555FF))
                ) {
                    Text("Zur Anmeldung")
                }
            }

            !sessionReady -> {
                CircularProgressIndicator(color = Color(0xFFFF3F8E))
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Reset-Link wird geprüft …",
                    color = Color(0xFFC9B7D7),
                    fontSize = 14.sp
                )
            }

            else -> {
                Text(
                    text = "Wähle ein neues Passwort mit mindestens 6 Zeichen.",
                    color = Color(0xFFC9B7D7),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                RecoveryPasswordField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        localError = null
                    },
                    label = "Neues Passwort"
                )
                Spacer(modifier = Modifier.height(10.dp))
                RecoveryPasswordField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        localError = null
                    },
                    label = "Passwort wiederholen"
                )

                if (localError != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(localError!!, color = Color(0xFFFF8EAA), fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = {
                        when {
                            newPassword.length < 6 -> {
                                localError = "Das Passwort muss mindestens 6 Zeichen lang sein."
                            }

                            newPassword != confirmPassword -> {
                                localError = "Die beiden Passwörter stimmen nicht überein."
                            }

                            else -> {
                                scope.launch {
                                    isLoading = true
                                    localError = null
                                    val result = runCatching {
                                        SupabaseConfig.client.auth.updateUser {
                                            password = newPassword
                                        }
                                        SupabaseConfig.client.auth.signOut()
                                    }
                                    isLoading = false
                                    if (result.isSuccess) {
                                        isDone = true
                                        newPassword = ""
                                        confirmPassword = ""
                                    } else {
                                        localError = result.exceptionOrNull()?.message
                                            ?: "Das Passwort konnte nicht geändert werden."
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA555FF))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.height(22.dp)
                        )
                    } else {
                        Text("Passwort speichern", fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onReturnToLogin, modifier = Modifier.fillMaxWidth()) {
                    Text("Abbrechen", color = Color(0xFFD6A9FF))
                }
            }
        }
    }
}

@Composable
private fun AuthCallbackContent(
    deepLinkError: String?,
    onReturnToLogin: () -> Unit
) {
    AuthCallbackBackground {
        if (deepLinkError == null) {
            CircularProgressIndicator(color = Color(0xFFFF3F8E))
            Spacer(modifier = Modifier.height(14.dp))
            Text("Anmeldung wird bestätigt …", color = Color.White, fontSize = 16.sp)
        } else {
            StatusCard("Link nicht gültig", deepLinkError)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onReturnToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Zurück zur Anmeldung")
            }
        }
    }
}

@Composable
private fun AuthCallbackBackground(
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF16051F), Color(0xFF08030F), Color(0xFF04030B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
private fun RecoveryPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFFF3F8E),
            unfocusedBorderColor = Color(0xFF70448B),
            focusedContainerColor = Color(0xC0180A2A),
            unfocusedContainerColor = Color(0xB3140A24),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color(0xFFFFB5D2),
            unfocusedLabelColor = Color(0xFFC9B7D7),
            cursorColor = Color(0xFFFF3F8E)
        )
    )
}

@Composable
private fun StatusCard(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(message, color = Color(0xFFC9B7D7), fontSize = 13.5.sp)
    }
}
