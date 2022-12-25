class Day07 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 95437
        private const val TEST_OUTPUT_2 = 24933642

        private const val MAX_SIZE = 100000
        private const val TOTAL_SIZE = 70000000
        private const val MIN_FREE_SIZE = 30000000

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

        fun depthFirst(): Sequence<FileSystemEntry> = sequence {
            yield(this@Dir)
            children.forEach { (_, v) ->
                when (v) {
                    is Dir -> yieldAll(v.depthFirst())
                    is File -> yield(v)
                }
            }
        }
    }

    private data class ParseState(
        val dirs: Map<String, Dir> = mapOf("/" to Dir("/", "")),
        val curDir: String = "/",
    )

    private fun parseLines(lines: List<String>): Dir =
        lines.fold(ParseState()) { state, line ->
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
        }.dirs["/"]!!

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        parseLines(lines)
            .depthFirst()
            .filterIsInstance<Dir>()
            .filter { it.size <= MAX_SIZE }
            .sumOf { it.size }
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { lines ->
        parseLines(lines).let { rootDir ->
            val spaceToFree = MIN_FREE_SIZE - (TOTAL_SIZE - rootDir.size)
            rootDir.depthFirst()
                .filterIsInstance<Dir>()
                .filter { it.size > spaceToFree }
                .minOf { it.size }
        }
    }
}

fun main() = solutionMain<Day07>(true)
