package advent.y2025

import advent.util.AStarPath
import advent.util.Map2d
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.SOUTH_EAST
import advent.util.Map2d.Direction.Companion.SOUTH_WEST
import advent.util.plus
import advent.util.readAllLines

fun main() {
    val allLines = readAllLines("2025/day7_input.txt")
    val day07 = Day07(allLines)
    println("Number of splits: ${day07.getNumberOfSplits()}")
    println("Number of timelines: ${day07.getNumberOfTimelines()}")
}

class Day07(private val lines: List<String>) {

    fun getNumberOfSplits(): Long {
        val tachyonMap: Map2d<Char> = Map2d(lines) { it }
        var nbSplits = 0L
        (0 until tachyonMap.getHeight()).forEach { row ->
            tachyonMap
                .filter { (position, char) -> position.second == row && char == 'S' }
                .forEach { (position, _) ->
                    val south = tachyonMap.getOrNull(SOUTH + position)
                    when (south) {
                        '^' -> {
                            tachyonMap.set(SOUTH_WEST + position, 'S')
                            tachyonMap.set(SOUTH_EAST + position, 'S')
                            nbSplits++
                        }

                        '.' -> {
                            tachyonMap.set(SOUTH + position, 'S')
                        }
                    }
                }

        }
        return nbSplits
    }

    private fun getNumberOfSplits(position: Pair<Int, Int>): Long {
        val tachyonMap: Map2d<Char> = Map2d(lines) { it }
        val south = tachyonMap.getOrNull(SOUTH + position)
        return when (south) {
            '^' -> 1 + getNumberOfSplits(SOUTH_EAST + position) + getNumberOfSplits(SOUTH_WEST + position)
            '.' -> getNumberOfSplits(SOUTH + position)
            else -> 0
        }
    }

    fun getNumberOfTimelines(): Long {
        val cache: MutableMap<Pair<Int, Int>, Long> = mutableMapOf()
        val initialMap = Map2d(lines) { it }
        return getNumberOfTimelines(initialMap, initialMap.filter { (_, char) -> char == 'S' }[0].first, cache)
    }

    private fun getNumberOfTimelines(
        initialMap: Map2d<Char>,
        position: Pair<Int, Int>,
        cache: MutableMap<Pair<Int, Int>, Long>,
    ): Long {
        if (cache.containsKey(position)) {
            return cache[position]!!
        }
        val south = initialMap.getOrNull(SOUTH + position)
        val numberOfTimelines = when (south) {
            '^' -> getNumberOfTimelines(initialMap, SOUTH_WEST + position, cache) +
                    getNumberOfTimelines(initialMap, SOUTH_EAST + position, cache)

            '.' -> getNumberOfTimelines(initialMap, SOUTH + position, cache)
            else -> 1
        }
        cache[position] = numberOfTimelines
        return numberOfTimelines
    }

}



