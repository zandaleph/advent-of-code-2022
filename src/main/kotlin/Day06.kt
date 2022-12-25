class Day06 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 7
        private const val TEST_OUTPUT_2 = 19

        private const val START_MARKER_LEN = 4
        private const val MESSAGE_MARKER_LEN = 14
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { firstMarkerIndex(it.first(), START_MARKER_LEN) }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { firstMarkerIndex(it.first(), MESSAGE_MARKER_LEN) }

    private fun firstMarkerIndex(line: String, markerLen: Int) =
        line.windowedSequence(markerLen).indexOfFirst { it.toSet().size == markerLen } + markerLen
}

fun main() = solutionMain<Day06>()
