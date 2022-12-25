import java.util.PriorityQueue
import kotlin.math.max

class Day01 : Solution<Int> {
    companion object {
        private const val TEST_OUTPUT = 24000
    }

    private data class Part1State(
        val cur: Int = 0,
        val best: Int = 0,
    ) {
        fun updateBest() = copy(cur = 0, best = max(cur, best))
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { input ->
        input.fold(Part1State()) { state, line ->
            if (line.isEmpty()) {
                state.updateBest()
            } else {
                state.copy(cur = state.cur + line.toInt())
            }
        }.updateBest().best
    }

    private data class Part2State(
        val cur: Int = 0,
        val best: PriorityQueue<Int> = PriorityQueue(),
    ) {
        companion object {
            const val SIZE = 3
        }

        fun updateBest(): Part2State {
            val newBest = PriorityQueue(best)
            newBest.add(cur)
            if (newBest.size > SIZE) {
                newBest.poll()
            }
            return copy(cur = 0, best = newBest)
        }
    }

    override val part2 = SolutionPart { input ->
        input.fold(Part2State()) { state, line ->
            if (line.isEmpty()) {
                state.updateBest()
            } else {
                state.copy(cur = state.cur + line.toInt())
            }
        }.updateBest().best.sum()
    }
}

fun main() = solutionMain<Day01>()
