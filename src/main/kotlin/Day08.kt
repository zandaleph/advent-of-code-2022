class Day08 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 21
    }

    private data class ViewState(
        val tallest: Char = '0' - 1,
        val visible: Set<Pair<Int, Int>> = setOf(),
    )

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        val numCols = lines.size
        val numRows = lines.first().length
        check(lines.all { it.length == numRows })
        views(numCols, numRows).fold(setOf<Pair<Int, Int>>()) { seen, view ->
            view.fold(ViewState()) { state, pair ->
                val tree = lines[pair.first][pair.second]
                if (tree > state.tallest) {
                    state.copy(tallest = tree, visible = state.visible + pair)
                } else state
            }.visible + seen
        }.size
    }

    private fun views(cols: Int, rows: Int): Sequence<Sequence<Pair<Int, Int>>> = sequence {
        (0 until cols).forEach { col ->
            val steps = (0 until rows).map { row -> col to row }
            yield(steps.asSequence())
            yield(steps.asReversed().asSequence())
        }
        (0 until rows).forEach { row ->
            val steps = (0 until cols).map { col -> col to row }
            yield(steps.asSequence())
            yield(steps.asReversed().asSequence())
        }
    }

    override val part2 = SolutionPart { 0 }
}

fun main() = solutionMain<Day08>()
