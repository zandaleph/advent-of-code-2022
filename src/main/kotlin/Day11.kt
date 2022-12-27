import java.util.SortedMap

class Day11 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 10605
        private const val TEST_OUTPUT_2 = 0

        private const val WORRY_RELIEF = 3

        private fun regexGroup(id: String, pattern: String) = "(?<$id>$pattern)"

        private const val MONKEY_ID_LINE = 0
        private const val MONKEY_ID_ID = "id"
        private val MONKEY_ID_GROUP = regexGroup(MONKEY_ID_ID, "\\d+")
        private val MONKEY_ID_REGEX = Regex("Monkey $MONKEY_ID_GROUP:")

        private const val STARTING_ITEMS_LINE = 1
        private const val STARTING_ITEMS_ID = "items"
        private val STARTING_ITEMS_GROUP = regexGroup(STARTING_ITEMS_ID, "\\d+(, \\d+)*")
        private val STARTING_ITEMS_REGEX = Regex("  Starting items: $STARTING_ITEMS_GROUP")

        private const val OPERATION_LINE = 2
        private const val OPERATION_LHS_ID = "lhs"
        private val OPERATION_LHS_GROUP = regexGroup(OPERATION_LHS_ID, "\\d+|(old)")
        private const val OPERATION_OP_ID = "op"
        private val OPERATION_OP_GROUP = regexGroup(OPERATION_OP_ID, "\\+|\\*")
        private const val OPERATION_RHS_ID = "rhs"
        private val OPERATION_RHS_GROUP = regexGroup(OPERATION_RHS_ID, "\\d+|(old)")
        private val OPERATION_REGEX =
            Regex("  Operation: new = $OPERATION_LHS_GROUP $OPERATION_OP_GROUP $OPERATION_RHS_GROUP")

        private const val TEST_DIVISOR_LINE = 3
        private const val TEST_DIVISOR_ID = "divisor"
        private val TEST_DIVISOR_GROUP = regexGroup(TEST_DIVISOR_ID, "\\d+")
        private val TEST_DIVISOR_REGEX = Regex("  Test: divisible by $TEST_DIVISOR_GROUP")

        private const val BRANCH_TRUE_LINE = 4
        private const val BRANCH_FALSE_LINE = 5
        private const val BRANCH_VALUE_ID = "value"
        private val BRANCH_VALUE_GROUP = regexGroup(BRANCH_VALUE_ID, "(true)|(false)")
        private const val BRANCH_DEST_ID = "dest"
        private val BRANCH_DEST_GROUP = regexGroup(BRANCH_DEST_ID, "\\d+")
        private val BRANCH_REGEX = Regex("    If $BRANCH_VALUE_GROUP: throw to monkey $BRANCH_DEST_GROUP")

        private const val MONKEY_DESCRIPTION_LINES = 7
        private const val MONKEY_ROUNDS = 20
    }

    @JvmInline
    value class MonkeyId(private val id: Int) : Comparable<MonkeyId> {
        override fun compareTo(other: MonkeyId) = id - other.id
    }

    enum class MonkeyMathOp(val apply: (Int, Int) -> Int) {
        ADD(Int::plus),
        MULTIPLY(Int::times);
    }

    sealed interface MonkeyMathOperand {
        fun value(worry: Int): Int
    }

    object MonkeyMathWorryOperand : MonkeyMathOperand {
        override fun value(worry: Int) = worry
    }

    data class MonkeyMathValueOperand(private val value: Int) : MonkeyMathOperand {
        override fun value(worry: Int) = value
    }

    data class MonkeyMath(
        val op: MonkeyMathOp,
        val lhs: MonkeyMathOperand,
        val rhs: MonkeyMathOperand,
    ) {
        fun compute(worry: Int): Int {
            return op.apply(lhs.value(worry), rhs.value(worry))
        }
    }

    data class Monkey(
        val id: MonkeyId,
        val startingItems: List<Int>,
        val operation: MonkeyMath,
        val testDivisor: Int,
        val testTrueDest: MonkeyId,
        val testFalseDest: MonkeyId,
    ) {
        fun inspect(worry: Int): Pair<Int, MonkeyId> {
            val newWorry = operation.compute(worry).floorDiv(WORRY_RELIEF)
            val newMonkey = if (newWorry % testDivisor == 0) testTrueDest else testFalseDest
            return newWorry to newMonkey
        }
    }

    data class MonkeyState(
        val monkey: Monkey,
        val heldItems: List<Int> = monkey.startingItems,
        val inspections: Int = 0,
    ) {
        fun turn(): Pair<MonkeyState, Map<MonkeyId, List<Int>>> {
            val newState = copy(heldItems = listOf(), inspections = inspections + heldItems.size)
            val tosses = heldItems.map { monkey.inspect(it) }.groupBy({ it.second }) { it.first }
            return newState to tosses
        }
    }

    data class JungleState(
        val monkeys: SortedMap<MonkeyId, MonkeyState>,
    ) {
        companion object {
            fun fromMonkeys(monkeys: List<Monkey>) =
                JungleState(monkeys.associate { it.id to MonkeyState(it) }.toSortedMap())
        }

        fun round(): JungleState {
            return monkeys.keys.fold(this) { state, monkeyId ->
                val (newMonkey, tosses) = state.monkeys[monkeyId]!!.turn()
                val newMonkeys = (state.monkeys + (newMonkey.monkey.id to newMonkey)).mapValues { (id, curMonkey) ->
                    curMonkey.copy(heldItems = curMonkey.heldItems + (tosses[id] ?: listOf()))
                }.toSortedMap()
                copy(monkeys = newMonkeys)
            }
        }
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        val monkeys = lines.chunked(MONKEY_DESCRIPTION_LINES).map { chunk ->
            val matchResult = MONKEY_ID_REGEX.find(chunk[MONKEY_ID_LINE])
            val id = MonkeyId(checkNotNull(matchResult?.groups?.get(MONKEY_ID_ID)).value.toInt())
            val startingItems = checkNotNull(
                STARTING_ITEMS_REGEX.find(chunk[STARTING_ITEMS_LINE])?.groups?.get(STARTING_ITEMS_ID),
            ).value.split(", ").map { it.toInt() }
            val operationGroups = checkNotNull(OPERATION_REGEX.find(chunk[OPERATION_LINE])?.groups)
            val opLhs = checkNotNull(
                operationGroups[OPERATION_LHS_ID]?.value?.let {
                    if (it == "old") MonkeyMathWorryOperand else MonkeyMathValueOperand(it.toInt())
                },
            )
            val opOp = checkNotNull(
                operationGroups[OPERATION_OP_ID]?.value?.let {
                    if (it == "+") MonkeyMathOp.ADD else MonkeyMathOp.MULTIPLY
                },
            )
            val opRhs = checkNotNull(
                operationGroups[OPERATION_RHS_ID]?.value?.let {
                    if (it == "old") MonkeyMathWorryOperand else MonkeyMathValueOperand(it.toInt())
                },
            )
            val operation = MonkeyMath(opOp, opLhs, opRhs)
            val testDivisor = checkNotNull(
                TEST_DIVISOR_REGEX.find(chunk[TEST_DIVISOR_LINE])?.groups?.get(TEST_DIVISOR_ID),
            ).value.toInt()
            val testTrueDest = MonkeyId(
                checkNotNull(
                    BRANCH_REGEX.find(chunk[BRANCH_TRUE_LINE])?.groups?.get(BRANCH_DEST_ID),
                ).value.toInt(),
            )
            val testFalseDest = MonkeyId(
                checkNotNull(
                    BRANCH_REGEX.find(chunk[BRANCH_FALSE_LINE])?.groups?.get(BRANCH_DEST_ID),
                ).value.toInt(),
            )

            Monkey(id, startingItems, operation, testDivisor, testTrueDest, testFalseDest)
        }
        val finalState = (1..MONKEY_ROUNDS).fold(JungleState.fromMonkeys(monkeys)) { state, _ -> state.round() }
        finalState.monkeys.values.map { it.inspections }.sorted().takeLast(2).fold(1, Int::times)
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { 0 }
}

fun main() = solutionMain<Day11>()
