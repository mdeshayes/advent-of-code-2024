package advent.y2025

import advent.util.readAllLines
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    val allLines = readAllLines("2025/day8_input.txt")
    val day08 = Day08(allLines)
    println("Result: ${day08.getResult(1000)}")
    println("Result 2: ${day08.getResult()}")
}

data class Position3d(val x: Long, val y: Long, val z: Long)

class Day08(private val lines: List<String>) {

    val positions = lines.map {
        val coordinates = it.split(",")
        Position3d(coordinates[0].toLong(), coordinates[1].toLong(), coordinates[2].toLong())
    }

    fun getResult(numberOfConnections: Int? = null): Long {
        val distancesByPair = mutableMapOf<Pair<Position3d, Position3d>, Double>()
        val pairsByDistance = mutableMapOf<Double, Pair<Position3d, Position3d>>()
        val distances = mutableListOf<Double>()
        for (i in 0..positions.size - 1) {
            for (j in i + 1..positions.size - 1) {
                val distance = computeDistance(positions[i], positions[j])
                distancesByPair[positions[i] to positions[j]] = distance
                pairsByDistance[distance] = positions[i] to positions[j]
                distances.add(distance)
            }
        }
        val pairedBoxes = distances
            .sorted()
            .map { mutableSetOf(pairsByDistance[it]!!.first, pairsByDistance[it]!!.second) }
            .take(numberOfConnections ?: distances.size)
        val circuits = mutableSetOf<MutableSet<Position3d>>()
        for (junction in pairedBoxes) {
            val box1 = junction.first()
            val box2 = junction.last()
            if (circuits.any { it.contains(box1) && it.contains(box2) }) continue
            if (circuits.isEmpty()) {
                circuits.add(mutableSetOf(box1, box2))
                continue
            }
            val box1Circuit = circuits.find { it.contains(box1) } ?: mutableSetOf(box1)
            val box2Circuit = circuits.find { it.contains(box2) } ?: mutableSetOf(box2)
            circuits.removeAll { it.contains(box1) || it.contains(box2) }
            val newCircuit = box1Circuit + box2Circuit
            circuits.add(newCircuit.toMutableSet())
            if (circuits.size == 1 && circuits.first().size == 1000) {
                return box1.x * box2.x
            }
        }
        return circuits.sortedByDescending { it.size }.take(3).map { it.size }.reduce(Int::times).toLong()
    }

    private fun computeDistance(box1: Position3d, box2: Position3d): Double {
        return sqrt(
            (box1.x - box2.x).toDouble().pow(2) + (box1.y - box2.y).toDouble().pow(2) + (box1.z - box2.z).toDouble()
                .pow(2)
        )
    }

}



