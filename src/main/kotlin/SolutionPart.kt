class SolutionPart<T>(
    val testOutput: T? = null,
    val compute: (input: List<String>) -> T,
)
