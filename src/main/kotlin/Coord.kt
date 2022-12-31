typealias Coord = Pair<Int, Int>

val Coord.row
    inline get() = first

val Coord.col
    inline get() = second

fun Coord.copy(row: Int? = null, col: Int? = null) = (row ?: this.row) to (col ?: this.col)

operator fun <T> List<List<T>>.get(coord: Coord): T? =
    this[coord.row][coord.col]

operator fun List<String>.get(coord: Coord): Char = this[coord.row][coord.col]

operator fun <T> MutableList<MutableList<T>>.set(coord: Coord, value: T) {
    this[coord.row][coord.col] = value
}

fun <T> List<List<T>>.findAll(t: T): List<Coord> =
    flatMapIndexed { row, line ->
        line.mapIndexedNotNull { col, v ->
            if (v == t) row to col else null
        }
    }

fun Coord.neighbors(numRows: Int, numCols: Int): List<Coord> =
    mutableListOf<Coord>().also { result ->
        (row - 1).let { if (it >= 0) result.add(copy(row = it)) }
        (row + 1).let { if (it < numRows) result.add(copy(row = it)) }
        (col - 1).let { if (it >= 0) result.add(copy(col = it)) }
        (col + 1).let { if (it < numCols) result.add(copy(col = it)) }
    }

fun <T> List<List<T>>.gridSize(): Coord = (size to first().size).apply { check(all { it.size == second }) }

@JvmName("gridSizeInput")
fun List<String>.gridSize(): Coord = (size to first().length).apply { check(all { it.length == second }) }
