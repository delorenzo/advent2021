import java.io.File
import java.lang.IllegalArgumentException
import java.math.BigInteger

var VERSIONS = 0

val BITSMap = mapOf(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111",
)
fun main() {
    val input = File("src/input/input16-1.txt").readLines().map { line -> line.toCharArray().map{
        hexToBit(it.toString())
    }}.flatten().joinToString(separator="")
    println(input)

    val queue = ArrayDeque<Packet>()
    val ins = ArrayDeque<String>()
    val root = Packet(0, 0, lastIndex = input.length, parent = null)
    queue.add(root)
    while (!queue.isEmpty()) {
        val next = queue.removeLast()
        parsePacket(next, input, queue, instructions = ins)
    }
    println("Part 1:  $VERSIONS")
    println(ins)

    val part2 = ArrayDeque<Char>(File("src/input/input16-2.txt").readLines().map { it.toCharArray().toList() }.flatten().flatMap { c: Char ->  BITSMap[c]!!.split("") }.filterNot { it.isEmpty() }.map { it.single() })
    println(part2.parsePacketRecursive())
}

fun ArrayDeque<Char>.parsePacketRecursive() : Pair<Int, Long> {
    var current = ""
    val version = ("" + removeFirst() + removeFirst() + removeFirst()).toInt(2)
    VERSIONS += version
    println("Version total:  $VERSIONS")
    val typeId = ("" + removeFirst() + removeFirst() + removeFirst()).toString().toInt(2)
    println("Version: $version  Type:  $typeId")
    val subPackets = mutableListOf<Long>()
    if (typeId == 4) {
        val literal = mutableListOf<String>()
        var current = ""
        while (true) {
            current += ("" + removeFirst() + removeFirst() + removeFirst() + removeFirst() + removeFirst())
            literal.add(current.takeLast(5))
            current = ""
            if (literal.last().startsWith('0')) break
        }
        val literalValue = literal.joinToString ("") { it.takeLast(4) }.toLong(2)
        subPackets.add(literalValue)
    } else {
        when (removeFirst().toString()) {
            "0" -> {
                current = ""
                for (i in 1..15) {  current += removeFirst()  }
                val length = current.toInt(2)
                println(length)
                var subBits = ArrayDeque<Char>()
                for (i in 1..length) {
                    subBits += removeFirst()
                }
                while (subBits.isNotEmpty()) {
                    if (subBits.all { it == '0' }) break
                    val p = subBits.parsePacketRecursive()
                    subPackets += p.second
                }
            }
            "1" -> {
                println("Length type:  1")
                current = ""
                for (i in 1..11) {  current += removeFirst()  }
                val numPackets = current.toInt(2)

                for (i in 0 until numPackets) {
                    val packet = parsePacketRecursive()
                    subPackets += packet.second
                }
            }
            else -> throw Exception("No thank you")
        }
    }
    return version to operate(subPackets, typeId)
}

fun parsePacket(packet: Packet, input: String, packets: ArrayDeque<Packet>, instructions: ArrayDeque<String>) {
    if (packet.startingIndex + 7 > input.length) { return }
    val version = input.subSequence(packet.startingIndex, packet.startingIndex+3).toString().toInt(2)
    VERSIONS += version
    println("Version total:  $VERSIONS")
    val typeId = input.subSequence(packet.startingIndex+3, packet.startingIndex+6).toString().toInt(2)
    packet.typeId = typeId
    println("Version: $version  Type:  $typeId")
    if (typeId == 4) {
        val value = getValuePacket(packet, packet.startingIndex+6, input, packets, instructions)
        instructions.add(value.toLong().toString())
        println("Value packet:  $value")
    } else {
        when (typeId) {
            0 -> instructions.add("sum")
            1 -> instructions.add("x")
            2 ->  instructions.add("min")
            3 -> instructions.add("max")
            5 -> instructions.add(">")
            6 -> instructions.add("<")
            7 -> instructions.add("=")
            else -> {}
        }
        when (input.subSequence(packet.startingIndex+6, packet.startingIndex+7)) {
            "0" -> {
                println("Length type:  0")
                var firstIndex = packet.startingIndex+7
                var lastIndex = firstIndex + 15
                if (lastIndex > input.length) { return }
                val length = input.subSequence(firstIndex, lastIndex).toString().toInt(2)
                println(length)
                instructions.add("{$length}")
                val newPacket = Packet(lastIndex, 0,  lastIndex = lastIndex+length, parentIndex = packet.lastIndex, remaining = packet.remaining, parent = packet)
                packet.children.add(newPacket)
                packets.add(newPacket)
            }
            "1" -> {
                println("Length type:  1")
                var firstIndex = packet.startingIndex + 7
                var lastIndex = firstIndex + 11
                if (lastIndex > input.length) { return }
                val numPackets = input.subSequence(firstIndex, lastIndex).toString().toInt(2)
                println("Num packets:  $numPackets")
                instructions.add("[$numPackets]")
                val newPacket = Packet(lastIndex, 1, lastIndex = packet.lastIndex, remaining = numPackets + packet.remaining, siblingsRemaining = numPackets-1, parent = packet)
                packet.children.add(newPacket)
                packets.add(newPacket)
            }
            else -> throw Exception("No thank you")
        }
    }
}

fun getValuePacket(packet: Packet, startingIndex: Int, input: String, packets: ArrayDeque<Packet>, instructions: ArrayDeque<String>) : BigInteger {
    var startingIndex = startingIndex
    val sb = StringBuilder()
    while (true) {
        val firstDigit = input[startingIndex]
        // maybe check length here
        sb.append(input.subSequence(startingIndex+1, startingIndex +5).toString())
        if (firstDigit == '0') {
            if (packet.remaining > 0) {
                if (packet.siblingsRemaining > 0) {
                    val newPacket =
                        Packet(startingIndex + 5, 1, remaining = packet.remaining - 1, siblingsRemaining = packet.siblingsRemaining -1, parent = packet.parent)
                    packet.parent?.children?.add(newPacket)
                    packets.add(newPacket)
                }
                else {
                    packet.remaining --
                    var current = packet
                    while (current.siblingsRemaining == 0 && current.parent != null) {
                        current = current.parent!!
                    }
                    val newPacket =
                        Packet(startingIndex + 5, 1, remaining = current.remaining - 1, siblingsRemaining = current.siblingsRemaining-1, parent = current.parent)
                    current.parent?.children?.add(newPacket)
                    packets.add(newPacket)
                }
            } else if (startingIndex + 12 < packet.lastIndex){
                val newPacket = Packet(startingIndex+5, 0, lastIndex = packet.lastIndex, parent = packet.parent)
                packet.parent?.children?.add(newPacket)
                packets.add(newPacket)
            } else if (startingIndex + 12 < packet.parentIndex) {
                var current = packet
                while (startingIndex + 12 < packet.lastIndex && current.parent != null) {
                    current = current.parent!!
                }
                val newPacket = Packet(startingIndex+5, 0, lastIndex = current.lastIndex, parent = current.parent)
                current.parent?.children?.add(newPacket)
                packets.add(newPacket)
            }
            println(sb.toString())
            packet.computedValue = BigInteger(sb.toString(), 2)
            return packet.computedValue
        }
        startingIndex+=5
    }
}

data class Packet(val startingIndex: Int, val type: Int, val lastIndex: Int = 0, var remaining: Int = 0,
                  val siblingsRemaining: Int = 0,
                  val parentIndex: Int = 0,
                  val parent: Packet?, val children: MutableList<Packet> = mutableListOf()
) {
    lateinit var computedValue: BigInteger
    fun valueInitialized() : Boolean { return this::computedValue.isInitialized }

    var typeId : Int = -1
}

fun hexToBit(it: String) : String {
    return it.toInt(16).toString(2).padStart(4, '0')
}

fun operate(current: List<Long>, type: Int) : Long {
    return when (type) {
        0 -> {
            current.sumOf { it }
        }
        1 -> {
            return current.map { it }.fold(1L) { a, b ->
                a.times(
                    b
                )
            }
        }
        2 -> {
            return current.minOf { it }
        }
        3 -> {
            return current.maxOf { it }
        }
        4 -> {
            return current[0]
        }
        5 -> {
            if (current.size != 2) {
                throw IllegalArgumentException("more than 2 packets for op")
            }
            val compare = current.first().compareTo(current[1])
            return when (compare) {
                1 -> 1L
                else -> 0L
            }
        }
        6 -> {
            if (current.size != 2) {
                throw IllegalArgumentException("more than 2 packets for op")
            }
            val compare = current.first().compareTo(current[1])
            return when (compare) {
                -1 -> 1L
                else -> 0L
            }
        }
        7 -> {
            if (current.size != 2) {
                throw IllegalArgumentException("more than 2 packets for op")
            }
            val compare = current.first().compareTo(current[1])
            return when (compare) {
                0 -> 1L
                else -> 0L
            }
        }
        else -> current.first()
    }
}