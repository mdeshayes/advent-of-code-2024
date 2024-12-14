package advent.util

import advent.util.Map2d.Direction.Companion.EAST
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.WEST
import kotlin.math.abs

class AStarPath(
    private val map: Map2d<Char>,
    private val overCostForTurn: Int = 1000
) {

    fun getAllBestPaths(start: Pair<Int, Int>, end: Pair<Int, Int>): MutableSet<List<Node>> {
        return getAllBestPaths(
            Node(start.first, start.second, direction = SOUTH),
            Node(end.first, end.second, direction = SOUTH),
        )
    }

    fun getAllBestPaths(start: Node, end: Node): MutableSet<List<Node>> {
        val bestScore = getBestPath(start, end).last().costFromStart
        return map.map { (coord, char) ->
            if (char != '#') {
                val (x, y) = coord
                val firstPath = AStarPath(map, overCostForTurn).getBestPath(
                    Node(start.x, start.y, direction = EAST),
                    Node(x, y, direction = NORTH)
                )
                val firstPathCost = firstPath.last().costFromStart
                if (firstPathCost > bestScore) {
                    return@map mutableListOf()
                }
                val fullPath = AStarPath(map, overCostForTurn).getBestPath(
                    firstPath.last(),
                    Node(end.x, end.y, direction = NORTH)
                )
                val secondPathCost = fullPath.last().costFromStart
                if (firstPathCost + secondPathCost > bestScore) {
                    return@map mutableListOf()
                } else {
                    fullPath
                }
            } else {
                emptyList()
            }
        }.filter { it.isNotEmpty() }.toMutableSet()

    }

    fun getBestPath(start: Pair<Int, Int>, end: Pair<Int, Int>): MutableList<Node> {
        return getBestPath(
            Node(start.first, start.second, direction = SOUTH),
            Node(end.first, end.second, direction = SOUTH)
        )
    }

    fun getBestPath(start: Node, end: Node): MutableList<Node> {
        val openList = mutableListOf(start)
        val closedList = mutableSetOf<Node>()
        start.costFromStart = 0
        start.estimatedDistance = manhattanDistance(start, end)
        while (openList.isNotEmpty()) {
            val current = openList.minByOrNull { it.totalCost } ?: break

            if (current.x == end.x && current.y == end.y) {
                return reconstructPath(current)
            }

            openList.remove(current)
            closedList.add(current)

            for (neighbor in getNeighbors(current)) {
                if (neighbor in closedList || !map.isValid(neighbor.x, neighbor.y) || map.getOrNull(
                        neighbor.x,
                        neighbor.y
                    ) == '#'
                ) {
                    continue
                }

                var tentativeCostFromStart = current.costFromStart + 1
                if (neighbor.direction != current.direction) {
                    tentativeCostFromStart += overCostForTurn
                }
                if (tentativeCostFromStart <= neighbor.costFromStart) {
                    neighbor.costFromStart = tentativeCostFromStart
                    neighbor.estimatedDistance = manhattanDistance(neighbor, end)
                    neighbor.parent = current

                    if (neighbor !in openList) {
                        openList.add(neighbor)
                    }
                }
            }
        }
        return mutableListOf()
    }

    private fun manhattanDistance(a: Node, b: Node): Int {
        return abs(a.x - b.x) + abs(a.y - b.y)
    }

    private fun reconstructPath(node: Node): MutableList<Node> {
        val path = mutableListOf<Node>()
        var current: Node? = node
        while (current != null) {
            path.add(current)
            current = current.parent
        }
        return path.asReversed()
    }

    private fun getNeighbors(node: Node) = listOf(NORTH, SOUTH, WEST, EAST)
        .map { Node(node.x + it.deltaX, node.y + it.deltaY, direction = it) }
        .filter { isValidPosition(it) }

    private fun isValidPosition(node: Node) = map.isValid(node.x, node.y) && map.get(node.x, node.y) != '#'
}

data class Node(
    val x: Int,
    val y: Int,
    var costFromStart: Int = Int.MAX_VALUE,
    var estimatedDistance: Int = 0,
    var parent: Node? = null,
    var direction: Map2d.Direction
) {
    val totalCost: Int get() = costFromStart + estimatedDistance
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        return 31 * x + y
    }

    override fun toString(): String {
        return "$x:$y"
    }
}