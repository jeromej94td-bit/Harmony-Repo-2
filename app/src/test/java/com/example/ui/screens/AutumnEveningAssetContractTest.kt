package com.example.ui.screens

import java.io.File
import javax.imageio.ImageIO
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AutumnEveningAssetContractTest {

    @Test
    fun `all autumn choices ship as high resolution card images`() {
        val appRoot = sequenceOf(File("."), File(".."))
            .first { File(it, "src/main/res").isDirectory }
        val drawableRoot = File(appRoot, "src/main/res/drawable-nodpi")
        val names = listOf("story", "drink", "snack", "nook", "sound", "scent")
            .flatMap { group -> (1..4).map { index -> "autumn_${group}_%02d.png".format(index) } }

        names.forEach { name ->
            val file = File(drawableRoot, name)
            assertTrue("Missing autumn artwork: $name", file.isFile)

            val image = ImageIO.read(file)
            assertNotNull("Invalid PNG: $name", image)
            assertTrue("Artwork is too small: $name", image.width >= 768 && image.height >= 768)

            val ratio = image.width.toDouble() / image.height.toDouble()
            assertTrue("Artwork has an unsuitable card ratio: $name ($ratio)", ratio in 0.60..1.05)
        }
    }
}
