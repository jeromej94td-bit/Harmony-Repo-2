package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.db.HarmonyDatabase
import com.example.data.model.AnswerEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CoupleStatsEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.MomentEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SharedPicEntity
import com.example.widget.PicShareWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class HarmonyRepository(
    private val db: HarmonyDatabase,
    private val context: Context
) {

    val profileFlow: Flow<ProfileEntity?> = db.profileDao().getProfile()
    val answersFlow: Flow<List<AnswerEntity>> = db.answerDao().getAllAnswers()
    val chatMessagesFlow: Flow<List<ChatMessageEntity>> = db.chatDao().getAllMessages()
    val sharedPicsFlow: Flow<List<SharedPicEntity>> = db.sharedPicDao().getAllPics()
    val momentsFlow: Flow<List<MomentEntity>> = db.momentDao().getAllMoments()
    val statsFlow: Flow<CoupleStatsEntity?> = db.coupleStatsDao().getStats()    private val answerSaveMutex = Mutex()

    suspend fun ensureInitialData() {
        // Production starts neutral. Real identity/couple names come from AppSession.
        val existingProfile = db.profileDao().getProfile().firstOrNull()
        if (existingProfile == null) {
            db.profileDao().insertOrUpdateProfile(ProfileEntity(id = 1))
        }

        val stats = db.coupleStatsDao().getStats().firstOrNull()
        if (stats == null) {
            db.coupleStatsDao().insertOrUpdateStats(CoupleStatsEntity(id = 1))
        }
    }

    suspend fun ensureDemoData() {
        val now = System.currentTimeMillis()
        val profile = db.profileDao().getProfile().firstOrNull() ?: ProfileEntity(id = 1)
        db.profileDao().insertOrUpdateProfile(
            profile.copy(
                userName = "Jerome",
                partnerName = "Alex",
                startDate = now - (830L * 24 * 3600 * 1000),
                simulatorEnabled = true
            )
        )

        if (db.chatDao().getAllMessages().firstOrNull().isNullOrEmpty()) {
            db.chatDao().insertMessage(ChatMessageEntity(sender = "them", text = "Hey du 💕 wie war dein Tag?", timestamp = now - 3 * 3600000))
            db.chatDao().insertMessage(ChatMessageEntity(sender = "me", text = "Stressig — aber jetzt wird’s besser ☺️", timestamp = now - 3 * 3600000 + 60000))
            db.chatDao().insertMessage(ChatMessageEntity(sender = "them", text = "Ich hab schon an unser Wiedersehen gedacht 🥹", timestamp = now - 2 * 3600000))
        }

        if (db.momentDao().getAllMoments().firstOrNull().isNullOrEmpty()) {
            db.momentDao().insertMoment(
                MomentEntity(
                    title = "Unser erstes Videodate",
                    content = "Vier Stunden geredet und die Zeit vergessen.",
                    emoji = "🥰",
                    timestamp = now - (40L * 24 * 3600 * 1000)
                )
            )
            db.momentDao().insertMoment(
                MomentEntity(
                    title = "Überraschungspaket",
                    content = "Kekse und ein Brief — ich musste weinen vor Freude.",
                    emoji = "💌",
                    timestamp = now - (12L * 24 * 3600 * 1000)
                )
            )
        }

        db.coupleStatsDao().insertOrUpdateStats(
            CoupleStatsEntity(id = 1, visitedCities = 7, visitedCountries = 3)
        )
    }

    suspend fun updateProfile(userName: String, partnerName: String, startDate: Long) {
        val current = db.profileDao().getProfile().firstOrNull() ?: ProfileEntity()
        db.profileDao().insertOrUpdateProfile(
            current.copy(
                userName = userName,
                partnerName = partnerName,
                startDate = startDate
            )
        )
    }

    suspend fun setSimulatorEnabled(enabled: Boolean) {
        val current = db.profileDao().getProfile().firstOrNull() ?: ProfileEntity()
        db.profileDao().insertOrUpdateProfile(current.copy(simulatorEnabled = enabled))
    }

    suspend fun saveAnswer(packId: String, questionIndex: Int, answerText: String) {
        answerSaveMutex.withLock {
            val existing = db.answerDao().getAllAnswersDirect().firstOrNull {
                it.packId == packId && it.questionIndex == questionIndex
            }
            if (existing?.answerText == answerText) {
                return@withLock
            }
            db.answerDao().insertAnswer(
                AnswerEntity(
                    packId = packId,
                    questionIndex = questionIndex,
                    answerText = answerText
                )
            )
        }
    }

    

    suspend fun sendChatMessage(text: String, sender: String = "me") {
        db.chatDao().insertMessage(ChatMessageEntity(sender = sender, text = text))
    }

    suspend fun sendChatImage(uri: Uri, sender: String = "me") {
        val path = copyMediaToApp(uri, "chat") ?: return
        db.chatDao().insertMessage(ChatMessageEntity(sender = sender, text = "", imagePath = path))
    }

    suspend fun sendChatVoiceMessage(audioPath: String, durationSeconds: Int, sender: String = "me") {
        db.chatDao().insertMessage(
            ChatMessageEntity(
                sender = sender,
                text = "",
                audioPath = audioPath,
                audioDurationSeconds = durationSeconds
            )
        )
    }

    suspend fun updateProfileAvatar(uri: Uri, isUser: Boolean) {
        val path = copyMediaToApp(uri, "avatars") ?: return
        val current = db.profileDao().getProfile().firstOrNull() ?: ProfileEntity()
        db.profileDao().insertOrUpdateProfile(
            if (isUser) current.copy(userAvatarPath = path)
            else current.copy(partnerAvatarPath = path)
        )
    }

    suspend fun addSharedPictures(uris: List<Uri>, addedBy: String = "me") {
        uris.forEach { uri ->
            val path = copyMediaToApp(uri, "picshare") ?: return@forEach
            db.sharedPicDao().insertPic(SharedPicEntity(filePath = path, addedBy = addedBy))
        }
        PicShareWidgetProvider.refreshAll(context)
    }

    suspend fun updateSharedPicture(pic: SharedPicEntity) {
        db.sharedPicDao().updatePic(pic)
        PicShareWidgetProvider.refreshAll(context)
    }

    suspend fun addMoment(title: String, content: String, imageUris: List<Uri> = emptyList(), emoji: String = "💕") {
        val paths = imageUris.mapNotNull { uri ->
            copyMediaToApp(uri, "moments")
        }
        val pathsJson = paths.joinToString(prefix = "[", postfix = "]") { "\"${it.replace("\\", "\\\\").replace("\"", "\\\"")}\"" }
        db.momentDao().insertMoment(
            MomentEntity(
                title = title,
                content = content,
                emoji = emoji,
                imagePathsJson = pathsJson
            )
        )
    }

    suspend fun updateStats(cities: Int, countries: Int) {
        db.coupleStatsDao().insertOrUpdateStats(CoupleStatsEntity(id = 1, visitedCities = cities, visitedCountries = countries))
    } = db.brainDao().clearQuestions()

    private suspend fun copyMediaToApp(uri: Uri, folder: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            val directory = File(context.filesDir, folder).apply { mkdirs() }
            val mime = context.contentResolver.getType(uri).orEmpty()
            val extension = when {
                mime.contains("png") -> "png"
                mime.contains("webp") -> "webp"
                else -> "jpg"
            }
            val target = File(directory, "${System.currentTimeMillis()}-${UUID.randomUUID()}.$extension")
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "Bild konnte nicht geöffnet werden" }
                target.outputStream().use { output -> input.copyTo(output) }
            }
            target.absolutePath
        }.getOrNull()
    }
}
