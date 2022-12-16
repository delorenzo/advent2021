import java.io.File

fun main() {
    val regex = Regex("(on|off) x=(-?[0-9]+)..(-?[0-9]+),y=(-?[0-9]+)..(-?[0-9]+),z=(-?[0-9]+)..(-?[0-9]+)")
    val partOneMap = mutableMapOf<List<Long>, Boolean>()
    var activeCuboids = mutableListOf<Cuboid>()
    File("src/input/input22-3.txt").readLines().map {
        val match = regex.matchEntire(it)!!
        val on = match.groupValues[1] == "on"
        val x = LongRange(match.groupValues[2].toLong(), match.groupValues[3].toLong())
        val y = LongRange(match.groupValues[4].toLong(), match.groupValues[5].toLong())
        val z = LongRange(match.groupValues[6].toLong(), match.groupValues[7].toLong())

        if (x.first() > 50 || y.first() > 50 || z.first() > 50 || x.last() < -50 ||  y.last() < -50 || z.last() < -50) {
            //println("Skipping ${x.first()}..${x.last()}")
        } else {
            x.map { a-> y.map { b->  z.map { c->  partOneMap[listOf(a,b,c)] = on } } }
        }
        val newCuboid = Cuboid(x, y, z)
        activeCuboids = activeCuboids.map { cube -> cube.removeChunk(newCuboid) }.flatten().toMutableList()
        if (on) {
            activeCuboids.add(newCuboid)
        }
    }
    print("Part one:  ")
    println(partOneMap.filterValues { it }.count())
    print("Part two:  ")
    println(activeCuboids.sumOf { it.sum() })
}

fun LongRange.inside(other: LongRange) : Boolean {
    return first >= other.first && last <= other.last
}

fun LongRange.overlap(other: LongRange) : LongRange? {
    return when {
        // they are identical
        first == other.first && last == other.last -> first..last
        // no overlap
        last < other.first -> null
        other.last < first -> null
        // one is completely inside the other.. return the inside range
        first < other.first && last > other.last ->  other.first..other.last
        other.first < first && other.last > last -> first..last
        // they start the same but one is shorter
        first == other.first && last < other.last ->  first..last
        first == other.first && other.last < last -> first..other.last
        // the end the same but one starts later
        last == other.last && first < other.first -> other.first..last
        last == other.last ->  first..last
        // one starts before the other but ends before the other
        first < other.first && last < other.last && last > other.first -> other.first..last
        first > other.first && last > other.last && first < other.last -> first..other.last
        // one overlap
        last == other.first -> last..last
        first == other.last -> first..first
        else -> null
    }
}

fun LongRange.remove(other: LongRange) : List<LongRange> {
    val currentRange = this
    val overlap = overlap(other) ?: run { return listOf(first..last) }
    if (overlap.first <= first && overlap.last >= last) { return emptyList() }
    return mutableListOf<LongRange>().apply {
        if (overlap.first > currentRange.first) add(currentRange.first until overlap.first)
        if (overlap.last < currentRange.last) add(overlap.last + 1 .. last)
    }
}

data class Cuboid(val X: LongRange, val Y: LongRange, val Z: LongRange) {
    fun sum() : Long {
        return ( X.last - X.first + 1) * (Y.last-Y.first +  1) * (Z.last - Z.first +1)
    }

    fun overlappingCuboid(other: Cuboid): Cuboid? {
        val xOverlap = X.overlap(other.X) ?: run { return null }
        val yOverlap = Y.overlap(other.Y) ?: run { return null }
        val zOverlap = Z.overlap(other.Z) ?: run { return null }
        return Cuboid(xOverlap, yOverlap, zOverlap)
    }

    fun cuboidInside(other: Cuboid): Boolean {
        return X.inside(other.X) && Y.inside(other.Y) && Z.inside(other.Z)
    }

    fun copy() : Cuboid {
        return Cuboid(X, Y, Z)
    }

    fun removeChunk(other: Cuboid) : List<Cuboid> {
        val overlap = overlappingCuboid(other) ?: run { return listOf(copy()) }
        return mutableSetOf<Cuboid>().apply {
            val leftRight = X.remove(other.X).map { Cuboid(it, Y, Z) }
            val topBottom = Y.remove(other.Y).map { Cuboid(overlap.X, it, Z) }
            val frontBack = Z.remove(other.Z).map { Cuboid(overlap.X, overlap.Y, it)}
            addAll(leftRight)
            addAll(topBottom)
            addAll(frontBack)
        }.toList()
    }
}