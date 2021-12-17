import java.io.File
//71,48,197
var VERSIONS = 0
fun main() {
    val input = File("src/input/input16-2.txt").readLines().map { line -> line.toCharArray().map{
        hexToBit(it.toString())
    }}.flatten().joinToString(separator="")
    println(input)

    val queue = ArrayDeque<Packet>()
    queue.add(Packet(0, 0, lastIndex = input.length))
    while (!queue.isEmpty()) {
        val next = queue.removeLast()
        parsePacket(next, input, queue)
    }
    println("Part 1:  $VERSIONS")
}

fun parsePacket(packet: Packet, input: String, packets: ArrayDeque<Packet>) {
    if (packet.startingIndex + 7 > input.length) { return }
    val version = input.subSequence(packet.startingIndex, packet.startingIndex+3).toString().toInt(2)
    VERSIONS += version
    println(input.subSequence(packet.startingIndex+3, packet.startingIndex+6).toString())
    val typeId = input.subSequence(packet.startingIndex+3, packet.startingIndex+6).toString().toInt(2)
    println("Version: $version  Type:  $typeId")
    if (typeId == 4) {
        val value = getValuePacket(packet, packet.startingIndex+6, input, packets)
        println("Value packet:  $value")
    } else {
        when (input.subSequence(packet.startingIndex+6, packet.startingIndex+7)) {
            "0" -> {
                println("Length type:  0")
                var firstIndex = packet.startingIndex+7
                var lastIndex = firstIndex + 15
                if (lastIndex > input.length) { return }
                val length = input.subSequence(firstIndex, lastIndex).toString().toInt(2)
                println(length)
                if (packet.type == 0) {
                    packets.add(Packet(lastIndex, 0,  lastIndex = packet.lastIndex))
                } else {
                    packets.add(Packet(lastIndex, 0,  lastIndex = lastIndex+length, remaining = packet.remaining))
                }
            }
            "1" -> {
                println("Length type:  1")
                var firstIndex = packet.startingIndex + 7
                var lastIndex = firstIndex + 11
                if (lastIndex > input.length) { return }
                val numPackets = input.subSequence(firstIndex, lastIndex).toString().toInt(2)
                println("Num packets:  $numPackets")
                packets.add(Packet(lastIndex, 1, remaining = numPackets + packet.remaining))
//                val increment = (input.length-lastIndex) / numPackets -1
//                println("Increment:  $increment")
//                for (i in 0 until numPackets) {
//                    firstIndex = lastIndex
//                    lastIndex += increment
//                    packets.add(firstIndex to lastIndex)
//                }
            }
            else -> throw Exception("No thank you")
        }
    }
}

fun getValuePacket(packet: Packet, startingIndex: Int, input: String, packets: ArrayDeque<Packet>) : Int {
//    var startingIndex = 6
//    val sb = StringBuilder()
//    while (startingIndex +  5 <= input.length) {
//        sb.append(input.subSequence(startingIndex + 1, startingIndex + 5).toString())
//        startingIndex+=5
//    }
//    println(sb.toString())
//    val num = sb.toString().toInt(2)
//    return num
    var startingIndex = startingIndex
    val sb = StringBuilder()
    while (true) {
        val firstDigit = input[startingIndex]
        // maybe check length here
        sb.append(input.subSequence(startingIndex+1, startingIndex +5).toString())
        if (firstDigit == '0') {
            if (packet.remaining > 0) {
                packets.add(Packet(startingIndex+5, 1, remaining = packet.remaining-1))
            } else if (startingIndex + 5 < packet.lastIndex){
                packets.add(Packet(startingIndex+5, 0, lastIndex = packet.lastIndex))
            }
            println(sb.toString())
            return sb.toString().toInt(2)
        }
        startingIndex+=5
    }
}

data class Packet(val startingIndex: Int, val type: Int, val lastIndex: Int = 0, val remaining: Int = 0)

fun hexToBit(it: String) : String {
    return it.toInt(16).toString(2).padStart(4, '0')
//    return when (it) {
//        "0" -> "0000",
//        "1" ->  "0001",
//        "2" ->  "0010",
//        "3" ->  "0011",
//        "4" ->  "0100",
//        "5" -> return "0101",
//        "6" ->  "0110",
//        "7" -> "0111",
//        "8" -> "1000",
//        "9" -> "1001",
//        "A" -> "1010",
//            "B" -> "1011",
//        "C" -> "1100",
//                "D" -> "1101",
//            "E" -> "1110",
//        "F" -> return "1111"
//        else -> throw Exception()
//    }
}