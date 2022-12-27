import kotlin.math.abs

class Day09 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 13
        private const val TEST_OUTPUT_2 = 1

        private const val ROPE_LEN_PART_1 = 2
        private const val ROPE_LEN_PART_2 = 10
    }

    private data class RopeState(
        val rope: List<Pair<Int, Int>>,
        val visited: Set<Pair<Int, Int>> = setOf(rope.last()),
    ) {
        companion object {
            fun ofLength(len: Int) = RopeState(List(len) { 0 to 0 })
        }

        fun move(heading: String): RopeState {
            val head = rope.first()
            val newHead = when (heading) {
                "L" -> head.copy(second = head.second - 1)
                "R" -> head.copy(second = head.second + 1)
                "D" -> head.copy(first = head.first - 1)
                "U" -> head.copy(first = head.first + 1)
                else -> error("Invalid heading: $heading")
            }
            val newRope = mutableListOf(newHead)
            rope.subList(1, rope.size).forEach {
                newRope.add(updateTail(newRope.last(), it))
            }
            return copy(rope = newRope, visited = visited + newRope.last())
        }

        private fun updateTail(head: Pair<Int, Int>, tail: Pair<Int, Int>): Pair<Int, Int> {
            val rowDiff = head.first - tail.first
            val colDiff = head.second - tail.second
            return if (abs(rowDiff) > 1 || abs(colDiff) > 1) {
                (tail.first + rowDiff.direction()) to (tail.second + colDiff.direction())
            } else tail
        }

        private fun Int.direction(): Int {
            return if (this == 0) 0 else abs(this) / this
        }
    }

    private fun solve(lines: List<String>, initialState: RopeState) =
        lines.fold(initialState) { state, line ->
            val (heading, steps) = line.split(' ').let { it[0] to it[1].toInt() }
            (1..steps).fold(state) { s, _ -> s.move(heading) }
        }.visited.size

    override val part1 = SolutionPart(TEST_OUTPUT) { solve(it, RopeState.ofLength(ROPE_LEN_PART_1)) }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { solve(it, RopeState.ofLength(ROPE_LEN_PART_2)) }
}

fun main() = solutionMain<Day09>()
