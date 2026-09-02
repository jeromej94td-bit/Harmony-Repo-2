package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email as EmailProvider
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private val harmonyPink = Color(0xFFFF3F8E)
private val harmonyViolet = Color(0xFFA555FF)
private val harmonyBlue = Color(0xFF4FAAFF)
private val cosmicInk = Color(0xFF08030F)
private val floatingHearts = listOf(
    HeartParticle(0.07f, 0.18f, 7f, 0.62f, 0.0f),
    HeartParticle(0.91f, 0.27f, 8f, 0.46f, 0.35f),
    HeartParticle(0.12f, 0.74f, 6f, 0.34f, 0.72f),
    HeartParticle(0.87f, 0.80f, 9f, 0.42f, 0.18f),
    HeartParticle(0.46f, 0.11f, 5f, 0.36f, 0.55f)
)

private data class HeartParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val alpha: Float,
    val phase: Float
)

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
    val accentGradient = remember {
        Brush.horizontalGradient(listOf(harmonyPink, harmonyViolet, harmonyBlue))
    }

    LaunchedEffect(Unit) {
        val existingSession = runCatching {
            SupabaseConfig.client.auth.currentSessionOrNull()
        }.getOrNull()
        if (existingSession != null) {
            onAuthSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicLoginBackdrop(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HarmonyLoginLogo()

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "HARMONY",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 7.sp,
                    brush = accentGradient
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        val res = kotlin.runCatching {
                            val activity = context.findActivity()
                                ?: throw Exception("Activity Context nicht gefunden")
                            performResilientGoogleSignIn(
                                activity = activity,
                                credentialManager = credentialManager
                            )
                        }
                        isLoading = false
                        if (res.isSuccess) {
                            when (res.getOrThrow()) {
                                GoogleSignInOutcome.SESSION_CREATED -> onAuthSuccess()
                                GoogleSignInOutcome.OAUTH_REDIRECT_STARTED -> {
                                    successMessage =
                                        "Google-Anmeldung wurde geöffnet. Bitte schließe sie dort ab."
                                }
                            }
                        } else {
                            val exception = res.exceptionOrNull()
                            Log.e("AuthScreen", "Google Login Error", exception)
                            errorMessage = if (exception is GetCredentialException) {
                                "Google-Login abgebrochen oder fehlgeschlagen: ${exception.message}"
                            } else {
                                exception?.message ?: "Fehler beim Google-Login"
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF171321)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 9.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            ) {
                GoogleBrandMark(modifier = Modifier.size(25.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    "Mit Google anmelden",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.20f)
                )
                Text(
                    "oder",
                    color = Color(0xFFB7A2C7),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.20f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            HarmonyAuthField(
                value = email,
                onValueChange = { email = it },
                placeholder = "E-Mail",
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0xFFD0B3F3))
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            HarmonyAuthField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Passwort",
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFD0B3F3))
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Passwort verbergen" else "Passwort anzeigen",
                            tint = Color(0xFFD0B3F3)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFFF8EAA),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (successMessage != null) {
                Text(
                    text = successMessage!!,
                    color = Color(0xFF8FF0BA),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    color = harmonyPink,
                    trackColor = Color.White.copy(alpha = 0.14f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {
                        val currentEmail = email.trim()
                        val currentPassword = password

                        if (currentEmail.isEmpty() ||
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()
                        ) {
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
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(accentGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Anmelden", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        val currentEmail = email.trim()
                        val currentPassword = password

                        if (currentEmail.isEmpty() ||
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()
                        ) {
                            errorMessage =
                                "Bitte gib eine gültige E-Mail-Adresse für die Registrierung ein."
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
                            val session = kotlin.runCatching {
                                SupabaseConfig.client.auth.currentSessionOrNull()
                            }.getOrNull()
                            if (session != null) {
                                onAuthSuccess()
                            } else {
                                successMessage =
                                    "Registrierung erfolgreich! Bitte überprüfe dein Postfach und bestätige deine E-Mail-Adresse über den Link."
                            }
                        } else {
                            errorMessage =
                                res.exceptionOrNull()?.message ?: "Fehler bei der Registrierung"
                        }
                    }
                },
                border = null,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .border(1.5.dp, accentGradient, RoundedCornerShape(16.dp))
            ) {
                Text("Registrieren", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(7.dp))

            TextButton(
                onClick = {
                    onAuthSuccess()
                }
            ) {
                Text(
                    "App im Demo-Modus testen",
                    color = Color(0xFF9FCEFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "Passwort vergessen?",
                color = Color(0xFFD6A9FF),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 1.dp)
                    .clickable(enabled = !isLoading) {
                        scope.launch {
                            val currentEmail = email.trim()
                            if (currentEmail.isEmpty() ||
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()
                            ) {
                                errorMessage = "Bitte gib eine gültige E-Mail-Adresse ein."
                                successMessage = null
                                return@launch
                            }

                            isLoading = true
                            errorMessage = null
                            successMessage = null
                            val res = kotlin.runCatching {
                                SupabaseConfig.client.auth.resetPasswordForEmail(currentEmail)
                            }
                            isLoading = false

                            if (res.isSuccess) {
                                successMessage =
                                    "Passwort-Reset-Link wurde gesendet. Bitte prüfe dein Postfach."
                            } else {
                                errorMessage = res.exceptionOrNull()?.message
                                    ?: "Passwort-Reset konnte nicht gestartet werden."
                            }
                        }
                    }
            )
        }
    }
}

@Composable
private fun HarmonyAuthField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit),
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val fieldShape = RoundedCornerShape(15.dp)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFB4A9C1)) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = harmonyPink,
            unfocusedBorderColor = Color(0xFF70448B),
            focusedContainerColor = Color(0xC0180A2A),
            unfocusedContainerColor = Color(0xB3140A24),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = harmonyPink
        ),
        shape = fieldShape,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
    )
}

@Composable
private fun CosmicLoginBackdrop(modifier: Modifier = Modifier) {
    val motion = rememberInfiniteTransition(label = "cosmic-login-motion")
    val drift by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart-drift"
    )
    val pulse by motion.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star-pulse"
    )

    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF16051F), cosmicInk, Color(0xFF04030B))
            )
        )

        val center = Offset(size.width * 0.5f, size.height * 0.28f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0x3CA555FF), Color.Transparent),
                center = center,
                radius = size.width * 0.72f
            ),
            radius = size.width * 0.72f,
            center = center
        )

        listOf(0.50f, 0.74f, 1.03f).forEachIndexed { index, scale ->
            drawOval(
                color = if (index == 1) harmonyPink.copy(alpha = 0.13f)
                else Color(0xFFA555FF).copy(alpha = 0.11f),
                topLeft = Offset(
                    center.x - size.width * scale * 0.5f,
                    center.y - size.width * scale * 0.22f
                ),
                size = Size(size.width * scale, size.width * scale * 0.44f),
                style = Stroke(width = if (index == 1) 1.5.dp.toPx() else 1.dp.toPx())
            )
        }

        repeat(24) { index ->
            val angle = index * 0.82f + drift * 0.35f
            val radius = size.width * (0.16f + (index % 5) * 0.12f)
            val star = Offset(
                center.x + cos(angle.toDouble()).toFloat() * radius,
                center.y + sin(angle.toDouble()).toFloat() * radius * 0.42f
            )
            val alpha = (0.18f + (index % 4) * 0.12f) * pulse
            drawCircle(
                color = Color.White.copy(alpha = alpha.coerceAtMost(0.68f)),
                radius = (if (index % 6 == 0) 2.3f else 1.1f).dp.toPx(),
                center = star
            )
        }

        floatingHearts.forEach { heart ->
            val wobble = sin(((drift + heart.phase) * 2f * PI).toDouble()).toFloat()
            drawHeart(
                center = Offset(
                    size.width * heart.x + wobble * 13.dp.toPx(),
                    size.height * (heart.y + wobble * 0.015f)
                ),
                heartSize = heart.size.dp.toPx(),
                color = if (heart.phase < 0.5f) harmonyPink else harmonyViolet,
                alpha = heart.alpha * (0.65f + pulse * 0.35f)
            )
        }
    }
}

@Composable
private fun HarmonyLoginLogo() {
    val glow = rememberInfiniteTransition(label = "logo-glow").animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo-glow-alpha"
    ).value

    Canvas(modifier = Modifier.size(122.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(harmonyPink.copy(alpha = 0.34f * glow), Color.Transparent),
                center = center,
                radius = size.width * 0.55f
            ),
            radius = size.width * 0.55f,
            center = center
        )

        val ribbon = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.46f)
            cubicTo(
                size.width * 0.20f, size.height * 0.18f,
                size.width * 0.47f, size.height * 0.12f,
                size.width * 0.63f, size.height * 0.35f
            )
            cubicTo(
                size.width * 0.83f, size.height * 0.63f,
                size.width * 0.84f, size.height * 0.76f,
                size.width * 0.66f, size.height * 0.80f
            )
            cubicTo(
                size.width * 0.48f, size.height * 0.85f,
                size.width * 0.32f, size.height * 0.71f,
                size.width * 0.18f, size.height * 0.46f
            )
        }
        drawPath(
            path = ribbon,
            brush = Brush.linearGradient(
                listOf(harmonyPink, harmonyViolet, harmonyBlue),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            ),
            style = Stroke(width = 17.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val crossRibbon = Path().apply {
            moveTo(size.width * 0.80f, size.height * 0.46f)
            cubicTo(
                size.width * 0.77f, size.height * 0.18f,
                size.width * 0.51f, size.height * 0.12f,
                size.width * 0.36f, size.height * 0.35f
            )
            cubicTo(
                size.width * 0.18f, size.height * 0.62f,
                size.width * 0.17f, size.height * 0.76f,
                size.width * 0.35f, size.height * 0.80f
            )
        }
        drawPath(
            path = crossRibbon,
            brush = Brush.linearGradient(
                listOf(harmonyBlue, harmonyViolet, harmonyPink),
                start = Offset(size.width, 0f),
                end = Offset(0f, size.height)
            ),
            style = Stroke(width = 17.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        drawCircle(color = Color.White.copy(alpha = glow), radius = 4.dp.toPx(), center = center)
    }
}

@Composable
private fun GoogleBrandMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.18f
        val radius = size.minDimension * 0.33f
        val center = Offset(size.width / 2f, size.height / 2f)
        drawArc(Color(0xFF4285F4), -42f, 88f, false,
            Offset(center.x - radius, center.y - radius), Size(radius * 2f, radius * 2f),
            style = Stroke(stroke, cap = StrokeCap.Butt))
        drawArc(Color(0xFF34A853), 48f, 88f, false,
            Offset(center.x - radius, center.y - radius), Size(radius * 2f, radius * 2f),
            style = Stroke(stroke, cap = StrokeCap.Butt))
        drawArc(Color(0xFFFBBC05), 136f, 88f, false,
            Offset(center.x - radius, center.y - radius), Size(radius * 2f, radius * 2f),
            style = Stroke(stroke, cap = StrokeCap.Butt))
        drawArc(Color(0xFFEA4335), 224f, 94f, false,
            Offset(center.x - radius, center.y - radius), Size(radius * 2f, radius * 2f),
            style = Stroke(stroke, cap = StrokeCap.Butt))
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(center.x + radius * 0.12f, center.y),
            end = Offset(center.x + radius * 1.28f, center.y),
            strokeWidth = stroke,
            cap = StrokeCap.Butt
        )
    }
}

private fun DrawScope.drawHeart(
    center: Offset,
    heartSize: Float,
    color: Color,
    alpha: Float
) {
    val half = heartSize / 2f
    val path = Path().apply {
        moveTo(center.x, center.y + half)
        cubicTo(
            center.x - heartSize, center.y - half * 0.1f,
            center.x - half * 0.7f, center.y - heartSize,
            center.x, center.y - half * 0.15f
        )
        cubicTo(
            center.x + half * 0.7f, center.y - heartSize,
            center.x + heartSize, center.y - half * 0.1f,
            center.x, center.y + half
        )
        close()
    }
    drawPath(path, color.copy(alpha = alpha))
}
