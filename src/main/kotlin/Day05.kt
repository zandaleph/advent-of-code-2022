class Day05 : Solution<String> {

    companion object {
        private const val TEST_OUTPUT = "CMZ"
        private const val TEST_OUTPUT_2 = "MCD"

        private const val CHUNK_SIZE = 4
    }

    object MoveParser : Parser() {
        val COUNT = ParserField("\\d+") { toInt() }
        val FROM = ParserField("\\d+") { toInt() }
        val TO = ParserField("\\d+") { toInt() }
        override val pattern = "move ${field(COUNT)} from ${field(FROM)} to ${field(TO)}"
    }

    private data class CraneState(
        val stacks: List<List<Char>>,
    ) {
        fun move(count: Int, from: Int, to: Int) = CraneState(
            stacks.mapIndexed { idx, stack ->
                if (idx == from - 1) {
                    stack.subList(0, stack.size - count)
                } else if (idx == to - 1) {
                    stack + stacks[from - 1].run { subList(size - count, size).asReversed() }
                } else {
                    stack
                }
            },
        )

        fun moveBetter(count: Int, from: Int, to: Int) = CraneState(
            stacks.mapIndexed { idx, stack ->
                if (idx == from - 1) {
                    stack.subList(0, stack.size - count)
                } else if (idx == to - 1) {
                    stack + stacks[from - 1].run { subList(size - count, size) }
                } else {
                    stack
                }
            },
        )
    }

    private fun parseAndFold(lines: List<String>, block: (CraneState, Triple<Int, Int, Int>) -> CraneState): String {
        val diagramLines = lines.takeWhile { it.isNotEmpty() }
        val numStacks = diagramLines.last().chunked(CHUNK_SIZE).last().trim().toInt()
        val initialState = CraneState(
            diagramLines.dropLast(1)
                .map {
                    it.chunked(CHUNK_SIZE) { crate ->
                        crate[1]
                    }
                }.let { rows ->
                    List(numStacks) { stackIdx ->
                        rows.asReversed().mapNotNull { row -> row.getOrNull(stackIdx) }.takeWhile { it != ' ' }
                    }
                },
        )
        val finalState = lines.drop(diagramLines.size + 1).fold(initialState) { state, line ->
            val triple = MoveParser.run { parse(line).let { Triple(it[COUNT], it[FROM], it[TO]) } }
            block(state, triple)
        }
        return finalState.stacks.map { it.last() }.joinToString(separator = "")
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        parseAndFold(lines) { state, (count, from, to) ->
            state.move(count, from, to)
        }
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { lines ->
        parseAndFold(lines) { state, (count, from, to) ->
            state.moveBetter(count, from, to)
        }
    }
}

fun main() = solutionMain<Day05>()
