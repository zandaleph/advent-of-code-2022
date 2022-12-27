class Day10 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 13140

        private const val FIRST_MEASURE_CYCLE = 20
        private const val LAST_MEASURE_CYCLE = 220
        private const val MEASURE_CYCLE_STEP = 40
        private val measureCycles =
            IntProgression.fromClosedRange(FIRST_MEASURE_CYCLE, LAST_MEASURE_CYCLE, MEASURE_CYCLE_STEP)

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

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        val instructions = lines.asSequence().map { line ->
            when {
                line.startsWith(NOOP_CODE) -> Noop
                line.startsWith(ADDX_CODE_PREFIX) -> AddX(line.removePrefix(ADDX_CODE_PREFIX).toInt())
                else -> error("Invalid code: $line")
            }
        }
        val cpuStates = sequence {
            instructions.fold(CpuState()) { state, inst ->
                repeat(inst.cycles) { yield(state) }
                inst.operation(state)
            }
        }
        cpuStates.filterIndexed { idx, _ -> measureCycles.contains(idx + 1) }
            .zip(measureCycles.asSequence())
            .map { (state, cycle) -> state.x * cycle }
            .sum()
    }

    override val part2 = SolutionPart { 0 }
}

fun main() = solutionMain<Day10>()
