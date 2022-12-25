class Day04 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 2
        private const val TEST_OUTPUT_2 = 4
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { input ->
        input.count { line ->
            val ranges = line.split(',').map {
                val bounds = it.split('-')
                bounds[0].toInt()..bounds[1].toInt()
            }
            ranges[0].containsRange(ranges[1]) || ranges[1].containsRange(ranges[0])
        }
    }

    private fun IntProgression.containsRange(other: IntProgression) = contains(other.first) && contains(other.last)
    private fun IntProgression.overlapsRange(other: IntProgression) = contains(other.first) || other.contains(first)

    override val part2 = SolutionPart(TEST_OUTPUT_2) { input ->
        input.count { line ->
            val ranges = line.split(',').map {
                val bounds = it.split('-')
                bounds[0].toInt()..bounds[1].toInt()
            }
            ranges[0].overlapsRange(ranges[1])
        }
    }
}

fun main() = solutionMain<Day04>()
