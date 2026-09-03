package com.example.ui.introspection

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class IntrospectionGoldenMasterAudioContractTest {
    @Test
    fun `build bundles the verified old-main narrator audio without replacing Repo 2 source assets`() {
        val moduleRoot = sequenceOf(File("."), File(".."))
            .first { File(it, "build.gradle.kts").exists() && File(it, "src/main/java").exists() }
        val gradle = File(moduleRoot, "build.gradle.kts").readText()
        val media = File(
            moduleRoot,
            "src/main/java/com/example/ui/introspection/IntrospectionMediaController.kt"
        ).readText()

        val assets = listOf(
            Triple(
                "introspection_animal_golden.mp3",
                "66118d56ba8e038f36b687a22dd6e7d945391622d9133da4bb469d5cf5dc74a5",
                2_320_970
            ),
            Triple(
                "introspection_color_golden.mp3",
                "840a4943e15eb5a3c10a136124de1f446c6ffbac1a40e250943d7a0cb76f41e2",
                1_949_822
            ),
            Triple(
                "introspection_reveal_golden.mp3",
                "ce220eab86bbf679e90b36842d5a0d75529d1432e281f9010822d5bb0a46b7de",
                2_159_220
            ),
            Triple(
                "introspection_water_golden.mp3",
                "80c13ee1070157b62f72c2d2bd77f08d66a48303f54b165628f2b987b7c99bf1",
                2_923_458
            )
        )

        assertTrue(gradle.contains("SyncIntrospectionGoldenMasterAudioTask"))
        assertTrue(gradle.contains("harmony-static-assets/introspection"))
        assertTrue(gradle.contains("generated/goldenMasterIntrospectionRes"))
        assertTrue(gradle.contains("syncIntrospectionGoldenMasterAudio"))

        assets.forEach { (name, sha256, size) ->
            assertTrue("Missing generated golden resource $name", gradle.contains(name))
            assertTrue("Missing SHA-256 contract for $name", gradle.contains(sha256))
            assertTrue("Missing size contract for $name", gradle.contains(size.toString()))
        }

        assertTrue(media.contains("R.raw.introspection_color_golden"))
        assertTrue(media.contains("R.raw.introspection_animal_golden"))
        assertTrue(media.contains("R.raw.introspection_water_golden"))
        assertTrue(media.contains("R.raw.introspection_reveal_golden"))

        // Existing source resources remain in place; the Golden Master is an additive build resource.
        assertTrue(File(moduleRoot, "src/main/res/raw/introspection_color.mp3").exists())
        assertTrue(File(moduleRoot, "src/main/res/raw/introspection_animal.mp3").exists())
        assertTrue(File(moduleRoot, "src/main/res/raw/introspection_water.mp3").exists())
        assertTrue(File(moduleRoot, "src/main/res/raw/introspection_reveal.mp3").exists())
    }
}
