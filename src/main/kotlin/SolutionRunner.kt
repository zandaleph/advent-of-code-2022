import kotlin.reflect.full.createInstance
import kotlin.streams.toList

class SolutionRunner(private val solution: Solution, private val debug: Boolean = false) {

    fun run() {
        bothParts("_test") { output, part ->
            if (debug) println(output)
            part.testOutput?.let { check(it == output) }
        }
        bothParts { output, _ ->
            println(output)
        }
    }

    private fun bothParts(suffix: String? = null, block: (output: Int, part: SolutionPart) -> Unit) {
        val input = checkNotNull(
            solution.javaClass.getResourceAsStream("${solution.javaClass.simpleName}${suffix ?: ""}.txt"),
        ).bufferedReader().lines().toList()
        solution.part1.let { block(it.compute(input), it) }
        solution.part2.let { block(it.compute(input), it) }
    }
}

inline fun <reified T : Solution> solutionMain(debug: Boolean = false) {
    SolutionRunner(T::class.createInstance(), debug).run()
}
