package advent.y2025

import java.io.File


fun main() {
    val allLines1 =
        File("/home/mathias/IdeaProjects/perso/advent-of-code-2024/src/main/resources/2025/day10_input.txt")
            .readLines()
    val allLines = allLines1
    val day10 = Day10(allLines)
//    println("Fewest button pressed: ${day10.getFewestButtonsPressed()}")
    println("Fewest button pressed for joltages: ${day10.getFewestButtonsPressedForJoltages()}")
}

data class Machine(val lights: MutableList<Boolean>, val buttons: List<List<Int>>, val joltages: List<Int>)

const val regex: String = "\\[(.*)](.*)\\{(.*)}"
const val limit: Long = 10L
const val limitJoltages: Long = 20L

class Day10(lines: List<String>) {

    private var bestFound = limit
    private var bestFoundJoltages = limitJoltages

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
        var count = 0
        return machines.sumOf {
            bestFound = limit
            getFewestButtonsPressed(it, it.lights.map { false }, 0).also { println(count++) }
        }
    }

    fun getFewestButtonsPressedForJoltages(): Long {
        var count = 0
        return machines.sumOf {
            bestFoundJoltages = limitJoltages
            getFewestButtonsPressedForJoltages(it, it.joltages.map { 0 }, 0).also { println(count++) }
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

    private fun getFewestButtonsPressedForJoltages(
        machine: Machine,
        joltages: List<Int>,
        currentCount: Long,
    ): Long {
        if (machine.joltages == joltages) {
            if (currentCount < bestFoundJoltages) {
                bestFoundJoltages = currentCount
            }
            return currentCount
        }
        if (currentCount > bestFoundJoltages || machine.joltages.mapIndexed { index, i -> joltages[index] > i }
                .any { it }) {
            return Long.MAX_VALUE
        }
        return machine.buttons.minOf {
            getFewestButtonsPressedForJoltages(
                machine,
                applyButtonForJoltages(joltages, it),
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

    private fun applyButtonForJoltages(
        joltages: List<Int>,
        button: List<Int>,
    ): List<Int> {
        val newJoltages = joltages.toMutableList()
        button.forEach { newJoltages[it]++ }
        return newJoltages
    }


}



