class Day14 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 24

        private const val SAND_ENTRY_COL = 500
        private val next_cols = sequenceOf(0, -1, 1)

        private const val AIR = '.'
        private const val ROCK = '#'
        private const val SAND = 'o'
    }

    private fun parseGrid(lines: List<String>): Pair<Int, MutableList<MutableList<Char>>> {
        val rockForms: List<List<Coord>> = lines.map { line ->
            line.split(" -> ").map {
                it.split(",", limit = 2).let { p -> p[1].toInt() to p[0].toInt() }
            }
        }

        val maxRow = rockForms.maxOf { form -> form.maxOf { it.row } }
        val numRows = maxRow + 1

        val minCol = rockForms.minOf { form -> form.minOf { it.col } } - 1
        val maxCol = rockForms.maxOf { form -> form.maxOf { it.col } } + 1
        val numCols = maxCol - minCol + 1

        val grid = MutableList(numRows) { MutableList(numCols) { AIR } }

        rockForms.forEach { form ->
            form.windowed(2).forEach { line ->
                drawLine(line, grid, minCol)
            }
        }

        return minCol to grid
    }

    private fun drawLine(
        line: List<Coord>,
        grid: MutableList<MutableList<Char>>,
        minCol: Int,
    ) {
        val (from, to) = line
        if (from.row == to.row) {
            val range = listOf(from.col, to.col).sorted().let { (a, b) -> a..b }
            range.forEach { col -> grid[from.row to (col - minCol)] = ROCK }
        } else {
            check(from.col == to.col)
            val range = listOf(from.row, to.row).sorted().let { (a, b) -> a..b }
            range.forEach { row -> grid[row to (from.col - minCol)] = ROCK }
        }
    }

    private fun dropSand(grid: List<List<Char>>, entryCol: Int): Coord? {
        var cur = 0 to entryCol
        while (cur.row < grid.size - 1) {
            val nextRow = cur.row + 1
            cur = next_cols.mapNotNull { col ->
                (nextRow to (cur.col + col)).let { if (grid[it] == AIR) it else null }
            }.firstOrNull() ?: break
        }
        return if (cur.row < grid.size - 1) cur else null
    }

//    private fun printGrid(grid: List<List<Char>>) {
//        grid.forEach { println(it.joinToString("")) }
//        println()
//    }

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        val (minCol, grid) = parseGrid(lines)
//        printGrid(grid)
        val entryCol = SAND_ENTRY_COL - minCol
        var grains = 0
        while (true) {
            val nextLoc = dropSand(grid, entryCol) ?: break
            grid[nextLoc] = SAND
//            printGrid(grid)
            grains++
        }
        grains
    }

    override val part2 = SolutionPart { 0 }
}

fun main() = solutionMain<Day14>()
