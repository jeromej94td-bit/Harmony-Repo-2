import java.util.Base64
import java.io.File

val keystore = File(rootDir, "debug.keystore")
val keystoreB64 = File(rootDir, "debug.keystore.base64")

if (keystoreB64.exists() && !keystore.exists()) {
    try {
        val decoded = Base64.getDecoder().decode(keystoreB64.readText().trim())
        keystore.writeBytes(decoded)
        println("Restored debug.keystore from base64 to ensure Google Sign-In identity is preserved.")
    } catch (e: Exception) {
        println("Failed to restore debug.keystore: ${e.message}")
    }
}

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Harmony"
include(":app")
