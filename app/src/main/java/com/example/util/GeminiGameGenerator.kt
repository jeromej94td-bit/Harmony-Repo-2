package com.example.util

import android.content.Context
import android.util.Log
import com.example.data.model.AnswerEntity
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import com.example.data.DeveloperDataManager
import com.example.data.model.ProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiGameGenerator {
    private const val TAG = "GeminiGameGenerator"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateAndSaveGame(
        context: Context,
        profile: ProfileEntity,
        answers: List<AnswerEntity>
    ): Result<QuestionPack> = withContext(Dispatchers.IO) {
        runCatching {
            val apiKey = com.example.util.GeminiImageService.resolveGeminiApiKey()
            if (apiKey.isBlank()) {
                throw IllegalStateException("Gemini API Key is not configured.")
            }

            // 1. Analyze previous answers to obtain interests

            // 2. Build answers context for the last 15 answers
            val answersContext = answers.takeLast(15).map { ans ->
                val pack = com.example.data.model.HarmonyPacksData.PACKS.find { it.id == ans.packId }
                val qText = if (pack != null && ans.questionIndex >= 0) {
                    if (pack.type == "tot" && ans.questionIndex < pack.pairs.size) {
                        val pair = pack.pairs[ans.questionIndex]
                        "Das oder das: ${pair.first} vs ${pair.second}"
                    } else if (ans.questionIndex < pack.questions.size) {
                        pack.questions[ans.questionIndex].q
                    } else ""
                } else ""
                "- Frage/Aussage: $qText | Antwort: ${ans.answerText}"
            }.joinToString("\n")

            // 3. Construct Prompt
            val prompt = buildString {
                append("Du bist der kreative Spielemacher-Algorithmus von Harmony Brain 🧠.\n")
                append("Erstelle ein neues, individuelles Beziehungsspiel (Fragenpaket) für das Paar:\n")
                append("- Partner A (${profile.userName.takeIf { !it.isNullOrBlank() } ?: "Ich"}): ${profile.userName}\n")
                append("- Partner B (${profile.partnerName.takeIf { !it.isNullOrBlank() } ?: "Mein Partner"}): ${profile.partnerName}\n\n")

                if (answersContext.isNotEmpty()) {
                    append("Hier sind einige ihrer bisherigen individuellen Antworten und Entscheidungen:\n")
                    append(answersContext)
                    append("\n\n")
                }

                append("Deine Aufgabe:\n")
                append("Generiere ein unterhaltsames, spannendes und romantisches Fragenpaket (QuestionPack) in deutscher Sprache, das auf ihren Interessen oder Antworten basiert.\n")
                append("Wähle zufällig eines der folgenden passenden Formate aus:\n")
                append("1. 'wer' (Wer würde eher?): type='quiz', questions mit 4 Optionen: [\"${profile.userName}\", \"${profile.partnerName}\", \"Beide\", \"Keiner\"].\n")
                append("2. 'nie' (Ich habe noch nie): type='disc', Fragen/Aussagen mit leeren options (Diskussionsthemen).\n")
                append("3. 'tot' (Das oder das?): type='tot', pairs mit zwei Auswahlmöglichkeiten (z. B. 'Strand 🏖️' zu 'Berge 🏔️').\n")
                append("4. 'quiz' (Zustimmen oder Ablehnen): type='quiz', questions mit Optionen wie [\"Zustimmen 👍\", \"Ablehnen 👎\"].\n")
                append("5. 'tief' (Tiefe Gespräche): type='disc', offene Fragen mit leeren options.\n\n")

                append("Das Spiel darf KEINE Bildgenerierung oder Medien-Anfragen beinhalten. Alles muss reines Textformat sein.\n\n")
                append("Gib das Ergebnis als ein einzelnes, valides JSON-Objekt in folgender Struktur zurück:\n")
                append("{\n")
                append("  \"id\": \"ai_pack_${System.currentTimeMillis()}\",\n")
                append("  \"title\": \"Kreativer, deutscher Titel (z.B. 'Unsere Sushi-Liebe', 'Wer plant besser?', etc.)\",\n")
                append("  \"emoji\": \"Ein passendes Emoji\",\n")
                append("  \"tags\": [\"beziehung\", \"ki_generiert\", \"individuell\"],\n")
                append("  \"cat\": \"wer\", // oder \"nie\", \"tot\", \"quiz\", \"tief\"\n")
                append("  \"topic\": \"aufwaermen\", // oder \"beziehung\", \"sex\", \"moral\", \"geld\", \"kennen\", \"reisen\", \"familie\", \"hobbys\", \"filme_serien\", \"essen\"\n")
                append("  \"type\": \"quiz\", // oder \"tot\", \"disc\"\n")
                append("  \"questions\": [\n")
                append("    {\n")
                append("      \"q\": \"Fragetext?\",\n")
                append("      \"options\": [\"Option A\", \"Option B\"]\n")
                append("    }\n")
                append("  ],\n")
                append("  \"pairs\": [\n")
                append("    {\n")
                append("      \"first\": \"Option Links\",\n")
                append("      \"second\": \"Option Rechts\"\n")
                append("    }\n")
                append("  ]\n")
                append("}\n\n")

                append("Anforderungen:\n")
                append("- Erstelle 5 bis 10 abwechslungsreiche Fragen/Paare.\n")
                append("- Achte darauf, dass die IDs und Werte exakt befüllt sind.\n")
                append("- Baue konkrete Witze oder Fragen über ihre Interessen oder vorherige Antworten ein, wenn passend.\n")
                append("- Antworte AUSSCHLIESSLICH mit dem validen JSON-Objekt. Verwende keinen Markdown-Wrapper (wie ```json ... ```) und keine Erklärungen davor oder danach.")
            }

            Log.d(TAG, "Prompt built: $prompt")

            val jsonRequest = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonRequest.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("HTTP error from Gemini: ${response.code} ${response.message}")
                }
                val responseBody = response.body?.string() ?: throw IOException("Empty response from Gemini")
                Log.d(TAG, "Raw Response: $responseBody")

                val jsonResponse = JSONObject(responseBody)
                val textResponse = jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                Log.d(TAG, "Parsed Text: $textResponse")

                val cleanText = textResponse.trim().removeSurrounding("```json", "```").trim()
                val parsedPackJson = JSONObject(cleanText)

                val id = parsedPackJson.getString("id")
                val title = parsedPackJson.getString("title")
                val emoji = parsedPackJson.getString("emoji")
                val cat = parsedPackJson.getString("cat")
                val topic = parsedPackJson.getString("topic")
                val type = parsedPackJson.getString("type")

                val tagsList = mutableListOf<String>()
                val tagsArr = parsedPackJson.optJSONArray("tags")
                if (tagsArr != null) {
                    for (i in 0 until tagsArr.length()) {
                        tagsList.add(tagsArr.getString(i))
                    }
                }

                val questionsList = mutableListOf<Question>()
                val questionsArr = parsedPackJson.optJSONArray("questions")
                if (questionsArr != null) {
                    for (i in 0 until questionsArr.length()) {
                        val qObj = questionsArr.getJSONObject(i)
                        val qText = qObj.getString("q")
                        val optList = mutableListOf<String>()
                        val optArr = qObj.optJSONArray("options")
                        if (optArr != null) {
                            for (j in 0 until optArr.length()) {
                                optList.add(optArr.getString(j))
                            }
                        }
                        questionsList.add(Question(qText, optList))
                    }
                }

                val pairsList = mutableListOf<Pair<String, String>>()
                val pairsArr = parsedPackJson.optJSONArray("pairs")
                if (pairsArr != null) {
                    for (i in 0 until pairsArr.length()) {
                        val pairObj = pairsArr.getJSONObject(i)
                        val first = pairObj.getString("first")
                        val second = pairObj.getString("second")
                        pairsList.add(first to second)
                    }
                }

                val generatedPack = QuestionPack(
                    id = id,
                    title = title,
                    tags = tagsList,
                    cat = cat,
                    topic = topic,
                    type = type,
                    questions = questionsList,
                    pairs = pairsList,
                    emoji = emoji
                )

                // Save generated pack to SharedPreferences and Sync!
                DeveloperDataManager.savePack(context, generatedPack)
                DeveloperDataManager.syncWithHarmonyData()

                generatedPack
            }
        }.onFailure {
            Log.e(TAG, "Failed to generate game: ", it)
        }
    }
}
