class Day08 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 21
        private const val TEST_OUTPUT_2 = 8
    }

    private data class ViewState(
        val tallest: Char = '0' - 1,
        val visible: Set<Pair<Int, Int>> = setOf(),
    )

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        val numRows = lines.size
        val numCols = lines.first().length
        check(lines.all { it.length == numCols })
        views(numRows, numCols).fold(setOf<Pair<Int, Int>>()) { seen, view ->
            view.fold(ViewState()) { state, pair ->
                val tree = lines[pair.first][pair.second]
                if (tree > state.tallest) {
                    state.copy(tallest = tree, visible = state.visible + pair)
                } else state
            }.visible + seen
        }.size
    }

    private fun views(rows: Int, cols: Int): Sequence<Sequence<Pair<Int, Int>>> = sequence {
        (0 until rows).forEach { row ->
            val steps = (0 until cols).map { col -> row to col }
            yield(steps.asSequence())
            yield(steps.asReversed().asSequence())
        }
        (0 until cols).forEach { col ->
            val steps = (0 until rows).map { row -> row to col }
            yield(steps.asSequence())
            yield(steps.asReversed().asSequence())
        }
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { lines ->
        val numRows = lines.size
        val numCols = lines.first().length
        check(lines.all { it.length == numCols })
        (1 until (numRows - 1)).asSequence().flatMap { row ->
            (1 until (numCols - 1)).asSequence().map { col ->
                val curHeight = lines[row][col]
                viewsFrom(row to col, numRows, numCols).fold(1) { acc, view ->
                    val shorter = view.takeWhile { lines[it.first][it.second] < curHeight }.count()
                    acc * (shorter + if (shorter < view.size) 1 else 0)
                }
            }
        }.max()
    }

    private fun viewsFrom(point: Pair<Int, Int>, rows: Int, cols: Int): List<List<Pair<Int, Int>>> = listOf(
        IntProgression.fromClosedRange(point.first - 1, 0, -1).map { it to point.second },
        IntProgression.fromClosedRange(point.second - 1, 0, -1).map { point.first to it },
        IntProgression.fromClosedRange(point.first + 1, rows - 1, 1).map { it to point.second },
        IntProgression.fromClosedRange(point.second + 1, cols - 1, 1).map { point.first to it },
    )
}

fun main() = solutionMain<Day08>(true)
