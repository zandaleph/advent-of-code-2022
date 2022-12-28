import java.util.LinkedList
import java.util.Queue

typealias Coord = Pair<Int, Int>

class Day12 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 31
        private const val TEST_OUTPUT_2 = 29
    }

    private operator fun <T> List<List<T>>.get(coord: Coord): T? =
        this[coord.first][coord.second]

    private operator fun MutableList<MutableList<Int?>>.set(coord: Coord, value: Int?) {
        this[coord.first][coord.second] = value
    }

    private fun <T> List<List<T>>.findAll(t: T): List<Coord> =
        flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, v ->
                if (v == t) row to col else null
            }
        }

    private fun <T> List<T>.only(): T = this.also { check(it.size == 1) }.first()

    private fun Coord.neighbors(numRows: Int, numCols: Int): List<Coord> =
        mutableListOf<Coord>().also { result ->
            (first - 1).let { if (it >= 0) result.add(copy(first = it)) }
            (first + 1).let { if (it < numRows) result.add(copy(first = it)) }
            (second - 1).let { if (it >= 0) result.add(copy(second = it)) }
            (second + 1).let { if (it < numCols) result.add(copy(second = it)) }
        }

    private fun chartTerrain(terrain: List<List<Char>>): List<List<Int?>> {
        val numRows = terrain.size
        val numCols = terrain.first().size
        check(terrain.all { it.size == numCols })
        val end = terrain.findAll('E').only()

        val pathState: MutableList<MutableList<Int?>> = MutableList(numRows) { MutableList(numCols) { null } }
        pathState[end] = 0
        val toVisit: Queue<Coord> = LinkedList()
        toVisit.add(end)
        while (toVisit.isNotEmpty()) {
            val visiting = toVisit.remove()
            val nextHeight = (terrain[visiting]!! - 1).let { if (it == 'D') 'z' else it }
            val nextValue = checkNotNull(pathState[visiting]) + 1
            visiting.neighbors(numRows, numCols).forEach { coord ->
                if (pathState[coord] == null && terrain[coord]!! >= nextHeight) {
                    pathState[coord] = nextValue
                    toVisit.add(coord)
                }
            }
        }

//        pathState.forEach { line -> println(line.joinToString(" ") { (it ?: -1).toString().padStart(2, '0') }) }
        return pathState
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        val terrain = lines.map { it.map { c -> if (c == 'S') 'a' - 1 else c } }
        val start = terrain.findAll('a' - 1).only()
        chartTerrain(terrain)[start]!!
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { lines ->
        val terrain = lines.map { it.map { c -> if (c == 'S') 'a' - 1 else c } }
        val chart = chartTerrain(terrain)
        // this is lazy - chartTerrain is greedy; we could short-circuit
        // upon finding an 'a' but that would require more work to write
        terrain.findAll('a').asSequence().mapNotNull { chart[it] }.min()
    }
}

fun main() = solutionMain<Day12>()
