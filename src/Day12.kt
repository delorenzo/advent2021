import java.io.File

const val START = "start"
const val END = "end"
fun main() {
    val cave = mutableMapOf<String, MutableList<String>>()
    File("src/input/input12-2.txt").readLines().map {
        val connection = it.split("-")
        cave.computeIfPresent(connection.first()) { _, list -> list.add(connection[1]); list }
        cave.putIfAbsent(connection.first(), mutableListOf(connection[1]))
        cave.computeIfPresent(connection[1]) { _, list -> list.add(connection.first()); list }
        cave.putIfAbsent(connection[1], mutableListOf(connection.first()))
    }
    partOne(cave)
    partTwo(cave)
}

fun partOne(cave : Map<String, List<String>>) {
    val queue = ArrayDeque<SearchItem>()
    queue.add(SearchItem(mutableListOf(START)))
    val result = doSearch(queue, cave)
    println(result.count())
}

fun partTwo(cave : Map<String, List<String>>) {
    val queue = ArrayDeque<SearchItem>()
    cave.keys.filter { isSmallCave(it) }.map {
        queue.add(SearchItem(mutableListOf(START), it))
    }
    val result = doSearch(queue, cave)
    println(result.count())
}

fun doSearch(queue: ArrayDeque<SearchItem>, cave: Map<String, List<String>>) : List<List<String>> {
    val paths = mutableListOf<List<String>>()
    while (!queue.isEmpty()) {
        val current = queue.removeFirst()
        cave.getOrDefault(current.path.last(), mutableListOf()).filterNot { current.checkVisited(it) }.map {
            if (it == END) {
                val newPath = current.path.toMutableList()
                newPath.add(it)
                paths.add(newPath)
            } else {
                queue.add(current.spawnSearch(it))
            }
        }
    }
    return paths.distinct().toList()
}

data class SearchItem(val path: MutableList<String>,
                      val smallDouble : String = "",
                      val visited: MutableMap<String, Int> = mutableMapOf(START to 1)) {
    fun checkVisited(item: String) : Boolean {
        if (item == START) {
            if (visited.getOrDefault(item,0) == 1) return true
        }
        if (!isSmallCave(item)) return false
        if (item == smallDouble) {
            if (visited.getOrDefault(item,0) == 2) return true
        } else {
            if (visited.getOrDefault(item,0) == 1) return true
        }
        return false
    }

    fun spawnSearch(item: String) : SearchItem {
        val newPath = path.toMutableList()
        newPath.add(item)
        val newVisited = visited.toMutableMap()
        if (newVisited.containsKey(item)) {
            newVisited[item] = newVisited[item]!! + 1
        } else {
            newVisited[item] = 1
        }
        return SearchItem(newPath, this.smallDouble, newVisited)
    }
}

fun isSmallCave(string: String) : Boolean {
    return string != START && string != END && !string.all { it.isUpperCase() }
}