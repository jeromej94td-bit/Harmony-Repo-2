package com.example.widget

import android.content.Context
import androidx.room.InvalidationTracker
import com.example.data.db.HarmonyDatabase

/** Keeps homescreen memory widgets in sync with changes made inside the app. */
object MemoryWidgetDatabaseObserver {
    @Volatile
    private var installed = false
    private val lock = Any()
    private var observer: InvalidationTracker.Observer? = null

    fun install(context: Context) {
        if (installed) return
        synchronized(lock) {
            if (installed) return
            val appContext = context.applicationContext
            val database = HarmonyDatabase.getInstance(appContext)
            val newObserver = object : InvalidationTracker.Observer("memory_entries") {
                override fun onInvalidated(tables: Set<String>) {
                    MemoryWidgetProvider.refreshAll(appContext)
                }
            }
            database.invalidationTracker.addObserver(newObserver)
            observer = newObserver
            installed = true
        }
    }
}
