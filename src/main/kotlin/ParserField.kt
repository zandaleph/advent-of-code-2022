class ParserField<T : Any>(
    val pattern: String,
    val parser: String.() -> T,
)
