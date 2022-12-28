import java.util.SortedMap

class Day11 : Solution<Long> {

    companion object {
        private const val TEST_OUTPUT = 10605L
        private const val TEST_OUTPUT_2 = 2713310158L

        private const val WORRY_RELIEF = 3L

        private const val MONKEY_DESCRIPTION_LINES = 7
        private const val MONKEY_ROUNDS = 20
        private const val MONKEY_ROUNDS_2 = 10000
    }

    object MonkeyIdParser : Parser() {
        const val LINE = 0
        val ID = ParserField("\\d+") { MonkeyId(toInt()) }
        override val pattern = "Monkey ${field(ID)}:"
    }

    object StartingItemsParser : Parser() {
        const val LINE = 1
        val ITEMS = ParserField("\\d+(, \\d+)*") { split(", ").map { it.toLong() } }
        override val pattern = "  Starting items: ${field(ITEMS)}"
    }

    object OperationParser : Parser() {
        const val LINE = 2
        private fun String.parseOperand() =
            if (this == "old") MonkeyMathWorryOperand else MonkeyMathValueOperand(toLong())

        val OPERATOR = ParserField("\\+|\\*") { if (this == "+") MonkeyMathOp.ADD else MonkeyMathOp.MULTIPLY }
        val OP_LHS = ParserField("\\d+|(old)") { parseOperand() }
        val OP_RHS = ParserField("\\d+|(old)") { parseOperand() }

        override val pattern = "  Operation: new = ${field(OP_LHS)} ${field(OPERATOR)} ${field(OP_RHS)}"
    }

    object TestDivisorParser : Parser() {
        const val LINE = 3
        val DIVISOR = ParserField("\\d+") { toLong() }
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

    enum class MonkeyMathOp(val apply: (Long, Long) -> Long) {
        ADD(Long::plus),
        MULTIPLY(Long::times);
    }

    sealed interface MonkeyMathOperand {
        fun value(worry: Long): Long
    }

    object MonkeyMathWorryOperand : MonkeyMathOperand {
        override fun value(worry: Long) = worry
    }

    data class MonkeyMathValueOperand(private val value: Long) : MonkeyMathOperand {
        override fun value(worry: Long) = value
    }

    data class MonkeyMath(
        val op: MonkeyMathOp,
        val lhs: MonkeyMathOperand,
        val rhs: MonkeyMathOperand,
    ) {
        fun compute(worry: Long): Long {
            return op.apply(lhs.value(worry), rhs.value(worry))
        }
    }

    data class Monkey(
        val id: MonkeyId,
        val startingItems: List<Long>,
        val operation: MonkeyMath,
        val testDivisor: Long,
        val testTrueDest: MonkeyId,
        val testFalseDest: MonkeyId,
        val isWorryRelieved: Boolean = true,
    ) {
        fun inspect(worry: Long): Pair<Long, MonkeyId> {
            val newWorry = operation.compute(worry).let { if (isWorryRelieved) it / WORRY_RELIEF else it }
            val newMonkey = if (newWorry % testDivisor == 0L) testTrueDest else testFalseDest
            return newWorry to newMonkey
        }
    }

    data class MonkeyState(
        val monkey: Monkey,
        val heldItems: List<Long> = monkey.startingItems,
        val inspections: Long = 0L,
    ) {
        fun turn(totalDivisor: Long): Pair<MonkeyState, Map<MonkeyId, List<Long>>> {
            val newState = copy(heldItems = listOf(), inspections = inspections + heldItems.size)
            val tosses = heldItems.map { monkey.inspect(it) }.groupBy({ it.second }) { it.first % totalDivisor }
            return newState to tosses
        }
    }

    data class JungleState(
        val monkeys: SortedMap<MonkeyId, MonkeyState>,
        val totalDivisor: Long = monkeys.values.map { it.monkey.testDivisor }.reduce { a, b -> a * b },
    ) {
        companion object {
            fun fromMonkeys(monkeys: List<Monkey>) =
                JungleState(monkeys.associate { it.id to MonkeyState(it) }.toSortedMap())
        }

        fun round(): JungleState {
            return monkeys.keys.fold(this) { state, monkeyId ->
                val (newMonkey, tosses) = state.monkeys[monkeyId]!!.turn(totalDivisor)
                val newMonkeys = (state.monkeys + (newMonkey.monkey.id to newMonkey)).mapValues { (id, curMonkey) ->
                    curMonkey.copy(heldItems = curMonkey.heldItems + (tosses[id] ?: listOf()))
                }.toSortedMap()
                copy(monkeys = newMonkeys)
            }
        }
    }

    private fun parseMonkeys(lines: List<String>): List<Monkey> {
        return lines.chunked(MONKEY_DESCRIPTION_LINES).map { chunk ->
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
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        val monkeys = parseMonkeys(lines)
        val finalState = (1..MONKEY_ROUNDS).fold(JungleState.fromMonkeys(monkeys)) { state, _ -> state.round() }
        finalState.monkeys.values.map { it.inspections }.sorted().takeLast(2).reduce { a, b -> a * b }
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { lines ->
        val monkeys = parseMonkeys(lines).map { it.copy(isWorryRelieved = false) }
        val finalState = (1..MONKEY_ROUNDS_2).fold(JungleState.fromMonkeys(monkeys)) { state, _ -> state.round() }
        finalState.monkeys.values.map { it.inspections }.sorted().takeLast(2).reduce { a, b -> a * b }
    }
}

fun main() = solutionMain<Day11>(true)
