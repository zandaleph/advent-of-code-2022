class Day03 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 157
        private const val TEST_OUTPUT_2 = 70

        private const val GROUP_SIZE = 3
        private const val CAPITAL_OFFSET = 27
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { input ->
        input.sumOf { line ->
            val half = line.length / 2
            val firstHalf = line.subSequence(0, half).toSet()
            val secondHalf = line.subSequence(half, line.length).toSet()
            firstHalf.intersect(secondHalf).only().priorityValue()
        }
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { input ->
        input.chunked(GROUP_SIZE).sumOf { lines ->
            lines.map { it.toSet() }
                .reduce { l, r -> l.intersect(r) }
                .only().priorityValue()
        }
    }

    private fun Char.priorityValue() = if (this in 'a'..'z') this - 'a' + 1 else this - 'A' + CAPITAL_OFFSET
}

fun main() = solutionMain<Day03>()
