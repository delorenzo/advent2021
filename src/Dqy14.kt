import java.io.File
import kotlin.math.roundToLong

fun main() {
    val input = File("src/input/input14-2.txt").readLines()
    val polymer = input.first()
    val pattern = Regex("([A-Z])([A-Z]) -> ([A-Z])")
    val ruleMap = mutableMapOf<String, List<String>>()
    var current = mutableMapOf<String, Long>()
    input.mapNotNull { pattern.matchEntire(it) }.map {
        ruleMap[it.groupValues[1] + it.groupValues[2]] =
            listOf(it.groupValues[1] + it.groupValues[3], it.groupValues[3] + it.groupValues[2])
    }
    ruleMap.keys.map { current[it] = 0 }
    ruleMap.values.map { list -> list.map { current[it] = 0 } }

    for (i in 0 until polymer.length - 1) {
        val pair = polymer.substring(i, i + 2)
        current.putIfAbsent(pair, 0L)
        current[pair] = current[pair]!! + 1
    }

    println(current)
    for (i in 0 until 40) {
        val newMap = mutableMapOf<String, Long>()
        current.keys.map {
            ruleMap[it]!!.map { newValue ->
                newMap.putIfAbsent(newValue, 0L)
                newMap[newValue] = newMap[newValue]!! + current[it]!!
            }
            newMap.putIfAbsent(it, 0L)
        }
        current = newMap
        val counts = current.entries.map { entry -> entry.key.map { char -> char to entry.value } }.flatten()
            .groupBy { it.first }.map { it.key to it.value.sumOf { pair -> pair.second / 2.0 } }
        val max = counts.maxOf { it.second }
        val min = counts.minOf { it.second }
        val difference = (max - min).roundToLong()
        println(difference)
    }
}