import java.io.File

fun main() {
    val input = File("src/input/input1-2.txt").readLines().map { it.trim().toInt() }
    val count = countIncrements(input)
    println("Increments:  $count")
    val countSlides = countSlides(input)
    println("Slides:  $countSlides")
}

fun countIncrements(depths: List<Int>) : Int {
    var count = 0
    var current = depths.first()
    depths.forEach {
        if (it > current) {
            count++
        }
        current = it
    }
    return count
}

fun countSlides(depths: List<Int>) : Int {
    var count = 0
    var current = depths.first() + depths[1] + depths[2]
    depths.forEachIndexed { index, i ->
        if (index + 2 >= depths.size) return count
        val next = depths[index] + depths[index+1] + depths[index+2]
        if (next > current) {
            count++
        }
        current = next
    }
    return count
}