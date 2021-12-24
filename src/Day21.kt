import java.util.*
import kotlin.Comparator
import kotlin.system.exitProcess

var PLAYER_ONE_WINS = 0L
var PLAYER_TWO_WINS = 0L
fun main() {
    //partOne(4, 8)
    //println(rolls)
    val result = partTwo(mutableMapOf(), GameState(0, 0, 4, 8))
    println(result)
    println("1 $PLAYER_ONE_WINS")
    println("2 $PLAYER_TWO_WINS")
}
//
//val rolls = listOf(1,1,1).flatMap { a -> listOf(2,2,2).flatMap { b-> listOf(3,3,3).map { c -> listOf(a, b, c) } }}

val rolls = listOf(
    listOf(1, 1, 1),
    listOf(1, 1, 2),
    listOf(1, 1, 3),
    listOf(1, 2, 1),
    listOf(1, 2, 2),
    listOf(1, 2, 3),
    listOf(1, 3, 1),
    listOf(1, 3, 2),
    listOf(1, 3, 3),
    listOf(2, 1, 1),
    listOf(2, 1, 2),
    listOf(2, 1, 3),
listOf(2, 2, 1),
listOf(2, 2, 2),
listOf(2, 2, 3),
listOf(2, 3, 1),
listOf(2, 3, 2),
listOf(2, 3, 3),
    listOf(3, 1, 1),
    listOf(3, 1, 2),
    listOf(3, 1, 3),
    listOf(3, 2, 1),
    listOf(3, 2, 2),
    listOf(3, 2, 3),
    listOf(3, 3, 1),
    listOf(3, 3, 2),
    listOf(3, 3, 3)
)

fun partTwo(memo: MutableMap<Int, Pair<Int, Int>>, state: GameState) : Pair<Int, Int> {
//    val comparator:  Comparator<GameState> = compareBy {
//        maxOf(it.oneScore + it.next.second.size, it.twoScore + it.next.second.size)
//    }
//    val games = PriorityQueue<GameState>(comparator)
    //games.add(GameState(0, 0, playerOne, playerTwo, 1 to listOf()))
    if (state.oneScore >= 21) { return 1 to 0 }
    if (state.twoScore >= 21) { return 0 to 1 }
    val m = memo.getOrDefault(state.cache(), 0 to 0)
    if (m.toList().any { it > 0  }) { return m }
    var wins = 0 to 0

    rolls.forEach {
        val nextState = state.copy()
        nextState.onePosition = it.sum()
        if (nextState.onePosition > 10) { nextState.onePosition %= 10 }
        if (nextState.onePosition == 0) { nextState.onePosition = 10 }
        val result = partTwo(memo, GameState(nextState.twoPosition, nextState.twoScore, nextState.onePosition, nextState.oneScore))
        wins = result.second to result.first
    }
    memo[state.cache()] = wins
    return wins
//    memo[state] = wins
//    //for (i in 0 until 5) {
//    while (!games.isEmpty()) {
//        val game = games.poll()
//        if (game.next.first == 1) {
//            if (game.next.second.size == 3) {
//                var playerOneMovement = game.next.second.sum()
//                game.onePosition = (game.onePosition + playerOneMovement)
//                if (game.onePosition > 10) {
//                    game.onePosition %= 10
//                    if (game.onePosition == 0) { game.onePosition = 10 }
//                }
//                game.oneScore += game.onePosition
//                if (game.oneScore >= 21) {
//                    PLAYER_ONE_WINS++
//                    println(PLAYER_ONE_WINS)
//                } else {
//                    games.add(GameState(game.oneScore,game.twoScore, game.onePosition, game.twoPosition, 2 to listOf()))
//                }
//            }
//            else {
//                val one = game.next.second.toMutableList()
//                one.add(1)
//                val two = game.next.second.toMutableList()
//                two.add(2)
//                val three = game.next.second.toMutableList()
//                three.add(3)
//                games.add(GameState(game.oneScore, game.twoScore, game.onePosition, game.twoPosition, 1 to one))
//                games.add(GameState(game.oneScore, game.twoScore, game.onePosition, game.twoPosition, 1 to two))
//                games.add(GameState(game.oneScore, game.twoScore, game.onePosition, game.twoPosition, 1 to three))
//            }
//        }
//        else {
//            if (game.next.second.size == 3) {
//                var playerTwoMovement = game.next.second.sum()
//                game.twoPosition = (game.twoPosition + playerTwoMovement)
//                if (game.twoPosition > 10) {
//                    game.twoPosition %= 10
//                    if (game.twoPosition == 0) { game.twoPosition = 10 }
//                }
//                game.twoScore += game.twoPosition
//                if (game.twoScore >= 21) {
//                    PLAYER_TWO_WINS++
//                } else {
//                    games.add(GameState(game.oneScore,game.twoScore, game.onePosition, game.twoPosition, 2 to listOf()))
//                }
//            }
//            else {
//                val one = game.next.second.toMutableList()
//                one.add(1)
//                val two = game.next.second.toMutableList()
//                two.add(2)
//                val three = game.next.second.toMutableList()
//                three.add(3)
//                games.add(GameState(game.oneScore, game.twoScore, game.onePosition, game.twoPosition, 2 to one))
//                games.add(GameState(game.oneScore, game.twoScore, game.onePosition, game.twoPosition, 2 to two))
//                games.add(GameState(game.oneScore, game.twoScore, game.onePosition, game.twoPosition, 2 to three))
//            }
//        }
//    }

}

fun partOne(playerOne: Int, playerTwo: Int) {
    var rolls = 0
    var playerOneScore = 0
    var playerTwoScore = 0
    var playerOnePosition = playerOne
    var playerTwoPosition = playerTwo
    var die = 0
    //for (i in 0 until 5) {
    while (playerOneScore <  1000 && playerTwoScore < 1000) {
        var playerOneMovement = 0
        for (i in 0 until 3) {
            rolls++
            die = (die + 1)
            if (die > 100) {
                die %= 100
            }
            playerOneMovement += die
        }
        playerOnePosition = (playerOnePosition + playerOneMovement)
        if (playerOnePosition > 10) {
            playerOnePosition %= 10
            if (playerOnePosition == 0) { playerOnePosition = 10 }
        }
        playerOneScore += playerOnePosition
        if (playerOneScore >= 1000) {
            println("${playerTwoScore * rolls}")
            exitProcess(0)
        }
        var playerTwoMovement = 0
        for (i in 0 until 3) {
            rolls++
            die = (die + 1)
            if (die > 100) {
                die %= 100
            }
            playerTwoMovement += die
        }
        playerTwoPosition = (playerTwoPosition + playerTwoMovement)
        if (playerTwoPosition > 10) {
            playerTwoPosition %= 10
            if (playerTwoPosition == 0) { playerTwoPosition = 10 }
        }
        playerTwoScore += playerTwoPosition
        if (playerTwoScore >= 1000) {
            println("${playerOneScore * rolls}")
            exitProcess(0)
        }

        println("Player one $playerOnePosition $playerOneScore")
        println("Player two $playerTwoPosition $playerTwoScore")
    }
}

data class GameState(var oneScore: Int, var twoScore: Int, var onePosition: Int, var twoPosition: Int) {
    override fun equals(other: Any?): Boolean {
        if (!(other is GameState)) { return false }
        return oneScore == other.oneScore && twoScore == other.twoScore && onePosition == other.onePosition && twoPosition == other.twoPosition
    }

    fun cache() : Int {
        return oneScore  * 10 * 31 * 10 + (onePosition-1) * 31 * 10  + (twoScore) * 10 + (twoPosition-1)
    }
}