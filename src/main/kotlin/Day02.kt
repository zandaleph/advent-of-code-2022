class Day02 : Solution {
    companion object {
        private const val TEST_OUTPUT = 15
        private const val TEST_OUTPUT_2 = 12

        private const val NUM_SYMBOLS = 3
        private const val OUTCOME_MULTIPLIER = 3
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { input ->
        input.sumOf { line ->
            val (opp, me) = line.split(' ')
            (scoreRound(opp, me) + playValue(me))
        }
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { input ->
        input.sumOf { line ->
            val (opp, result) = line.split(' ')
            (scoreResult(result) + playValue(playForResult(opp, result)))
        }
    }

    // Slight hack to get 'X' to equal 1
    private fun playValue(me: String): Int = me[0] - 'W'

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
    private fun scoreRound(opp: String, me: String): Int {
        val oppValue = opp[0] - 'A'
        val meValue = playValue(me)
        // ugh, java's % operator gives negative results for negative input :(
        return ((meValue - oppValue + NUM_SYMBOLS) % NUM_SYMBOLS) * OUTCOME_MULTIPLIER
    }

    private fun scoreResult(result: String) = (result[0] - 'X') * OUTCOME_MULTIPLIER

    private fun playForResult(opp: String, result: String): String {
        val oppValue = opp[0] - 'A'
        val resultValue = result[0] - 'Y' // draws at 0, loss at -1, win at +1
        val meValue = 'X' + ((oppValue + resultValue + NUM_SYMBOLS) % NUM_SYMBOLS)
        return meValue.toString()
    }
}

fun main() = solutionMain<Day02>()
