import java.io.File

fun main() {
    val partOnePosition = Position()
    val partTwoPosition = Position()
    val instructions = File("src/input/input2-2.txt").readLines().map{
        instructionFromString(it.trim())
    }.toList()
    instructions.forEach {
        partOnePosition.part1(it)
        partTwoPosition.part2(it)
    }
    println("Part 1:")
    println("Position: $partOnePosition")
    println("Multiplied:  ${partOnePosition.multiply()}")
    println("-----")
    println("Part 2:")
    println("Position: $partTwoPosition")
    println("Multiplied:  ${partTwoPosition.multiply()}")
}

fun instructionFromString(string: String) : Instruction {
    val regex = Regex("(up|down|forward) (\\d+)")
    val forward = "forward"
    val down = "down"
    val up = "up"
    val match = regex.matchEntire(string)!!
    return when {
        match.groupValues[1] == forward -> {
            Instruction(Direction.FORWARD, match.groupValues[2].toInt())
        }
        match.groupValues[1] == down -> {
            Instruction(Direction.DOWN, match.groupValues[2].toInt())
        }
        match.groupValues[1] == up -> {
            Instruction(Direction.UP, match.groupValues[2].toInt())
        }
        else -> throw Exception("Direction ${match.groupValues[1]} is unexpected value")
    }
}

data class Instruction(val direction: Direction, val scalar: Int)

data class Position(var x: Long = 0L, var depth: Long = 0L, var aim: Long = 0L) {
    fun part1(instruction: Instruction) {
        when (instruction.direction) {
            Direction.FORWARD -> {
                x += instruction.scalar
            }
            Direction.DOWN -> {
                depth += instruction.scalar
            }
            Direction.UP -> {
                depth -= instruction.scalar
            }
        }
    }

    fun part2(instruction: Instruction) {
        when (instruction.direction) {
            Direction.FORWARD -> {
                x += instruction.scalar
                depth += aim * instruction.scalar
            }
            Direction.DOWN -> {
                aim += instruction.scalar
            }
            Direction.UP -> {
                aim -= instruction.scalar
            }
        }
    }

    fun multiply() : Long {
        return x * depth
    }
}

enum class Direction {
    FORWARD, DOWN, UP
}