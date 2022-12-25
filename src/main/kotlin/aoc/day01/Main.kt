package aoc.day01

import readInput
import java.util.PriorityQueue
import kotlin.math.max

data class Part1State(
    val cur: Int = 0,
    val best: Int = 0,
) {
    fun updateBest() = copy(cur = 0, best = max(cur, best))
}

fun part1(input: List<String>): Int {
    val result = input.fold(Part1State()) { state, line ->
        if (line.isEmpty()) {
            state.updateBest()
        } else {
            state.copy(cur = state.cur + line.toInt())
        }
    }.updateBest()
    return result.best
}

data class Part2State(
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

fun part2(input: List<String>): Int {
    val result = input.fold(Part2State()) { state, line ->
        if (line.isEmpty()) {
            state.updateBest()
        } else {
            state.copy(cur = state.cur + line.toInt())
        }
    }.updateBest()
    return result.best.sum()
}

const val TEST_OUTPUT = 24000

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == TEST_OUTPUT)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
