package com.example.data.db

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BrainMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        HarmonyDatabase::class.java
    )

    @Test
    fun `migration from version 5 creates brain tables successfully`() {
        helper.createDatabase(TEST_DB, 5).apply {
            execSQL(
                """
                INSERT INTO profiles(
                    id, userName, partnerName, startDate, simulatorEnabled, userAvatarPath, partnerAvatarPath
                ) VALUES (1, 'Jerome', 'Alex', 42, 1, NULL, NULL)
                """.trimIndent()
            )
            execSQL(
                """
                INSERT INTO answers(packId, questionIndex, answerText, timestamp)
                VALUES ('cat_travel_1', 0, 'Italien', 1000)
                """.trimIndent()
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(
            TEST_DB,
            6,
            true,
            HarmonyDatabase.MIGRATION_5_6
        )

        // Verify profile and answers still exist
        migrated.query("SELECT COUNT(*) FROM profiles").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }
        migrated.query("SELECT COUNT(*) FROM answers").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }

        // Verify new tables can be written to
        migrated.execSQL(
            """
            INSERT INTO brain_answer_history(
                id, packId, questionId, questionIndex, questionText, category, topic, contentType,
                answerPersonA, answerPersonB, createdAt, liked, disliked, skipped, source,
                generatedContentId, metadataJson
            ) VALUES (
                'test-id-1', 'cat_travel_1', 'cat_travel_1-0', 0, 'Wo wollen wir hin?', 'Reisen', 'Urlaub', 'QUESTION',
                'Italien', 'Japan', 1000, 0, 0, 0, 'STATIC', NULL, NULL
            )
            """.trimIndent()
        )

        migrated.query("SELECT COUNT(*) FROM brain_answer_history").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }

        migrated.execSQL(
            """
            INSERT INTO brain_preferences(
                scope, tag, score, confidence, engagement, positiveSignals, negativeSignals,
                saturation, lastSeenAt, lastUsedForContentAt, updatedAt
            ) VALUES (
                'COUPLE', 'italien', 0.85, 0.5, 0.4, 2, 0, 0.1, 1000, NULL, 1000
            )
            """.trimIndent()
        )

        migrated.query("SELECT COUNT(*) FROM brain_preferences").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }

        migrated.close()
    }

    private companion object {
        const val TEST_DB = "brain-migration-v5-v6-test"
    }
}
