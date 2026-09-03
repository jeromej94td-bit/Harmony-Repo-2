import java.io.File
import java.net.URI
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import org.gradle.api.GradleException

/**
 * Additive Golden-Master narrator restore for "Tauche ins Unterbewusstsein".
 *
 * The current Repo-2 narrator source files remain untouched. During preBuild this script
 * materializes four additional *_golden.mp3 resources from Harmony's own Supabase static
 * asset bucket, verifies their exact byte size and SHA-256, and only then exposes them to
 * Android resource processing. The old Harmony-App repository is not needed at runtime or
 * build time.
 */
data class IntrospectionGoldenAudioAsset(
    val fileName: String,
    val remotePath: String,
    val expectedSize: Long,
    val expectedSha256: String
)

val introspectionGoldenAudioAssets = listOf(
    IntrospectionGoldenAudioAsset(
        fileName = "introspection_animal_golden.mp3",
        remotePath = "introspection/introspection_animal.mp3",
        expectedSize = 2_320_970L,
        expectedSha256 = "66118d56ba8e038f36b687a22dd6e7d945391622d9133da4bb469d5cf5dc74a5"
    ),
    IntrospectionGoldenAudioAsset(
        fileName = "introspection_color_golden.mp3",
        remotePath = "introspection/introspection_color.mp3",
        expectedSize = 1_949_822L,
        expectedSha256 = "840a4943e15eb5a3c10a136124de1f446c6ffbac1a40e250943d7a0cb76f41e2"
    ),
    IntrospectionGoldenAudioAsset(
        fileName = "introspection_reveal_golden.mp3",
        remotePath = "introspection/introspection_reveal.mp3",
        expectedSize = 2_159_220L,
        expectedSha256 = "ce220eab86bbf679e90b36842d5a0d75529d1432e281f9010822d5bb0a46b7de"
    ),
    IntrospectionGoldenAudioAsset(
        fileName = "introspection_water_golden.mp3",
        remotePath = "introspection/introspection_water.mp3",
        expectedSize = 2_923_458L,
        expectedSha256 = "80c13ee1070157b62f72c2d2bd77f08d66a48303f54b165628f2b987b7c99bf1"
    )
)

val introspectionGoldenAudioBaseUrl =
    "https://rspgnonlpkxdudbjxnrl.supabase.co/storage/v1/object/public/harmony-static-assets/introspection"
val introspectionGoldenRawDir = file("src/main/res/raw")

fun sha256(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().buffered().use { input ->
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            if (read > 0) digest.update(buffer, 0, read)
        }
    }
    return digest.digest().joinToString(separator = "") { "%02x".format(it) }
}

fun verifyGoldenAudio(file: File, asset: IntrospectionGoldenAudioAsset) {
    if (!file.isFile) {
        throw GradleException("Golden narrator audio is missing: ${asset.fileName}")
    }
    if (file.length() != asset.expectedSize) {
        throw GradleException(
            "Golden narrator ${asset.fileName} has ${file.length()} bytes; expected ${asset.expectedSize}"
        )
    }
    val actualSha256 = sha256(file)
    if (actualSha256 != asset.expectedSha256) {
        throw GradleException(
            "Golden narrator ${asset.fileName} SHA-256 is $actualSha256; expected ${asset.expectedSha256}"
        )
    }
}

val syncIntrospectionGoldenMasterAudio by tasks.registering {
    group = "build"
    description = "Downloads and verifies the working old-main introspection narrator audio as additive Repo-2 resources."

    outputs.files(introspectionGoldenAudioAssets.map { File(introspectionGoldenRawDir, it.fileName) })

    doLast {
        Files.createDirectories(introspectionGoldenRawDir.toPath())

        introspectionGoldenAudioAssets.forEach { asset ->
            val target = File(introspectionGoldenRawDir, asset.fileName)
            val alreadyValid = target.isFile &&
                target.length() == asset.expectedSize &&
                runCatching { sha256(target) == asset.expectedSha256 }.getOrDefault(false)

            if (!alreadyValid) {
                val remoteUrl = "$introspectionGoldenAudioBaseUrl/${asset.remotePath.substringAfterLast('/')}"
                val temporary = Files.createTempFile(
                    introspectionGoldenRawDir.toPath(),
                    "${asset.fileName.removeSuffix(".mp3")}_",
                    ".tmp"
                )
                try {
                    URI(remoteUrl).toURL().openConnection().apply {
                        connectTimeout = 15_000
                        readTimeout = 60_000
                    }.getInputStream().buffered().use { input ->
                        Files.newOutputStream(temporary).buffered().use { output ->
                            input.copyTo(output)
                        }
                    }

                    val downloaded = temporary.toFile()
                    verifyGoldenAudio(downloaded, asset)
                    try {
                        Files.move(
                            temporary,
                            target.toPath(),
                            StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING
                        )
                    } catch (_: AtomicMoveNotSupportedException) {
                        Files.move(
                            temporary,
                            target.toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                        )
                    }
                } finally {
                    Files.deleteIfExists(temporary)
                }
            }

            verifyGoldenAudio(target, asset)
            logger.lifecycle(
                "Golden narrator ready: ${asset.fileName} (${asset.expectedSize} bytes, sha256=${asset.expectedSha256})"
            )
        }
    }
}

// Safe even when the Android plugin registers preBuild later in configuration.
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(syncIntrospectionGoldenMasterAudio)
}
