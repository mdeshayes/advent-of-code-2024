package advent.util

import advent.util.Map2d.Direction

class Map2d<T : Any>(lines: List<String>, mapFunc: (Char) -> T) : Iterable<Pair<Pair<Int, Int>, T>> {

    private var internalMap: Array<Array<Any>>
    private var nbLines: Int = lines.size
    private var nbColumns: Int = lines[0].length

    init {
        internalMap = Array(nbColumns) { lineIndex ->
            Array(nbLines) { columnIndex ->
                mapFunc(lines[columnIndex][lineIndex])
            }
        }
    }

    fun set(x: Int, y: Int, value: T) {
        internalMap[x][y] = value
    }

    fun set(coord: Pair<Int, Int>, value: T) {
        set(coord.first, coord.second, value)
    }

    fun getOrNull(coord: Pair<Int, Int>): T? {
        return getOrNull(coord.first, coord.second)
    }

    fun get(coord: Pair<Int, Int>) = getOrNull(coord)!!

    fun getOrNull(x: Int, y: Int): T? {
        return try {
            internalMap[x][y] as T?
        } catch (e: ArrayIndexOutOfBoundsException) {
            return null
        }
    }

    fun get(x: Int, y: Int) = getOrNull(x, y)!!

    fun getOrNull(x: Int, y: Int, direction: Direction): T? {
        return try {
            internalMap[x + direction.deltaX][y + direction.deltaY] as T?
        } catch (e: IndexOutOfBoundsException) {
            return null
        }
    }

    fun get(x: Int, y: Int, direction: Direction) = getOrNull(x, y, direction)!!

    fun isValid(x: Int, y: Int) = x in 0..<nbColumns && y in 0..<nbLines

    fun isValid(x: Int, y: Int, direction: Direction) =
        x + direction.deltaX in 0..<nbColumns && y + direction.deltaY in 0..<nbLines

    fun getHeight() = nbLines

    fun getWidth() = nbColumns

    override fun toString(): String {
        var string = ""
        for (lineIndex in 0 until nbLines) {
            for (columnIndex in 0 until nbColumns) {
                string += internalMap[columnIndex][lineIndex]
            }
            string += "\n"
        }
        return string
    }

    class Direction(internal val deltaX: Int, internal val deltaY: Int) {

        companion object {
            val NORTH = Direction(0, -1)
            val SOUTH = Direction(0, 1)
            val EAST = Direction(1, 0)
            val WEST = Direction(-1, 0)
            val NORTH_WEST = NORTH + WEST
            val NORTH_EAST = NORTH + EAST
            val SOUTH_WEST = SOUTH + WEST
            val SOUTH_EAST = SOUTH + EAST
        }

        override fun toString(): String {
            return (deltaX to deltaY).toString()
        }

        operator fun plus(direction: Map2d.Direction): Direction {
            return Direction(this.deltaX + direction.deltaX, this.deltaY + direction.deltaY)
        }
    }

    override fun iterator(): Iterator<Pair<Pair<Int, Int>, T>> {
        return sequence {
            for (x in internalMap.indices) {
                for (y in internalMap[x].indices) {
                    yield(Pair(x, y) to internalMap[x][y] as T)
                }
            }
        }.iterator()
    }

}

operator fun Direction.plus(coord: Pair<Int, Int>): Pair<Int, Int> =
    coord.first + this.deltaX to coord.second + this.deltaY

