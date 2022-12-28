import java.util.SortedMap

class Day11 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 10605
        private const val TEST_OUTPUT_2 = 0

        private const val WORRY_RELIEF = 3

        private const val MONKEY_DESCRIPTION_LINES = 7
        private const val MONKEY_ROUNDS = 20
    }

    object MonkeyIdParser : Parser() {
        const val LINE = 0
        val ID = ParserField("\\d+") { MonkeyId(toInt()) }
        override val pattern = "Monkey ${field(ID)}:"
    }

    object StartingItemsParser : Parser() {
        const val LINE = 1
        val ITEMS = ParserField("\\d+(, \\d+)*") { split(", ").map { it.toInt() } }
        override val pattern = "  Starting items: ${field(ITEMS)}"
    }

    object OperationParser : Parser() {
        const val LINE = 2
        private fun String.parseOperand() =
            if (this == "old") MonkeyMathWorryOperand else MonkeyMathValueOperand(toInt())

        val OPERATOR = ParserField("\\+|\\*") { if (this == "+") MonkeyMathOp.ADD else MonkeyMathOp.MULTIPLY }
        val OP_LHS = ParserField("\\d+|(old)") { parseOperand() }
        val OP_RHS = ParserField("\\d+|(old)") { parseOperand() }

        override val pattern = "  Operation: new = ${field(OP_LHS)} ${field(OPERATOR)} ${field(OP_RHS)}"
    }

    object TestDivisorParser : Parser() {
        const val LINE = 3
        val DIVISOR = ParserField("\\d+") { toInt() }
        override val pattern = "  Test: divisible by ${field(DIVISOR)}"
    }

    object BranchParser : Parser() {
        const val TRUE_LINE = 4
        const val FALSE_LINE = 5
        val DEST = ParserField("\\d+") { MonkeyId(toInt()) }
        override val pattern = "    If (true|false): throw to monkey ${field(DEST)}"
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
            val id = MonkeyIdParser.run { parse(chunk[LINE])[ID] }
            val startingItems = StartingItemsParser.run { parse(chunk[LINE])[ITEMS] }
            val operation = OperationParser.run {
                parse(chunk[LINE]).let { MonkeyMath(it[OPERATOR], it[OP_LHS], it[OP_RHS]) }
            }
            val testDivisor = TestDivisorParser.run { parse(chunk[LINE])[DIVISOR] }
            val testTrueDest = BranchParser.run { parse(chunk[TRUE_LINE])[DEST] }
            val testFalseDest = BranchParser.run { parse(chunk[FALSE_LINE])[DEST] }
            Monkey(id, startingItems, operation, testDivisor, testTrueDest, testFalseDest)
        }
        val finalState = (1..MONKEY_ROUNDS).fold(JungleState.fromMonkeys(monkeys)) { state, _ -> state.round() }
        finalState.monkeys.values.map { it.inspections }.sorted().takeLast(2).fold(1, Int::times)
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { 0 }
}

fun main() = solutionMain<Day11>()
