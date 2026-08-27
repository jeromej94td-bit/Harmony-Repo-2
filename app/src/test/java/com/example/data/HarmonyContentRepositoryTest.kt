package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.AppLanguage
import com.example.ui.TranslationCatalog
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HarmonyContentRepositoryTest {

    private lateinit var context: Context
    private val cacheFileName = "harmony_content_cache.json"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Clean up previous files
        File(context.filesDir, cacheFileName).delete()
        File(context.filesDir, "$cacheFileName.tmp").delete()
        TranslationCatalog.clearDynamicTranslations()
    }

    /**
     * Test Case 1: Verification of sorting and mapping logic from cached tables JSON.
     */
    @Test
    fun testCategoryAndPackageMappingAndSorting() {
        val cachePayload = JSONObject().apply {
            put("content_version", "v1.2.3")
            
            // Active Categories sorted by sort_order
            put("harmony_categories", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", "cat_2")
                    put("slug", "cat_2")
                    put("name", "Category Two")
                    put("is_active", true)
                    put("is_archived", false)
                    put("tag_color_hex", 0xFF00FF00)
                })
                put(JSONObject().apply {
                    put("id", "cat_1")
                    put("slug", "cat_1")
                    put("name", "Category One")
                    put("is_active", true)
                    put("is_archived", false)
                    put("tag_color_hex", 0xFFFF0000)
                })
            })

            // Packages (one tot, one quiz)
            put("harmony_packages", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", "pkg_tot")
                    put("slug", "pkg_tot")
                    put("title", "Tot Package")
                    put("category_id", "cat_1")
                    put("topic_id", "topic_test")
                    put("game_type_key", "tot")
                    put("is_active", true)
                    put("is_archived", false)
                    put("metadata", "{\"emoji\":\"🐨\"}")
                })
                put(JSONObject().apply {
                    put("id", "pkg_quiz")
                    put("slug", "pkg_quiz")
                    put("title", "Quiz Package")
                    put("category_id", "cat_2")
                    put("topic_id", "topic_test")
                    put("game_type_key", "quiz")
                    put("is_active", true)
                    put("is_archived", false)
                    put("metadata", "{\"emoji\":\"🦁\"}")
                })
            })

            // Items (tot pair and quiz question)
            put("harmony_items", JSONArray().apply {
                // Pair items for tot
                put(JSONObject().apply {
                    put("id", "item_tot_1")
                    put("package_id", "pkg_tot")
                    put("left_text", "Frühstück im Bett")
                    put("right_text", "Mitternachtssnack")
                    put("sort_order", 1)
                })
                // Question item for quiz
                put(JSONObject().apply {
                    put("id", "item_quiz_1")
                    put("package_id", "pkg_quiz")
                    put("prompt", "Wie verbringst du ein perfektes Wochenende?")
                    put("sort_order", 1)
                })
            })

            // Quiz options
            put("harmony_item_options", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", "opt_1")
                    put("item_id", "item_quiz_1")
                    put("text", "Ausschlafen")
                    put("option_index", 1)
                })
                put(JSONObject().apply {
                    put("id", "opt_2")
                    put("item_id", "item_quiz_1")
                    put("text", "Früh aufstehen")
                    put("option_index", 2)
                })
            })

            put("harmony_assets", JSONArray())
            put("harmony_asset_links", JSONArray())
            put("harmony_localizations", JSONArray())
        }

        // Save mock cache to file
        val cacheFile = File(context.filesDir, cacheFileName)
        cacheFile.writeText(cachePayload.toString())

        // Apply cache
        kotlinx.coroutines.runBlocking { HarmonyContentRepository.applyCache(context) }

        // Verify mapped content in memory
        val packs = HarmonyContentRepository.getPacks()
        val categories = HarmonyContentRepository.getCategories()

        assertEquals(2, categories.size)
        assertEquals(2, packs.size)

        // Verify "tot" package mappings
        val totPack = packs.find { it.id == "pkg_tot" }
        assertNotNull(totPack)
        assertEquals("Tot Package", totPack!!.title)
        assertEquals("tot", totPack.type)
        assertEquals("🐨", totPack.emoji)
        assertEquals(1, totPack.pairs.size)
        assertEquals("Frühstück im Bett", totPack.pairs[0].first)
        assertEquals("Mitternachtssnack", totPack.pairs[0].second)

        // Verify "quiz" package mappings with options sorted by index
        val quizPack = packs.find { it.id == "pkg_quiz" }
        assertNotNull(quizPack)
        assertEquals("Quiz Package", quizPack!!.title)
        assertEquals("quiz", quizPack.type)
        assertEquals("🦁", quizPack.emoji)
        assertEquals(1, quizPack.questions.size)
        assertEquals("Wie verbringst du ein perfektes Wochenende?", quizPack.questions[0].q)
        assertEquals(2, quizPack.questions[0].options.size)
        assertEquals("Ausschlafen", quizPack.questions[0].options[0])
        assertEquals("Früh aufstehen", quizPack.questions[0].options[1])
    }

    /**
     * Test Case 2: Verification of asset links and role mapping for This-or-That (tot).
     */
    @Test
    fun testAssetLinksResolutionForTot() {
        val cachePayload = JSONObject().apply {
            put("content_version", "v1.2.3")
            put("harmony_categories", JSONArray())
            put("harmony_packages", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", "pkg_tot_asset")
                    put("title", "Tot Asset Package")
                    put("category_id", "cat_1")
                    put("game_type_key", "tot")
                    put("is_active", true)
                    put("is_archived", false)
                })
            })

            put("harmony_items", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", "item_tot_asset_1")
                    put("package_id", "pkg_tot_asset")
                    put("left_text", "Links")
                    put("right_text", "Rechts")
                    put("sort_order", 1)
                })
            })

            // Assets table
            put("harmony_assets", JSONArray().apply {
                put(JSONObject().apply {
                    put("key", "img_left_key")
                    put("url", "https://example.com/left.png")
                })
                put(JSONObject().apply {
                    put("key", "img_right_key")
                    put("url", "right.png") // Relative path test
                })
            })

            // Asset Links linking item to asset key with roles left/right
            put("harmony_asset_links", JSONArray().apply {
                put(JSONObject().apply {
                    put("item_id", "item_tot_asset_1")
                    put("asset_key", "img_left_key")
                    put("role", "left")
                })
                put(JSONObject().apply {
                    put("item_id", "item_tot_asset_1")
                    put("asset_key", "img_right_key")
                    put("role", "right")
                })
            })

            put("harmony_item_options", JSONArray())
            put("harmony_localizations", JSONArray())
        }

        val cacheFile = File(context.filesDir, cacheFileName)
        cacheFile.writeText(cachePayload.toString())

        // Set default project ID in Supabase provider for build Urls
        SupabaseClientProvider.init("yepluyipizbbrgoffqdq", "sb_anon")

        kotlinx.coroutines.runBlocking { HarmonyContentRepository.applyCache(context) }

        // Verify dynamic overrides are registered
        assertEquals("https://example.com/left.png", DeveloperDataManager.imagePathFor("Links"))
        assertTrue(DeveloperDataManager.imagePathFor("Rechts")!!.contains("yepluyipizbbrgoffqdq.supabase.co"))
    }

    /**
     * Test Case 3: Dynamic translation resolution from harmony_localizations.
     */
    @Test
    fun testDynamicLocalizationMapping() {
        val cachePayload = JSONObject().apply {
            put("content_version", "v1.2.3")
            put("harmony_categories", JSONArray())
            put("harmony_packages", JSONArray())
            put("harmony_items", JSONArray())
            put("harmony_item_options", JSONArray())
            put("harmony_assets", JSONArray())
            put("harmony_asset_links", JSONArray())
            
            // Dynamic translations
            put("harmony_localizations", JSONArray().apply {
                put(JSONObject().apply {
                    put("original_text", "Frühstück im Bett")
                    put("language_code", "en")
                    put("translated_text", "Breakfast in Bed")
                })
                put(JSONObject().apply {
                    put("original_text", "Frühstück im Bett")
                    put("language_code", "it")
                    put("translated_text", "Colazione a letto")
                })
            })
        }

        val cacheFile = File(context.filesDir, cacheFileName)
        cacheFile.writeText(cachePayload.toString())

        kotlinx.coroutines.runBlocking { HarmonyContentRepository.applyCache(context) }

        // Verify lookups inside TranslationCatalog
        assertEquals("Breakfast in Bed", TranslationCatalog.exact("Frühstück im Bett", AppLanguage.ENGLISH))
        assertEquals("Colazione a letto", TranslationCatalog.exact("Frühstück im Bett", AppLanguage.ITALIAN))
        
        // Missing translation fallback test (falls back to original German text)
        assertEquals("Frühstück im Bett", TranslationCatalog.exact("Frühstück im Bett", AppLanguage.JAPANESE))
    }

    /**
     * Test Case 4: Package deactivation check (is_active = false or is_archived = true).
     */
    @Test
    fun testDeactivatedPackageIsSkipped() {
        val cachePayload = JSONObject().apply {
            put("content_version", "v1.2.3")
            put("harmony_categories", JSONArray())
            put("harmony_packages", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", "pkg_active")
                    put("title", "Active Package")
                    put("game_type_key", "tot")
                    put("is_active", true)
                    put("is_archived", false)
                })
                put(JSONObject().apply {
                    put("id", "pkg_inactive")
                    put("title", "Inactive Package")
                    put("game_type_key", "tot")
                    put("is_active", false)
                    put("is_archived", false)
                })
                put(JSONObject().apply {
                    put("id", "pkg_archived")
                    put("title", "Archived Package")
                    put("game_type_key", "tot")
                    put("is_active", true)
                    put("is_archived", true)
                })
            })
            put("harmony_items", JSONArray())
            put("harmony_item_options", JSONArray())
            put("harmony_assets", JSONArray())
            put("harmony_asset_links", JSONArray())
            put("harmony_localizations", JSONArray())
        }

        val cacheFile = File(context.filesDir, cacheFileName)
        cacheFile.writeText(cachePayload.toString())

        kotlinx.coroutines.runBlocking { HarmonyContentRepository.applyCache(context) }

        val packs = HarmonyContentRepository.getPacks()
        assertEquals(1, packs.size)
        assertEquals("pkg_active", packs[0].id)
    }

    /**
     * Test Case 5: Verification of offline start & corrupted/unparseable file fallbacks.
     */
    @Test
    fun testCorruptedCacheAndOfflineFallbacks() {
        // Create corrupted cache file
        val cacheFile = File(context.filesDir, cacheFileName)
        cacheFile.writeText("invalid{json: corrupted")

        // Applying cache should complete gracefully without throwing exceptions
        try {
            kotlinx.coroutines.runBlocking { HarmonyContentRepository.applyCache(context) }
        } catch (e: Exception) {
            fail("Applying a corrupted cache must not crash the application.")
        }

        // Verify repository keeps list empty (and drops down to built-in fallbacks)
        assertTrue(HarmonyContentRepository.getPacks().isEmpty())
        assertTrue(HarmonyContentRepository.getCategories().isEmpty())
    }
}
