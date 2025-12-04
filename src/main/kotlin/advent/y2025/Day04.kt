package advent.y2025

import advent.util.Map2d
import advent.util.Map2d.Direction.Companion.EAST
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Map2d.Direction.Companion.NORTH_EAST
import advent.util.Map2d.Direction.Companion.NORTH_WEST
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.SOUTH_EAST
import advent.util.Map2d.Direction.Companion.SOUTH_WEST
import advent.util.Map2d.Direction.Companion.WEST
import advent.util.plus
import java.io.File

fun main() {
    val allLines =
        File("/home/mathias/IdeaProjects/perso/advent-of-code-2024/src/main/resources/2025/day4_input.txt").readLines()
    val day04 = Day04(allLines)
    println("Number of accessible papers: ${day04.getNumberOfAccessiblePapers()}")
}

class Day04(lines: List<String>) {

    val grid = Map2d(lines) { it }

    fun getNumberOfAccessiblePapers(): Long {
        return grid.count { (position, _) -> isAccessible(position.first, position.second) }.toLong()
    }

    private fun isAccessible(x: Int, y: Int): Boolean {
        if (grid.get(x, y) != '@') return false
        val neighbors = listOf(
            NORTH + (x to y),
            NORTH_WEST + (x to y),
            NORTH_EAST + (x to y),
            WEST + (x to y),
            EAST + (x to y),
            SOUTH + (x to y),
            SOUTH_WEST + (x to y),
            SOUTH_EAST + (x to y),
        ).count { grid.getOrNull(it) == '@' }
        return neighbors < 4
    }


}

