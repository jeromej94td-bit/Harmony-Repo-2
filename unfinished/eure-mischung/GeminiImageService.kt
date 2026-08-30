package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
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
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

// --- GOOGLE API ERROR PAYLOAD MODELS ---

@Serializable
data class GoogleApiErrorDetails(
    val reason: String? = null,
    val domain: String? = null,
    val metadata: Map<String, String>? = null
)

@Serializable
data class GoogleApiErrorPayload(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null,
    val details: List<GoogleApiErrorDetails> = emptyList()
)

@Serializable
data class GoogleApiErrorEnvelope(
    val error: GoogleApiErrorPayload? = null
)

// --- STRUCTURED DOMAIN EXCEPTIONS ---

sealed class GeminiImageException(
    override val message: String,
    val code: Int,
    val status: String?,
    val technicalDetails: String
) : Exception(message) {

    class RateLimitExceeded(
        message: String,
        code: Int = 429,
        status: String? = "RESOURCE_EXHAUSTED",
        technicalDetails: String
    ) : GeminiImageException(message, code, status, technicalDetails)

    class ModelUnavailable(
        message: String,
        code: Int = 404,
        status: String? = "NOT_FOUND",
        technicalDetails: String
    ) : GeminiImageException(message, code, status, technicalDetails)

    class AuthenticationFailed(
        message: String,
        code: Int = 401,
        status: String? = "UNAUTHENTICATED",
        technicalDetails: String
    ) : GeminiImageException(message, code, status, technicalDetails)

    class InvalidRequest(
        message: String,
        code: Int = 400,
        status: String? = "INVALID_ARGUMENT",
        technicalDetails: String
    ) : GeminiImageException(message, code, status, technicalDetails)

    class ServerError(
        message: String,
        code: Int = 500,
        status: String? = "INTERNAL",
        technicalDetails: String
    ) : GeminiImageException(message, code, status, technicalDetails)

    class Unknown(
        message: String,
        code: Int = -1,
        status: String? = null,
        technicalDetails: String
    ) : GeminiImageException(message, code, status, technicalDetails)
}

interface GeminiImageApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateImageContent(
        @Path("model") model: String,
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

    private const val TAG = "GeminiImageService"

    val IMAGE_MODELS = listOf(
        "gemini-3.1-flash-image",
        "gemini-3.1-flash-image-preview",
        "gemini-2.5-flash-image"
    )

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

    /**
     * Resolves the configured Gemini API key centrally.
     */
    fun resolveGeminiApiKey(): String {
        return BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() } ?: ""
    }

    /**
     * Parses Google API error payloads into clear, actionable domain exceptions.
     */
    fun parseGoogleError(httpCode: Int, rawBody: String?): GeminiImageException {
        val trimmed = rawBody?.trim().orEmpty()
        val parsedEnvelope = try {
            if (trimmed.isNotEmpty()) {
                json.decodeFromString<GoogleApiErrorEnvelope>(trimmed)
            } else null
        } catch (_: Exception) {
            null
        }

        val errPayload = parsedEnvelope?.error
        val apiMsg = errPayload?.message ?: ""
        val apiStatus = errPayload?.status ?: ""
        val detailsStr = if (trimmed.isNotBlank()) trimmed else "HTTP $httpCode (Keine Detaildaten vom Server empfangen)"

        return when (httpCode) {
            429 -> {
                val isQuotaIssue = apiMsg.contains("quota", ignoreCase = true) ||
                        apiMsg.contains("limit", ignoreCase = true) ||
                        apiMsg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
                        apiMsg.contains("billing", ignoreCase = true)

                val userMsg = if (isQuotaIssue) {
                    "Die KI-Bildgenerierung ist momentan nicht verfügbar. Das Nutzungslimit oder die Bild-Quota des Gemini-Projekts wurde erreicht."
                } else {
                    "Die KI-Bildgenerierung ist momentan ausgelastet (Rate-Limit). Bitte versuche es in wenigen Sekunden erneut."
                }

                GeminiImageException.RateLimitExceeded(
                    message = userMsg,
                    code = 429,
                    status = apiStatus.ifBlank { "RESOURCE_EXHAUSTED" },
                    technicalDetails = if (apiMsg.isNotBlank()) "$apiStatus: $apiMsg" else detailsStr
                )
            }
            400 -> {
                GeminiImageException.InvalidRequest(
                    message = "Ungültige Bildgenerierungs-Anfrage: ${apiMsg.ifBlank { "Die Parameter oder Referenzbilder wurden vom Modell nicht akzeptiert." }}",
                    code = 400,
                    status = apiStatus.ifBlank { "INVALID_ARGUMENT" },
                    technicalDetails = if (apiMsg.isNotBlank()) "$apiStatus: $apiMsg" else detailsStr
                )
            }
            401, 403 -> {
                GeminiImageException.AuthenticationFailed(
                    message = "Ungültiger API-Schlüssel oder fehlende Berechtigung für Bildgenerierung im Google-AI-Studio-Projekt.",
                    code = httpCode,
                    status = apiStatus.ifBlank { "PERMISSION_DENIED" },
                    technicalDetails = if (apiMsg.isNotBlank()) "$apiStatus: $apiMsg" else detailsStr
                )
            }
            404 -> {
                GeminiImageException.ModelUnavailable(
                    message = "Das angeforderte Bildmodell ist für dieses Projekt nicht verfügbar.",
                    code = 404,
                    status = apiStatus.ifBlank { "NOT_FOUND" },
                    technicalDetails = if (apiMsg.isNotBlank()) "$apiStatus: $apiMsg" else detailsStr
                )
            }
            500, 503 -> {
                GeminiImageException.ServerError(
                    message = "Google KI-Dienst ist vorübergehend nicht erreichbar. Bitte versuche es gleich noch einmal.",
                    code = httpCode,
                    status = apiStatus.ifBlank { "UNAVAILABLE" },
                    technicalDetails = if (apiMsg.isNotBlank()) "$apiStatus: $apiMsg" else detailsStr
                )
            }
            else -> {
                GeminiImageException.Unknown(
                    message = "Bildgenerierung fehlgeschlagen (Code $httpCode): ${apiMsg.ifBlank { "Unbekannter Fehler" }}",
                    code = httpCode,
                    status = apiStatus,
                    technicalDetails = if (apiMsg.isNotBlank()) "$apiStatus: $apiMsg" else detailsStr
                )
            }
        }
    }

    /**
     * Executes API call with progressive backoff:
     * - Attempt 1: immediate
     * - Attempt 2: after 2000 ms
     * - Attempt 3: after 5000 ms
     */
    private suspend fun executeWithBackoff(
        model: String,
        call: suspend () -> GeminiImgGenerateResponse
    ): GeminiImgGenerateResponse {
        val delays = listOf(0L, 2000L, 5000L)
        var lastError: Exception? = null

        for ((index, delayMs) in delays.withIndex()) {
            if (delayMs > 0) {
                Log.d(TAG, "Retry ${index + 1}/${delays.size} for model=$model after ${delayMs}ms...")
                delay(delayMs)
            }

            try {
                return call()
            } catch (e: HttpException) {
                val code = e.code()
                val rawBody = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
                val parsed = parseGoogleError(code, rawBody)
                Log.e(TAG, "HTTP $code from Gemini API (Model: $model): ${parsed.technicalDetails}")

                // Only retry on transient rate limits (429) or transient server errors (500/503)
                if ((code == 429 || code == 500 || code == 503) && index < delays.size - 1) {
                    lastError = parsed
                    continue
                }
                throw parsed
            } catch (e: IOException) {
                Log.e(TAG, "Network IOException on model=$model: ${e.message}")
                if (index < delays.size - 1) {
                    lastError = e
                    continue
                }
                throw GeminiImageException.ServerError(
                    message = "Netzwerkfehler: Verbindung zu Google KI konnte nicht hergestellt werden. Bitte Internetverbindung prüfen.",
                    code = -1,
                    status = "NETWORK_ERROR",
                    technicalDetails = e.message ?: "IOException"
                )
            }
        }

        throw lastError ?: GeminiImageException.Unknown(
            message = "Bildgenerierung nach Wiederholungsversuchen fehlgeschlagen.",
            code = -1,
            status = null,
            technicalDetails = "Max retries reached"
        )
    }

    /**
     * Executes generation with model fallback chain.
     */
    private suspend fun executeImageGenerationWithFallback(
        apiKey: String,
        request: GeminiImgGenerateRequest
    ): GeminiImgGenerateResponse {
        var lastException: Exception? = null

        for (model in IMAGE_MODELS) {
            Log.d(TAG, "Starting Gemini Image request with model=$model")
            try {
                return executeWithBackoff(model) {
                    api.generateImageContent(model = model, apiKey = apiKey, request = request)
                }
            } catch (e: GeminiImageException.ModelUnavailable) {
                Log.w(TAG, "Model $model returned 404 / Unavailable. Trying next model in fallback chain...")
                lastException = e
            } catch (e: GeminiImageException) {
                Log.e(TAG, "Unrecoverable Gemini error on model=$model: HTTP ${e.code}, status=${e.status}")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error on model=$model: ${e.message}")
                lastException = e
            }
        }

        throw lastException ?: GeminiImageException.Unknown(
            message = "Keines der verfügbaren Bildmodelle konnte erreicht werden.",
            code = -1,
            status = null,
            technicalDetails = "Models attempted: ${IMAGE_MODELS.joinToString()}"
        )
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
            val apiKey = resolveGeminiApiKey()
            if (apiKey.isBlank()) {
                throw GeminiImageException.AuthenticationFailed(
                    message = "Gemini API-Schlüssel ist nicht konfiguriert. Bitte prüfe die App-Einstellungen.",
                    code = 401,
                    status = "MISSING_KEY",
                    technicalDetails = "GEMINI_API_KEY is empty in BuildConfig"
                )
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

            val response = executeImageGenerationWithFallback(apiKey, request)
            val candidate = response.candidates.firstOrNull()
                ?: throw GeminiImageException.Unknown(
                    message = "Keine Antwortkandidaten vom Gemini-Bildmodell empfangen.",
                    code = 200,
                    status = "EMPTY_RESPONSE",
                    technicalDetails = "Candidates list was empty"
                )

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

            val inline = imagePart ?: throw GeminiImageException.Unknown(
                message = "Es wurde kein Bild im Antwortdatenstrom gefunden. Das Modell hat eventuell nur Text zurückgegeben.",
                code = 200,
                status = "NO_IMAGE_DATA",
                technicalDetails = "Text received: ${textPart.take(200)}"
            )

            val imageBytes = Base64.decode(inline.data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: throw GeminiImageException.Unknown(
                    message = "Das empfangene Bild konnte nicht dekodiert werden.",
                    code = 200,
                    status = "DECODE_ERROR",
                    technicalDetails = "ByteArray size: ${imageBytes.size}"
                )

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
            val apiKey = resolveGeminiApiKey()
            if (apiKey.isBlank()) {
                throw GeminiImageException.AuthenticationFailed(
                    message = "Gemini API-Schlüssel ist nicht konfiguriert. Bitte prüfe die App-Einstellungen.",
                    code = 401,
                    status = "MISSING_KEY",
                    technicalDetails = "GEMINI_API_KEY is empty in BuildConfig"
                )
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

            val response = executeImageGenerationWithFallback(apiKey, request)
            val candidate = response.candidates.firstOrNull()
                ?: throw GeminiImageException.Unknown(
                    message = "Keine Antwortkandidaten vom Gemini-Bildmodell empfangen.",
                    code = 200,
                    status = "EMPTY_RESPONSE",
                    technicalDetails = "Candidates list was empty"
                )

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

            val inline = imagePart ?: throw GeminiImageException.Unknown(
                message = "Es wurde kein Bild im Antwortdatenstrom gefunden. Das Modell hat eventuell nur Text zurückgegeben.",
                code = 200,
                status = "NO_IMAGE_DATA",
                technicalDetails = "Text received: ${textPart.take(200)}"
            )

            val imageBytes = Base64.decode(inline.data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: throw GeminiImageException.Unknown(
                    message = "Das empfangene Bild konnte nicht dekodiert werden.",
                    code = 200,
                    status = "DECODE_ERROR",
                    technicalDetails = "ByteArray size: ${imageBytes.size}"
                )

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

            val scaled = scaleBitmapDown(bitmap, 768)
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
