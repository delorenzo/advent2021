import java.io.File
//2351
fun main() {
    val regex = Regex("target area: x=(-?[0-9]+)..(-?[0-9]+), y=(-?[0-9]+)..(-?[0-9]+)")
    val match = regex.matchEntire(File("src/input/input17-2.txt").readLines().first())!!
    println(match.groupValues)
    val targetX = IntRange(match.groupValues[1].toInt(), match.groupValues[2].toInt())
    val targetY = IntRange(match.groupValues[3].toInt(), match.groupValues[4].toInt())

    var maxY = 0
    var set = mutableSetOf<Pair<Int,Int>>()

    for (i in 0 until 10000) {
        for (j in -10000 until 10000) {
            val p = Probe(0, 0, i, j)
            val result = p.doSteps(targetX, targetY)
            maxY = maxOf(result, maxY)
            if (result > -1) {
                set.add(i to j)
            }
        }
    }
    println("Max y is $maxY")
    println("Count is ${set.count()}")
}

data class Probe(var posX: Int = 0, var posY: Int = 0, var xVelocity: Int, var yVelocity: Int) {
    fun doSteps(targetX: IntRange, targetY: IntRange) : Int {
        var maxY = 0
        var target = false
        while (posX <= targetX.last && posY >= targetY.first) {
            if (targetX.contains(posX) && targetY.contains(posY)) {
                target = true
            }
            maxY = maxOf(maxY, posY)
            step()
        }
        return if (target) {
            maxY
        } else {
            -1
        }
    }

    fun step() {
        posX += xVelocity
        posY += yVelocity

        if (xVelocity > 0) { xVelocity -- }
        if (xVelocity < 0) { xVelocity ++ }
        yVelocity --
    }
}