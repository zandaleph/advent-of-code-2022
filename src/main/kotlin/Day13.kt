class Day13 : Solution<Int> {

    companion object {
        private const val TEST_OUTPUT = 13
        private const val TEST_OUTPUT_2 = 140

        private const val CHUNK_SIZE = 3

        private const val DIVISOR_1 = 2
        private const val DIVISOR_2 = 6
    }

    sealed interface Packet : Comparable<Packet> {
        override operator fun compareTo(other: Packet): Int
    }

    data class ListPacket(val list: List<Packet> = listOf()) : Packet {
        constructor(onlyItem: Packet) : this(listOf(onlyItem))

        override operator fun compareTo(other: Packet): Int =
            when (other) {
                is ListPacket -> compareList(other)
                is IntPacket -> this.compareTo(ListPacket(other))
            }

        private fun compareList(other: ListPacket) =
            list.asSequence().zip(other.list.asSequence())
                .map { (l, r) -> l.compareTo(r) }
                .filter { it != 0 }
                .firstOrNull() ?: list.size.compareTo(other.list.size)

        override fun equals(other: Any?): Boolean {
            return when (other) {
                is ListPacket -> compareTo(other) == 0
                else -> false
            }
        }

        override fun hashCode(): Int {
            return list.hashCode()
        }
    }

    data class IntPacket(val value: Int) : Packet {

        override operator fun compareTo(other: Packet): Int =
            when (other) {
                is ListPacket -> ListPacket(this).compareTo(other)
                is IntPacket -> value.compareTo(other.value)
            }

        override fun equals(other: Any?): Boolean {
            return when (other) {
                is IntPacket -> compareTo(other) == 0
                else -> false
            }
        }

        override fun hashCode(): Int {
            return value
        }
    }

    // 0 = start item, 1 = start list, 2 = end list, 3 = mid-number
    // state 0    1    2    3
    // [     1+1  1+1  E    E
    // ]     E    2-1  2-1  2-1
    // ,     E    E    0    0
    // \d    3    3    E    3
    // EOF   E    E    C@0  C@0

    // [[],1,23]
    //   ...  S()->null
    // [ List[...]?  S()->N()""->null
    // [ List[List[...]?]?  S()->N()""->N()""->null
    // ] List[List[]?]? N([])""->N()""->null
    // , List[List[],...]? S([])->N()""->null
    // 1 List[List[], 1?]? N([])"1"->N()""->null
    // , List[List[], 1,...]? S([],1)->N()""->null
    // 2 List[List[], 1, 2?]? N([],1)"2"->N()""->null
    // 3 List[List[], 1, 2?]? N([],1)"23"->N()""->null
    // ] List[List[], 1, 2]? N([[],1,23])""->null
    sealed interface PacketParserState {
        fun nextChar(c: Char): PacketParserState
        fun toPacket(): Packet
    }

    data class StartPacketParserState(
        val parent: NextPacketParserState? = null,
        val prev: List<Packet> = listOf(),
    ) : PacketParserState {
        override fun nextChar(c: Char): PacketParserState {
            return when (c) {
                '[' -> StartPacketParserState(NextPacketParserState(parent, prev))
                ']' -> {
                    check(prev.isEmpty()) { "']' only valid after '[' or number" }
                    checkNotNull(parent).copy(prev = parent.prev + ListPacket())
                }

                in '0'..'9' -> NextPacketParserState(parent, prev, c.toString())
                else -> error("Invalid next character: '$c'")
            }
        }

        override fun toPacket(): Packet {
            error("Cannot convert to packet at beginning of packet")
        }
    }

    data class NextPacketParserState(
        val parent: NextPacketParserState?,
        val prev: List<Packet> = listOf(),
        val digits: String = "",
    ) : PacketParserState {
        override fun nextChar(c: Char): PacketParserState {
            return when (c) {
                in '0'..'9' -> copy(digits = digits + c)
                ',' -> StartPacketParserState(parent, getList())
                ']' -> checkNotNull(parent).copy(prev = parent.prev + ListPacket(getList()))
                else -> error("Invalid next character: '$c'")
            }
        }

        private fun getList() = if (digits.isNotEmpty()) prev + IntPacket(digits.toInt()) else prev

        override fun toPacket(): Packet {
            check(parent == null)
            return if (digits.isEmpty()) {
                ListPacket(prev)
            } else {
                check(prev.isEmpty())
                IntPacket(digits.toInt())
            }
        }
    }

    private fun parsePacket(raw: String) =
        raw.fold(StartPacketParserState(), PacketParserState::nextChar).toPacket()

    private fun isOrdered(chunk: List<String>): Boolean {
        check((chunk.size == CHUNK_SIZE && chunk[CHUNK_SIZE - 1].isEmpty()) || chunk.size == CHUNK_SIZE - 1)
        return parsePacket(chunk[0]) < parsePacket(chunk[1])
    }

    override val part1 = SolutionPart(TEST_OUTPUT) { lines ->
        lines.chunked(CHUNK_SIZE)
            .mapIndexedNotNull { idx, chunk -> if (isOrdered(chunk)) idx + 1 else null }
            .sum()
    }

    override val part2 = SolutionPart(TEST_OUTPUT_2) { lines ->
        val firstDivider = ListPacket(ListPacket(IntPacket(DIVISOR_1)))
        val secondDivider = ListPacket(ListPacket(IntPacket(DIVISOR_2)))
        val sorted = lines.filter { it.isNotEmpty() }
            .map { parsePacket(it) }
            .let { it + firstDivider + secondDivider }
            .sorted()
        val firstIndex = sorted.indexOf(firstDivider) + 1
        val secondIndex = sorted.indexOf(secondDivider) + 1
        firstIndex * secondIndex
    }
}

fun main() = solutionMain<Day13>()
