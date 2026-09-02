import com.example.data.GeneratedContentRegistry

fun main() {
    val scenarioPacks = GeneratedContentRegistry.PACKS.filter {
        "mechanik_szenario" in it.tags || it.cat == "h360_szenario"
    }
    scenarioPacks.forEach { pack ->
        if (pack.questions.size != 8) {
            println("Pack ${pack.id} has ${pack.questions.size} questions")
        }
    }
}
