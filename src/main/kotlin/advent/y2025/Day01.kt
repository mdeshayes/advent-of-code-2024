package advent.y2025

import java.io.File
import java.lang.Math.floorMod
import kotlin.math.abs

fun main() {
    val allLines =
        File("/home/mathias/IdeaProjects/perso/advent-of-code-2024/src/main/resources/2025/day1_input.txt").readLines()
    val day01 = Day01(allLines)
    println("Part1: The code is ${day01.getPassword()}")
    println("Part1: The 0x434C49434B code is ${day01.get0x434C49434BPassword()}")
}

class Day01(lines: List<String>) {

    private val rotations = lines.map {
        if (it[0] == 'L') {
            -it.substring(1).toInt()
        } else {
            it.substring(1).toInt()
        }
    }

    fun getPassword() = rotations.fold(50 to 0) { p, e ->
        floorMod(p.first + e, 100) to if (pointingZero(p.first, e)) p.second + 1 else p.second
    }.second

    fun get0x434C49434BPassword() = rotations.fold(50 to 0) { p, e ->
        floorMod(p.first + e, 100) to p.second + passByZero(p.first, e)
    }.second

    private fun pointingZero(position: Int, rotation: Int): Boolean = floorMod(position + rotation, 100) == 0

    private fun passByZero(position: Int, rotation: Int): Int {
        val target = position + rotation % 100
        val nbTours = abs(rotation) / 100
        val additionalTour = target !in 1..99 && position != 0
        var nbZero = nbTours + if (additionalTour) 1 else 0
        println("position=$position, rotation=${rotation},nbTours=$nbTours, target=$target, additionalTour=$additionalTour, nbZero = $nbZero")
        return nbZero
    }
}

