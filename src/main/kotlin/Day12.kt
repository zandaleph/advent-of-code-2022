import java.util.LinkedList
import java.util.Queue

class Day12 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 31
        private const val TEST_OUTPUT_2 = 29
    }

    private fun chartTerrain(terrain: List<List<Char>>): List<List<Int?>> {
        val (numRows, numCols) = terrain.gridSize()
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
