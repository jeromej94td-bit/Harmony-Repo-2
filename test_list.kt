import com.example.data.model.*
import com.example.data.*
fun main() {
    val list = HarmonyPacksData.CATALOG_PACKS.filter { it.topic == "essen" }
    val ids = list.map { it.id }
    val dups = ids.groupBy { it }.filter { it.value.size > 1 }
    println("Essen pack IDs: ${ids.size}")
    if (dups.isNotEmpty()) {
        println("Duplicates in essen: $dups")
    }
}
