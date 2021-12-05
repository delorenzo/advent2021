import java.io.File

fun main() {
    val regex = Regex("([0-9]+),([0-9]+) \\-> ([0-9]+),([0-9]+)")
    val overlapCount = mutableMapOf<Point, Int>()
    File("src/input/input5-2.txt").readLines().map{
        val match = regex.matchEntire(it.trim())
        match!!
        val point1 = Point(match.groupValues[1].toInt(), match.groupValues[2].toInt())
        val point2 = Point(match.groupValues[3].toInt(), match.groupValues[4].toInt())
        val line = Line(point1, point2)
        line.map(overlapCount)
    }
    println(overlapCount.filter { it.value >=2 }.count())
}

fun printMap(overlapCount: Map<Point, Int>) {
        for (y in 0..100) {
        for (x in 0.. 100  ) {
            val p = Point(x,y)
            if (overlapCount.containsKey(p)) {
                print(overlapCount[p])
            } else {
                print(".")
            }
        }
        println()
    }
}

data class Line(val point1: Point, val point2: Point) {
    fun map(map : MutableMap<Point, Int>) {
        if (point1.x == point2.x || point1.y == point2.y) {
            mapStraightLine(map)
        }
        else {
            mapDiagonal(map)
        }
    }

    private fun mapStraightLine(map : MutableMap<Point, Int>) {
        val minX = point1.x.coerceAtMost(point2.x)
        val maxX = point1.x.coerceAtLeast(point2.x)
        val minY = point1.y.coerceAtMost(point2.y)
        val maxY = point1.y.coerceAtLeast(point2.y)
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                val current = Point(x, y)
                if (map.containsKey(current)) {
                    map[current] = map[current]?.plus(1) ?: 1
                } else {
                    map[current] = 1
                }
            }
        }
    }

    private fun mapDiagonal(map : MutableMap<Point, Int>) {
        if (point1.x < point2.x) {
            if (point1.y < point2.y) {
                var y = point1.y
                for (x in point1.x .. point2.x) {
                    val current = Point(x, y)
                    if (map.containsKey(current)) {
                        map[current] = map[current]?.plus(1) ?: 1
                    } else {
                        map[current] = 1
                    }
                    y++
                }
            } else {
                var y = point1.y
                for (x in point1.x .. point2.x) {
                    val current = Point(x, y)
                    if (map.containsKey(current)) {
                        map[current] = map[current]?.plus(1) ?: 1
                    } else {
                        map[current] = 1
                    }
                    y--
                }
            }
        } else {
            var x = point1.x
            if (point1.y < point2.y) {
                var y = point1.y
                while (x >= point2.x) {
                    val current = Point(x, y)
                    if (map.containsKey(current)) {
                        map[current] = map[current]?.plus(1) ?: 1
                    } else {
                        map[current] = 1
                    }
                    x--
                    y++
                }
            } else {
                var y = point1.y
                while (x >= point2.x) {
                    val current = Point(x, y)
                    if (map.containsKey(current)) {
                        map[current] = map[current]?.plus(1) ?: 1
                    } else {
                        map[current] = 1
                    }
                    x--
                    y--
                }
            }
        }
    }
}

data class Point(val x: Int, val y: Int)