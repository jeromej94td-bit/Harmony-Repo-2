with open("settings.gradle.kts", "r") as f:
    content = f.read()

snippet = """import java.util.Base64
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

"""

if "debug.keystore.base64" not in content:
    with open("settings.gradle.kts", "w") as f:
        f.write(snippet + content)
    print("Updated settings.gradle.kts")
