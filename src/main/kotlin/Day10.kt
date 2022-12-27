import kotlin.math.abs

class Day10 : Solution<String> {

    companion object {
        private const val TEST_OUTPUT = "13140"
        private val TEST_OUTPUT_2 = """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
        """.trimIndent().replace('.', ' ').replace('#', '█')

        private const val FIRST_MEASURE_CYCLE = 20
        private const val LAST_MEASURE_CYCLE = 220
        private const val MEASURE_CYCLE_STEP = 40
        private val measureCycles =
            IntProgression.fromClosedRange(FIRST_MEASURE_CYCLE, LAST_MEASURE_CYCLE, MEASURE_CYCLE_STEP)

        private const val TOTAL_CYCLES = 240

        private const val NOOP_CODE = "noop"
        private const val ADDX_CODE_PREFIX = "addx "
    }

    sealed interface Instruction {
        val cycles: Int
        fun operation(state: CpuState): CpuState
    }

    object Noop : Instruction {
        override val cycles = 1
        override fun operation(state: CpuState) = state
    }

    data class AddX(val value: Int) : Instruction {
        override val cycles = 2
        override fun operation(state: CpuState) = state.copy(x = state.x + value)
    }

    data class CpuState(
        val x: Int = 1,
    )

    private fun cpuStates(lines: List<String>) = sequence<CpuState> {
        lines.asSequence().map { line ->
            when {
                line.startsWith(NOOP_CODE) -> Noop
                line.startsWith(ADDX_CODE_PREFIX) -> AddX(line.removePrefix(ADDX_CODE_PREFIX).toInt())
                else -> error("Invalid code: $line")
            }
        }.fold(CpuState()) { state, inst ->
            repeat(inst.cycles) { yield(state) }
            inst.operation(state)
        }
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        cpuStates(lines).filterIndexed { idx, _ -> measureCycles.contains(idx + 1) }
            .zip(measureCycles.asSequence())
            .map { (state, cycle) -> state.x * cycle }
            .sum().toString()
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { lines ->
        cpuStates(lines).take(TOTAL_CYCLES)
            .chunked(MEASURE_CYCLE_STEP)
            .joinToString(separator = "\n") {
                it.mapIndexed { idx, state -> if (abs(idx - state.x) <= 1) '█' else ' ' }
                    .joinToString(separator = "")
            }
    }
}

fun main() = solutionMain<Day10>()
