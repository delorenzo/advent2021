import java.io.File

fun main() {
    val input = File("src/input/input3-2.txt").readLines().map { it.trim() }

    val len = input[0].length

    val result = getGammaEpsilon(len, input)
    println("Part 1:  ")
    println("Result:  $result")
    println("Result:  ${result.first * result.second}")
    println("-------")
    println("Part 2:  ")
    val oxygen = getOxygenGenerator(len, input)
    val co2 = getCO2Scrubber(len, input)
    println("Oxygen:  $oxygen CO2:  $co2")
    println("Result:   ${oxygen * co2}")
}

fun getGammaEpsilon(len : Int, input: List<String>) : Pair<Int, Int> {
    val mostCommon = StringBuilder()
    val leastCommon = StringBuilder()
    for (i in 0 until len) {
        val common = input.map { it[i] }.groupingBy { it }.eachCount()
        val max = common.maxByOrNull { it.value }?.key!!
        val min = common.minByOrNull { it.value }?.key!!
        mostCommon.append(max)
        leastCommon.append(min)
    }
    val gamma = binaryToDecimal(mostCommon.toString())
    val epsilon = binaryToDecimal(leastCommon.toString())
    return gamma to epsilon
}

fun getOxygenGenerator(len : Int, input: List<String>) : Int {
    var candidates = input
    for (i in 0 until len) {
        val first = candidates.map{it[i]}
        val common = first.groupingBy { it }.eachCount()
        val max = when {
            common.getOrDefault('0', 0) > common.getOrDefault('1', 0) -> '0'
            else -> '1'
        }
        candidates = candidates.filter { it[i] == max }
        if (candidates.size == 1) {
            return binaryToDecimal(candidates.first())
        }
    }
    throw Exception("Could not find candidate")
}

fun getCO2Scrubber(len : Int, input: List<String>) : Int {
    var candidates = input
    for (i in 0 until len) {
        val first = candidates.map{it[i]}
        val common = first.groupingBy { it }.eachCount()
        val min = when {
            common.getOrDefault('1', 0) < common.getOrDefault('0', 0) -> '1'
            else -> '0'
        }
        candidates = candidates.filter { it[i] == min }
        if (candidates.size == 1) {
            return binaryToDecimal(candidates.first())
        }
    }
    throw Exception("Could not find candidate")
}

fun binaryToDecimal(num: String) : Int {
    var result = 0
    var bit = 0
    var n = num.length -1
    while (n >= 0) {
        if (num[n] == '1') {
            result += (1 shl bit)
        }
        n--
        bit++
    }
    return result
}