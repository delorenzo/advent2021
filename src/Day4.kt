import java.io.File
import kotlin.system.exitProcess

fun main() {
    var bingoNums: List<Int>
    val input = File("src/input/input4-1.txt").readLines()
    bingoNums = input[0].split(",").map { it.toInt() }
    val boards = mutableListOf<BingoBoard>()
    var i = 2
    while (i < input.size) {
        val board = mutableListOf<List<Int>>()
        val map = mutableMapOf<Int, Boolean>()
        for (j in i until i+5) {
            board.add(input[j].split(Regex(" +")).filterNot { it.isBlank() }.map {
                val num = it.toInt()
                map[num] = false
                num
            })
        }
        boards.add(BingoBoard(board, map))
        i+=6
    }

    var winningBoards = 0
    bingoNums.forEach { num ->
        boards.forEach { board ->
            if (board.bingoMap[num] == false) {
                board.bingoMap[num] = true
                if (!board.hasWon() && board.isWinner()) {
                    board.won()
                    winningBoards++
                    if (winningBoards == 1) {
                        println("First board to win's score:  ${board.score(num)}")
                    }
                    if (winningBoards >= boards.size) {
                        println("Last board to win's score:  ${board.score(num)}")
                        exitProcess(0)
                    }
                }
            }
        }
    }
}

data class BingoBoard(val board: List<List<Int>>, val bingoMap: MutableMap<Int, Boolean>) {
    private var won = false
    fun isWinner(): Boolean {
        return isRowWinner() || isColumnWinner()
    }

    fun won() {
        won = true
    }

    fun hasWon() : Boolean {
        return won
    }

    private fun isRowWinner(): Boolean {
        return board.any {  row -> (row.all { bingoMap[it] == true }) }
    }

    private fun isColumnWinner(): Boolean {
        for (i in board.indices) {
            val col = board.map { it[i] }
            if (col.all{bingoMap[it] == true}) return true
        }
        return false
    }

    // is this really not part of bingo?  have I been playing it wrong this whole time?
    private fun isDiagonalWinner(): Boolean {
        var x = 0
        val downDiagonal = board.map {
            it[x++]
        }
        if (downDiagonal.all { bingoMap[it] == true }) return true
        x = 0
        val upDiagonal = board.map {
            it[board.size-1-x++]
        }
        if (upDiagonal.all { bingoMap[it] == true }) return true
        return false
    }

    fun score(num: Int) : Long {
        val unmarkedSum = board.flatten().filterNot { bingoMap[it] == true }.sum().toLong()
        return unmarkedSum * num
    }
}