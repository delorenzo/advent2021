import java.io.File
import kotlin.collections.ArrayDeque

fun main() {
    val input = File("src/input/input10-2.txt").readLines()
    val partOne = input.sumOf { getCorruptedChunkScore(it.toCharArray()) }
    val partTwo = input.filter { getCorruptedChunkScore(it.toCharArray()) == 0L }
                       .map { incompleteChunk(it.toCharArray()) }
                       .sorted()
    val middleIndex = partTwo.size/2
    println("Part one:  $partOne")
    println("Part two:  ${partTwo[middleIndex]}")
}

fun incompleteChunk(input: CharArray) : Long {
    val stack = ArrayDeque<Char>()
    input.forEach {
        if (INPUTS.contains(it)) {
            stack.add(it)
        } else {
            stack.removeLast()
        }
    }
    return stack.reversed().map { CHUNKS[it] }.fold(0L) { sum, char -> sum * 5L + SCORE_PART_TWO[char]!!}
}

fun getCorruptedChunkScore(input: CharArray) : Long {
    val stack = ArrayDeque<Char>()
    input.forEach {
        if (INPUTS.contains(it)) {
            stack.add(it)
        } else {
            val starting = stack.removeLast()
            if (CHUNKS[starting] != it) {
                return SCORE[it]!!
            }
        }
    }
    return 0L
}

val CHUNKS = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>'
);

val INPUTS = CHUNKS.map { it.key }

val SCORE = mapOf(
    ')' to 3L,
    ']' to 57L,
    '}' to 1197L,
    '>' to 25137L
)

val SCORE_PART_TWO = mapOf(
    ')' to 1L,
    ']' to 2L,
    '}' to 3L,
    '>' to 4L
)