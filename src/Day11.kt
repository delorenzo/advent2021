import java.io.File
import kotlin.system.exitProcess

fun main() {
    var octopuses = File("src/input/input11-2.txt").readLines()
        .map { line -> line.toCharArray()
        .map { it.toString().toInt() }.toMutableList()}
    var flashes = 0
    val flashMap = mutableMapOf<Pair<Int, Int>, Boolean>()

    for (i in 1 .. 1000) {
        octopuses = octopuses.map { list -> list.map { it + 1 }.toMutableList() }
        octopuses.mapIndexed { y, ints -> ints.mapIndexed { x, num ->
            if (num > 9) {
                flashes += flash(octopuses, flashMap, x to y)
            }
        } }
        flashMap.mapKeys { octopuses[it.key.second][it.key.first] = 0 }
        flashMap.clear()

        if (i == 100) {
            println("Part one - $flashes flashes")
        }
        if (octopuses.all { row -> row.all { num -> num == 0 } }) {
            println("Part two - step #$i")
            exitProcess(0)
        }
    }
}

fun printMap(octopi: List<List<Int>>) {
    octopi.map {
        println(it)
    }
    println()
}

fun flash(octopuses : List<MutableList<Int>>, flashMap: MutableMap<Pair<Int, Int>, Boolean>, start: Pair<Int, Int>) : Int {
    val queue = ArrayDeque<Pair<Int,Int>>()
    queue.add(start)
    var flashes = 0
    while (!queue.isEmpty()) {
        val current = queue.removeFirst()
        if (octopuses[current.second][current.first] > 9 && !flashMap.getOrDefault(current, false)) {
            flashes++
            flashMap[current] = true
            val neighbors = getNeighbors(current, octopuses)
            neighbors.map { octopuses[it.second][it.first] = octopuses[it.second][it.first]+1 }
            queue.addAll(neighbors)
        }
    }
    return flashes
}

fun getNeighbors(current: Pair<Int, Int>, octopi: List<List<Int>>) : List<Pair<Int, Int>> {
    return listOf(
        current.first + 1 to current.second,
        current.first -1 to current.second,
        current.first to current.second+1,
        current.first to current.second-1,
        current.first -1 to current.second-1,
        current.first +1 to current.second+1,
        current.first -1 to current.second+1,
        current.first +1 to current.second-1)
        .filterNot { it.first < 0 || it.second < 0 || it.first >= octopi[0].size || it.second >= octopi.size }
}