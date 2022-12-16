import java.math.BigInteger
import java.util.*
import kotlin.Comparator
import kotlin.system.exitProcess

var PLAYER_ONE_WINS = 0L
var PLAYER_TWO_WINS = 0L
fun main() {
    val die = listOf(1,2,3)
    val possibleRolls = die.flatMap { a -> die.flatMap { b-> die.map { c -> listOf(a, b, c) } }}
    val positions = possibleRolls.map { it.sum() }.groupBy { it }.map { it.key to it.value.size }
    val wins = calculateWins(4, 21, 5, 21, positions)
    println(wins)
}

data class Wins(var one: BigInteger = BigInteger.ZERO, var two: BigInteger = BigInteger.ZERO, var totalOne : BigInteger = BigInteger.ZERO, var totalTwo: BigInteger = BigInteger.ZERO)

fun calculateWins(playerMoving: Int, remainingScoreOne: Int, playerMovingNext: Int, remainingScoreTwo: Int, rolls: List<Pair<Int,Int>>) : Pair<Long, Long> {
    if (remainingScoreTwo <= 0) { return 0L to 1L }

    var winsOne = 0L
    var winsTwo = 0L

    rolls.forEach {
        val position = (playerMoving + it.first) % 10
        val remainingScore = remainingScoreOne - 1 - position
        val result = calculateWins(playerMovingNext, remainingScoreTwo, position, remainingScore, rolls)
        winsOne += (result.second * it.second)
        winsTwo += (result.first * it.second)
    }

    return winsOne to winsTwo
}

//fun partTwo(memo: MutableMap<GameState, Wins>, state: GameState, rolls: List<Pair<Int, Int>>) {
//    val comparator:  Comparator<GameState> = compareBy {
//        maxOf(it.oneScore, it.twoScore)
//    }
//    val games = PriorityQueue(Collections.reverseOrder(comparator))
//    games.add(GameState(0, 0, 4, 8))
//
//    while (!games.isEmpty()) {
//        val game = games.poll()
//        if (memo.containsKey(game)) {
//            val value = memo[game]!!
//            value.totalOne += value.one
//            value.totalTwo += value.two
//            continue
//            //val value = memo[game]!!
//            //memo[game] = value.first * rolls.size to value.second * rolls.size
//        }
//
//        rolls.forEach {
//            var movement = it
//            if (game.nextTurn == 1) {
//                val oneRoll = game.copy()
//                oneRoll.movePlayerOne(movement.first)
//                oneRoll.nextTurn = 2
//                if (oneRoll.oneScore >= 21) {
//                    val value = memo.getOrDefault(game, Wins())
//                    value.one += (BigInteger.ONE.times(BigInteger.valueOf(movement.second.toLong())))
//                    value.totalOne += (BigInteger.ONE.times(BigInteger.valueOf(movement.second.toLong())))
//                    memo[game] = memo.getOrDefault(game, value)
//                }  else if (memo.containsKey(oneRoll)) {
//                    val value = memo[oneRoll]!!
//                    value.totalOne += value.one
//                    value.totalTwo += value.two
//                }
//                else {
//                    games.add(oneRoll)
//                }
//            } else {
//                val twoRoll = game.copy()
//                twoRoll.movePlayerTwo(movement.first)
//                twoRoll.nextTurn = 1
//                if (twoRoll.twoScore >= 21) {
//                    val value = memo.getOrDefault(game, Wins())
//                    value.two += (BigInteger.ONE.times(BigInteger.valueOf(movement.second.toLong())))
//                    value.totalTwo += (BigInteger.ONE.times(BigInteger.valueOf(movement.second.toLong())))
//                    memo[game] = memo.getOrDefault(game, value)
//                } else if (memo.containsKey(twoRoll)) {
//                    val value = memo[twoRoll]!!
//                    value.totalOne += value.one
//                    value.totalTwo += value.two
//                } else {
//                    games.add(twoRoll)
//                }
//            }
//        }
//    }
//
//    val oneWins = memo.map { it.value.totalOne }.reduce { acc, bigInteger -> acc + bigInteger } to 1
//    val twoWins = memo.map { it.value.totalTwo }.reduce { acc, bigInteger -> acc + bigInteger } to 2
//
//    println(oneWins)
//    println(twoWins)
//}

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

data class GameState(var oneScore: Int, var twoScore: Int, var onePosition: Int, var twoPosition: Int, var nextTurn: Int = 1) {
    override fun equals(other: Any?): Boolean {
        if (!(other is GameState)) { return false }
        return oneScore == other.oneScore && twoScore == other.twoScore && onePosition == other.onePosition && twoPosition == other.twoPosition
    }

    fun cache() : Int {
        return oneScore  * 10 * 31 * 10 + (onePosition-1) * 31 * 10  + (twoScore) * 10 + (twoPosition-1)
    }

    fun copy() : GameState {
        return GameState(oneScore, twoScore, onePosition, twoPosition)
    }

    fun movePlayerOne(movement: Int) {
        onePosition = (onePosition + movement)
        if (onePosition > 10) {
            onePosition %= 10
            if (onePosition == 0) { onePosition = 10 }
        }
        oneScore += onePosition
    }

    fun movePlayerTwo(movement: Int) {
        twoPosition = (twoPosition + movement)
        if (twoPosition > 10) {
            twoPosition %= 10
            if (twoPosition == 0) { twoPosition = 10 }
        }
        twoScore += twoPosition
    }
}