import java.io.File
import java.lang.IllegalArgumentException
import java.math.BigInteger
import kotlin.properties.Delegates

//71,48,197,207
var VERSIONS = 0
fun main() {
    val input = File("src/input/input16-16.txt").readLines().map { line -> line.toCharArray().map{
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

    partTwo(root)
}

fun partTwo(root: Packet) {
    val queue = ArrayDeque<Packet>()
    queue.add(root)
    while (!queue.isEmpty()) {
        val current = queue.removeLast()
        when {
            current.valueInitialized() -> {} // don't do anything.  done!
            current.children.all { it.valueInitialized() } -> {
                // compute
                when (current.typeId) {
                    0 -> {
                        current.computedValue = current.children.sumOf { it.computedValue }
                    }
                    1 -> {
                        current.computedValue = current.children.map { it.computedValue }.fold(BigInteger.ONE) { a, b ->
                            a.times(
                                b
                            )
                        }
                    }
                    2 -> {
                        current.computedValue = current.children.minOf { it.computedValue }
                    }
                    3 -> {
                        current.computedValue = current.children.maxOf { it.computedValue }
                    }
                    4 -> {
                        println("Oops, this is a mistake packet")
                        current.parent?.children?.remove(current)
                    }
                    5 -> {
                        if (current.children.size != 2) { throw IllegalArgumentException("more than 2 packets for op") }
                        val compare = current.children.first().computedValue.compareTo(current.children[1].computedValue)
                        current.computedValue = when (compare) {
                            1 -> BigInteger.ONE
                            else -> BigInteger.ZERO
                        }
                    }
                    6 -> {
                        if (current.children.size != 2) { throw IllegalArgumentException("more than 2 packets for op") }
                        val compare = current.children.first().computedValue.compareTo(current.children[1].computedValue)
                        current.computedValue = when (compare) {
                            -1 -> BigInteger.ONE
                            else -> BigInteger.ZERO
                        }
                    }
                    7 -> {
                        if (current.children.size != 2) { throw IllegalArgumentException("more or less than 2 packets for op") }
                        val compare = current.children.first().computedValue.compareTo(current.children[1].computedValue)
                        current.computedValue = when (compare) {
                            0 -> BigInteger.ONE
                            else -> BigInteger.ZERO
                        }
                    }
                    else -> {
                        current.parent?.children?.remove(current)
                    }
                }
            }
            else -> {
                queue.addAll(current.children.filterNot { it.valueInitialized() })
                queue.add(current)
            } // back in the queue
        }
    }

    println("Root's value is ${root.computedValue}")
}

fun operate(instructions: ArrayDeque<String>) {
    val operators = ArrayDeque<String>()
    val values = ArrayDeque<String>()

    for (i in instructions) {
        when (i) {
            "sum", "x", ">", "<", "=" -> operators.add(i)
            else ->  values.add(i)
        }
    }
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
//                if (packet.type == 0) {
//                    val newPacket = Packet(lastIndex, 0,  lastIndex = packet.lastIndex, remaining = packet.remaining, parent = packet)
//                    packet.children.add(newPacket)
//                    packets.add(newPacket)
//                } else {
//                    val newPacket = Packet(lastIndex, 0,  lastIndex = lastIndex+length, remaining = packet.remaining, parent = packet)
//                    packet.children.add(newPacket)
//                    packets.add(newPacket)
//                }
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