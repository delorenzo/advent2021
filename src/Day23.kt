import java.io.File
import java.util.*

fun main() {
    val input = File("src/input/input23-4.txt").readLines().map{it}
    val hallway = Hallway(11)
    val guys = listOf('A', 'B', 'C', 'D')
    input[1].mapIndexed { index, c -> if (guys.contains(c)) {
            hallway.tiles[index] = Amphipod.parse(c)
        }
    }
    val size = 4
    val roomLines = input.filterIndexed { index, s -> index > 1 && index <= (1 + size) }
    val rooms = IntRange(0,3).toList().map { index ->
        Room(
            type = Amphipod.fromIndex(index),
            size = size,
            slots = roomLines.map { line -> Amphipod.parse(line[3 + 2 * index])  }.toMutableList()
        )
    }
    println(solve(Cave(rooms, hallway)))
}

fun solve(cave: Cave) : Int {
    val next = PriorityQueue<CaveElement>(compareBy { it.cost - it.correctlyPlaced() })
    next.add(CaveElement(cave, 0))
    val visited = mutableMapOf<CaveElement, Boolean>()
    var minSolve = Int.MAX_VALUE

    while (!next.isEmpty()) {
        val current = next.poll()
        if (current.item.full()) {
            return current.cost
        }
        current.item.moves()?.filterNot { visited.getOrDefault(it, false)}?.map {
            visited[it] = true
            next.add(CaveElement(it.item, it.cost + current.cost))
        }
    }
    return minSolve
}

data class CaveElement(val item: Cave, val cost: Int) {
    fun correctlyPlaced(): Int {
        return item.rooms.sumOf { room -> room.slots.count { it == room.type } }
    }
}

enum class Amphipod(val symbol: Char, val energy : Int) {
    AMBER('A', 1),
    BRONZE('B', 10),
    COPPER('C', 100),
    DESERT('D', 1000);

    companion object {
        fun parse(symbol: Char): Amphipod? {
            return when (symbol) {
                'A' -> AMBER
                'B' -> BRONZE
                'C' -> COPPER
                'D' -> DESERT
                else -> null
            }
        }

        fun fromIndex(i : Int) : Amphipod {
            return when (i) {
                0 -> AMBER
                1 -> BRONZE
                2 -> COPPER
                3 -> DESERT
                else -> throw Exception()
            }
        }
    }
}

data class Room(val type: Amphipod, val size: Int, val slots : MutableList<Amphipod?> = MutableList(size) { null; }) {
    fun copy() : Room {
        return Room(type, size, slots.toMutableList())
    }

    fun accepts(other: Amphipod) : Boolean {
        return other.symbol == type.symbol && ( slots.all { it == null || it == type } )
    }

    fun full() : Boolean {
        return slots.all { it != null && it == type }
    }

    fun empty() : Boolean {
        return slots.all { it == null }
    }

    fun accept(other: Amphipod) : Int {
        assert(accepts(other))
        assert(!full())
        val firstEmpty = slots.indexOfFirst { it == null }
        slots[firstEmpty] = other
        return (firstEmpty + 1)
    }

    fun take() : Pair<Amphipod, Int>? {
        for (i in slots.indices) {
            if (slots[i] == null) continue
            if (slots[i] != type || (i + 1 < slots.size && slots[i+1] != type)) {
                val amphipod = slots[i]!!
                slots[i] = null
                return amphipod to i+1
            }
        }
        return null
    }
}

fun Int.roomToTile() : Int {
    return (2 * this) + 2
}


data class Hallway(val size: Int, val tiles: MutableList<Amphipod?> = MutableList<Amphipod?>(size) { null } ) {
    fun copy() : Hallway {
        return Hallway(size, tiles.toMutableList())
    }

    fun moveRoomToRoom(sourceRoom: Int, targetRoom: Int) : Int {
        return move(sourceRoom.roomToTile(), targetRoom.roomToTile(), false)
    }

    fun moveRoomToTile(sourceRoom: Int, target: Int) : Int {
        return move(sourceRoom.roomToTile(), target, true)
    }

    fun moveTileToRoom(source: Int, targetRoom: Int) : Int {
        return move(source, targetRoom.roomToTile(), false)
    }

    fun move(source: Int, target: Int, targetIsTile : Boolean) : Int {
        if (source == target) return 0
        if (targetIsTile && tileInFrontOfRoom(target)) return 0
        if (source < target) {
            if (targetIsBlocked(source+1, target)) return 0
            return target - source
        } else {
            if (targetIsBlocked(target, source-1)) return 0
            return source - target
        }
    }

    fun targetIsBlocked(source: Int, target: Int) : Boolean {
        for (i in source .. target) {
            if (tiles[i] != null) { return true }
        }
        return false
    }

    fun tileInFrontOfRoom(target: Int) : Boolean {
        return target >= 2 && target % 2 == 0 && target <= this.size-2
    }
}

fun List<Room>.copy() : List<Room> {
    return this.map { it.copy() }.toList()
}

data class Cave(val rooms : List<Room>, val hallways: Hallway = Hallway(11)) {
    fun full(): Boolean {
        return rooms.all { it.full() }
    }

    fun copy(): Cave {
        return Cave(
            rooms = this.rooms.copy(),
            hallways = this.hallways.copy()
        )
    }

    fun moves() : List<CaveElement>? {
        if (full()) return null
        val moves = mutableListOf<CaveElement>()
        for (r in rooms.indices) {
            val cave = this.copy()
            checkForMovingFromHallwayToRoom(cave, r, moves)
            checkForMovingOutOfRoom(cave, r, moves)
        }
        return moves
    }

    fun checkForMovingFromHallwayToRoom(cave : Cave, r: Int, moves: MutableList<CaveElement>) {
        for (h in hallways.tiles.indices) {
            val amphipod = hallways.tiles[h]
            amphipod?.let {
                if (rooms[r].accepts(it)) {
                    val moveCost = hallways.moveTileToRoom(source = h, targetRoom = r)
                    if (moveCost > 0) {
                        val newCave = cave.copy()
                        val roomCost = newCave.rooms[r].accept(it)
                        newCave.hallways.tiles[h] = null
                        moves.add(
                            CaveElement(
                                newCave, (roomCost + moveCost) * it.energy
                            )
                        )
                    }
                }
            }
        }
    }

    fun checkForMovingOutOfRoom(oldCave: Cave, s: Int, moves: MutableList<CaveElement>) {
        val cave = oldCave.copy()
        val moveOut = cave.rooms[s].take() ?: return
        for (t in cave.rooms.indices) {
            if (s == t) continue
            if (!cave.rooms[t].accepts(moveOut.first)) continue
            val accessCost = cave.hallways.moveRoomToRoom(s, t)
            if (accessCost > 0) {
                val newCave = cave.copy()
                val moveInCost = newCave.rooms[t].accept(moveOut.first)
                moves.add(CaveElement(
                    newCave,
                    (moveOut.second + accessCost + moveInCost) * moveOut.first.energy
                ))
            }
        }

        for (h in cave.hallways.tiles.indices) {
            val access = cave.hallways.moveRoomToTile(s, h)
            if (access > 0) {
                val newCave = cave.copy()
                newCave.hallways.tiles[h] = moveOut.first
                moves.add(CaveElement(
                    newCave,
                    (moveOut.second + access) * moveOut.first.energy
                ))
            }
        }
    }
}