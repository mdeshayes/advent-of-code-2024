package advent.y2025

import advent.util.readAllLines
import kotlin.math.min


fun main() {
    val allLines = readAllLines("2025/day10_input.txt")
    val day10 = Day10(allLines)
    println("Fewest button pressed: ${day10.getFewestButtonsPressed()}")
}

data class Machine(val lights: MutableList<Boolean>, val buttons: List<List<Int>>, val joltages: List<Int>)

const val regex: String = "\\[(.*)\\](.*)\\{(.*)\\}"
const val limit: Long = 10L

class Day10(lines: List<String>) {

    private var bestFound = limit

    private val machines = lines.map {
        val matches = Regex(regex).matchEntire(it)
        val lights = matches!!.groupValues[1].map { light -> light == '#' }.toMutableList()
        val buttons: List<List<Int>> = matches.groupValues[2].trim().split(" ").map { button ->
            button.replace("(", "").replace(")", "").split(",").map(String::toInt)
        }
        val joltages = matches.groupValues[3].split(",").map(String::toInt)
        Machine(lights, buttons, joltages)
    }

    fun getFewestButtonsPressed(): Long {
        return machines.sumOf {
            bestFound = limit
            getFewestButtonsPressed(it, it.lights.map { false }, 0).also { count -> println(count) }
        }
    }

    private fun getFewestButtonsPressed(
        machine: Machine,
        lights: List<Boolean>,
        currentCount: Long,
    ): Long {
        if (machine.lights == lights) {
            if (currentCount < bestFound) {
                bestFound = currentCount
            }
            return currentCount
        }
        if (currentCount > bestFound) {
            return Long.MAX_VALUE
        }
        return machine.buttons.minOf {
            getFewestButtonsPressed(
                machine,
                applyButton(lights, it),
                currentCount + 1
            )
        }
    }

    private fun applyButton(
        lights: List<Boolean>,
        button: List<Int>,
    ): List<Boolean> {
        val newLights = lights.toMutableList()
        button.forEach { newLights[it] = !newLights[it] }
        return newLights
    }


}



