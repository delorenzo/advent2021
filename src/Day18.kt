import java.io.File
import java.lang.Math.ceil
import java.util.*
import kotlin.collections.ArrayDeque

var NODE_COUNT = 0
fun main() {
    val input = File("src/input/input18-2.txt").readLines()
    val partOne = partOne(input)
    println("Part one:  $partOne")
    val partTwo = partTwo(input)
    println("Part two:  $partTwo")
}

fun partTwo(input: List<String>) : Int {
    val indices = input.indices.flatMap { it-> input.indices.map { other ->
        it to other
    } }.filterNot { it.first == it.second }
    val partTwo = indices.maxOf {
        partOne(listOf(input[it.first], input[it.second])) }
    return partTwo
}

fun partOne(input: List<String>) : Int {
    var homework = HomeworkNode()
    parseLine(homework, input.first())
    for (i in 1 until input.size) {
        val new = HomeworkNode()
        parseLine(new, input[i])
        homework.add(new)
        homework = homework.parent!!
        //homework.print()

        var check = false
        while (!check) {
            check = checkForExplode(homework)
            check = check && checkForSplits(homework)
            //homework.print()
        }
        //println("---")
    }
    return homework.magnitude()
}

fun checkForExplode(homework: HomeworkNode) : Boolean {
    val nest = homework.getNests()
    if (nest.isNotEmpty()) {
        //println("Explode!!")
        nest.first().explode()
        return false
    }
    return true
}

fun checkForSplits(homework: HomeworkNode) : Boolean {
    val splits = homework.getSplits()
    if (splits.isNotEmpty()) {
        //println("Splits!!")
        splits.first().split()
        return false
    }
    return true
}

fun parseLine(current: HomeworkNode, str: String) : HomeworkNode {
    val parenQueue = ArrayDeque<String>()
    for (i in 1 until str.length) {
        if (str[i] == '[') {
            parenQueue.add(")")
        }
        if (str[i] == ']') {
            parenQueue.removeFirst()
        }
        if (str[i] == ',' && parenQueue.isEmpty()) {
            val first = str.substring(1, i)
            val second = str.substring(i+1, str.length-1)
            if (first.length == 1) {
                current.first = HomeworkNode(parent = current, leafValue = first.toInt())
            } else {
                current.first = parseLine(HomeworkNode(parent = current), first)
            }
            if (second.length == 1) {
                current.second = HomeworkNode(parent = current, leafValue = second.toInt())
            } else {
                current.second = parseLine(HomeworkNode(parent = current), second)
            }
            break
        }
    }
    return current
}

data class HomeworkNode(val id: Int = NODE_COUNT++, var first: HomeworkNode? = null, var second: HomeworkNode? = null, var parent: HomeworkNode? = null, var leafValue: Int? = null) {
    fun print() {
        println(getPrintString())
    }

    override fun equals(other: Any?): Boolean {
        return other is HomeworkNode && this.id == other.id
    }

    private fun getPrintString() : String {
        if( leafValue != null) { return leafValue.toString() }
        first?.let {
            return "[${first?.getPrintString()},${second?.getPrintString()}]"
        }
        return ""
    }

    fun add(other: HomeworkNode) : HomeworkNode {
        val newRoot = HomeworkNode(first = this, second = other)
        this.parent = newRoot
        other.parent = newRoot
        return newRoot
    }

    fun getNests() : List<HomeworkNode> {
        val result = mutableListOf<HomeworkNode>()
        countNests(result, 0)
        return result
    }

    fun getSplits(): List<HomeworkNode>  {
        val result = mutableListOf<HomeworkNode>()
        getSplits(result)
        return result
    }

    fun countNests(nestList: MutableList<HomeworkNode>, nestCount: Int = 0) {
        if (nestCount == 4) {
            nestList.add(this)
            return
        }
        if (first != null && first?.leafValue == null) {
            first?.countNests(nestList, nestCount + 1)
        }
        if (second != null && second?.leafValue == null) {
            second?.countNests(nestList, nestCount +1)
        }
    }

    fun getSplits(splitList: MutableList<HomeworkNode>) {
        if (this.leafValue?.compareTo(9) == 1) {
            splitList.add(this)
            return
        }
        first?.getSplits(splitList)
        second?.getSplits(splitList)
    }

    fun split() {
        val left = this.leafValue!!.div(2)
        val right = kotlin.math.ceil(this.leafValue!!.toDouble() / 2.0).toInt()
        this.leafValue = null
        this.first = HomeworkNode(leafValue = left, parent = this)
        this.second = HomeworkNode(leafValue = right, parent = this)
    }

    fun explode() {
        val left = this.seekLeftPair()
        left?.let {
            val total = it.leafValue!! + this.first!!.leafValue!!
            it.leafValue = total
        }

        val right = this.seekRightPair()
        right?.let {
            val total = it.leafValue!! + this.second!!.leafValue!!
            it.leafValue = total
        }

        this.first = null
        this.second = null
        this.leafValue = 0
    }

    fun seekLeftPair() : HomeworkNode? {
        var current : HomeworkNode? = this
        while (current != null) {
            if (current?.parent?.second == current) {
                return findRightmostChild(current)
            }
            current = current.parent
        }
        return null
    }

    fun seekRightPair() : HomeworkNode? {
        var current : HomeworkNode? = this
        while (current != null) {
            if (current?.parent?.first == current) {
                return findLeftmostChild(current)
            }
            current = current.parent
        }
        return null
    }

    fun findRightmostChild(node: HomeworkNode) : HomeworkNode? {
        var current = node.parent!!.first
        while (current!!.leafValue == null) {
            current = current.second
        }
        return current
    }

    fun findLeftmostChild(node: HomeworkNode) : HomeworkNode? {
        var current = node.parent!!.second
        while (current!!.leafValue == null) {
            current = current.first
        }
        return current
    }

    fun magnitude() : Int {
        return if (this.first == null && this.second == null) {
            this.leafValue!!
        } else {
            this.first!!.magnitude() * 3 + this.second!!.magnitude() * 2
        }
    }
}