import kotlin.math.abs

class Day09 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 13
    }

    private data class RopeState(
        val head: Pair<Int, Int> = 0 to 0,
        val tail: Pair<Int, Int> = head,
        val visited: Set<Pair<Int, Int>> = setOf(tail),
    ) {
        fun move(heading: String): RopeState {
            val newHead = when (heading) {
                "L" -> head.copy(second = head.second - 1)
                "R" -> head.copy(second = head.second + 1)
                "D" -> head.copy(first = head.first - 1)
                "U" -> head.copy(first = head.first + 1)
                else -> error("Invalid heading: $heading")
            }
            val newTail = updateTail(newHead, tail)
            return copy(head = newHead, tail = newTail, visited = visited + newTail)
        }

        private fun updateTail(head: Pair<Int, Int>, tail: Pair<Int, Int>): Pair<Int, Int> {
            val rowDiff = head.first - tail.first
            val colDiff = head.second - tail.second
            return if (abs(rowDiff) > 1 || abs(colDiff) > 1) {
                tail.copy(first = tail.first + rowDiff.direction(), second = tail.second + colDiff.direction())
            } else tail
        }

        private fun Int.direction(): Int {
            return if (this == 0) 0 else abs(this) / this
        }
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        lines.fold(RopeState()) { state, line ->
            val (heading, steps) = line.split(' ').let { it[0] to it[1].toInt() }
            (1..steps).fold(state) { s, _ -> s.move(heading) }
        }.visited.size
    }

    override val part2 = SolutionPart { 0 }
}

fun main() = solutionMain<Day09>()
