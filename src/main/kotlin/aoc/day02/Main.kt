package aoc.day02

import readInput

/*
opp\me   ROCK PAPER SCISSOR
ROCK     DRAW WIN   LOSE
PAPER    LOSE DRAW  WIN
SCISSOR  WIN  LOSE  DRAW

- 1 2 3
0 1 2 0
1 0 1 2
2 2 0 1
 */

// Slight hack to get 'X' to equal 1
fun playValue(me: String): Int = me[0] - 'W'

const val NUM_SYMBOLS = 3
const val OUTCOME_MULTIPLIER = 3

fun scoreRound(opp: String, me: String): Int {
    val oppValue = opp[0] - 'A'
    val meValue = playValue(me)
    // ugh, java's % operator gives negative results for negative input :(
    return ((meValue - oppValue + NUM_SYMBOLS) % NUM_SYMBOLS) * OUTCOME_MULTIPLIER
}

fun part1(input: List<String>): Int {
    return input.sumOf { line ->
        val (opp, me) = line.split(' ')
        (scoreRound(opp, me) + playValue(me))
    }
}

fun part2(input: List<String>): Int {
    return input.size
}

const val TEST_OUTPUT = 15

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    val testOutput = part1(testInput)
    println(testOutput)
    check(testOutput == TEST_OUTPUT)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
