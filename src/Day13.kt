import java.io.File
import java.util.regex.Pattern

fun main() {
    val fold = Regex("fold along (x|y)=(\\d+)")
    var coordinates = mutableListOf<Pair<Int, Int>>()
    val folds = mutableListOf<Pair<String, Int>>()
    File("src/input/input13-2.txt").readLines().map {
        when {
            it.contains(",") -> {
                val pair = it.split(",")
                coordinates.add(pair[0].toInt() to pair[1].toInt())
            }
            fold.matches(it) -> {
                val match = fold.matchEntire(it)
                folds.add(match!!.groupValues[1] to match.groupValues[2].toInt())
            }
            else -> {}
        }
    }

    var current : List<Pair<Int, Int>> = coordinates.toList()
    folds.forEach {
        current = fold(it, current)
        println("Visible coordinates:  ${current.count()}")
    }
    current.printMap()
}

fun List<Pair<Int,Int>>.printMap() {
    val maxX = this.maxOf { it.first }
    val maxY = this.maxOf{ it.second}
    for (i in 0..maxY) {
        for (j in 0..maxX) {
            if (this.contains(j to i)) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }
}

fun fold(fold: Pair<String, Int>, coordinates: List<Pair<Int, Int>>) : List<Pair<Int, Int>> {
    return if (fold.first == "y") {
        foldHorizontal(fold.second, coordinates)
    } else {
        foldVertical(fold.second, coordinates)
    }
}

fun foldHorizontal(y: Int, coordinates: List<Pair<Int, Int>>) : List<Pair<Int, Int>> {
    return coordinates.filterNot { it.second == y }.map {
        if (it.second > y) {
            val distance = it.second - y
            it.first to y - distance
        } else {
            it
        }
    }.distinct()
}

fun foldVertical(x: Int, coordinates: List<Pair<Int, Int>>) : List<Pair<Int, Int>> {
    return coordinates.filterNot { it.first == x }.map {
        if (it.first > x) {
            val distance = it.first - x
            x - distance to it.second
        } else {
            it
        }
    }.distinct()
}