package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PHOTO_ONLY_ANSWER = "Ein gemeinsames Foto, das mir besonders viel bedeutet"

@Composable
internal fun PhotoQuestionBoard(
    mode: PhotoQuestionMode,
    rawQuestion: String,
    selectedAnswer: String?,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val presentation = PhotoQuestionPolicy.presentation(mode)
    val storedImage = remember(rawQuestion) {
        photoAnswerFile(context, rawQuestion).takeIf(File::exists)?.absolutePath
    }
    var imagePath by remember(rawQuestion) { mutableStateOf(storedImage) }
    var isCopying by remember(rawQuestion) { mutableStateOf(false) }
    var selectedChoice by remember(rawQuestion, selectedAnswer) {
        mutableStateOf(
            PhotoQuestionPolicy.normalizeLegacyChoice(selectedAnswer)
                ?.takeIf { it in presentation.options }
        )
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            isCopying = true
            scope.launch {
                val copied = withContext(Dispatchers.IO) {
                    copyPhotoAnswerImage(context, rawQuestion, uri)
                }
                if (copied != null) imagePath = copied
                isCopying = false
            }
        }
    }

    val questionText = when (mode) {
        PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO -> tr(
            "Welche Art von gemeinsamen Fotos magst du am liebsten? 📸",
            "Which kind of photos of you together do you like most? 📸"
        )
        PhotoQuestionMode.PHOTO_ONLY -> tr(
            "Welches gemeinsame Foto bedeutet dir besonders viel? 📸",
            "Which photo of you together means the most to you? 📸"
        )
    }
    val instruction = when (mode) {
        PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO -> tr(
            "Wähle zuerst die Art von Fotos. Wenn du möchtest, kannst du direkt ein Lieblingsfoto aus deiner Galerie hinzufügen.",
            "Choose the kind of photos first. If you like, you can also add a favorite photo from your gallery."
        )
        PhotoQuestionMode.PHOTO_ONLY -> tr(
            "Hier ist das Foto selbst die Antwort. Wähle eines aus deiner Galerie aus.",
            "Here, the photo itself is the answer. Choose one from your gallery."
        )
    }

    FullscreenMechanicShell(
        kicker = tr("📸 FOTO-ANTWORT", "📸 PHOTO ANSWER"),
        question = questionText,
        instruction = instruction,
        modifier = modifier.testTag("photo_question_board")
    ) {
        when (mode) {
            PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO -> {
                val items = listOf(
                    MechanicOption(
                        raw = "Lustige Schnappschüsse",
                        label = tr("Lustige Schnappschüsse", "Funny snapshots")
                    ),
                    MechanicOption(
                        raw = "Romantische Fotos",
                        label = tr("Romantische Fotos", "Romantic photos")
                    ),
                    MechanicOption(
                        raw = "Urlaubsfotos",
                        label = tr("Urlaubsfotos", "Vacation photos")
                    )
                )
                Column(modifier = Modifier.fillMaxSize()) {
                    LargeOptionGrid(
                        items = items,
                        selectedRaw = selectedChoice,
                        onSelect = { item ->
                            triggerMiniVibration(context, 36L)
                            selectedChoice = item.raw
                        },
                        modifier = Modifier.weight(1f),
                        tagPrefix = "photo_choice"
                    )
                    Spacer(Modifier.height(12.dp))
                    PhotoAttachmentPanel(
                        imagePath = imagePath,
                        isBusy = isCopying,
                        optional = true,
                        onChoose = {
                            triggerMiniVibration(context, 32L)
                            imagePicker.launch("image/*")
                        },
                        onRemove = {
                            removePhotoAnswerImage(context, rawQuestion)
                            imagePath = null
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    PrimaryMechanicButton(
                        text = tr("Antwort speichern & weiter", "Save answer & continue"),
                        enabled = selectedChoice != null && !isCopying,
                        onClick = { selectedChoice?.let(onPick) },
                        testTag = "photo_optional_submit"
                    )
                }
            }

            PhotoQuestionMode.PHOTO_ONLY -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    PhotoAttachmentPanel(
                        imagePath = imagePath,
                        isBusy = isCopying,
                        optional = false,
                        onChoose = {
                            triggerMiniVibration(context, 32L)
                            imagePicker.launch("image/*")
                        },
                        onRemove = {
                            removePhotoAnswerImage(context, rawQuestion)
                            imagePath = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.height(14.dp))
                    PrimaryMechanicButton(
                        text = tr("Dieses Foto auswählen & weiter", "Choose this photo & continue"),
                        enabled = imagePath != null && !isCopying,
                        onClick = { onPick(PHOTO_ONLY_ANSWER) },
                        testTag = "photo_only_submit"
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoAttachmentPanel(
    imagePath: String?,
    isBusy: Boolean,
    optional: Boolean,
    onChoose: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    val label = when {
        isBusy -> tr("Foto wird vorbereitet …", "Preparing photo …")
        imagePath != null -> tr("📷 Foto ändern", "📷 Change photo")
        optional -> tr("📷 Lieblingsfoto hinzufügen (optional)", "📷 Add a favorite photo (optional)")
        else -> tr("📷 Foto aus Galerie auswählen", "📷 Choose photo from gallery")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.30f),
                        HarmonySurface2.copy(alpha = 0.96f),
                        HarmonySurface.copy(alpha = 0.98f)
                    )
                )
            )
            .border(1.dp, if (imagePath != null) HarmonyPink.copy(alpha = 0.7f) else HarmonyLine, shape)
            .padding(10.dp)
            .testTag("photo_attachment_panel"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (imagePath != null) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = tr("Ausgewähltes Lieblingsfoto", "Selected favorite photo"),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (optional) 128.dp else 260.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .clickable(enabled = !isBusy, onClick = onChoose)
                    .testTag("photo_answer_preview")
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onChoose, enabled = !isBusy) {
                    Text(label, color = HarmonyPinkSoft, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onRemove, enabled = !isBusy) {
                    Text(tr("Entfernen", "Remove"), color = HarmonyMuted)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (optional) 116.dp else 320.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(Color.White.copy(alpha = 0.035f))
                    .border(1.dp, Color.White.copy(alpha = 0.13f), RoundedCornerShape(17.dp))
                    .clickable(enabled = !isBusy, onClick = onChoose)
                    .testTag("photo_answer_picker"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 18.dp)
                ) {
                    Text("📷", fontSize = if (optional) 30.sp else 46.sp)
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = if (optional) 14.sp else 17.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    if (optional && !isBusy) {
                        Text(
                            text = tr(
                                "Deine Auswahl oben funktioniert auch ohne Foto.",
                                "Your choice above also works without adding a photo."
                            ),
                            color = HarmonyMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private fun photoAnswerFile(context: Context, rawQuestion: String): File {
    val directory = File(context.filesDir, "photo_answers").apply { mkdirs() }
    return File(directory, PhotoQuestionPolicy.storageFileName(rawQuestion))
}

private fun copyPhotoAnswerImage(context: Context, rawQuestion: String, uri: Uri): String? = runCatching {
    val target = photoAnswerFile(context, rawQuestion)
    context.contentResolver.openInputStream(uri)?.use { input ->
        target.outputStream().use { output -> input.copyTo(output) }
    } ?: return@runCatching null
    target.absolutePath
}.getOrNull()

private fun removePhotoAnswerImage(context: Context, rawQuestion: String) {
    runCatching { photoAnswerFile(context, rawQuestion).delete() }
}
