class ParserField<T : Any>(
    val pattern: String,
    val parser: String.() -> T,
) {
    override fun equals(other: Any?) = this === other
    override fun hashCode() = super.hashCode()
}
