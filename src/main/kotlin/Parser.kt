abstract class Parser {
    abstract val pattern: String

    val regex by lazy { pattern.toRegex() }

    private val fields: MutableMap<ParserField<*>, String> = mutableMapOf()

    protected fun field(f: ParserField<*>): String {
        val id = "f${fields.size}"
        check(fields.put(f, id) == null) { "Duplicate field: $f" }
        return "(?<$id>${f.pattern})"
    }

    fun parse(text: String): ParseResult {
        val result = regex.find(text) ?: error("Could not parse $text with $regex")
        return ParseResult(fields.mapValues { (k, v) -> k.parser(checkNotNull(result.groups[v]).value) })
    }
}
