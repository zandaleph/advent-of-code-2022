package aoc.day03

import readInput

const val CAPITAL_OFFSET = 27

fun Char.priorityValue() = if (this in 'a'..'z') this - 'a' + 1 else this - 'A' + CAPITAL_OFFSET

fun part1(input: List<String>): Int {
    return input.sumOf { line ->
        val half = line.length / 2
        val firstHalf = line.subSequence(0, half).toSet()
        val secondHalf = line.subSequence(half, line.length).toSet()
        firstHalf.intersect(secondHalf).also {
            check(it.size == 1)
        }.first().priorityValue()
    }
}

private const val GROUP_SIZE = 3

fun part2(input: List<String>): Int {
    return input.chunked(GROUP_SIZE).sumOf { lines ->
        lines.map { it.toSet() }
            .reduce { l, r -> l.intersect(r) }
            .also { check(it.size == 1) }
            .first().priorityValue()
    }
}

private const val TEST_OUTPUT = 157
private const val TEST_OUTPUT_2 = 70

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    val testOutput = part1(testInput)
//    println(testOutput)
    check(testOutput == TEST_OUTPUT)
    val testOutput2 = part2(testInput)
    println(testOutput2)
    check(testOutput2 == TEST_OUTPUT_2)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
