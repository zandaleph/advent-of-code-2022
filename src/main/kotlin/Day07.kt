class Day07 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 95437

        private const val MAX_SIZE = 100000
        private const val CMD_CD_ROOT = "$ cd /"
        private const val CMD_CD_UP = "$ cd .."
        private const val CMD_CD = "$ cd "
        private const val CMD_LS = "$ ls"
    }

    private sealed interface FileSystemEntry {
        val name: String
        val parentDir: String
        val size: Int
    }

    private data class File(
        override val name: String,
        override val parentDir: String,
        override val size: Int,
    ) : FileSystemEntry

    private data class Dir(
        override val name: String,
        override val parentDir: String,
        val children: Map<String, FileSystemEntry> = mapOf(),
    ) : FileSystemEntry {
        override val size = children.values.sumOf { it.size }

        fun forEach(block: (FileSystemEntry) -> Unit) {
            block(this)
            children.forEach { (_, v) ->
                when (v) {
                    is Dir -> v.forEach(block)
                    is File -> block(v)
                }
            }
        }
    }

    private data class Part1State(
        val dirs: Map<String, Dir> = mapOf("/" to Dir("/", "")),
        val curDir: String = "/",
    )

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        var sum = 0
        lines.fold(Part1State()) { state, line ->
            when {
                line.startsWith(CMD_LS) -> state
                line.startsWith(CMD_CD_ROOT) -> state.copy(curDir = "/")
                line.startsWith(CMD_CD_UP) -> {
                    val parentDir = checkNotNull(state.dirs[state.curDir]).parentDir
                    check(state.dirs.contains(parentDir))
                    state.copy(curDir = parentDir)
                }

                line.startsWith(CMD_CD) -> {
                    val dirName = state.curDir + line.removePrefix(CMD_CD).trim() + "/"
                    val dir = checkNotNull(state.dirs[dirName])
                    check(dir.parentDir == state.curDir)
                    state.copy(curDir = dirName)
                }

                else -> {
                    val (size, name) = line.split(' ', limit = 2)
                    val newEntry = when (size) {
                        "dir" -> Dir("$name/", state.curDir)
                        else -> File(name, state.curDir, size.toInt())
                    }
                    val updatedDirs = generateSequence(newEntry) {
                        state.dirs[it.parentDir]?.run { copy(children = children + (it.name to it)) }
                    }.filterIsInstance<Dir>().associateBy { it.parentDir + it.name }
                    state.copy(dirs = state.dirs + updatedDirs)
                }
            }
        }.dirs["/"]?.forEach {
            if (it is Dir && it.size <= MAX_SIZE) {
                sum += it.size
            }
        }
        sum
    }

    override val part2 = SolutionPart { 0 }
}

fun main() = solutionMain<Day07>(true)
