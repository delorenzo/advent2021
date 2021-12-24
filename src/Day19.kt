import java.io.File
import kotlin.math.abs

fun main() {
    val regex = Regex("--- scanner [0-9]+ ---")
    val regex2 = Regex("(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)$")
    var scannerCount = 0
    val scanners = mutableListOf<List<ScannerData>>()
    var dataList = mutableListOf<ScannerData>()
    File("src/input/input19-1.txt").readLines().map {
        if (it.isNotBlank()) {
            if (regex.matches(it)) {
                scanners.add(dataList)
                scannerCount++
                dataList = mutableListOf<ScannerData>()
            }
            else {
                val match = regex2.matchEntire(it)!!
                dataList.add(ScannerData(match.groupValues[1].toInt(), match.groupValues[2].toInt(), match.groupValues[3].toInt()))
            }
        }
    }
    scanners.removeAt(0)

    val theOneTrueScanner = findMatches(scanners)
    println("Part one:  ${theOneTrueScanner.size}")
}

fun findMatches(scanners: List<List<ScannerData>>) : List<ScannerData> {
    val theOneTrueScanner = scanners.first()
    val rotatedScanners : List<List<List<ScannerData>>> = rotateScanners(scanners)
    val foundScannerIndices = mutableListOf<Int>()
    val goodScanners = MutableList<List<ScannerData>>(scanners.size) { mutableListOf() }
    val scannersOffsetFromZero = MutableList<ScannerData>(scanners.size) { ScannerData(0,0,0) }
    foundScannerIndices.add(0)
    goodScanners[0] = scanners.first()

    while (foundScannerIndices.size < scanners.size) {
        var foundAnyMatches = false
//        scanners.filterIndexed { index, scannerData -> !foundScannerIndices.contains(index) }
//            .forEachIndexed { index, scannerData ->
        for (index in 1 until scanners.size) {
            if (foundScannerIndices.contains(index)) { continue }
            for (r in 0..23) {
                var foundMatch = false
                val current = rotatedScanners[index][r]
                for (i in scanners.indices) {
                    val good = goodScanners[i]
                    val offset = good.matches(current)
                    if (offset != null) {
                        val offsetFromZero = scannersOffsetFromZero[i].sum(offset)
                        scannersOffsetFromZero[index] = offsetFromZero.copy()
                        goodScanners[index] = current.toList()
                        theOneTrueScanner.combine(current, offsetFromZero)
                        foundMatch = true
                        break
                    }
                }
                if (foundMatch) {
                    foundScannerIndices.add(index)
                    foundAnyMatches = true
                    break
                }
            }
        }
        if ((foundScannerIndices.size < scanners.size) && !foundAnyMatches) {
            throw Exception("Matching hit an error.")
        }
    }
    return theOneTrueScanner
}

fun List<ScannerData>.matches(other: List<ScannerData>) : ScannerData? {
    this.flatMap { it -> other.map { other -> it to other}}.map {
        val offset = it.first.difference(it.second)
        val matchingCount = this.count { a ->
            other.map { b -> b.sum(offset) }.any { sum -> sum == a }
        }
        if (matchingCount >= 12) {
            return offset
        }
    }
    return null
}

fun rotateScanners(scanners: List<List<ScannerData>>) : List<List<List<ScannerData>>> {
     return scanners.map {
        IntRange(0, 23).map { rotation -> it.rotate(rotation) }
    }
}

data class ScannerData(val x: Int, val y: Int, val z: Int) {
    fun rotate(i: Int) : ScannerData {
        return this
    }

    fun manhattan(other: ScannerData) {
        abs(this.x-other.x) + abs(this.y-other.y) +  abs(this.z-other.z)
    }

    fun sum(other:ScannerData) : ScannerData {
        return ScannerData(
            this.x + other.x,
            this.y + other.y,
            this.z + other.z
        )
    }

    fun difference(other: ScannerData) : ScannerData {
        return ScannerData(
            this.x - other.x,
            this.y - other.y,
            this.z - other.z
        )
    }
}

fun List<ScannerData>.rotate(rotation: Int) : List<ScannerData> {
    return this.map {
        when (rotation / 4)  {
            0 -> it
            1 -> ScannerData(-it.x, it.y, -it.z)
            2 -> ScannerData(-it.z, it.y, -it.x)
            3 -> ScannerData(-it.z, it.y, it.x)
            4 -> ScannerData(it.x, it.z, -it.y)
            5 -> ScannerData(it.x, -it.z, it.y)
            else -> throw Exception("invalid rotation")
        }
    }.map {
        when (rotation % 4) {
            0 -> it
            1 -> ScannerData(-it.y, it.x, it.z)
            2 -> ScannerData(-it.x, -it.y, it.z)
            3 -> ScannerData(it.y, -it.x, it.z)
            else -> throw Exception("invalid rotation")
        }
    }
}

fun List<ScannerData>.combine(current: List<ScannerData>, offset: ScannerData) : List<ScannerData> {
    val new = current.map { it.sum(offset) }
    val set = current.toMutableSet()
    set.addAll(new)
    return set.toList()
}