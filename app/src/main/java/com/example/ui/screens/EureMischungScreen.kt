package com.example.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.AsyncImage
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyTeal
import com.example.ui.theme.HarmonyText
import com.example.util.BlendGender
import com.example.util.BlendScenario
import com.example.util.BlendStyle
import com.example.util.GeminiImageException
import com.example.util.GeminiImageService
import com.example.util.GeneratedImageResult
import com.example.util.LanguageManager
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EureMischungScreen(
    profile: ProfileEntity,
    appLanguage: String = "de",
    onClose: () -> Unit,
    onAddMoment: (title: String, content: String, emoji: String, imagePath: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    var parent1CustomUri by remember { mutableStateOf<Uri?>(null) }
    var parent2CustomUri by remember { mutableStateOf<Uri?>(null) }

    val parent1Source = parent1CustomUri?.toString() ?: profile.userAvatarPath
    val parent2Source = parent2CustomUri?.toString() ?: profile.partnerAvatarPath

    var selectedScenario by remember { mutableStateOf(BlendScenario.BABY) }
    var selectedStyle by remember { mutableStateOf(BlendStyle.ANIME) }
    var selectedGender by remember { mutableStateOf(BlendGender.SURPRISE) }
    var customNotes by remember { mutableStateOf("") }

    var isGenerating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var technicalErrorDetails by remember { mutableStateOf<String?>(null) }
    var isTechDetailsExpanded by remember { mutableStateOf(false) }
    var currentResult by remember { mutableStateOf<GeneratedImageResult?>(null) }
    var isFullscreenImageOpen by remember { mutableStateOf(false) }
    var isMomentSaved by remember { mutableStateOf(false) }

    val historyList = remember { mutableStateListOf<GeneratedImageResult>() }

    val pickParent1Launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) parent1CustomUri = uri
    }
    val pickParent2Launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) parent2CustomUri = uri
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(HarmonySurface2)
                        .testTag("close_eure_mischung_button")
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
                        Text(text = "👶✨", fontSize = 18.sp)
                    }
                    Text(
                        text = LanguageManager.tr("Wie könnten eure Kinder aussehen?", appLanguage),
                        fontSize = 12.sp,
                        color = HarmonyMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                HarmonyPink.copy(alpha = 0.22f),
                                HarmonyPurple.copy(alpha = 0.26f),
                                HarmonySurface2.copy(alpha = 0.40f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                HarmonyPinkSoft.copy(alpha = 0.45f),
                                HarmonyPurpleLight.copy(alpha = 0.45f)
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
                            text = LanguageManager.tr("Genetik & Liebe vereint", appLanguage),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarmonyText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = LanguageManager.tr(
                                "Gemini kombiniert Gesichtsmerkmale, Augenfarben & Lächeln von ${profile.userName} und ${profile.partnerName} zu einer Zukunftsvision.",
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

            Text(
                text = LanguageManager.tr("1. Eltern-Fotos auswählen", appLanguage),
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
                ParentPhotoCard(
                    name = profile.userName,
                    roleLabel = LanguageManager.tr("Du", appLanguage),
                    source = parent1Source,
                    onPickNewPhoto = { pickParent1Launcher.launch("image/*") },
                    testTag = "parent1_photo_picker"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(HarmonyPinkSoft.copy(alpha = 0.35f), HarmonyPurple.copy(alpha = 0.15f))
                                )
                            )
                            .border(1.dp, HarmonyPink.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "➕", fontSize = 16.sp, color = HarmonyPinkSoft)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "💞", fontSize = 14.sp)
                }

                ParentPhotoCard(
                    name = profile.partnerName,
                    roleLabel = LanguageManager.tr("Partner", appLanguage),
                    source = parent2Source,
                    onPickNewPhoto = { pickParent2Launcher.launch("image/*") },
                    testTag = "parent2_photo_picker"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                BlendScenario.values().forEach { scenario ->
                    val isSelected = selectedScenario == scenario
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedScenario = scenario },
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
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("scenario_chip_${scenario.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                BlendStyle.values().forEach { style ->
                    val isSelected = selectedStyle == style
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStyle = style },
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
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("style_chip_${style.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                BlendGender.values().forEach { gender ->
                    val isSelected = selectedGender == gender
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGender = gender },
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
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("gender_chip_${gender.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = LanguageManager.tr("Besondere Wünsche (optional)", appLanguage),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = HarmonyMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = customNotes,
                onValueChange = { customNotes = it },
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
                    .testTag("custom_notes_input")
            )

            Spacer(modifier = Modifier.height(22.dp))

            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF38121B).copy(alpha = 0.85f))
                        .border(1.2.dp, Color(0xFFFF4D6D).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color(0xFFFF4D6D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Hinweis zur KI-Bildgenerierung",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }

                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFFB3C1),
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp
                        )

                        if (!technicalErrorDetails.isNullOrBlank()) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { isTechDetailsExpanded = !isTechDetailsExpanded }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isTechDetailsExpanded) "▼ Technische Details verbergen" else "▶ Technische Details anzeigen",
                                    color = HarmonyGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            AnimatedVisibility(visible = isTechDetailsExpanded) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = technicalErrorDetails ?: "",
                                        color = Color(0xFFE0E0E0),
                                        fontSize = 10.5.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isGenerating) Brush.horizontalGradient(
                            listOf(HarmonyPurple.copy(alpha = 0.5f), HarmonyPink.copy(alpha = 0.5f))
                        ) else Brush.horizontalGradient(
                            listOf(HarmonyPink, HarmonyPurple, HarmonyPurpleLight)
                        )
                    )
                    .clickable(enabled = !isGenerating) {
                        keyboardController?.hide()
                        isGenerating = true
                        errorMessage = null
                        technicalErrorDetails = null
                        isMomentSaved = false

                        coroutineScope.launch {
                            val result = GeminiImageService.generateCoupleBlend(
                                context = context,
                                parent1Source = parent1Source,
                                parent2Source = parent2Source,
                                parent1Name = profile.userName,
                                parent2Name = profile.partnerName,
                                scenario = selectedScenario,
                                style = selectedStyle,
                                gender = selectedGender,
                                customNotes = customNotes
                            )

                            isGenerating = false
                            result.onSuccess { gen ->
                                currentResult = gen
                                historyList.add(0, gen)
                                Toast.makeText(context, "Eure Mischung wurde erschaffen! ✨", Toast.LENGTH_SHORT).show()
                            }.onFailure { err ->
                                if (err is GeminiImageException) {
                                    errorMessage = err.message
                                    technicalErrorDetails = "[${err.code}] ${err.status ?: "STATUS"}: ${err.technicalDetails}"
                                } else {
                                    errorMessage = err.localizedMessage ?: "Fehler bei der Bildgenerierung."
                                    technicalErrorDetails = err.message
                                }
                            }
                        }
                    }
                    .testTag("generate_mischung_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isGenerating) {
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
                            text = LanguageManager.tr("Mische Gene & erschaffe Bild...", appLanguage),
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

            currentResult?.let { res ->
                ResultCard(
                    result = res,
                    appLanguage = appLanguage,
                    isMomentSaved = isMomentSaved,
                    onOpenFullscreen = { isFullscreenImageOpen = true },
                    onSaveToGallery = {
                        val uri = GeminiImageService.saveToDeviceGallery(context, res.bitmap, "Mischung_${profile.userName}_${profile.partnerName}")
                        if (uri != null) {
                            Toast.makeText(context, "Bild in Galerie gespeichert! 💾", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Konnte Bild nicht speichern", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onSaveToMoments = {
                        if (!isMomentSaved) {
                            onAddMoment(
                                "Eure Mischung: ${res.promptSummary}",
                                res.aiDescription,
                                "👶",
                                res.localFilePath
                            )
                            isMomentSaved = true
                            Toast.makeText(context, "In Momente gespeichert 💞", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onShare = {
                        GeminiImageService.shareGeneratedImage(
                            context = context,
                            filePath = res.localFilePath,
                            title = "Eure Mischung (${profile.userName} & ${profile.partnerName})"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (historyList.size > 1) {
                Text(
                    text = LanguageManager.tr("Weitere erstellte Mischungen", appLanguage),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarmonyText
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    historyList.forEach { hist ->
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    if (hist == currentResult) 2.dp else 1.dp,
                                    if (hist == currentResult) HarmonyPink else HarmonyLine,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    currentResult = hist
                                    isMomentSaved = false
                                }
                        ) {
                            Image(
                                bitmap = hist.bitmap.asImageBitmap(),
                                contentDescription = hist.promptSummary,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (isFullscreenImageOpen && currentResult != null) {
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
                    bitmap = currentResult!!.bitmap.asImageBitmap(),
                    contentDescription = "Vollbildansicht",
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
                        .background(Color.Black.copy(alpha = 0.6f))
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

@Composable
private fun ResultCard(
    result: GeneratedImageResult,
    appLanguage: String,
    isMomentSaved: Boolean,
    onOpenFullscreen: () -> Unit,
    onSaveToGallery: () -> Unit,
    onSaveToMoments: () -> Unit,
    onShare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(HarmonySurface)
            .border(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(HarmonyPink.copy(alpha = 0.6f), HarmonyPurple.copy(alpha = 0.3f), Color.Transparent)
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👶", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LanguageManager.tr("Euer Ergebnis", appLanguage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HarmonyText
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HarmonyPurple.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = result.promptSummary,
                        fontSize = 11.sp,
                        color = HarmonyPurpleLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black)
                    .clickable { onOpenFullscreen() }
                    .testTag("generated_mischung_image")
            ) {
                Image(
                    bitmap = result.bitmap.asImageBitmap(),
                    contentDescription = "Generierte Mischung",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = LanguageManager.tr("Vergrößern", appLanguage),
                            fontSize = 10.5.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = result.aiDescription,
                fontSize = 13.sp,
                color = HarmonyText.copy(alpha = 0.9f),
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSaveToGallery,
                    colors = ButtonDefaults.buttonColors(containerColor = HarmonySurface2),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_to_gallery_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = HarmonyPinkSoft,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = LanguageManager.tr("Galerie", appLanguage),
                        fontSize = 12.5.sp,
                        color = HarmonyText
                    )
                }

                Button(
                    onClick = onSaveToMoments,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMomentSaved) HarmonyTeal.copy(alpha = 0.25f) else HarmonySurface2
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_to_moments_button")
                ) {
                    Icon(
                        imageVector = if (isMomentSaved) Icons.Default.CheckCircle else Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        tint = if (isMomentSaved) HarmonyTeal else HarmonyPurpleLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isMomentSaved) LanguageManager.tr("Gemerkt", appLanguage) else LanguageManager.tr("Moment", appLanguage),
                        fontSize = 12.5.sp,
                        color = if (isMomentSaved) HarmonyTeal else HarmonyText
                    )
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HarmonySurface2)
                        .testTag("share_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Teilen",
                        tint = HarmonyGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
