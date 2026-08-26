package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.model.KidGender
import com.example.data.model.KidScenario
import com.example.data.model.KidStyle
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyTeal
import com.example.ui.theme.HarmonyText
import com.example.ui.viewmodel.KidGeneratorViewModel
import com.example.util.GeminiImageService
import com.example.util.LanguageManager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidGeneratorScreen(
    profile: ProfileEntity,
    appLanguage: String = "de",
    onClose: () -> Unit,
    onAddMoment: (title: String, content: String, emoji: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    // Initialize our ViewModel
    val viewModel: KidGeneratorViewModel = viewModel(
        factory = remember {
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return KidGeneratorViewModel(context) as T
                }
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    val parent1Source = uiState.userMainUri?.toString() ?: profile.userAvatarPath
    val parent2Source = uiState.partnerMainUri?.toString() ?: profile.partnerAvatarPath

    // Image pickers for main photos
    val pickParent1MainLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.selectUserMainPhoto(uri)
    }
    val pickParent2MainLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.selectPartnerMainPhoto(uri)
    }

    // Image pickers for additional photos
    val addUserPhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.addAdditionalUserPhoto(uri)
    }
    val addPartnerPhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.addAdditionalPartnerPhoto(uri)
    }

    var isFullscreenImageOpen by remember { mutableStateOf(false) }
    var isMomentSaved by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HarmonyBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(HarmonySurface2)
                        .testTag("close_kid_generator_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Zurück",
                        tint = HarmonyText
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = LanguageManager.tr("Eure Mischung", appLanguage),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HarmonyText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "👶💖", fontSize = 18.sp)
                    }
                    Text(
                        text = LanguageManager.tr("Zukunftsvision über Supabase Edge-KI", appLanguage),
                        fontSize = 12.sp,
                        color = HarmonyMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Intro Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                HarmonyPink.copy(alpha = 0.18f),
                                HarmonyPurple.copy(alpha = 0.22f),
                                HarmonySurface2.copy(alpha = 0.40f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                HarmonyPinkSoft.copy(alpha = 0.4f),
                                HarmonyPurpleLight.copy(alpha = 0.4f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(HarmonyPink, HarmonyPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🧬", fontSize = 24.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = LanguageManager.tr("Sichere Server-Generierung", appLanguage),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarmonyText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = LanguageManager.tr(
                                "Die Bildmischung erfolgt sicher serverseitig. Eure Bilder werden harmonisch verschmolzen.",
                                appLanguage
                            ),
                            fontSize = 12.sp,
                            color = HarmonyMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Photo Selection Area
            Text(
                text = LanguageManager.tr("1. Eltern-Fotos (Hauptbilder)", appLanguage),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = HarmonyText
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Partner 1 Main
                ParentPhotoCard(
                    name = profile.userName,
                    roleLabel = LanguageManager.tr("Du", appLanguage),
                    source = parent1Source,
                    onPickNewPhoto = { pickParent1MainLauncher.launch("image/*") },
                    testTag = "kid_user_main_photo"
                )

                // Plus indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(HarmonySurface2)
                            .border(1.dp, HarmonyPink.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "➕", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "✨", fontSize = 14.sp)
                }

                // Partner 2 Main
                ParentPhotoCard(
                    name = profile.partnerName,
                    roleLabel = LanguageManager.tr("Partner", appLanguage),
                    source = parent2Source,
                    onPickNewPhoto = { pickParent2MainLauncher.launch("image/*") },
                    testTag = "kid_partner_main_photo"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Additional Photos Area
            Text(
                text = LanguageManager.tr("Zusätzliche Referenzfotos hinzufügen (optional)", appLanguage),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = HarmonyText
            )
            Text(
                text = LanguageManager.tr("Bis zu 3 weitere Fotos für genauere Merkmale", appLanguage),
                fontSize = 11.sp,
                color = HarmonyMuted
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User Additional List
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = LanguageManager.tr("Für Dich", appLanguage),
                        fontSize = 12.sp,
                        color = HarmonyPinkSoft,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        uiState.additionalUserUris.forEach { uri ->
                            Box(modifier = Modifier.size(46.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .clickable { viewModel.removeAdditionalUserPhoto(uri) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Entfernen",
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                        if (uiState.additionalUserUris.size < 3) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HarmonySurface2)
                                    .border(1.dp, HarmonyLine, RoundedCornerShape(8.dp))
                                    .clickable { addUserPhotoLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = HarmonyMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Partner Additional List
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = LanguageManager.tr("Für Partner", appLanguage),
                        fontSize = 12.sp,
                        color = HarmonyPurpleLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        uiState.additionalPartnerUris.forEach { uri ->
                            Box(modifier = Modifier.size(46.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .clickable { viewModel.removeAdditionalPartnerPhoto(uri) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Entfernen",
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                        if (uiState.additionalPartnerUris.size < 3) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HarmonySurface2)
                                    .border(1.dp, HarmonyLine, RoundedCornerShape(8.dp))
                                    .clickable { addPartnerPhotoLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = HarmonyMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Scenario Selection
            Text(
                text = LanguageManager.tr("2. Szenario & Alter wählen", appLanguage),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = HarmonyText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KidScenario.values().forEach { scenario ->
                    val isSelected = uiState.selectedScenario == scenario
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setScenario(scenario) },
                        label = {
                            Text(
                                text = "${scenario.emoji} ${LanguageManager.tr(scenario.titleDe, appLanguage)}",
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = HarmonySurface2,
                            labelColor = HarmonyMuted,
                            selectedContainerColor = HarmonyPink.copy(alpha = 0.28f),
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = HarmonyLine,
                            selectedBorderColor = HarmonyPink
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Style Selection
            Text(
                text = LanguageManager.tr("3. Kunst- & Fotostil", appLanguage),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = HarmonyText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KidStyle.values().forEach { style ->
                    val isSelected = uiState.selectedStyle == style
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setStyle(style) },
                        label = {
                            Text(
                                text = "${style.emoji} ${LanguageManager.tr(style.titleDe, appLanguage)}",
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = HarmonySurface2,
                            labelColor = HarmonyMuted,
                            selectedContainerColor = HarmonyPurple.copy(alpha = 0.30f),
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = HarmonyLine,
                            selectedBorderColor = HarmonyPurpleLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Gender / Kids Selection
            Text(
                text = LanguageManager.tr("4. Geschlecht / Kinder", appLanguage),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = HarmonyText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KidGender.values().forEach { gender ->
                    val isSelected = uiState.selectedGender == gender
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setGender(gender) },
                        label = {
                            Text(
                                text = "${gender.emoji} ${LanguageManager.tr(gender.titleDe, appLanguage)}",
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = HarmonySurface2,
                            labelColor = HarmonyMuted,
                            selectedContainerColor = HarmonyTeal.copy(alpha = 0.26f),
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = HarmonyLine,
                            selectedBorderColor = HarmonyTeal
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Optional wishes text field
            Text(
                text = LanguageManager.tr("Besondere Wünsche (optional)", appLanguage),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = HarmonyMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = uiState.wishes,
                onValueChange = { viewModel.setWishes(it) },
                placeholder = {
                    Text(
                        text = LanguageManager.tr("z. B. Grüne Augen, lockiges Haar, Lächeln im Sonnenschein...", appLanguage),
                        color = HarmonyMuted.copy(alpha = 0.7f),
                        fontSize = 12.5.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = HarmonySurface2,
                    unfocusedContainerColor = HarmonySurface2,
                    focusedBorderColor = HarmonyPink,
                    unfocusedBorderColor = HarmonyLine,
                    focusedTextColor = HarmonyText,
                    unfocusedTextColor = HarmonyText
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kid_generator_wishes")
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Error display if any
            if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF38121B).copy(alpha = 0.85f))
                        .border(1.2.dp, Color(0xFFFF4D6D).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color(0xFFFF4D6D),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = Color(0xFFFFB3C1),
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // CTA Generate Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (uiState.isGenerating) Brush.horizontalGradient(
                            listOf(HarmonyPurple.copy(alpha = 0.5f), HarmonyPink.copy(alpha = 0.5f))
                        ) else Brush.horizontalGradient(
                            listOf(HarmonyPink, HarmonyPurple, HarmonyPurpleLight)
                        )
                    )
                    .clickable(enabled = !uiState.isGenerating) {
                        keyboardController?.hide()
                        viewModel.generate(context, profile)
                    }
                    .testTag("generate_kid_button"),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isGenerating) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                        Text(
                            text = LanguageManager.tr("Mische Gene über Edge Function...", appLanguage),
                            color = Color.White,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = LanguageManager.tr("✨ Eure Mischung jetzt generieren", appLanguage),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Result Display
            uiState.result?.let { res ->
                val localPath = uiState.generatedLocalPath
                if (localPath != null) {
                    val bitmap = BitmapFactory.decodeFile(localPath)
                    if (bitmap != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(HarmonySurface2)
                                .border(1.dp, HarmonyLine, RoundedCornerShape(22.dp))
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { isFullscreenImageOpen = true }
                            ) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Generierte Mischung",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(12.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = "🔍 Tippen für Vollbild",
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Eure Mischung ist da! ✨",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarmonyText
                            )

                            Text(
                                text = "Stil: ${uiState.selectedStyle.titleDe} | Szenario: ${uiState.selectedScenario.titleDe}",
                                fontSize = 12.sp,
                                color = HarmonyMuted,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Save to Moments
                                Button(
                                    onClick = {
                                        if (!isMomentSaved) {
                                            onAddMoment(
                                                "Eure Mischung: ${uiState.selectedScenario.titleDe} (${uiState.selectedStyle.titleDe})",
                                                "Unser KI-generiertes Zukunfts-Kind im ${uiState.selectedStyle.titleDe}-Stil.",
                                                "👶"
                                            )
                                            isMomentSaved = true
                                            Toast.makeText(context, "In Momente gespeichert 💞", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isMomentSaved) HarmonyTeal.copy(alpha = 0.2f) else HarmonyPink,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isMomentSaved) Icons.Default.CheckCircle else Icons.Default.BookmarkAdd,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isMomentSaved) "Gespeichert" else "Momente",
                                        fontSize = 12.sp
                                    )
                                }

                                // Share
                                Button(
                                    onClick = {
                                        GeminiImageService.shareGeneratedImage(
                                            context = context,
                                            filePath = localPath,
                                            title = "Eure Mischung von ${profile.userName} & ${profile.partnerName}"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HarmonyPurple,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Teilen", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Restart
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextButton(
                                    onClick = { viewModel.clearResult() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = HarmonyMuted
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Nochmal generieren",
                                        color = HarmonyMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Fullscreen Dialog
    if (isFullscreenImageOpen && uiState.generatedLocalPath != null) {
        val bitmap = BitmapFactory.decodeFile(uiState.generatedLocalPath)
        if (bitmap != null) {
            Dialog(
                onDismissRequest = { isFullscreenImageOpen = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.95f))
                        .clickable { isFullscreenImageOpen = false }
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Vollbild",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                    IconButton(
                        onClick = { isFullscreenImageOpen = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(16.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentPhotoCard(
    name: String,
    roleLabel: String,
    source: String?,
    onPickNewPhoto: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(HarmonySurface2)
                .border(2.dp, Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)), CircleShape)
                .clickable { onPickNewPhoto() },
            contentAlignment = Alignment.Center
        ) {
            if (!source.isNullOrBlank()) {
                val model = if (source.startsWith("content://") || source.startsWith("file://")) {
                    Uri.parse(source)
                } else {
                    File(source)
                }
                AsyncImage(
                    model = model,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = name.take(1).uppercase(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarmonyPinkSoft
                )
            }

            // Edit Overlay Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(HarmonyPink)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Foto ändern",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = HarmonyText
        )
        Text(
            text = roleLabel,
            fontSize = 11.sp,
            color = HarmonyMuted
        )
    }
}
