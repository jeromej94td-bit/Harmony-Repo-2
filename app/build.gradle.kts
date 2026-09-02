import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.Base64
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.secrets)
//  alias(libs.plugins.google.services)
}

abstract class ReconstructMerlinThemeTask : DefaultTask() {
  @get:InputFiles
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val chunks: ConfigurableFileCollection

  @get:OutputFile
  abstract val outputFile: RegularFileProperty

  @get:Input
  abstract val expectedSize: Property<Int>

  @get:Input
  abstract val expectedSha256: Property<String>

  @TaskAction
  fun reconstruct() {
    val expectedNames = (1..12).map { "merlin_theme_%02d.b64".format(it) }
    val chunkFiles = chunks.files.sortedBy { it.name }
    val actualNames = chunkFiles.map { it.name }
    if (actualNames != expectedNames) {
      throw GradleException(
        "Unexpected Merlin chunks: expected $expectedNames, got $actualNames"
      )
    }

    val encoded = chunkFiles
      .joinToString(separator = "") { it.readText(Charsets.US_ASCII) }
      .filterNot { it.isWhitespace() }
    val decoded = try {
      Base64.getDecoder().decode(encoded)
    } catch (error: IllegalArgumentException) {
      throw GradleException("Merlin Base64 assets are invalid", error)
    }

    val actualSize = decoded.size
    if (actualSize != expectedSize.get()) {
      throw GradleException(
        "Merlin theme has $actualSize bytes; expected ${expectedSize.get()}"
      )
    }

    val actualSha256 = MessageDigest.getInstance("SHA-256")
      .digest(decoded)
      .joinToString(separator = "") { "%02x".format(it) }
    if (actualSha256 != expectedSha256.get()) {
      throw GradleException(
        "Merlin theme SHA-256 is $actualSha256; expected $expectedSha256.get()"
      )
    }

    val output = outputFile.get().asFile.toPath()
    Files.createDirectories(output.parent)
    val temporary = Files.createTempFile(output.parent, "merlin_theme_", ".tmp")
    try {
      Files.write(temporary, decoded)
      try {
        Files.move(
          temporary,
          output,
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING
        )
      } catch (_: AtomicMoveNotSupportedException) {
        Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING)
      }
    } finally {
      Files.deleteIfExists(temporary)
    }

    logger.lifecycle(
      "Merlin theme reconstructed: $actualSize bytes, sha256=$actualSha256"
    )
  }
}

abstract class ReconstructMoralGreyZonesIntroTask : DefaultTask() {
  @get:InputFiles
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val chunks: ConfigurableFileCollection

  @get:OutputFile
  abstract val outputFile: RegularFileProperty

  @get:Input
  abstract val expectedSize: Property<Int>

  @get:Input
  abstract val expectedSha256: Property<String>

  @TaskAction
  fun reconstruct() {
    val expectedNames = (1..13).map { "moral_grey_zones_intro_%02d.b64".format(it) }
    val chunkFiles = chunks.files.sortedBy { it.name }
    val actualNames = chunkFiles.map { it.name }
    if (actualNames != expectedNames) {
      throw GradleException("Unexpected moral grey zones video chunks: expected $expectedNames, got $actualNames")
    }

    val encoded = chunkFiles.joinToString(separator = "") { it.readText(Charsets.US_ASCII) }.filterNot { it.isWhitespace() }
    val decoded = try {
      Base64.getDecoder().decode(encoded)
    } catch (error: IllegalArgumentException) {
      throw GradleException("Moral grey zones video Base64 assets are invalid", error)
    }

    val actualSize = decoded.size
    if (actualSize != expectedSize.get()) {
      throw GradleException("Moral grey zones intro has $actualSize bytes; expected ${expectedSize.get()}")
    }

    val actualSha256 = MessageDigest.getInstance("SHA-256").digest(decoded).joinToString(separator = "") { "%02x".format(it) }
    if (actualSha256 != expectedSha256.get()) {
      throw GradleException("Moral grey zones intro SHA-256 is $actualSha256; expected ${expectedSha256.get()}")
    }

    val output = outputFile.get().asFile.toPath()
    Files.createDirectories(output.parent)
    val temporary = Files.createTempFile(output.parent, "moral_grey_zones_intro_", ".tmp")
    try {
      Files.write(temporary, decoded)
      try {
        Files.move(temporary, output, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
      } catch (_: AtomicMoveNotSupportedException) {
        Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING)
      }
    } finally {
      Files.deleteIfExists(temporary)
    }

    logger.lifecycle("Moral grey zones intro reconstructed: $actualSize bytes, sha256=$actualSha256")
  }
}

abstract class ReconstructIntrospectionIntroTask : DefaultTask() {
  @get:InputFiles
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val chunks: ConfigurableFileCollection

  @get:OutputFile
  abstract val outputFile: RegularFileProperty

  @get:Input
  abstract val expectedSize: Property<Int>

  @get:Input
  abstract val expectedSha256: Property<String>

  @TaskAction
  fun reconstruct() {
    val expectedNames = (0..24).map { "introspection_intro_%02d.b64".format(it) }
    val chunkFiles = chunks.files.sortedBy { it.name }
    val actualNames = chunkFiles.map { it.name }
    if (actualNames != expectedNames) {
      throw GradleException("Unexpected introspection intro chunks: expected $expectedNames, got $actualNames")
    }

    val encoded = chunkFiles.joinToString(separator = "") {
      it.readText(Charsets.US_ASCII)
    }.filterNot { it.isWhitespace() }
    val decoded = try {
      Base64.getDecoder().decode(encoded)
    } catch (error: IllegalArgumentException) {
      throw GradleException("Introspection intro Base64 assets are invalid", error)
    }

    if (decoded.size != expectedSize.get()) {
      throw GradleException(
        "Introspection intro has ${decoded.size} bytes; expected ${expectedSize.get()}"
      )
    }

    val actualSha256 = MessageDigest.getInstance("SHA-256")
      .digest(decoded)
      .joinToString(separator = "") { "%02x".format(it) }
    if (actualSha256 != expectedSha256.get()) {
      throw GradleException(
        "Introspection intro SHA-256 is $actualSha256; expected ${expectedSha256.get()}"
      )
    }

    val output = outputFile.get().asFile.toPath()
    Files.createDirectories(output.parent)
    val temporary = Files.createTempFile(output.parent, "introspection_intro_", ".tmp")
    try {
      Files.write(temporary, decoded)
      try {
        Files.move(
          temporary,
          output,
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING
        )
      } catch (_: AtomicMoveNotSupportedException) {
        Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING)
      }
    } finally {
      Files.deleteIfExists(temporary)
    }

    logger.lifecycle("Introspection intro reconstructed: ${decoded.size} bytes, sha256=$actualSha256")
  }
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.harmony.couples.xqvz"
    minSdk = 24
    targetSdk = 36
    versionCode = 3
    versionName = "1.2"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  lint {
    abortOnError = false
    checkReleaseBuilds = false
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: (if (file("${rootDir}/my-upload-key.jks").exists()) "${rootDir}/my-upload-key.jks" else "${rootDir}/debug.keystore")
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { signingConfig = signingConfigs.getByName("debugConfig") }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  sourceSets {
    getByName("main") {
      assets.directories.add("$projectDir/schemas")
    }
  }
}

ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
}

secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

val reconstructMerlinTheme by tasks.registering(ReconstructMerlinThemeTask::class) {
  group = "build"
  description = "Reconstructs and verifies the Merlin background theme from Base64 assets."
  chunks.from(
    fileTree("src/main/assets/introspection") {
      include("merlin_theme_*.b64")
    }
  )
  outputFile.set(layout.projectDirectory.file("src/main/res/raw/merlin_theme.ogg"))
  expectedSize.set(134_361)
  expectedSha256.set("43a81cfc7254d69dce6027d7fadbdef32f3ce70c27d7d99507a20fe02127de24")
}

val reconstructMoralGreyZonesIntro by tasks.registering(ReconstructMoralGreyZonesIntroTask::class) {
  group = "build"
  description = "Reconstructs and verifies the moral grey zones intro video from Base64 assets."
  chunks.from(
    fileTree("src/main/assets/introspection") {
      include("moral_grey_zones_intro_*.b64")
    }
  )
  outputFile.set(layout.projectDirectory.file("src/main/res/raw/moral_grey_zones_intro.mp4"))
  expectedSize.set(4863529)
  expectedSha256.set("1cb564f6029af8f2222dae5ab62d6ce429bba6f76d7b9eef3c15f85843d66cf3")
}

val reconstructIntrospectionIntro by tasks.registering(ReconstructIntrospectionIntroTask::class) {
  group = "build"
  description = "Reconstructs and verifies the introspection intro video from Base64 assets."
  chunks.from(
    fileTree("src/main/assets/introspection") {
      include("introspection_intro_*.b64")
    }
  )
  outputFile.set(layout.projectDirectory.file("src/main/res/raw/introspection_intro.mp4"))
  expectedSize.set(7297407)
  expectedSha256.set("dbb693c0b39920e7b88aab52b99277d4a4d78446eff1d3d33463ca98e3c37778")
}

val verifyProductionSourceIsolation by tasks.registering {
  group = "verification"
  description = "Fails the build if archived Harmony features are reactivated in production sources."

  doLast {
    val sourceRoot = file("src/main/java")
    val violations = mutableListOf<String>()

    fun read(relativePath: String): String {
      val target = file("src/main/java/$relativePath")
      if (!target.exists()) {
        violations += "$relativePath is missing"
        return ""
      }
      return target.readText()
    }

    val mainActivity = read("com/example/MainActivity.kt")
    val chatScreen = read("com/example/ui/screens/ChatScreen.kt")
    val homeScreen = read("com/example/ui/screens/HomeScreen.kt")
    val gamesScreen = read("com/example/ui/screens/GamesScreen.kt")
    val legacyBridge = read("com/example/ui/screens/ChatScreenLegacyBridge.kt")

    if (mainActivity.contains("brainEnabled = true")) {
      violations += "MainActivity.kt explicitly re-enables an archived Brain surface"
    }
    if (mainActivity.contains("HARMONY_BRAIN_ENABLED = true")) {
      violations += "MainActivity.kt explicitly re-enables Harmony Brain"
    }

    listOf("Harmony Brain", "isBrainChatMode", "onSendBrainMessage", "onSendVoiceBrainMessage").forEach { marker ->
      if (chatScreen.contains(marker)) {
        violations += "ChatScreen.kt contains archived Brain marker: $marker"
      }
    }

    if (!homeScreen.contains("brainEnabled: Boolean = false")) {
      violations += "HomeScreen.kt lost the fail-closed archived Brain default"
    }
    if (!gamesScreen.contains("brainEnabled: Boolean = false")) {
      violations += "GamesScreen.kt lost the fail-closed archived Brain default"
    }

    val bridgeBody = legacyBridge.substringAfter(") {", missingDelimiterValue = "")
    listOf(
      "onSendBrainMessage",
      "onSendVoiceBrainMessage",
      "onToggleBrainChatMode",
      "onResetBrainChat",
      "onSaveSuggestionToNotes",
      "onSuggestionFeedback"
    ).forEach { marker ->
      if (bridgeBody.contains(marker)) {
        violations += "ChatScreenLegacyBridge.kt executes archived callback: $marker"
      }
    }

    sourceRoot.walkTopDown()
      .filter { it.isFile && it.extension == "kt" && it.name != "RemovedGameCatalogPolicy.kt" }
      .forEach { sourceFile ->
        val text = sourceFile.readText()
        val relative = sourceFile.relativeTo(projectDir).invariantSeparatorsPath

        if (text.contains("HARMONY_BRAIN_ENABLED = true")) {
          violations += "$relative sets HARMONY_BRAIN_ENABLED to true"
        }
        if (text.contains("brainEnabled = true")) {
          violations += "$relative explicitly enables an archived Brain surface"
        }

        listOf(
          "id = \"mischung\"",
          "id=\"mischung\"",
          "cat = \"mischung\"",
          "cat=\"mischung\""
        ).forEach { marker ->
          if (text.contains(marker)) {
            violations += "$relative reintroduces removed catalog marker: $marker"
          }
        }
      }

    if (violations.isNotEmpty()) {
      throw GradleException(
        buildString {
          appendLine("Archived feature isolation check failed.")
          appendLine("Do not merge archived Harmony Brain / Mischung code directly into production.")
          appendLine("Restore ideas from archive/pre-production-isolation-2026-09-02 on a fresh feature branch.")
          violations.distinct().forEach { appendLine(" - $it") }
        }
      )
    }

    logger.lifecycle("Archived feature isolation check passed.")
  }
}

tasks.named("preBuild").configure {
  dependsOn(
    reconstructMerlinTheme,
    reconstructMoralGreyZonesIntro,
    reconstructIntrospectionIntro,
    verifyProductionSourceIsolation
  )
}

// googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  // implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(platform(libs.supabase.bom))
  implementation(libs.supabase.auth)
  implementation(libs.supabase.functions)
  implementation(libs.ktor.client.okhttp)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)

  // Google Sign-In via Credential Manager (uncommented for Supabase authentication)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.serialization)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  testImplementation(libs.androidx.room.testing)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
