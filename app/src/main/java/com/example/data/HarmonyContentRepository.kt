package com.example.data

import android.content.Context
import android.util.Log
import com.example.data.model.Category
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import com.example.ui.components.TotImageProvider
import com.example.ui.TranslationCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object HarmonyContentRepository {
    private const val TAG = "HarmonyContentRepo"
    private const val PREFS_NAME = "harmony_content_prefs"
    private const val KEY_CONTENT_VERSION = "cached_content_version"
    private const val CACHE_FILE_NAME = "harmony_content_cache.json"

    // Active dynamically mapped categories and packages loaded in-memory
    private val cachedCategories = mutableListOf<Category>()
    private val cachedPacks = mutableListOf<QuestionPack>()

    fun getCategories(): List<Category> = synchronized(cachedCategories) {
        cachedCategories.toList()
    }

    fun getPacks(): List<QuestionPack> = synchronized(cachedPacks) {
        cachedPacks.toList()
    }

    /**
     * Reads state from Supabase, checks version, syncs if modified, and applies cache fallback.
     * Returns true if sync was successful, false if it used the local cache fallback.
     */
    suspend fun initAndSync(context: Context): Boolean = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting initAndSync...")
        var syncSuccess = false
        try {
            // 1. Fetch remote content state
            val stateJson = makeRequest("harmony_content_state?select=*")
            if (stateJson != null) {
                val stateArray = JSONArray(stateJson)
                if (stateArray.length() > 0) {
                    val stateObj = stateArray.getJSONObject(0)
                    val remoteVersion = stateObj.optString("content_version", "")
                    val localVersion = getLocalContentVersion(context)

                    Log.d(TAG, "Remote content version: $remoteVersion, Local content version: $localVersion")

                    if (remoteVersion.isNotBlank() && remoteVersion != localVersion) {
                        Log.d(TAG, "New content version detected. Triggering full table download.")
                        syncSuccess = fetchAndCacheAllTables(context, remoteVersion)
                    } else {
                        Log.d(TAG, "Content version is up to date. Loading from local cache file.")
                        syncSuccess = true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking remote content state, falling back to local cache: ${e.message}")
        }

        // Apply local cache (or fallback to embedded) based on what's available
        applyCache(context)
        return@withContext syncSuccess
    }

    /**
     * Downloads all 13 tables and commits them atomically into the local file cache.
     */
    private suspend fun fetchAndCacheAllTables(context: Context, remoteVersion: String): Boolean {
        Log.d(TAG, "Downloading all content tables...")
        val tables = mapOf(
            "harmony_game_types" to "harmony_game_types?select=*",
            "harmony_categories" to "harmony_categories?select=*",
            "harmony_topics" to "harmony_topics?select=*",
            "harmony_packages" to "harmony_packages?select=*",
            "harmony_items" to "harmony_items?select=*",
            "harmony_item_options" to "harmony_item_options?select=*",
            "harmony_assets" to "harmony_assets?select=*",
            "harmony_asset_links" to "harmony_asset_links?select=*",
            "harmony_localizations" to "harmony_localizations?select=*",
            "harmony_navigation_nodes" to "harmony_navigation_nodes?select=*",
            "harmony_app_settings" to "harmony_app_settings?select=*",
            "harmony_feature_flags" to "harmony_feature_flags?select=*",
            "harmony_ui_surfaces" to "harmony_ui_surfaces?select=*"
        )

        val fetchedData = mutableMapOf<String, String>()

        for ((key, endpoint) in tables) {
            val response = makeRequest(endpoint)
            if (response == null) {
                Log.e(TAG, "Failed to download table: $key. Aborting sync to keep cache valid and intact.")
                return false
            }
            fetchedData[key] = response
        }

        // Build atomic JSON cache payload
        try {
            val cachePayload = JSONObject()
            cachePayload.put("content_version", remoteVersion)
            for ((key, jsonStr) in fetchedData) {
                cachePayload.put(key, JSONArray(jsonStr))
            }

            val saved = writeCacheAtomically(context, cachePayload.toString())
            if (saved) {
                setLocalContentVersion(context, remoteVersion)
                Log.i(TAG, "Successfully downloaded and atomically cached version $remoteVersion.")
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error packing downloaded tables into JSON cache: ${e.message}")
        }
        return false
    }

    /**
     * Loads the cached tables from file, parses and maps them, and updates the dynamic content layers.
     */
    suspend fun applyCache(context: Context) {
        val cacheFile = File(context.filesDir, CACHE_FILE_NAME)
        if (!cacheFile.exists()) {
            Log.w(TAG, "No cache file exists. Relying on default embedded content fallback.")
            return
        }

        try {
            val cacheContent = cacheFile.readText()
            val cacheObj = JSONObject(cacheContent)
            
            val categoriesJson = cacheObj.optJSONArray("harmony_categories") ?: JSONArray()
            val packagesJson = cacheObj.optJSONArray("harmony_packages") ?: JSONArray()
            val itemsJson = cacheObj.optJSONArray("harmony_items") ?: JSONArray()
            val optionsJson = cacheObj.optJSONArray("harmony_item_options") ?: JSONArray()
            val assetsJson = cacheObj.optJSONArray("harmony_assets") ?: JSONArray()
            val assetLinksJson = cacheObj.optJSONArray("harmony_asset_links") ?: JSONArray()
            val localizationsJson = cacheObj.optJSONArray("harmony_localizations") ?: JSONArray()

            // 1. Map categories
            val newCategories = mutableListOf<Category>()
            for (i in 0 until categoriesJson.length()) {
                val obj = categoriesJson.getJSONObject(i)
                val isActive = obj.optBoolean("is_active", true)
                val isArchived = obj.optBoolean("is_archived", false)
                if (isActive && !isArchived) {
                    val id = obj.optString("slug", obj.optString("id", ""))
                    val name = obj.optString("name", obj.optString("title", ""))
                    val emoji = obj.optString("emoji", "🎯")
                    val tagColorHex = obj.optLong("tag_color_hex", 0xFFFFC46B)
                    if (id.isNotBlank() && name.isNotBlank()) {
                        newCategories.add(Category(id, name, emoji, tagColorHex))
                    }
                }
            }

            // 2. Map localizations/translations dynamically
            TranslationCatalog.clearDynamicTranslations()
            for (i in 0 until localizationsJson.length()) {
                val obj = localizationsJson.getJSONObject(i)
                val german = obj.optString("german_text", obj.optString("original_text", ""))
                val langCode = obj.optString("language_code", "")
                val translation = obj.optString("translated_text", "")
                if (german.isNotBlank() && langCode.isNotBlank() && translation.isNotBlank()) {
                    TranslationCatalog.addDynamicTranslation(german, langCode, translation)
                }
            }

            // 3. Register image assets and links
            val assetMap = mutableMapOf<String, String>() // asset_key -> url
            for (i in 0 until assetsJson.length()) {
                val obj = assetsJson.getJSONObject(i)
                val key = obj.optString("key", obj.optString("id", ""))
                val url = obj.optString("url", obj.optString("path", ""))
                if (key.isNotBlank() && url.isNotBlank()) {
                    val fullUrl = if (url.startsWith("http")) url else getImageUrl(url)
                    assetMap[key] = fullUrl
                }
            }

            // Map item_id -> roles map -> asset_url
            val itemAssets = mutableMapOf<String, MutableMap<String, String>>()
            for (i in 0 until assetLinksJson.length()) {
                val obj = assetLinksJson.getJSONObject(i)
                val itemId = obj.optString("item_id", "")
                val assetKey = obj.optString("asset_key", obj.optString("asset_id", ""))
                val role = obj.optString("role", "")
                if (itemId.isNotBlank() && assetKey.isNotBlank() && role.isNotBlank()) {
                    val url = assetMap[assetKey]
                    if (url != null) {
                        itemAssets.getOrPut(itemId) { mutableMapOf() }[role] = url
                    }
                }
            }

            // Group items by package_id
            val itemsByPackage = mutableMapOf<String, MutableList<JSONObject>>()
            for (i in 0 until itemsJson.length()) {
                val obj = itemsJson.getJSONObject(i)
                val pkgId = obj.optString("package_id", "")
                if (pkgId.isNotBlank()) {
                    itemsByPackage.getOrPut(pkgId) { mutableListOf() }.add(obj)
                }
            }

            // Group options by item_id
            val optionsByItem = mutableMapOf<String, MutableList<JSONObject>>()
            for (i in 0 until optionsJson.length()) {
                val obj = optionsJson.getJSONObject(i)
                val itemId = obj.optString("item_id", "")
                if (itemId.isNotBlank()) {
                    optionsByItem.getOrPut(itemId) { mutableListOf() }.add(obj)
                }
            }

            // 4. Map packages
            val newPacks = mutableListOf<QuestionPack>()
            for (i in 0 until packagesJson.length()) {
                val obj = packagesJson.getJSONObject(i)
                val isActive = obj.optBoolean("is_active", true)
                val isArchived = obj.optBoolean("is_archived", false)
                if (isActive && !isArchived) {
                    val id = obj.optString("id", obj.optString("slug", ""))
                    val title = obj.optString("title", "")
                    val topicId = obj.optString("topic_id", "supabase")
                    val gameType = obj.optString("game_type_key", "tot")
                    val metadata = obj.optString("metadata", "")
                    val emoji = try {
                        if (metadata.startsWith("{")) JSONObject(metadata).optString("emoji", "🎁") else "🎁"
                    } catch (_: Exception) {
                        "🎁"
                    }

                    if (id.isNotBlank() && title.isNotBlank()) {
                        val items = itemsByPackage[id] ?: emptyList()
                        // Sort items by sort_order or item_index
                        val sortedItems = items.sortedBy { it.optInt("sort_order", it.optInt("item_index", 0)) }

                        val questions = mutableListOf<Question>()
                        val pairs = mutableListOf<Pair<String, String>>()

                        for (itemObj in sortedItems) {
                            val itemId = itemObj.optString("id", "")
                            
                            if (gameType == "tot") {
                                val left = itemObj.optString("left_text", "")
                                val right = itemObj.optString("right_text", "")
                                if (left.isNotBlank() && right.isNotBlank()) {
                                    pairs.add(left to right)
                                    // Set dynamic asset override URLs if they exist
                                    val rolesMap = itemAssets[itemId]
                                    rolesMap?.get("left")?.let { url ->
                                        DeveloperDataManager._imageOverrides[left] = url
                                        TotImageProvider.setCustomImage(left, url)
                                    }
                                    rolesMap?.get("right")?.let { url ->
                                        DeveloperDataManager._imageOverrides[right] = url
                                        TotImageProvider.setCustomImage(right, url)
                                    }
                                }
                            } else if (gameType == "quiz") {
                                val qText = itemObj.optString("prompt", itemObj.optString("text", ""))
                                if (qText.isNotBlank()) {
                                    val itemOpts = optionsByItem[itemId] ?: emptyList()
                                    val sortedOpts = itemOpts.sortedBy { it.optInt("option_index", 0) }
                                    val optionsList = sortedOpts.map { it.optString("option_text", it.optString("text", "")) }
                                    questions.add(Question(q = qText, options = optionsList))
                                }
                            } else {
                                // Default/disc/fallback
                                val qText = itemObj.optString("prompt", itemObj.optString("text", ""))
                                if (qText.isNotBlank()) {
                                    questions.add(Question(q = qText, options = emptyList()))
                                }
                            }
                        }

                        newPacks.add(
                            QuestionPack(
                                id = id,
                                title = title,
                                tags = listOf("supabase"),
                                cat = obj.optString("category_id", "tot"),
                                topic = topicId,
                                type = gameType,
                                questions = questions,
                                pairs = pairs,
                                emoji = emoji
                            )
                        )
                    }
                }
            }

            synchronized(cachedCategories) {
                cachedCategories.clear()
                cachedCategories.addAll(newCategories)
            }
            synchronized(cachedPacks) {
                cachedPacks.clear()
                cachedPacks.addAll(newPacks)
            }

            // Sync with active UI models layer
            withContext(Dispatchers.Main) {
                // Add categories
                DeveloperDataManager._customCategories.removeAll { cc -> newCategories.any { nc -> nc.id == cc.id } }
                DeveloperDataManager._customCategories.addAll(newCategories)

                // Add packages
                DeveloperDataManager._customPacks.removeAll { cp -> newPacks.any { np -> np.id == cp.id } }
                DeveloperDataManager._customPacks.addAll(newPacks)

                DeveloperDataManager.syncWithHarmonyData()
                Log.d(TAG, "Successfully applied cached content layers: ${newCategories.size} categories, ${newPacks.size} packs.")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error applying cached content to app layers: ${e.message}", e)
        }
    }

    private fun makeRequest(endpoint: String): String? {
        val projectId = SupabaseClientProvider.projectId
        val anonKey = SupabaseClientProvider.anonKey
        if (projectId.isBlank() || anonKey.isBlank()) return null

        val urlString = "${SupabaseClientProvider.baseUrl}/$endpoint"
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("apikey", anonKey)
            conn.setRequestProperty("Authorization", "Bearer $anonKey")
            conn.setRequestProperty("Accept", "application/json")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == 200) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                Log.e(TAG, "HTTP ${conn.responseCode} on $urlString")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during request to $urlString: ${e.message}")
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun writeCacheAtomically(context: Context, jsonString: String): Boolean {
        return try {
            val cacheFile = File(context.filesDir, CACHE_FILE_NAME)
            val tempFile = File(context.filesDir, "$CACHE_FILE_NAME.tmp")
            tempFile.writeText(jsonString)
            if (tempFile.exists()) {
                if (cacheFile.exists()) {
                    cacheFile.delete()
                }
                tempFile.renameTo(cacheFile)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write cache atomically", e)
            false
        }
    }

    private fun getImageUrl(imageKey: String): String {
        val projectId = SupabaseClientProvider.projectId
        return "https://$projectId.supabase.co/storage/v1/object/public/tot_images/$imageKey"
    }

    private fun getLocalContentVersion(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CONTENT_VERSION, "") ?: ""
    }

    private fun setLocalContentVersion(context: Context, version: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CONTENT_VERSION, version)
            .apply()
    }
}
