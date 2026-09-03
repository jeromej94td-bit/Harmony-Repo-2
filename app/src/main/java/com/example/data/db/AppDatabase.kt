package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AnswerEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CoupleStatsEntity
import com.example.data.model.MomentEntity
import com.example.data.model.MemoryCategoryEntity
import com.example.data.model.MemoryEntryEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SharedPicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ProfileEntity)
}

@Dao
interface AnswerDao {
    @Query("SELECT * FROM answers")
    fun getAllAnswers(): Flow<List<AnswerEntity>>

    @Query("SELECT * FROM answers")
    suspend fun getAllAnswersDirect(): List<AnswerEntity>

    @Query("SELECT * FROM answers WHERE packId = :packId")
    fun getAnswersForPack(packId: String): Flow<List<AnswerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: AnswerEntity)

    @Query("DELETE FROM answers WHERE packId = :packId")
    suspend fun deleteAnswersForPack(packId: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)
}

@Dao
interface SharedPicDao {
    @Query("SELECT * FROM shared_pics ORDER BY timestamp DESC")
    fun getAllPics(): Flow<List<SharedPicEntity>>

    @Query("SELECT * FROM shared_pics WHERE selectedForWidget = 1 ORDER BY timestamp DESC")
    suspend fun getWidgetPics(): List<SharedPicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPic(pic: SharedPicEntity): Long

    @Update
    suspend fun updatePic(pic: SharedPicEntity)
}

@Dao
interface MomentDao {
    @Query("SELECT * FROM moments ORDER BY timestamp DESC")
    fun getAllMoments(): Flow<List<MomentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoment(moment: MomentEntity)

    @Query("DELETE FROM moments WHERE id = :id")
    suspend fun deleteMoment(id: Long)
}

@Dao
interface CoupleStatsDao {
    @Query("SELECT * FROM couple_stats WHERE id = 1 LIMIT 1")
    fun getStats(): Flow<CoupleStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: CoupleStatsEntity)
}

@Database(
    entities = [
        ProfileEntity::class,
        AnswerEntity::class,
        ChatMessageEntity::class,
        SharedPicEntity::class,
        MomentEntity::class,
        CoupleStatsEntity::class,
        MemoryCategoryEntity::class,
        MemoryEntryEntity::class],
    version = 9,
    exportSchema = true
)
abstract class HarmonyDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun answerDao(): AnswerDao
    abstract fun chatDao(): ChatDao
    abstract fun sharedPicDao(): SharedPicDao
    abstract fun momentDao(): MomentDao
    abstract fun coupleStatsDao(): CoupleStatsDao
    abstract fun memoryDao(): MemoryDao
    companion object {
        @Volatile
        private var INSTANCE: HarmonyDatabase? = null

        fun getInstance(context: Context): HarmonyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HarmonyDatabase::class.java,
                    "harmony_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE profiles ADD COLUMN userAvatarPath TEXT")
                db.execSQL("ALTER TABLE profiles ADD COLUMN partnerAvatarPath TEXT")
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN imagePath TEXT")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS shared_pics (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        filePath TEXT NOT NULL,
                        caption TEXT NOT NULL,
                        addedBy TEXT NOT NULL,
                        target TEXT NOT NULL,
                        status TEXT NOT NULL,
                        selectedForWidget INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        internal val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS memory_categories (
                        id TEXT NOT NULL PRIMARY KEY,
                        systemKey TEXT,
                        customName TEXT,
                        colorKey TEXT NOT NULL,
                        iconKey TEXT NOT NULL,
                        sortOrder INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS memory_entries (
                        id TEXT NOT NULL PRIMARY KEY,
                        categoryId TEXT NOT NULL,
                        kind TEXT NOT NULL,
                        title TEXT NOT NULL,
                        body TEXT,
                        url TEXT,
                        previewTitle TEXT,
                        previewDescription TEXT,
                        previewImageUrl TEXT,
                        previewSiteName TEXT,
                        previewFetchedAt INTEGER,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        completedAt INTEGER,
                        FOREIGN KEY(categoryId) REFERENCES memory_categories(id) ON UPDATE NO ACTION ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_memory_entries_categoryId ON memory_entries(categoryId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_memory_entries_completedAt ON memory_entries(completedAt)")
            }
        }

        internal val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE memory_categories ADD COLUMN isVisible INTEGER NOT NULL DEFAULT 1"
                )
            }
        }

        internal val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS brain_interests (name TEXT NOT NULL PRIMARY KEY, category TEXT NOT NULL, confidence TEXT NOT NULL, reason TEXT NOT NULL, timestamp INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS brain_suggestions (id TEXT NOT NULL PRIMARY KEY, title TEXT NOT NULL, description TEXT NOT NULL, category TEXT NOT NULL, matchReason TEXT NOT NULL, feedback TEXT NOT NULL, timestamp INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS brain_questions (id TEXT NOT NULL PRIMARY KEY, text TEXT NOT NULL, category TEXT NOT NULL, difficulty TEXT NOT NULL, answered INTEGER NOT NULL, answerText TEXT, timestamp INTEGER NOT NULL)"
                )
            }
        }

        internal val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `brain_answer_history` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `packId` TEXT,
                        `questionId` TEXT NOT NULL,
                        `questionIndex` INTEGER,
                        `questionText` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `topic` TEXT,
                        `contentType` TEXT NOT NULL,
                        `answerPersonA` TEXT,
                        `answerPersonB` TEXT,
                        `createdAt` INTEGER NOT NULL,
                        `liked` INTEGER NOT NULL,
                        `disliked` INTEGER NOT NULL,
                        `skipped` INTEGER NOT NULL,
                        `source` TEXT NOT NULL,
                        `generatedContentId` TEXT,
                        `metadataJson` TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_answer_history_questionId` ON `brain_answer_history` (`questionId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_answer_history_category` ON `brain_answer_history` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_answer_history_topic` ON `brain_answer_history` (`topic`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_answer_history_createdAt` ON `brain_answer_history` (`createdAt`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `brain_preferences` (
                        `scope` TEXT NOT NULL,
                        `tag` TEXT NOT NULL,
                        `score` REAL NOT NULL,
                        `confidence` REAL NOT NULL,
                        `engagement` REAL NOT NULL,
                        `positiveSignals` INTEGER NOT NULL,
                        `negativeSignals` INTEGER NOT NULL,
                        `saturation` REAL NOT NULL,
                        `lastSeenAt` INTEGER NOT NULL,
                        `lastUsedForContentAt` INTEGER,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`scope`, `tag`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_preferences_scope_score` ON `brain_preferences` (`scope`, `score`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `brain_interactions` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `contentId` TEXT NOT NULL,
                        `contentType` TEXT NOT NULL,
                        `action` TEXT NOT NULL,
                        `category` TEXT,
                        `topic` TEXT,
                        `personScope` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_interactions_contentId` ON `brain_interactions` (`contentId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_interactions_category` ON `brain_interactions` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_interactions_createdAt` ON `brain_interactions` (`createdAt`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `brain_memory_facts` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `factText` TEXT NOT NULL,
                        `category` TEXT,
                        `personScope` TEXT NOT NULL,
                        `confidence` REAL NOT NULL,
                        `importance` REAL NOT NULL,
                        `sourceAnswerIdsJson` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `lastUsedAt` INTEGER
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_memory_facts_category` ON `brain_memory_facts` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_memory_facts_personScope` ON `brain_memory_facts` (`personScope`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `brain_generated_content` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `contentType` TEXT NOT NULL,
                        `category` TEXT,
                        `topic` TEXT,
                        `title` TEXT,
                        `normalizedText` TEXT NOT NULL,
                        `payloadJson` TEXT NOT NULL,
                        `sourceModel` TEXT,
                        `promptVersion` TEXT,
                        `status` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `firstShownAt` INTEGER,
                        `lastShownAt` INTEGER,
                        `playedCount` INTEGER NOT NULL,
                        `likeCount` INTEGER NOT NULL,
                        `dislikeCount` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_generated_content_contentType` ON `brain_generated_content` (`contentType`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_generated_content_normalizedText` ON `brain_generated_content` (`normalizedText`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_generated_content_status` ON `brain_generated_content` (`status`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `brain_pending_generation` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `mode` TEXT NOT NULL,
                        `query` TEXT NOT NULL,
                        `contextJson` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `lastAttemptAt` INTEGER,
                        `retryCount` INTEGER NOT NULL,
                        `lastError` TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_brain_pending_generation_status` ON `brain_pending_generation` (`status`)")
            }
        }

        internal val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN audioPath TEXT")
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN audioDurationSeconds INTEGER NOT NULL DEFAULT 0")
            }
        }

        internal val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE moments ADD COLUMN imagePathsJson TEXT NOT NULL DEFAULT '[]'")
            }
        }

        internal val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // The new visual question is inserted at index 0. Move existing answers
                // out of the primary-key range first, then shift them back by +1.
                db.execSQL(
                    "UPDATE answers SET questionIndex = questionIndex + 1000 WHERE packId = 'liebegleichgewicht'"
                )
                db.execSQL(
                    "UPDATE answers SET questionIndex = questionIndex - 999 WHERE packId = 'liebegleichgewicht'"
                )
                db.execSQL(
                    "UPDATE brain_answer_history SET questionIndex = questionIndex + 1 " +
                        "WHERE packId = 'liebegleichgewicht' AND questionIndex IS NOT NULL"
                )
            }
        }
    }
}
