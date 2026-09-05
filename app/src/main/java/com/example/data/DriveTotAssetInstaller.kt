package com.example.data

import android.content.Context
import android.util.Base64
import com.example.data.model.HarmonyPacksData
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.zip.ZipException
import java.util.zip.ZipInputStream

/**
 * Installs the bundled "Das oder das?" image assets that ship inside the APK.
 * Existing Drive-backed images stay in drive_tot_assets.zip. Generated bundles
 * are stored as split Base64 text assets and extracted to app-private storage.
 */
object DriveTotAssetInstaller {
    private const val DRIVE_ASSET_ZIP = "drive_tot_assets.zip"
    private const val OUTPUT_DIR = "drive_tot_assets_v3"
    private const val INSTALL_MARKER = ".install-complete"

    // Targeted repair for the blank Tokyo card in Reiseziele. The source image is
    // Harmony Brain > ChatGPT Generierte Bilder - strukturiert > 01_Reiseziele >
    // "Tokyo Tower über Zōjō-ji im Abendlicht.png" (Drive file ID below).
    // A separate marker intentionally makes this run once on existing installs too,
    // where the general bundle may already be marked as installed.
    private const val TOKYO_REPAIR_MARKER = ".tokyo-image-v1"
    private const val TOKYO_OPTION = "Tokyo, Japan"
    private const val TOKYO_FILE = "travel_tokyo.webp"
    private const val TOKYO_DRIVE_FILE_ID = "1ehJCs9FJ3Htwl3MqBIwOYtSgC3vynNxZ"
    private const val TOKYO_MIN_BYTES = 1_024L

    // Targeted repair for Traumhaus Außenbereich. These are optimized copies of the
    // exact images selected from Harmony Brain > Von ChatGPT generierte Bilder > Haus Außen 2
    // (22. August). A dedicated marker makes the assets appear on already-installed apps too.
    private const val OUTDOOR_REPAIR_MARKER = ".outdoor-area-images-v1"
    private const val OUTDOOR_POOL_OPTION = "Außenpool"
    private const val OUTDOOR_POOL_FILE = "outdoor_aussenpool.webp"
    private const val OUTDOOR_POOL_DRIVE_FILE_ID = "1rMZ3LgbcxLQX55Sp0cbsmO76ISuq9vH-"
    private const val OUTDOOR_WHIRLPOOL_OPTION = "Whirlpool"
    private const val OUTDOOR_WHIRLPOOL_FILE = "outdoor_whirlpool.webp"
    private const val OUTDOOR_WHIRLPOOL_DRIVE_FILE_ID = "1nFWTw3kEEOmuNefb4N3mSDCK1eabg6_c"
    private const val OUTDOOR_MIN_BYTES = 50_000L

    private val TOKYO_ASSET_CHUNKS = listOf(
        "travel_tokyo_asset.b64"
    )

    private val BRAND_ASSET_CHUNKS = listOf(
        "brand_everyday_assets_01.b64",
        "brand_everyday_assets_02.b64",
        "brand_everyday_assets_03_04.b64",
        "brand_everyday_assets_05_06.b64",
        "brand_everyday_assets_07_08.b64"
    )

    private val ENGAGEMENT_RING_CHUNKS = listOf(
        "engagement_rings_01.b64",
        "engagement_rings_02.b64",
        "engagement_rings_03.b64",
        "engagement_rings_04.b64",
        "engagement_rings_05.b64",
        "engagement_rings_06.b64",
        "engagement_rings_07.b64"
    )

    private val driveOptionToFile = linkedMapOf(
        "Cappuccino" to "drink_cappuccino.webp",
        "Matcha-Latte" to "drink_matcha_latte.webp",
        "Heiße Schokolade" to "drink_heisse_schokolade.webp",
        "Eistee" to "drink_schwarzer_eistee.webp",
        "Minzlimonade" to "drink_minzlimonade.webp",
        "Fruchtpunsch" to "drink_fruchtpunsch.webp",
        "Bier" to "drink_bier.webp",
        "Rote-Bete-Saft" to "drink_rote_bete_saft.webp",
        "Coca-Cola" to "drink_coca_cola.webp",
        "Fanta" to "drink_fanta.webp",
        "Orangensaft" to "drink_orangensaft.webp",
        "Apfelsaft" to "drink_apfelsaft.webp",
        "Kaffee" to "drink_kaffee.webp",
        "Tee" to "drink_tee.webp",
        "Hund" to "animal_hund.webp",
        "Katze" to "animal_katze.webp",
        "Singvogel" to "animal_singvogel.webp",
        "Pinguin" to "animal_pinguin.webp",
        "Kaninchen" to "animal_kaninchen.webp",
        "Otter" to "animal_otter.webp",
        "Roter Panda" to "animal_roter_panda.webp",
        "Fuchs" to "animal_fuchs.webp",
        "Meerschweinchen" to "animal_meerschweinchen.webp",
        "Giraffe" to "animal_giraffe.webp",
        "Löwe" to "animal_loewe.webp",
        "Gorilla" to "animal_gorilla.webp",
        "Meeresschildkröte" to "animal_meeresschildkroete.webp",
        "Igel" to "animal_igel.webp",
        "Tiger" to "animal_tiger.webp",
        "Wolf" to "animal_wolf.webp",
        "Adler" to "animal_adler.webp",
        "Delfin" to "animal_delfin.webp",
        "Töpfern" to "hobby_toepfern.webp",
        "Klavier spielen" to "hobby_klavier.webp",
        "Malen" to "hobby_malen.webp",
        "Zeichnen" to "hobby_zeichnen.webp",
        "Badminton" to "hobby_badminton.webp",
        "Mountainbike" to "hobby_mountainbike.webp",
        "Bowling" to "hobby_bowling.webp",
        "Holzwerken" to "hobby_holzwerken.webp",
        "Gitarre spielen" to "hobby_gitarre.webp",
        "Tennis" to "hobby_tennis.webp",
        "Brettspiele" to "hobby_brettspiele.webp",
        "Darts" to "hobby_darts.webp",
        "Miami, USA" to "travel_miami.webp",
        "Bangkok, Thailand" to "travel_bangkok.webp",
        "Chicago, USA" to "travel_chicago.webp",
        "Barcelona, Spanien" to "travel_barcelona.webp",
        "Lissabon, Portugal" to "travel_lissabon.webp",
        "Kopenhagen, Dänemark" to "travel_kopenhagen.webp",
        "Prag, Tschechien" to "travel_prag.webp",
        "Budapest, Ungarn" to "travel_budapest.webp",
        TOKYO_OPTION to TOKYO_FILE,
        OUTDOOR_POOL_OPTION to OUTDOOR_POOL_FILE,
        OUTDOOR_WHIRLPOOL_OPTION to OUTDOOR_WHIRLPOOL_FILE
    )

    private val brandOptionToFile = linkedMapOf(
        "McDonald’s" to "brand_mcdonalds.webp",
        "Burger King" to "brand_burger_king.webp",
        "iPhone" to "brand_iphone.webp",
        "Android" to "brand_android.webp",
        "Netflix" to "brand_netflix.webp",
        "Kino" to "brand_kino.webp",
        "Nike" to "brand_nike.webp",
        "Adidas" to "brand_adidas.webp",
        "Spotify" to "brand_spotify.webp",
        "YouTube Music" to "brand_youtube_music.webp",
        "PlayStation" to "brand_playstation.webp",
        "Xbox" to "brand_xbox.webp",
        "Coca-Cola" to "brand_coca_cola.webp",
        "Pepsi" to "brand_pepsi.webp",
        "IKEA" to "brand_ikea.webp",
        "Möbelhaus" to "brand_moebelhaus.webp",
        "Amazon" to "brand_amazon.webp",
        "Lokal einkaufen" to "brand_lokal_einkaufen.webp",
        "Disney" to "brand_disney.webp",
        "Studio Ghibli" to "brand_studio_ghibli.webp"
    )

    private val ringPairs = listOf(
        "Klassisch Solitär" to "Vintage verspielt",
        "Schmal & zart" to "Markant & breit",
        "Ovaler Diamant" to "Runder Diamant",
        "Großer Stein" to "Diamanten im Band",
        "Vintage Art déco" to "Modern geometrisch",
        "Moissanit" to "Saphir",
        "Diamant" to "Farbedelstein",
        "Ohne Stein" to "Statement-Ring",
        "Platin" to "Roségold",
        "Drei-Stein-Ring" to "Moderner Solitär",
        "Gelbgold" to "Weißgold"
    )

    private val ringOptionToFile = linkedMapOf(
        "Klassisch Solitär" to "ring_drive_01_a.webp",
        "Vintage verspielt" to "ring_drive_01_b.webp",
        "Schmal & zart" to "ring_drive_02_a.webp",
        "Markant & breit" to "ring_drive_02_b.webp",
        "Ovaler Diamant" to "ring_drive_03_a.webp",
        "Runder Diamant" to "ring_drive_03_b.webp",
        "Großer Stein" to "ring_drive_04_a.webp",
        "Diamanten im Band" to "ring_drive_04_b.webp",
        "Vintage Art déco" to "ring_drive_05_a.webp",
        "Modern geometrisch" to "ring_drive_05_b.webp",
        "Moissanit" to "ring_drive_06_a.webp",
        "Saphir" to "ring_drive_06_b.webp",
        "Diamant" to "ring_drive_07_a.webp",
        "Farbedelstein" to "ring_drive_07_b.webp",
        "Ohne Stein" to "ring_drive_08_a.webp",
        "Statement-Ring" to "ring_drive_08_b.webp",
        "Platin" to "ring_drive_09_a.webp",
        "Roségold" to "ring_drive_09_b.webp",
        "Drei-Stein-Ring" to "ring_drive_10_a.webp",
        "Moderner Solitär" to "ring_drive_10_b.webp",
        "Gelbgold" to "ring_drive_11_a.webp",
        "Weißgold" to "ring_drive_11_b.webp"
    )

    /**
     * These options now have real compiled drawable resources. The legacy extracted
     * ring files must not be registered for them, otherwise they mask the refreshed
     * WebP resource from TotImageProvider after every app start.
     */
    private val refreshedCompiledRingOptions = setOf(
        "Klassisch Solitär",
        "Vintage verspielt",
        "Schmal & zart",
        "Markant & breit",
        "Vintage Art déco",
        "Modern geometrisch",
        "Moderner Solitär",
        "Diamanten im Band",
        "Ohne Stein",
        "Statement-Ring"
    )

    private val forceBundledOutdoorOptions = setOf(
        OUTDOOR_POOL_OPTION,
        OUTDOOR_WHIRLPOOL_OPTION
    )

    private fun applyEngagementRingPack() {
        val current = HarmonyPacksData.PACKS
        val ringPack = current.firstOrNull { it.id == "ringe" } ?: return
        val updated = ringPack.copy(pairs = ringPairs)
        HarmonyPacksData.setDynamicPacks(current.map { if (it.id == "ringe") updated else it })
    }

    private fun extractZip(input: InputStream, outputDir: File, expectedFiles: Set<String>): Boolean {
        val extractedFiles = mutableListOf<File>()
        return try {
            ZipInputStream(input.buffered()).use { zip ->
                while (true) {
                    val entry = zip.nextEntry ?: break
                    if (!entry.isDirectory) {
                        val name = entry.name.substringAfterLast('/')
                        if (name in expectedFiles) {
                            val outputFile = File(outputDir, name)
                            extractedFiles += outputFile
                            outputFile.outputStream().buffered().use { out -> zip.copyTo(out) }
                        }
                    }
                    zip.closeEntry()
                }
            }
            true
        } catch (_: ZipException) {
            extractedFiles.forEach { it.delete() }
            false
        }
    }

    private fun decodeChunkedZip(context: Context, chunks: List<String>): ByteArray {
        val encoded = buildString {
            chunks.forEach { chunkName ->
                append(context.assets.open(chunkName).bufferedReader().use { it.readText() })
            }
        }
        return Base64.decode(encoded, Base64.DEFAULT)
    }

    private fun repairTokyoImage(context: Context, outputDir: File) {
        val marker = File(outputDir, TOKYO_REPAIR_MARKER)
        val target = File(outputDir, TOKYO_FILE)
        if (marker.isFile && target.isFile && target.length() >= TOKYO_MIN_BYTES) return

        val repaired = runCatching {
            ByteArrayInputStream(decodeChunkedZip(context, TOKYO_ASSET_CHUNKS)).use { input ->
                extractZip(input, outputDir, setOf(TOKYO_FILE))
            }
        }.getOrDefault(false)

        if (repaired && target.isFile && target.length() >= TOKYO_MIN_BYTES) {
            marker.writeText("drive:$TOKYO_DRIVE_FILE_ID")
        }
    }

    private fun repairOutdoorAreaImages(context: Context, outputDir: File) {
        val marker = File(outputDir, OUTDOOR_REPAIR_MARKER)
        val poolTarget = File(outputDir, OUTDOOR_POOL_FILE)
        val whirlpoolTarget = File(outputDir, OUTDOOR_WHIRLPOOL_FILE)
        if (
            marker.isFile &&
            poolTarget.isFile && poolTarget.length() >= OUTDOOR_MIN_BYTES &&
            whirlpoolTarget.isFile && whirlpoolTarget.length() >= OUTDOOR_MIN_BYTES
        ) {
            return
        }

        fun copyBundledAsset(assetName: String, target: File): Boolean = runCatching {
            context.assets.open(assetName).use { input ->
                target.outputStream().buffered().use { output -> input.copyTo(output) }
            }
            target.isFile && target.length() >= OUTDOOR_MIN_BYTES
        }.getOrDefault(false)

        val poolReady = copyBundledAsset(OUTDOOR_POOL_FILE, poolTarget)
        val whirlpoolReady = copyBundledAsset(OUTDOOR_WHIRLPOOL_FILE, whirlpoolTarget)
        if (poolReady && whirlpoolReady) {
            marker.writeText(
                "drive:$OUTDOOR_POOL_DRIVE_FILE_ID,$OUTDOOR_WHIRLPOOL_DRIVE_FILE_ID"
            )
        }
    }

    fun install(context: Context): Map<String, String> {
        CuisinePackInstaller.install(context)
        applyEngagementRingPack()
        ExtraBrandPairsInstaller.apply()

        val outputDir = File(context.filesDir, OUTPUT_DIR).apply { mkdirs() }
        val installMarker = File(outputDir, INSTALL_MARKER)
        val expectedFiles = (driveOptionToFile.values + brandOptionToFile.values + ringOptionToFile.values).toSet()
        val needsInstall = !installMarker.isFile

        if (needsInstall) {
            outputDir.listFiles()?.forEach { it.delete() }
            context.assets.open(DRIVE_ASSET_ZIP).use { extractZip(it, outputDir, expectedFiles) }
            ByteArrayInputStream(decodeChunkedZip(context, BRAND_ASSET_CHUNKS)).use {
                extractZip(it, outputDir, expectedFiles)
            }
            ByteArrayInputStream(decodeChunkedZip(context, ENGAGEMENT_RING_CHUNKS)).use {
                extractZip(it, outputDir, expectedFiles)
            }
            installMarker.writeText("1")
        }

        // Always check dedicated repair markers after the general install. This keeps the
        // exact selected Drive assets available on both clean installs and app upgrades.
        repairTokyoImage(context, outputDir)
        repairOutdoorAreaImages(context, outputDir)

        // Developer/exported images are installed before this legacy bundle. Do not
        // overwrite them afterwards just because an option has the same display name.
        // The two explicitly selected outdoor images are an exception: they are the
        // curated project assets for these stable keys and must replace stale remote fallbacks.
        val generatedOptionKeys = DeveloperDataManager.getGeneratedImages().keys
        val result = LinkedHashMap<String, String>()

        driveOptionToFile.forEach { (option, fileName) ->
            val file = File(outputDir, fileName)
            if (
                file.isFile &&
                file.length() > 0L &&
                (
                    option in forceBundledOutdoorOptions ||
                        TotImageSourcePolicy.shouldUseBundledInstallerImage(option, generatedOptionKeys)
                    )
            ) {
                result[option] = file.absolutePath
            }
        }
        brandOptionToFile.forEach { (option, fileName) ->
            val file = File(outputDir, fileName)
            if (
                file.isFile &&
                file.length() > 0L &&
                option !in result &&
                TotImageSourcePolicy.shouldUseBundledInstallerImage(option, generatedOptionKeys)
            ) {
                result[option] = file.absolutePath
            }
        }
        ringOptionToFile.forEach { (option, fileName) ->
            val file = File(outputDir, fileName)
            if (
                file.isFile &&
                file.length() > 0L &&
                TotImageSourcePolicy.shouldUseBundledInstallerImage(
                    option = option,
                    generatedOptionKeys = generatedOptionKeys,
                    preferCompiledResource = option in refreshedCompiledRingOptions
                )
            ) {
                result[option] = file.absolutePath
            }
        }
        return result
    }
}
