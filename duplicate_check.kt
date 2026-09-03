import com.example.data.model.*
import com.example.data.*
fun main() {
    val ids = HarmonyPacksData.CATALOG_PACKS.map { it.id }
    println("Total: ${ids.size}")
    println("Unique: ${ids.distinct().size}")
    val duplicates = ids.groupBy { it }.filter { it.value.size > 1 }.keys
    println("Duplicates: $duplicates")
}
