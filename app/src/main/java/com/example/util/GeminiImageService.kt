package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

// --- API MODELS FOR GEMINI IMAGE GENERATION ---

@Serializable
data class GeminiImgPart(
    val text: String? = null,
    val inlineData: GeminiImgInlineData? = null
)

@Serializable
data class GeminiImgInlineData(
    val mimeType: String,
    val data: String // Base64 encoded string
)

@Serializable
data class GeminiImgContent(
    val parts: List<GeminiImgPart>,
    val role: String? = null
)

@Serializable
data class GeminiImgImageConfig(
    val aspectRatio: String = "1:1",
    val imageSize: String = "1K"
)

@Serializable
data class GeminiImgGenerationConfig(
    val responseModalities: List<String> = listOf("IMAGE", "TEXT"),
    val imageConfig: GeminiImgImageConfig? = null,
    val temperature: Float? = 0.7f
)

@Serializable
data class GeminiImgGenerateRequest(
    val contents: List<GeminiImgContent>,
    val generationConfig: GeminiImgGenerationConfig? = null
)

@Serializable
data class GeminiImgCandidate(
    val content: GeminiImgContent? = null
)

@Serializable
data class GeminiImgGenerateResponse(
    val candidates: List<GeminiImgCandidate> = emptyList()
)

interface GeminiImageApiService {
    @POST("v1beta/models/gemini-2.5-flash-image:generateContent")
    suspend fun generateImageContent(
        @Query("key") apiKey: String,
        @Body request: GeminiImgGenerateRequest
    ): GeminiImgGenerateResponse
}

// --- DOMAIN ENUMS FOR "EURE MISCHUNG" ---

enum class BlendScenario(val titleDe: String, val emoji: String, val promptDesc: String) {
    BABY(
        "Neugeborenes Baby",
        "🍼",
        "The two parents are tenderly and lovingly holding their newborn baby together in a warm, cozy nursery with soft ambient lighting."
    ),
    TODDLER(
        "Kleinkind (2-3 Jahre)",
        "🧸",
        "A cute, joyful toddler child (around 2 to 3 years old) smiling and playing between the two happy parents in a sunny park or cozy home."
    ),
    SCHOOLKID(
        "Schulkind (6-8 Jahre)",
        "🎒",
        "A cheerful school-aged child (around 7 years old) with a radiant smile, happily standing together with both parents, sharing unmistakable features of both."
    ),
    FAMILY(
        "Familienporträt",
        "👨‍👩‍👧",
        "A beautiful, heartfelt family portrait showing the two parents together with their child, radiating genuine love, happiness and warmth."
    )
}

enum class BlendStyle(val titleDe: String, val emoji: String, val promptDesc: String) {
    ANIME(
        "Anime Romantik",
        "✨",
        "in a high-end cinematic modern anime art style, reminiscent of Makoto Shinkai and Studio Ghibli, featuring vibrant colors, soft emotional lighting, sparkling details, and romantic atmospheric flair."
    ),
    REALISTIC(
        "Fotorealistisch",
        "📸",
        "in an ultra-high quality, warm, natural portrait photography style with soft depth-of-field bokeh, natural skin tones, gentle golden-hour lighting, and authentic emotional expression."
    ),
    WATERCOLOR(
        "Künstlerisches Aquarell",
        "🎨",
        "in an artistic watercolor and pastel storybook illustration style, with delicate brush strokes, soft color blends, and a dreamy emotional aesthetic."
    ),
    PIXAR_3D(
        "3D Animations-Look",
        "🎬",
        "in a high-end 3D animated feature film character style (similar to Disney Pixar films), with expressive charming faces, rich textures, warm lighting, and joyful personality."
    )
}

enum class BlendGender(val titleDe: String, val emoji: String, val promptDesc: String) {
    SURPRISE(
        "🎲 Zufall / Überraschung",
        "🎲",
        "a healthy child that naturally blends the genetics and facial highlights of both parents."
    ),
    GIRL(
        "👧 Mädchen",
        "👧",
        "a beautiful baby girl / daughter who inherits harmonic facial qualities, eye sparkle, and hair texture from both parents."
    ),
    BOY(
        "👦 Junge",
        "👦",
        "a charming baby boy / son who inherits harmonic facial structure, eye shape, and smile from both parents."
    ),
    TWINS(
        "👶 Zwillinge",
        "👶👶",
        "two adorable twin babies/children (one boy and one girl or twin siblings) peacefully together with the parents."
    )
}

data class GeneratedImageResult(
    val bitmap: Bitmap,
    val localFilePath: String,
    val aiDescription: String,
    val promptSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

object GeminiImageService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: GeminiImageApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiImageApiService::class.java)
    }

    private suspend fun <T> retryWithBackoff(
        times: Int = 3,
        initialDelay: Long = 2000,
        maxDelay: Long = 10000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return block()
            } catch (e: HttpException) {
                if (e.code() != 429) throw e
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        return block()
    }

    /**
     * Generates an Anime visual of a couple's Date activity.
     */
    suspend fun generateAnimeDateVisual(
        context: Context,
        userAvatarPath: String?,
        partnerAvatarPath: String?,
        userName: String,
        partnerName: String,
        activityTitle: String,
        activityDescription: String
    ): Result<GeneratedImageResult> = withContext(Dispatchers.IO) {
        runCatching {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank()) {
                error("Gemini API Key ist nicht konfiguriert.")
            }

            val parts = mutableListOf<GeminiImgPart>()

            // 1. Text Prompt
            val textPrompt = buildString {
                append("Create a breathtaking, emotional, and romantic Anime artwork in Makoto Shinkai & Studio Ghibli art style. ")
                append("The couple $userName and $partnerName are happily experiencing this date activity: ")
                append("'$activityTitle - $activityDescription'. ")
                append("Portray the two anime characters with affectionate chemistry, matching the hairstyles and general appearance of the attached reference photos if provided. ")
                append("Include beautiful atmospheric lighting, glowing twilight or cozy ambient tones, detailed aesthetic scenery, and heartwarming romance.")
            }
            parts.add(GeminiImgPart(text = textPrompt))

            // 2. Attach user avatar if available
            loadBitmapAndEncode(context, userAvatarPath)?.let { (mime, base64) ->
                parts.add(GeminiImgPart(inlineData = GeminiImgInlineData(mimeType = mime, data = base64)))
            }

            // 3. Attach partner avatar if available
            loadBitmapAndEncode(context, partnerAvatarPath)?.let { (mime, base64) ->
                parts.add(GeminiImgPart(inlineData = GeminiImgInlineData(mimeType = mime, data = base64)))
            }

            val request = GeminiImgGenerateRequest(
                contents = listOf(GeminiImgContent(parts = parts)),
                generationConfig = GeminiImgGenerationConfig(
                    responseModalities = listOf("IMAGE", "TEXT"),
                    imageConfig = GeminiImgImageConfig(aspectRatio = "1:1", imageSize = "1K")
                )
            )

            val response = retryWithBackoff { api.generateImageContent(apiKey, request) }
            val candidate = response.candidates.firstOrNull()
                ?: error("Keine Antwort von Gemini Bildmodell erhalten.")

            var imagePart: GeminiImgInlineData? = null
            var textPart = ""

            candidate.content?.parts?.forEach { part ->
                if (part.inlineData != null) {
                    imagePart = part.inlineData
                }
                if (!part.text.isNullOrBlank()) {
                    textPart += part.text + " "
                }
            }

            val inline = imagePart ?: error("Es wurde kein Bild im Antwortdatenstrom gefunden.")
            val imageBytes = Base64.decode(inline.data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: error("Bild konnte nicht dekodiert werden.")

            val savedFile = saveBitmapToInternalStorage(context, bitmap, "anime_date_${System.currentTimeMillis()}")

            val description = if (textPart.isNotBlank()) textPart.trim() else "Anime-Visualisierung für euer Date: $activityTitle"

            GeneratedImageResult(
                bitmap = bitmap,
                localFilePath = savedFile.absolutePath,
                aiDescription = description,
                promptSummary = activityTitle
            )
        }
    }

    /**
     * Generates "Eure Mischung" (Future Child / Baby / Family blend from parent photos).
     */
    suspend fun generateCoupleBlend(
        context: Context,
        parent1Source: String?,
        parent2Source: String?,
        parent1Name: String,
        parent2Name: String,
        scenario: BlendScenario,
        style: BlendStyle,
        gender: BlendGender,
        customNotes: String = ""
    ): Result<GeneratedImageResult> = withContext(Dispatchers.IO) {
        runCatching {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank()) {
                error("Gemini API Key ist nicht konfiguriert.")
            }

            val parts = mutableListOf<GeminiImgPart>()

            val prompt = buildString {
                append("High quality emotional concept artwork of 'The Genetic Blend' (Eure Mischung) for the couple $parent1Name and $parent2Name. ")
                append("Create an image depicting: ${scenario.promptDesc} ")
                append("The artwork should be rendered ${style.promptDesc} ")
                append("Child details: ${gender.promptDesc} ")
                append("Harmoniously merge and blend the distinctive facial features, eye shapes, hair textures, skin tone harmony, and joyful smiles of both parents from the attached reference images into the child and family. ")
                if (customNotes.isNotBlank()) {
                    append("User special wishes: $customNotes. ")
                }
                append("The scene must radiate boundless love, warmth, safety, and deep emotional bond.")
            }

            parts.add(GeminiImgPart(text = prompt))

            // Add Parent 1 photo
            loadBitmapAndEncode(context, parent1Source)?.let { (mime, base64) ->
                parts.add(GeminiImgPart(inlineData = GeminiImgInlineData(mimeType = mime, data = base64)))
            }

            // Add Parent 2 photo
            loadBitmapAndEncode(context, parent2Source)?.let { (mime, base64) ->
                parts.add(GeminiImgPart(inlineData = GeminiImgInlineData(mimeType = mime, data = base64)))
            }

            val request = GeminiImgGenerateRequest(
                contents = listOf(GeminiImgContent(parts = parts)),
                generationConfig = GeminiImgGenerationConfig(
                    responseModalities = listOf("IMAGE", "TEXT"),
                    imageConfig = GeminiImgImageConfig(aspectRatio = "1:1", imageSize = "1K")
                )
            )

            val response = retryWithBackoff { api.generateImageContent(apiKey, request) }
            val candidate = response.candidates.firstOrNull()
                ?: error("Keine Antwort von Gemini erhalten.")

            var imagePart: GeminiImgInlineData? = null
            var textPart = ""

            candidate.content?.parts?.forEach { part ->
                if (part.inlineData != null) {
                    imagePart = part.inlineData
                }
                if (!part.text.isNullOrBlank()) {
                    textPart += part.text + " "
                }
            }

            val inline = imagePart ?: error("Es wurde kein Bild im Antwortdatenstrom gefunden.")
            val imageBytes = Base64.decode(inline.data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: error("Bild konnte nicht dekodiert werden.")

            val savedFile = saveBitmapToInternalStorage(context, bitmap, "mischung_${System.currentTimeMillis()}")

            val description = if (textPart.isNotBlank()) {
                textPart.trim()
            } else {
                "Eure Mischung: Eine harmonische Zukunftsvision von $parent1Name & $parent2Name (${scenario.titleDe} im ${style.titleDe}-Stil)."
            }

            GeneratedImageResult(
                bitmap = bitmap,
                localFilePath = savedFile.absolutePath,
                aiDescription = description,
                promptSummary = "${scenario.titleDe} · ${style.titleDe}"
            )
        }
    }

    /**
     * Saves bitmap to device MediaStore gallery so it appears in the device's Photos / Gallery app.
     */
    fun saveToDeviceGallery(context: Context, bitmap: Bitmap, title: String): Uri? {
        return runCatching {
            val sanitized = title.replace(Regex("[^a-zA-Z0-9_]"), "_").take(30)
            val filename = "Harmony_${sanitized}_${System.currentTimeMillis()}.png"
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Harmony")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
            }
            uri
        }.getOrNull()
    }

    /**
     * Shares image via Android Share Sheet.
     */
    fun shareGeneratedImage(context: Context, filePath: String, title: String = "Eure Mischung") {
        runCatching {
            val file = File(filePath)
            if (!file.exists()) return
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.devfiles",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "✨ Harmony: $title 💕")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Bild teilen"))
        }
    }

    private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, name: String): File {
        val dir = File(context.filesDir, "ai_images").apply { mkdirs() }
        val file = File(dir, "$name.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    private fun loadBitmapAndEncode(context: Context, source: String?): Pair<String, String>? {
        if (source.isNullOrBlank()) return null
        return runCatching {
            val bitmap = if (source.startsWith("content://") || source.startsWith("file://")) {
                val uri = Uri.parse(source)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } else {
                val file = File(source)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)
                } else null
            } ?: return null

            val scaled = scaleBitmapDown(bitmap, 512)
            val stream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            val base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
            "image/jpeg" to base64
        }.getOrNull()
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = minOf(maxDimension.toFloat() / width, maxDimension.toFloat() / height)
        val newWidth = (width * ratio).toInt().coerceAtLeast(1)
        val newHeight = (height * ratio).toInt().coerceAtLeast(1)

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
