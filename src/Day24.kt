import java.io.File
import kotlin.system.exitProcess

val variables = mutableListOf(0, 0, 0, 0)
val W = 0
val X = 1
val Y = 2
val Z = 3

fun main() {
    val regex = Regex("(inp|add|mul|div|mod|eql) ([wxyz])? ?(-?[0-9wxyz]+)?")
    val input = File("src/input/input24-2.txt").readLines()
    val args = mutableListOf<List<Int>>()
    var c = 4
    var a = 5
    var b = 15
    // Pull the variable arguments out of the input:
    // What's added to x, added to y, and divided from z (1 or 26)
    while (args.size < 14) {
        var line = input[a]
        var match = regex.matchEntire(line)
        val first = match!!.groupValues[3].toInt()
        line = input[b]
        match = regex.matchEntire(line)
        val second = match!!.groupValues[3].toInt()
        line = input[c]
        match = regex.matchEntire(line)
        val third = match!!.groupValues[3].toInt()
        a+=18
        b+=18
        c+=18
        args.add(listOf(first, second, third))
    }
    // Get the constrained inputs
    val inputs = solveForArgs(args)
    // Find possible serials using the constrained inputs
    val serial = findSerial(0, 0, inputs, args)
    // Find the min and max of this list
    println(serial.maxOrNull())
    println(serial.minOrNull())
}

/**
 * Starting from 0, run the sequence of transformations for each Z and W in the constrained set of inputs
 * and save the serial #s
 */
fun findSerial(index: Int, z: Int, input: List<Map<Int, MutableSet<Int>>>, args: List<List<Int>>) : List<String> {
    if (index == 14) return listOf("")
    return input[index].entries.filter { z in it.value }.flatMap { (w, _) ->
        val nextZ = check(z, w, args[index][0], args[index][1], args[index][2])
        findSerial(index+1, nextZ, input, args).map {
            w.toString() + it
        }
    }
}

/**
 * Z has to end with 0, so working backwards through the instructions, check which W values from 1 to 9
 * and which Z values from 0 to 1000000 will result in a valid output for Z.  Store these as a map of
 * {W : setOf(Zs)} for each digit: [{W : setOf(Zs)}, {W : setOf(Zs)}..], working backwards and using
 * the previous set of valid Zs to calculate each digit.
 * This constrains the input when looking for the set of valid serial numbers.
 */
fun solveForArgs(args : List<List<Int>>) : List<Map<Int, MutableSet<Int>>> {
    var validZRange = setOf(0)
    var index = args.size-1
    val inputs = List<MutableMap<Int, MutableSet<Int>>>(14) { mutableMapOf() }
    args.reversed().forEach { arg ->
        val validZ = mutableSetOf<Int>()
        for (w in 1..9) {
            IntRange(0, 1000000).filter { z -> check(z, w, arg[0], arg[1], arg[2]) in validZRange }.map { z ->
                val newSet = inputs[index].getOrPut(w) { mutableSetOf()  }
                newSet.add(z)
                validZ.add(z)
            }
        }
        validZRange = validZ
        index--
    }
    return inputs
}

fun check(z: Int, w : Int, arg: Int, arg2: Int, arg3: Int) : Int {
    var x = (z % 26) + arg
    var result = z / arg3
    x = if (x == w) {
        0
    } else {
        1
    }
    var y = 25
    y *= x
    y += 1
    result *= y
    y = (w + arg2) * x
    result += y
    return result
}

fun runMONAD(instructions: List<FullInstruction>, inputs: List<Int>) {
    var inputCount = 0
    var count = 0
    instructions.forEach {
        if (inputs[Z] > 100000000) { return }
        if (inputs[Z] < -100000000) { return }
        var bValue = 0
        if (it.b.isNotBlank()) {
            bValue = when(it.b) {
                "x" -> variables[X]
                "y" -> variables[Y]
                "w" -> variables[W]
                "z" -> variables[Z]
                else -> it.b.toInt()
            }
        }
        if (it.code == ALUInstruction.INP) {
            it.code.execute(it.a, inputs[inputCount++])
        } else {
            it.code.execute(it.a, bValue)
        }
        count++
    }
}

data class FullInstruction(val code: ALUInstruction, val a: Int, val b: String)

enum class ALUInstruction {
    INP {
        override fun execute(a: Int, b: Int?) {
            variables[a] = b!!
        }
    },
    ADD {
        override fun execute(a: Int, b: Int?) {
            variables[a] = variables[a] + b!!
        }
    },
    MUL {
        override fun execute(a: Int, b: Int?) {
            variables[a] = variables[a] * b!!
        }
    },
    DIV {
        override fun execute(a: Int, b: Int?) {
            variables[a] = variables[a].floorDiv(b!!)
        }
    },
    MOD {
        override fun execute(a: Int, b: Int?) {
            variables[a] = variables[a] % b!!
        }
    },
    EQL {
        override fun execute(a: Int, b: Int?) {
            if (variables[a] == b) {
                variables[a] = 1
            } else {
                variables[a] = 0
            }
        }
    };

    abstract fun execute (a : Int, b: Int?);
}