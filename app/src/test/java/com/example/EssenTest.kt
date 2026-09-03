package com.example

import org.junit.Test
import com.example.data.model.HarmonyPacksData
import com.example.data.CATALOG_PACKS
import com.example.util.LanguageManager
import com.example.data.CuisinePackInstaller
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@RunWith(AndroidJUnit4::class)
class EssenTest {
    @Test
    fun checkDuplicates() {
        val list = HarmonyPacksData.CATALOG_PACKS.filter { it.topic == "essen" }
        val ids = list.map { it.id }
        val dups = ids.groupBy { it }.filter { it.value.size > 1 }
        println("DUPLICATES FOUND: $dups")
        assert(dups.isEmpty()) { "Found duplicates: $dups" }
    }
}
