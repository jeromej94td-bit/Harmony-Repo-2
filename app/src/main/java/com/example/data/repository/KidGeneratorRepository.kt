package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.data.SupabaseKidGeneratorGateway
import com.example.data.model.KidGeneratorRequest
import com.example.data.model.KidGeneratorResponse
import com.example.data.model.KidScenario
import com.example.data.model.KidStyle
import com.example.data.model.KidGender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class KidGeneratorRepository(private val context: Context) {
    private val gateway = SupabaseKidGeneratorGateway()

    suspend fun generateKid(
        userName: String,
        partnerName: String,
        userMainSource: String?,
        partnerMainSource: String?,
        additionalUserSources: List<String>,
        additionalPartnerSources: List<String>,
        scenario: KidScenario,
        style: KidStyle,
        gender: KidGender,
        wishes: String
    ): Result<KidGeneratorResponse> = withContext(Dispatchers.IO) {
        runCatching {
            // Encode main user photo
            val userMainBase64 = loadAndEncode(userMainSource)
            if (userMainBase64 == null) {
                return@withContext Result.success(
                    KidGeneratorResponse(
                        ok = false,
                        error = "Bitte wähle für dich ein Hauptfoto aus oder hinterlege eines im Profil."
                    )
                )
            }

            // Encode main partner photo
            val partnerMainBase64 = loadAndEncode(partnerMainSource)
            if (partnerMainBase64 == null) {
                return@withContext Result.success(
                    KidGeneratorResponse(
                        ok = false,
                        error = "Bitte wähle für deinen Partner ein Hauptfoto aus oder hinterlege eines im Profil."
                    )
                )
            }

            // Encode additional user photos
            val additionalUserBase64List = additionalUserSources.mapNotNull { loadAndEncode(it) }

            // Encode additional partner photos
            val additionalPartnerBase64List = additionalPartnerSources.mapNotNull { loadAndEncode(it) }

            val request = KidGeneratorRequest(
                userName = userName,
                partnerName = partnerName,
                userBase64 = userMainBase64,
                partnerBase64 = partnerMainBase64,
                additionalUserBase64 = additionalUserBase64List,
                additionalPartnerBase64 = additionalPartnerBase64List,
                scenario = scenario.id,
                style = style.id,
                childOption = gender.id,
                wishes = wishes.takeIf { it.isNotBlank() }
            )

            val response = gateway.generateKid(request)
            response
        }
    }

    private fun loadAndEncode(source: String?): String? {
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
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        }.getOrNull()
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val (newWidth, newHeight) = if (width > height) {
            maxDimension to (maxDimension / ratio).toInt()
        } else {
            (maxDimension * ratio).toInt() to maxDimension
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
