package com.example.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TotImageSourcePolicyTest {

    @Test
    fun `generated export wins over bundled installer image`() {
        val generated = setOf("Klassisch Solitär")

        assertFalse(
            TotImageSourcePolicy.shouldUseBundledInstallerImage(
                option = "Klassisch Solitär",
                generatedOptionKeys = generated
            )
        )
    }

    @Test
    fun `generated export matching is case insensitive`() {
        val generated = setOf("klassisch solitär")

        assertFalse(
            TotImageSourcePolicy.shouldUseBundledInstallerImage(
                option = "Klassisch Solitär",
                generatedOptionKeys = generated
            )
        )
    }

    @Test
    fun `refreshed compiled ring image wins over legacy drive image`() {
        assertFalse(
            TotImageSourcePolicy.shouldUseBundledInstallerImage(
                option = "Vintage verspielt",
                generatedOptionKeys = emptySet(),
                preferCompiledResource = true
            )
        )
    }

    @Test
    fun `untouched legacy asset remains available as fallback`() {
        assertTrue(
            TotImageSourcePolicy.shouldUseBundledInstallerImage(
                option = "Ovaler Diamant",
                generatedOptionKeys = emptySet(),
                preferCompiledResource = false
            )
        )
    }
}
