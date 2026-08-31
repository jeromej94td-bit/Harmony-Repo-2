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

// Supabase-like dummy client to emulate the JS code you provided
object SupabaseAuthDummy {
    suspend fun signInWithPassword(email: String, pass: String): Result<Boolean> {
        return if (email.isNotEmpty() && pass.isNotEmpty()) Result.success(true)
        else Result.failure(Exception("Ungültige E-Mail oder Passwort (Supabase Error)"))
    }
    suspend fun signUp(email: String, pass: String): Result<Boolean> {
        return if (email.isNotEmpty() && pass.isNotEmpty()) Result.success(true)
        else Result.failure(Exception("Registrierung fehlgeschlagen (Supabase Error)"))
    }
}

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
            // Placeholder for the Cosmic Heart Logo
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

            // Google Button
            Button(
                onClick = { /* TODO: Google Auth */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Mit Google anmelden", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Divider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                Text(" oder ", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
                Divider(modifier = Modifier.weight(1f), color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Email Input
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

            // Password Input
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

            Spacer(modifier = Modifier.height(24.dp))

            // Anmelden Button
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val res = SupabaseAuthDummy.signInWithPassword(email, password)
                        isLoading = false
                        if (res.isSuccess) onAuthSuccess()
                        else errorMessage = res.exceptionOrNull()?.message
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

            // Registrieren Button
            OutlinedButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val res = SupabaseAuthDummy.signUp(email, password)
                        isLoading = false
                        if (res.isSuccess) onAuthSuccess()
                        else errorMessage = res.exceptionOrNull()?.message
                    }
                },
                border = null, // Will use modifier border for gradient
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(2.dp, accentGradient, RoundedCornerShape(12.dp))
            ) {
                Text("Registrieren", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Passwort vergessen?",
                color = Color(0xFFCC99FF),
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* TODO: Forgot Password */ }
            )
        }
    }
}
