import java.io.File
import java.io.File.separator
//5400
//18989
fun main() {
    val input = File("src/input/input20-2.txt").readLines()
    val imageEnhancement = input.first()
    val padding = List<Char>(1) {'.'}
    //val size = padding.size * 2 + input[3].length
    val size = input.size-2
    val image = MutableList<List<Char>>(size) { List<Char> (size) { '$' } }
    val mid = size
    val memo = mutableMapOf<String, Char>()
    for (i in 2 until input.size) {
        var newLine = input[i].toCharArray().toList()
        newLine = padding + newLine + padding
        image[i-2] = newLine
    }

    for (line in image) {
        println(line)
    }
    println()
    var current: MutableList<List<Char>> = image
    for (i in 0 until 50) {
        val padLine = List<Char>(current[0].size) { '.' }
        if (current.first().any { it == '#' }) {
            current.add(0, padLine)
        }
        if (current.last().any { it == '#'}) {
            current.add(padLine)
        }
        val newImage = current.mapIndexed { y, line ->
            val line = List(line.size) { x -> decodeImage(x, y, current, imageEnhancement, memo) }.toMutableList()
            if (line.first() != '.') {
                line.add(0, '.')
            }
            if (line.last() != '.') {
                line.add('.')
            }
            line.toList()
//            padding + List(line.size) { x -> decodeImage(x, y, current, imageEnhancement, memo) } + padding
        }.toMutableList()
        for (line in newImage) {
            println(line)
        }
        current = newImage
    }
    val count = current.sumOf { it.count { c -> c == '#' } }
    println(count)
}

fun decodeImage(x: Int, y: Int, image: List<List<Char>>, adjustment: String, memo: MutableMap<String, Char>) : Char {
    val coord = listOf(
        x - 1 to y-1, x to y-1, x+1 to y-1,
        x-1 to y, x to y, x+1 to y,
        x-1 to y+1, x to y+1, x+1 to y+1)
    val binaryString = coord.map {
            if (it.first <  0 || it.second < 0 || it.second >= image.size  || it.first >= image[it.second].size) {
                "0"
            }
            else {
                when (image[it.second][it.first]) {
                    '.' -> "0"
                    '#' -> "1"
                    else -> throw Exception("Invalid input")
                }
            }
        }.joinToString(separator = "")
    if (memo.containsKey(binaryString)) {
        return memo[binaryString]!!
    }
    val integer = Integer.parseInt(binaryString, 2)
    memo[binaryString] = adjustment[integer]
    return adjustment[integer]
}