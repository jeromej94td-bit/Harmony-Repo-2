package com.example.widget

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MemoryWidgetMigrationTest {
    @Test
    fun `memory widget provider and config activity are present after migration`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val pm = context.packageManager

        val providerClass = Class.forName("com.example.widget.MemoryWidgetProvider")
        val configClass = Class.forName("com.example.widget.MemoryWidgetConfigActivity")

        val provider = pm.getReceiverInfo(
            ComponentName(context, providerClass),
            PackageManager.GET_META_DATA
        )
        val activity = pm.getActivityInfo(ComponentName(context, configClass), 0)

        assertEquals(providerClass.name, provider.name)
        assertEquals(configClass.name, activity.name)
        assertTrue(provider.metaData.containsKey("android.appwidget.provider"))
    }
}
