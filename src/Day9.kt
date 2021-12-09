import java.io.File
import java.util.*

fun main() {
    val map = File("src/input/input9-2.txt").readLines().map{ line -> line.toCharArray().map { it.toString().toInt() }}
    val lowPoints = mutableListOf<Pair<Int, Int>>()
    val partOne = map.mapIndexed { y, ints ->
        ints.filterIndexed { x, num ->
            if (getAdjacents(
                y,
                x,
                map
            ).all { neighbor ->
                num < neighbor
            }) {
                lowPoints.add(x to y)
                true
            } else {
                false
            }
        }.sumOf { riskLevel(it) }
    }.sum()
    println("Part 1:  $partOne")
    val basins = lowPoints.map { findBasin(it.first, it.second, map) }
    val topThree = basins.sorted().takeLast(3).reduce { acc, i -> acc * i }
    println("Part 2:  $topThree")
}

fun findBasin(x: Int, y: Int, map: List<List<Int>>): Long {
    val basinQueue : Queue<Pair<Int, Int>> = LinkedList()
    basinQueue.add(x to y)
    var count = 0L
    val visited = mutableMapOf<Pair<Int,Int>,Boolean>()
    while (!basinQueue.isEmpty()) {
        val next = basinQueue.poll()
        if (map[next.second][next.first] == 9 || visited.getOrDefault(next.first to next.second, false)) continue
        visited[next.first to next.second] = true
        count++
        basinQueue.addAll(getAdjacentIndices(next.second, next.first, map))
    }
    return count
}

fun getAdjacentIndices(y : Int, x: Int, map: List<List<Int>>) : List<Pair<Int, Int>> {
    return listOf(x+1 to y, x - 1 to y, x to y+1, x to y-1)
        .filterNot { it.first < 0 || it.second < 0 || it.second >=  map.size || it.first >= map.first().size }
}

fun getAdjacents(y : Int, x: Int, map: List<List<Int>>) : List<Int> {
    return getAdjacentIndices(y, x, map).map { map[it.second][it.first] }
}

fun riskLevel(height: Int) : Int {
    return height + 1
}