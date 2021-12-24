import java.io.File

fun main() {
    val regex = Regex("(on|off) x=(-?[0-9]+)..(-?[0-9]+),y=(-?[0-9]+)..(-?[0-9]+),z=(-?[0-9]+)..(-?[0-9]+)")
    val onMap = mutableMapOf<List<Int>, Boolean>()
    File("src/input/input22-3.txt").readLines().map {
        val match = regex.matchEntire(it)!!
        val on = match.groupValues[1] == "on"
        val x = IntRange(match.groupValues[2].toInt(), match.groupValues[3].toInt()).toList()
        val y = IntRange(match.groupValues[4].toInt(), match.groupValues[5].toInt()).toList()
        val z = IntRange(match.groupValues[6].toInt(), match.groupValues[7].toInt()).toList()

//        val cubes = x.map { a-> y.map { b->  z.map { c->  listOf(a,b,c) } } }.flatten().flatten()
//        cubes.forEach { cube ->
//            onMap[cube] = on
//        }
        x.map { a-> y.map { b->  z.map { c->  onMap[listOf(a,b,c)] = on } } }
    }
    println(onMap.filterValues { it }.count())
}