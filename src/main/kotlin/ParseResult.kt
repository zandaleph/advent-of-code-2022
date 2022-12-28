data class ParseResult(private val fields: Map<ParserField<*>, Any>) {
    operator fun <T : Any> get(field: ParserField<T>): T {
        @Suppress("UNCHECKED_CAST")
        return fields[field] as T
    }
}
