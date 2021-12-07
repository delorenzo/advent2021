import java.io.File
import kotlin.math.abs

fun main() {
    val crabs = File("src/input/input7-2.txt").readLines().first().split(",").map { it.toInt() }.groupingBy { it }.eachCount()
    val partOne = mutableMapOf<Int, Int>()
    val partTwo = mutableMapOf<Int, Long>()
    val min = crabs.keys.minOrNull()!!
    val max = crabs.keys.maxOrNull()!!
    for (position in min..max) {
        partOne[position] = crabs.map { abs(it.key - position) * it.value  }.sum()
        partTwo[position] = crabs.map { fuelCost(it.key, position) * it.value}.sum()
    }
    println("Part one:  ${partOne.values.minOrNull()!!}")
    println("Part two:  ${partTwo.values.minOrNull()!!}")
}

fun fuelCost(starting: Int, ending: Int) : Long {
    if (starting == ending) return 0
    val smaller = minOf(starting, ending)
    val larger = maxOf(starting, ending)
    var current = smaller
    var cost = 0L
    var multiplier = 1L
    while (current < larger) {
        current++
        cost += multiplier
        multiplier++
    }
    return cost
}