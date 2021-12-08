import java.io.File

val ALL = mutableListOf('a', 'b', 'c', 'd', 'e', 'f', 'g')
fun main() {
    val input = File("src/input/input8-2.txt").readLines()
    var partOne = 0
    var partTwo = 0L
    val knownValues = setOf(2, 4, 3, 7)
    input.map { string ->
        val line = string.split(" ")
        val signals = line.take(10).map { it.toSortedSet().joinToString("") }
        val outputs = line.takeLast(4).map { it.toSortedSet().joinToString("") }
        partOne += outputs.count { knownValues.contains(it.length) }
        val decodeMap = getDecodeMap(signals)
        partTwo += getOutputNum(outputs, decodeMap)
    }

    println("Part 1:  $partOne")
    println("Part 2:  $partTwo")
}

fun getDecodeMap(signals: List<String>) : Map<String, String> {
    val signalCounts = signals.groupBy { it.length }
    val decodeMap = mutableMapOf(
        signalCounts[2]!!.first() to "1",
        signalCounts[4]!!.first() to "4",
        signalCounts[3]!!.first() to "7",
        signalCounts[7]!!.first() to "8"
    )

    val signalPatterns = deriveSignalMapping(signalCounts, decodeMap)

    populateDecodeMap(signalPatterns, decodeMap)

    return decodeMap
}

fun populateDecodeMap(signalPatterns: Map<Int, List<Char>>, result: MutableMap<String, String>) {
    val zero = ALL.filterNot { it == signalPatterns[3]!!.first() }.sorted().joinToString("")
    result[zero] = "0"
    val two = ALL.filterNot { it == signalPatterns[1]!!.first() || it == signalPatterns[5]!!.first() }.sorted().joinToString("")
    result[two] = "2"
    val three = ALL.filterNot { it == signalPatterns[1]!!.first() || it == signalPatterns[4]!!.first() }.sorted().joinToString("")
    result[three] = "3"
    val five = ALL.filterNot { it == signalPatterns[2]!!.first() || it == signalPatterns[4]!!.first() }.sorted().joinToString("")
    result[five] = "5"
    val sixCode = ALL.filterNot { it == signalPatterns[2]!!.first() }.sorted().joinToString("")
    result[sixCode] = "6"
}

fun getOutputNum(outputs: List<String>, decodeMap: Map<String, String>) : Long {
    return outputs.map { it.toSortedSet().joinToString("") }.map { convertToValue(it,decodeMap) }.reduce { a, b -> a + b }.toLong()
}

fun convertToValue(num: String, decodeMap: Map<String, String>) : String {
    return decodeMap[num]!!
}

private fun deriveSignalMapping(signalCounts: Map<Int, List<String>>, result: MutableMap<String, String>) : MutableMap<Int, MutableList<Char>> {
    val signalMap = mutableMapOf<Int, MutableList<Char>>()

    // The top right digit is either of the digits in 1
    signalMap[2] = mutableListOf(signalCounts[2]!!.first()[0], signalCounts[2]!!.first()[1])
    signalMap[3] = ALL.toMutableList()
    signalMap[4] = ALL.toMutableList()
    signalMap[6] = ALL.toMutableList()

    // The bottom right digit is either of the digits in 1
    signalMap[5] = signalMap[2]!!
    // We can narrow the top left digit to the digits in 4 not included in 1
    signalMap[1] = signalCounts[4]!!.first().toCharArray().filterNot { signalMap[2]!!.contains(it) }.toMutableList()
    // We can derive the top digit from the digit in 7 not included in 1
    signalMap[0] = signalCounts[3]!!.first().toCharArray().filterNot { signalMap[2]!!.contains(it) }.toMutableList()

    // Derive 9 from adding the top digit to the digits in 4, then looking for the 6-length signal that's only missing one from this set
    val newSet = signalCounts[4]!!.first().toCharArray().toMutableList()
    newSet.add(signalMap[0]!!.first())
    val nine = signalCounts[6]!!.first { it.toCharArray().filterNot { c -> newSet.contains(c) }.size == 1 }.toSortedSet().joinToString("")
    result[nine] = "9"
    // This means the bottom digit is the digit that was missing
    signalMap[6] = mutableListOf(nine.toCharArray().filterNot { newSet.contains(it) }.first())
    newSet.add(signalMap[6]!!.first())
    // Then, the bottom left digit is the only digit not present in 9
    signalMap[4] = mutableListOf(ALL.filterNot { newSet.contains(it) }.first())

    // Each six-digit code has one letter not present, but we only care about 0 and 6
    val zeroAndSix = signalCounts[6]!!.toMutableList().filterNot { it == nine }
    val uniqueMissingLetters = zeroAndSix.map { code -> ALL.filterNot { l -> code.contains(l) }.first() }
    // The top right digit is the one of these shared by 1
    signalMap[2] = uniqueMissingLetters.filter { signalMap[2]!!.contains(it) }.toMutableList()
    // The center digit is the one of these shared by 0
    signalMap[3] = uniqueMissingLetters.filterNot { signalMap[2]!!.contains(it) }.toMutableList()
    // Now that we know top right, bottom right is the other option shared by 1
    signalMap[5]!!.remove(signalMap[2]!!.first())
    // Now that we know center, top left is the other option shared by 4
    signalMap[1]!!.remove(signalMap[3]!!.first())

    return signalMap
}