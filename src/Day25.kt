import java.io.File
import javax.swing.SortOrder

val EAST = '>'
val SOUTH = 'v'
val EMPTY = '.'
fun main() {
    var sea = File("src/input/input25-2.txt").readLines().map {it.toCharArray().toMutableList()}
    var moved = 1
    var turns = 0
    while (moved > 0) {
        moved = 0
        moved += moveEast(sea)
        moved += moveSouth(sea)
        turns++
    }
    println("Turns: $turns")
}

fun List<MutableList<Char>>.copy() : List<MutableList<Char>>  {
    return this.map { it.toMutableList() }.toList()
}

fun moveEast(sea: List<MutableList<Char>>) : Int {
    var moves = 0
    val oldSea = sea.copy()
    for (y in oldSea.indices) {
        for (x in oldSea[y].indices) {
            if (oldSea[y][x] == EAST) {
                var next = x + 1
                if (next >= oldSea[y].size) {
                    next = 0 }
                if (oldSea[y][next] == EMPTY) {
                    sea[y][x] = EMPTY
                    sea[y][next] = EAST
                    moves++
                }
            }
        }
    }
    return moves
}

fun moveSouth(sea: List<MutableList<Char>>) : Int {
    var moves = 0
    val oldSea = sea.copy()
    for (y in oldSea.indices) {
        for (x in oldSea[y].indices) {
            if (oldSea[y][x] == SOUTH) {
                var next = y + 1
                if (next >= oldSea.size) { next = 0 }
                if (oldSea[next][x] == EMPTY) {
                    sea[y][x] = EMPTY
                    sea[next][x] = SOUTH
                    moves++
                }
            }
        }
    }
    return moves
}