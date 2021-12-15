package input

import java.io.File
import java.util.*
import kotlin.math.abs

var GOAL = 9 to 9
fun main() {
    val map = File("src/input/input15-2.txt").readLines().map{ row -> row.map { it.toString().toInt() }}
    val temp = mutableListOf<List<List<Int>>>()
    for (j in 0 until 5) {
        temp.add(generateRow(j, map))
    }
    val partTwoMap = temp.flatten()

    println("Part One:")
    search(map)
    println("Part Two:")
    search(partTwoMap)
}

fun search(map: List<List<Int>>) {
    GOAL = map[0].size-1 to map.size-1
    val memo = mutableMapOf<Pair<Int, Int>, Int>()
    val comparator: Comparator<MapItem> = compareBy {
        memo.getOrDefault(it.location, map[it.location.second][it.location.first] + it.cost) + it.distance }
    val next = PriorityQueue<MapItem>(comparator)
    next.addAll(getNeighbors(0 to 0, map, 0, Int.MAX_VALUE, memo))
    var minCost = Int.MAX_VALUE
    val visited = mutableMapOf<Pair<Int,Int>,Boolean>()
    while (!next.isEmpty()) {
        val current = next.poll()
        if (visited.getOrDefault(current.location, false) || current.cost >= minCost) { continue }
        if (current.location == GOAL) {
            minCost = current.cost
        } else {
            next.addAll(getNeighbors(current.location, map, current.cost, minCost, memo))
        }
        visited.putIfAbsent(current.location, true)
    }
    println(minCost)
}

fun Pair<Int,Int>.distanceFrom(other: Pair<Int,Int>): Int {
    return abs(this.first - other.first) + abs(this.second - other.second)
}

fun generateRow(increment: Int, map: List<List<Int>>): List<List<Int>> {
        return map.map { row ->
            val newRow = mutableListOf<Int>()
            for (i in 0 until 5) {
                val current = row.map {
                    val total = it + i + increment
                    if (total >= 10) {
                        (total % 10) + 1
                    } else {
                        total
                    }
                }
                newRow.addAll(current)
            }
            newRow.toList()
        }
}

data class MapItem(val location: Pair<Int, Int>, val cost: Int, val distance: Int = GOAL.distanceFrom(location))

fun getNeighbors(current: Pair<Int, Int>, map: List<List<Int>>, initialCost: Int, minCost: Int, memo: MutableMap<Pair<Int,Int>,Int>)
        : List<MapItem> {
    return listOf(
        current.first + 1 to current.second,
        current.first -1 to current.second,
        current.first to current.second+1,
        current.first to current.second-1)
        .filterNot { it.first < 0 || it.second < 0 || it.first >= map[0].size || it.second >= map.size }
        .map {
            val minValue = minOf(map[it.second][it.first] + initialCost, memo.getOrDefault(it.first to it.second, Int.MAX_VALUE))
            memo[it.first to it.second] = minValue
            MapItem(it, minValue)
        }.filterNot { it.cost >= minCost }
}