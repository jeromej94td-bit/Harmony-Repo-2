package com.example.data.brain.repository

import android.content.Context
import android.util.Log
import com.example.data.brain.db.BrainAnswerHistoryEntity
import com.example.data.brain.db.BrainGeneratedContentEntity
import com.example.data.brain.db.BrainInteractionEntity
import com.example.data.brain.db.BrainMemoryFactEntity
import com.example.data.brain.db.BrainPendingGenerationEntity
import com.example.data.brain.db.BrainPreferenceEntity
import com.example.data.brain.db.BrainRoomDao
import com.example.data.brain.engine.HarmonyContextBuilder
import com.example.data.brain.engine.HarmonyDuplicateDetector
import com.example.data.brain.engine.HarmonyLocalSignalExtractor
import com.example.data.brain.engine.HarmonyMemoryFactGenerator
import com.example.data.brain.engine.HarmonyPreferenceEngine
import com.example.data.brain.gateway.HarmonyBrainGateway
import com.example.data.brain.gateway.SupabaseHarmonyBrainGateway
import com.example.data.brain.model.BrainContentStatus
import com.example.data.brain.model.BrainContentType
import com.example.data.brain.model.BrainInteractionAction
import com.example.data.brain.model.BrainPendingStatus
import com.example.data.brain.model.BrainScope
import com.example.data.brain.model.HarmonyBrainContext
import com.example.data.model.AnswerEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.HarmonyPacksData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class BrainRepository(
    private val brainDao: BrainRoomDao,
    private val context: Context,
    private val gateway: HarmonyBrainGateway = SupabaseHarmonyBrainGateway.getInstance()
) {

    private val prefs = context.getSharedPreferences("harmony_brain_meta", Context.MODE_PRIVATE)

    // Flow getters
    val answerCountFlow: Flow<Int> = brainDao.getAnswerCountFlow()
    val interactionCountFlow: Flow<Int> = brainDao.getInteractionCountFlow()
    val allPreferencesFlow: Flow<List<BrainPreferenceEntity>> = brainDao.getAllPreferencesFlow()
    val memoryFactsFlow: Flow<List<BrainMemoryFactEntity>> = brainDao.getMemoryFactsFlow()
    val generatedContentFlow: Flow<List<BrainGeneratedContentEntity>> = brainDao.getAllGeneratedContentFlow()
    val pendingGenerationsFlow: Flow<List<BrainPendingGenerationEntity>> = brainDao.getPendingGenerationsFlow()

    /**
     * Records a new answer append-only into brain history and updates preferences & signals.
     */
    suspend fun recordAnswer(
        packId: String,
        questionIndex: Int,
        answerText: String,
        source: String = "STATIC"
    ) = withContext(Dispatchers.IO) {
        // Resolve pack and question details
        val pack = HarmonyPacksData.PACKS.firstOrNull { it.id == packId }
        val category = pack?.cat ?: "Alltag"
        val topic = pack?.topic

        var questionText = "Frage #${questionIndex + 1}"
        var contentType = "QUIZ"
        var answerA: String? = answerText
        var answerB: String? = null

        if (pack != null) {
            if (pack.type == "tot" && questionIndex < pack.pairs.size) {
                contentType = "PAIR"
                val pair = pack.pairs[questionIndex]
                questionText = "${pair.first} oder ${pair.second}?"
            } else if (questionIndex < pack.questions.size) {
                contentType = "QUESTION"
                questionText = pack.questions[questionIndex].q
            }
        }

        // Decode EitherOr / CoupleChoice if present
        val decoded = EitherOrAnswerCodec.decode(answerText)
        if (decoded != null) {
            contentType = "EITHER_OR"
            answerA = decoded.userChoice
            answerB = decoded.partnerChoice
        }

        val questionId = "$packId-$questionIndex"
        val historyEntry = BrainAnswerHistoryEntity(
            id = UUID.randomUUID().toString(),
            packId = packId,
            questionId = questionId,
            questionIndex = questionIndex,
            questionText = questionText,
            category = category,
            topic = topic,
            contentType = contentType,
            answerPersonA = answerA,
            answerPersonB = answerB,
            createdAt = System.currentTimeMillis(),
            source = source
        )

        brainDao.insertAnswerHistory(historyEntry)

        // Log Interaction
        brainDao.insertInteraction(
            BrainInteractionEntity(
                contentId = questionId,
                contentType = contentType,
                action = BrainInteractionAction.ANSWERED.name,
                category = category,
                topic = topic,
                personScope = if (answerB != null) BrainScope.COUPLE.scopeKey else BrainScope.PERSON_A.scopeKey
            )
        )

        // Extract Signals and update preferences
        val signals = HarmonyLocalSignalExtractor.extractSignals(
            category = category,
            topic = topic,
            questionText = questionText,
            answerPersonA = answerA,
            answerPersonB = answerB
        )

        applySignalsAndUpdateMemory(signals)
    }

    /**
     * Records a new moment into brain memory and updates interest preferences.
     */
    suspend fun recordMoment(title: String, content: String) = withContext(Dispatchers.IO) {
        val factText = "Erinnerung: $title. Details: $content"
        val entity = BrainMemoryFactEntity(
            id = UUID.randomUUID().toString(),
            factText = factText,
            category = "Erinnerung",
            personScope = "COUPLE",
            confidence = 1.0,
            importance = 0.9,
            createdAt = System.currentTimeMillis()
        )
        brainDao.insertMemoryFact(entity)

        val signals = HarmonyLocalSignalExtractor.extractSignals(
            category = "Momente",
            topic = "erinnerung",
            questionText = title,
            answerPersonA = content,
            answerPersonB = null
        )
        applySignalsAndUpdateMemory(signals)
    }

    /**
     * Records a skip action for a question.
     */
    suspend fun recordSkip(
        packId: String,
        questionIndex: Int
    ) = withContext(Dispatchers.IO) {
        val pack = HarmonyPacksData.PACKS.firstOrNull { it.id == packId }
        val category = pack?.cat ?: "Alltag"
        val topic = pack?.topic
        val questionId = "$packId-$questionIndex"

        brainDao.insertInteraction(
            BrainInteractionEntity(
                contentId = questionId,
                contentType = pack?.type?.uppercase() ?: "QUESTION",
                action = BrainInteractionAction.SKIPPED.name,
                category = category,
                topic = topic,
                personScope = BrainScope.COUPLE.scopeKey
            )
        )

        val signals = HarmonyLocalSignalExtractor.extractSignals(
            category = category,
            topic = topic,
            questionText = "",
            answerPersonA = null,
            answerPersonB = null,
            isSkipped = true
        )
        applySignalsAndUpdateMemory(signals)
    }

    /**
     * Records pack completion.
     */
    suspend fun recordPackFinished(packId: String) = withContext(Dispatchers.IO) {
        val pack = HarmonyPacksData.PACKS.firstOrNull { it.id == packId }
        val category = pack?.cat ?: "Alltag"

        brainDao.insertInteraction(
            BrainInteractionEntity(
                contentId = packId,
                contentType = "PACK",
                action = BrainInteractionAction.FINISHED_PACK.name,
                category = category,
                topic = pack?.topic,
                personScope = BrainScope.COUPLE.scopeKey
            )
        )
    }

    /**
     * Applies extracted signals and regenerates memory facts.
     */
    private suspend fun applySignalsAndUpdateMemory(signals: List<com.example.data.brain.engine.ExtractedSignal>) {
        if (signals.isEmpty()) return

        val allExisting = brainDao.getAllPreferences().associateBy { Pair(it.scope, it.tag) }
        val updated = HarmonyPreferenceEngine.applySignals(allExisting, signals)
        brainDao.insertOrUpdatePreferences(updated)

        // Refresh facts
        val recentAnswers = brainDao.getAllAnswerHistory()
        val allPrefs = brainDao.getAllPreferences()
        val derivedFacts = HarmonyMemoryFactGenerator.deriveFacts(recentAnswers, allPrefs)
        brainDao.insertMemoryFacts(derivedFacts)
    }

    /**
     * Performs a one-time idempotent backfill from legacy AnswerEntity into append-only BrainAnswerHistoryEntity.
     */
    suspend fun performInitialBackfillIfNeeded(legacyAnswers: List<AnswerEntity>) = withContext(Dispatchers.IO) {
        val alreadyDone = prefs.getBoolean("brain_history_backfill_v1_done", false)
        if (alreadyDone) return@withContext

        val existingHistoryCount = brainDao.getAnswerCount()
        if (existingHistoryCount > 0 && legacyAnswers.isEmpty()) {
            prefs.edit().putBoolean("brain_history_backfill_v1_done", true).apply()
            return@withContext
        }

        Log.d("BrainRepository", "Starting one-time backfill of ${legacyAnswers.size} legacy answers...")
        val historyToInsert = mutableListOf<BrainAnswerHistoryEntity>()

        for (ans in legacyAnswers) {
            val pack = HarmonyPacksData.PACKS.firstOrNull { it.id == ans.packId }
            val category = pack?.cat ?: "Alltag"
            val topic = pack?.topic

            var questionText = "Frage #${ans.questionIndex + 1}"
            var contentType = "QUIZ"
            var answerA: String? = ans.answerText
            var answerB: String? = null

            if (pack != null) {
                if (pack.type == "tot" && ans.questionIndex < pack.pairs.size) {
                    contentType = "PAIR"
                    val pair = pack.pairs[ans.questionIndex]
                    questionText = "${pair.first} oder ${pair.second}?"
                } else if (ans.questionIndex < pack.questions.size) {
                    contentType = "QUESTION"
                    questionText = pack.questions[ans.questionIndex].q
                }
            }

            val decoded = EitherOrAnswerCodec.decode(ans.answerText)
            if (decoded != null) {
                contentType = "EITHER_OR"
                answerA = decoded.userChoice
                answerB = decoded.partnerChoice
            }

            historyToInsert.add(
                BrainAnswerHistoryEntity(
                    id = UUID.nameUUIDFromBytes("${ans.packId}-${ans.questionIndex}-${ans.timestamp}".toByteArray()).toString(),
                    packId = ans.packId,
                    questionId = "${ans.packId}-${ans.questionIndex}",
                    questionIndex = ans.questionIndex,
                    questionText = questionText,
                    category = category,
                    topic = topic,
                    contentType = contentType,
                    answerPersonA = answerA,
                    answerPersonB = answerB,
                    createdAt = ans.timestamp,
                    source = "STATIC"
                )
            )
        }

        if (historyToInsert.isNotEmpty()) {
            brainDao.insertAnswerHistories(historyToInsert)
        }

        // Seed preferences from all answers
        val allSignals = historyToInsert.flatMap { item ->
            HarmonyLocalSignalExtractor.extractSignals(
                category = item.category,
                topic = item.topic,
                questionText = item.questionText,
                answerPersonA = item.answerPersonA,
                answerPersonB = item.answerPersonB
            )
        }

        val allExisting = brainDao.getAllPreferences().associateBy { Pair(it.scope, it.tag) }
        val updated = HarmonyPreferenceEngine.applySignals(allExisting, allSignals)
        brainDao.insertOrUpdatePreferences(updated)

        // Seed initial facts
        val allHistory = brainDao.getAllAnswerHistory()
        val allPrefs = brainDao.getAllPreferences()
        val derivedFacts = HarmonyMemoryFactGenerator.deriveFacts(allHistory, allPrefs)
        brainDao.insertMemoryFacts(derivedFacts)

        prefs.edit().putBoolean("brain_history_backfill_v1_done", true).apply()
        Log.d("BrainRepository", "Backfill completed. Seeded ${historyToInsert.size} answers, ${updated.size} preferences, ${derivedFacts.size} facts.")
    }

    /**
     * Builds the AI context using the single HarmonyContextBuilder pipeline.
     */
    suspend fun buildBrainContext(
        task: String,
        category: String? = null,
        query: String? = null,
        userName: String? = null,
        partnerName: String? = null
    ): HarmonyBrainContext = withContext(Dispatchers.IO) {
        val allAnswers = brainDao.getAllAnswerHistory()
        val allPreferences = brainDao.getAllPreferences()
        val allMemoryFacts = brainDao.getAllMemoryFacts()
        val totalInteractions = brainDao.getInteractionCount()

        HarmonyContextBuilder.buildContext(
            allAnswers = allAnswers,
            allPreferences = allPreferences,
            allMemoryFacts = allMemoryFacts,
            totalInteractions = totalInteractions,
            task = task,
            category = category,
            query = query,
            userName = userName,
            partnerName = partnerName
        )
    }

    /**
     * Queues a generation request when offline.
     */
    suspend fun queuePendingGeneration(
        mode: String,
        query: String,
        context: HarmonyBrainContext
    ) = withContext(Dispatchers.IO) {
        val entity = BrainPendingGenerationEntity(
            id = UUID.randomUUID().toString(),
            mode = mode,
            query = query,
            contextJson = HarmonyContextBuilder.serializeCompact(context),
            status = BrainPendingStatus.WAITING.name
        )
        brainDao.insertPendingGeneration(entity)
    }

    /**
     * Stores AI-generated questions safely through the duplicate gate and returns the count of newly inserted items.
     */
    suspend fun storeGeneratedQuestions(
        questions: List<com.example.data.brain.model.GeneratedBrainQuestion>,
        category: String? = "Harmony Brain"
    ): Int = withContext(Dispatchers.IO) {
        val targetCategory = category?.ifBlank { "Harmony Brain" } ?: "Harmony Brain"
        var inserted = 0
        val existing = brainDao.getAllGeneratedContent()
            .map { it.normalizedText }
            .toHashSet()

        questions.forEach { question ->
            val normalized = normalizeForDuplicateCheck(question.text)
            if (normalized.isBlank() || normalized in existing) return@forEach

            brainDao.insertGeneratedContent(
                BrainGeneratedContentEntity(
                    contentType = "QUESTION",
                    category = targetCategory,
                    topic = question.topic,
                    title = null,
                    normalizedText = normalized,
                    payloadJson = question.toJson(),
                    sourceModel = "supabase-gemini",
                    status = "PUBLISHED"
                )
            )
            existing += normalized
            inserted++
        }
        inserted
    }

    private fun normalizeForDuplicateCheck(value: String): String =
        value.lowercase(java.util.Locale.GERMAN)
            .replace(Regex("[^\\p{L}\\p{N} ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun com.example.data.brain.model.GeneratedBrainQuestion.toJson(): String {
        return org.json.JSONObject().apply {
            put("text", text)
            put("category", category)
            put("difficulty", difficulty)
            if (topic != null) put("topic", topic)
        }.toString()
    }

    private val generatedJson = kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun markGeneratedGameOpened(id: String) = withContext(Dispatchers.IO) {
        brainDao.markGeneratedGameOpened(id, System.currentTimeMillis())
    }

    suspend fun storeGeneratedGame(
        title: String,
        emoji: String,
        questions: List<com.example.data.brain.model.GeneratedBrainQuestion>,
        sourceModel: String? = "supabase-gemini"
    ): com.example.data.brain.db.BrainGeneratedContentEntity? = withContext(Dispatchers.IO) {
        val allHistoryTexts = brainDao.getAllAnswerHistory().map { it.questionText }
        val staticTexts = com.example.data.model.HarmonyPacksData.PACKS.flatMap { pack ->
            pack.questions.map { it.q } + pack.pairs.map { "${it.first} oder ${it.second}?" }
        }
        val previousGeneratedTexts = brainDao.getAllGeneratedContent().flatMap { entity ->
            if (entity.contentType == "GAME") {
                emptyList()
            } else {
                listOf(entity.normalizedText)
            }
        }
        val existingTexts = (allHistoryTexts + staticTexts + previousGeneratedTexts).distinct()

        val cleanQuestions = questions
            .map { it.text.trim() }
            .filter { it.length in 12..240 }
            .filter { !HarmonyDuplicateDetector.isDuplicate(it, existingTexts) }
            .distinctBy { HarmonyDuplicateDetector.normalizeText(it) }
            .take(7)
        if (cleanQuestions.size < 4) return@withContext null

        val id = "ai_game_${UUID.randomUUID()}"
        val safeEmoji = emoji.takeIf { it in ALLOWED_GAME_EMOJIS } ?: "✨"
        val payload = com.example.data.brain.model.GeneratedGamePayload(
            id = id,
            title = title.trim().take(48).ifBlank { "Neues Spiel für euch" },
            emoji = safeEmoji,
            questions = cleanQuestions.map {
                com.example.data.brain.model.GeneratedGameQuestion(text = it)
            },
            createdAt = System.currentTimeMillis()
        )
        val entity = BrainGeneratedContentEntity(
            id = id,
            contentType = "GAME",
            category = "Für dich",
            topic = "personalisiert",
            title = payload.title,
            normalizedText = HarmonyDuplicateDetector.normalizeText(
                payload.title + " " + cleanQuestions.joinToString(" ")
            ),
            payloadJson = generatedJson.encodeToString(
                com.example.data.brain.model.GeneratedGamePayload.serializer(), payload
            ),
            sourceModel = sourceModel,
            status = BrainContentStatus.PUBLISHED.name
        )
        brainDao.insertGeneratedContent(entity)
        entity
    }

    companion object {
        val ALLOWED_GAME_EMOJIS = setOf("🧠", "💞", "✨", "🎲", "💬", "🌙", "🍽️", "✈️", "🎯", "🔥")
    }

    // Direct access helper methods for DevStudio & UI
    suspend fun getAllHistory(): List<BrainAnswerHistoryEntity> = withContext(Dispatchers.IO) { brainDao.getAllAnswerHistory() }
    suspend fun getAllPreferences(): List<BrainPreferenceEntity> = withContext(Dispatchers.IO) { brainDao.getAllPreferences() }
    suspend fun getAllMemoryFacts(): List<BrainMemoryFactEntity> = withContext(Dispatchers.IO) { brainDao.getAllMemoryFacts() }
    suspend fun getAllGeneratedContent(): List<BrainGeneratedContentEntity> = withContext(Dispatchers.IO) { brainDao.getAllGeneratedContent() }
    suspend fun getPendingGenerations(): List<BrainPendingGenerationEntity> = withContext(Dispatchers.IO) { brainDao.getPendingGenerations() }
    suspend fun getInteractionCount(): Int = withContext(Dispatchers.IO) { brainDao.getInteractionCount() }
}
