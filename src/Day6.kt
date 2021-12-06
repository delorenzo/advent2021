import java.io.File

fun main() {
    var fish : MutableList<Int> = File("src/input/input6-2.txt").readLines().first().split(",").map { it.toInt() }.toMutableList()
    val days = 256
    val counts : Map<Int, Long> = fish.groupingBy { it }.eachCount().map { it.key to it.value.toLong() }.toMap()
    var current = counts
    for (i in 0 until days) {
        current = mapOf(
            0 to current.getOrDefault(1, 0L),
            1 to current.getOrDefault(2, 0L),
            2 to current.getOrDefault(3, 0L),
            3 to current.getOrDefault(4, 0L),
            4 to current.getOrDefault(5, 0L),
            5 to current.getOrDefault(6, 0L),
            6 to current.getOrDefault(7, 0L) + current.getOrDefault(0, 0L),
            7 to current.getOrDefault(8, 0L),
            8 to current.getOrDefault(0, 0L),
        )
    }
    println(current.values.sumOf { it })
}

fun partOne(input: MutableList<Int>, days: Int) {
    var fish = input
    for (i in 0 until days) {
        var newFish = 0
        fish = fish.map {
            var next = it -1
            if (next < 0) {
                next = 6
                newFish++
            }
            next
        }.toMutableList()
        for (i in 0 until newFish) {
            fish.add(8)
        }
        println(fish)
    }
}