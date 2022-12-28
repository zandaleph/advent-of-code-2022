import java.util.LinkedList
import java.util.Queue

class Day12 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 31
    }

    private operator fun <T> List<List<T>>.get(coords: Pair<Int, Int>): T? =
        this[coords.first][coords.second]

    private operator fun MutableList<MutableList<Int?>>.set(coords: Pair<Int, Int>, value: Int?) {
        this[coords.first][coords.second] = value
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        val terrain = lines.map { it.map { c -> if (c == 'S') 'a' - 1 else c } }
        val numRows = terrain.size
        val numCols = terrain.first().size
        check(terrain.all { it.size == numCols })
        val ends = terrain.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, char ->
                if (char == 'E') row to col else null
            }
        }
        check(ends.size == 1)
        val end = ends.first()
        val starts = terrain.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, char ->
                if (char == 'a' - 1) row to col else null
            }
        }
        check(starts.size == 1)
        val start = starts.first()

        val pathState: MutableList<MutableList<Int?>> = MutableList(numRows) { MutableList(numCols) { null } }
        pathState[end] = 0
        val toVisit: Queue<Pair<Int, Int>> = LinkedList()
        toVisit.add(end)
        while (toVisit.isNotEmpty()) {
            val visiting = toVisit.remove()
            val nextHeight = (terrain[visiting]!! - 1).let { if (it == 'D') 'z' else it }
            val nextValue = checkNotNull(pathState[visiting]) + 1
            val nextVisits = mutableListOf<Pair<Int, Int>>()
            visiting.copy(first = visiting.first - 1).let { if (it.first >= 0) nextVisits.add(it) }
            visiting.copy(first = visiting.first + 1).let { if (it.first < numRows) nextVisits.add(it) }
            visiting.copy(second = visiting.second - 1).let { if (it.second >= 0) nextVisits.add(it) }
            visiting.copy(second = visiting.second + 1).let { if (it.second < numCols) nextVisits.add(it) }
            nextVisits.forEach { coord ->
                if (pathState[coord] == null && terrain[coord]!! >= nextHeight) {
                    pathState[coord] = nextValue
                    toVisit.add(coord)
                }
            }
        }

//        pathState.forEach { line -> println(line.joinToString(" ") { it.toString().padStart(2, '0') }) }

        pathState[start]!!
    }

    override val part2 = SolutionPart { 0 }
}

fun main() = solutionMain<Day12>()
