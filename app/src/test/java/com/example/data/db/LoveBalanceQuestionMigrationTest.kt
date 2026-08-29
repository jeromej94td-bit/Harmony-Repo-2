package com.example.data.db

import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LoveBalanceQuestionMigrationTest {

    @Test
    fun `migration 8 to 9 shifts existing love balance answer indices without collisions`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "love-balance-question-migration-test"
        context.deleteDatabase(dbName)

        val openHelper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbName)
                .callback(
                    object : SupportSQLiteOpenHelper.Callback(8) {
                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            db.execSQL(
                                """
                                CREATE TABLE answers (
                                    packId TEXT NOT NULL,
                                    questionIndex INTEGER NOT NULL,
                                    answerText TEXT NOT NULL,
                                    timestamp INTEGER NOT NULL,
                                    PRIMARY KEY(packId, questionIndex)
                                )
                                """.trimIndent()
                            )
                            db.execSQL(
                                """
                                CREATE TABLE brain_answer_history (
                                    id TEXT NOT NULL PRIMARY KEY,
                                    packId TEXT,
                                    questionIndex INTEGER
                                )
                                """.trimIndent()
                            )
                        }

                        override fun onUpgrade(
                            db: androidx.sqlite.db.SupportSQLiteDatabase,
                            oldVersion: Int,
                            newVersion: Int
                        ) = Unit
                    }
                )
                .build()
        )

        val db = openHelper.writableDatabase
        db.execSQL(
            "INSERT INTO answers(packId, questionIndex, answerText, timestamp) VALUES ('liebegleichgewicht', 0, 'Alt 0', 10)"
        )
        db.execSQL(
            "INSERT INTO answers(packId, questionIndex, answerText, timestamp) VALUES ('liebegleichgewicht', 1, 'Alt 1', 20)"
        )
        db.execSQL(
            "INSERT INTO answers(packId, questionIndex, answerText, timestamp) VALUES ('anderes_pack', 0, 'Unverändert', 30)"
        )
        db.execSQL(
            "INSERT INTO brain_answer_history(id, packId, questionIndex) VALUES ('history-1', 'liebegleichgewicht', 0)"
        )

        HarmonyDatabase.MIGRATION_8_9.migrate(db)

        db.query(
            "SELECT questionIndex, answerText FROM answers WHERE packId = 'liebegleichgewicht' ORDER BY questionIndex"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
            assertEquals("Alt 0", cursor.getString(1))
            assertTrue(cursor.moveToNext())
            assertEquals(2, cursor.getInt(0))
            assertEquals("Alt 1", cursor.getString(1))
        }

        db.query(
            "SELECT questionIndex FROM answers WHERE packId = 'anderes_pack'"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
        }

        db.query(
            "SELECT questionIndex FROM brain_answer_history WHERE id = 'history-1'"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }

        openHelper.close()
        context.deleteDatabase(dbName)
    }
}
