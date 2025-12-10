package advent.y2025

import advent.util.CELL_SIZE
import advent.util.Day15Application
import advent.util.readAllLines
import advent.y2024.Day15
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.collections.map
import kotlin.math.abs
import kotlin.math.min

fun main() {
    val allLines = readAllLines("2025/day9_input.txt")
    val day09 = Day09(allLines)
    println("Largest rectangle: ${day09.getLargestRectangleArea()}")
    println("Largest rectangle: ${day09.getLargestRectangleAreaWithGreenAndRedTilesOnly()}")
}

data class Tile(val x: Int, val y: Int, val turn: String)

class Day09(lines: List<String>) {

    private val redTilePositions: List<Pair<Int, Int>> = lines.map {
        it.split(",")[0].toInt() to it.split(",")[1].toInt()
    }

    var redTiles: MutableList<Tile> = mutableListOf()

    var switchableTiles: MutableList<Pair<Int, Int>> = mutableListOf()

    var tile1: Tile? = null
    var tile2: Tile? = null

    init {
        val initialTurn = getTurn(redTilePositions.last(), redTilePositions[0], redTilePositions[1])
        redTiles.add(Tile(redTilePositions[0].first, redTilePositions[0].second, initialTurn))
        for (index in 1 until redTilePositions.size - 1) {
            val previousTilePosition = redTilePositions[index - 1]
            val nextTilePosition = redTilePositions[index + 1]
            val tilePosition = redTilePositions[index]
            val turn: String = getTurn(previousTilePosition, tilePosition, nextTilePosition)
            redTiles.add(Tile(tilePosition.first, tilePosition.second, turn))
        }
        val lastTurn =
            getTurn(redTilePositions[redTilePositions.size - 2], redTilePositions.last(), redTilePositions[0])
        redTiles.add(Tile(redTilePositions.last().first, redTilePositions.last().second, lastTurn))
        redTiles.forEach { println(it) }
        for (i in 0 until redTiles.size - 1) {
            val redTile = redTiles[i]
            var nextRedTile = redTiles[i + 1]
            if (redTile.x == nextRedTile.x) {
                if (redTile.y > nextRedTile.y) {
                    switchableTiles.addAll((nextRedTile.y..redTile.y).map { redTile.x to it })
                } else {
                    switchableTiles.addAll((redTile.y..nextRedTile.y).map { redTile.x to it })
                }
            } else {
                if (redTile.x > nextRedTile.x) {
                    switchableTiles.addAll((nextRedTile.x..redTile.x).map { it to redTile.y })
                } else {
                    switchableTiles.addAll((redTile.x..nextRedTile.x).map { it to redTile.y })
                }
            }
        }
        startVisualMode()
    }

    private fun getTurn(
        previousTilePosition: Pair<Int, Int>,
        tilePosition: Pair<Int, Int>,
        nextTilePosition: Pair<Int, Int>,
    ): String {
        val (previousX, previousY) = previousTilePosition
        val (x, y) = tilePosition
        val (nextX, nextY) = nextTilePosition
        return when {
            previousX == x && nextX > x && previousY > y && nextY == y -> "SE"
            previousX < x && nextX == x && previousY == y && nextY > y -> "SW"
            previousX == x && nextX < x && previousY < y && nextY == y -> "NW"
            previousX > x && nextX == x && previousY == y && nextY < y -> "NE"
            previousX > x && nextX == x && previousY == y && nextY > y -> "-NE"
            previousX == x && nextX < x && previousY > y && nextY == y -> "-NE"
            previousX < x && nextX == x && previousY == y && nextY < y -> "-SE"
            else -> "-SW"
        }
    }

    fun getLargestRectangleArea(): Long {
        var largestArea = 0L
        for (i in 0 until redTiles.size) {
            for (j in i + 1 until redTiles.size) {
                val tile1 = redTiles[i]
                val tile2 = redTiles[j]
                val area = (abs(tile1.x - tile2.x).toLong() + 1) * (abs(tile1.y - tile2.y).toLong() + 1)
                if (area > largestArea) {
                    largestArea = area
                }
            }
        }
        return largestArea
    }

    fun getLargestRectangleAreaWithGreenAndRedTilesOnly(): Long {
        var largestArea = 0L
        for (i in 0 until redTiles.size) {
            for (j in 0 until redTiles.size) {
                val tile1 = redTiles[i]
                val tile2 = redTiles[j]
                val area = (abs(tile1.x - tile2.x) + 1).toLong() * (abs(tile1.y - tile2.y).toLong() + 1)
                if (tile1.x == tile2.x || tile1.y == tile2.y) continue
                if (area > largestArea) {
                    if (allTilesAreSwitchable(tile1, tile2)) {
                        this.tile1 = tile1
                        this.tile2 = tile2
                        largestArea = area
                        println("$tile1 $tile2 $area")
                    }
                }
            }
        }
        return largestArea
    }

    private fun allTilesAreSwitchable(tile1: Tile, tile2: Tile): Boolean {
        val corner1 = tile1.x to tile1.y
        val corner2 = tile2.x to tile2.y
        val corner3 = tile1.x to tile2.y
        val corner4 = tile2.x to tile1.y
        val compatibleCorners = when (tile1.turn) {
            "SW" -> tile2.x > tile1.x && tile2.y > tile1.y && (tile2.turn == "-SE" || tile2.turn == "-NE" || tile2.turn == "-NW" || tile2.turn == "NE")
            "SE" -> tile2.x < tile1.x && tile2.y > tile1.y && (tile2.turn == "-SW" || tile2.turn == "-NE" || tile2.turn == "-NW" || tile2.turn == "NW")
            "NE" -> tile2.x < tile1.x && tile2.y < tile1.y && (tile2.turn == "-SW" || tile2.turn == "-SE" || tile2.turn == "-NW" || tile2.turn == "SW")
            "NW" -> tile2.x > tile1.x && tile2.y < tile1.y && (tile2.turn == "-SW" || tile2.turn == "-SE" || tile2.turn == "-NE" || tile2.turn == "SE")
            "-SW" -> tile2.turn != "SW"
            "-SE" -> tile2.turn != "SE"
            "-NE" -> tile2.turn != "NE"
            "-NW" -> tile2.turn != "NW"
            else -> throw Exception().also { println("$tile1 $tile2") }
        }
        val noRedTileInside = redTiles.none {
            it.x > listOf(corner1.first, corner2.first, corner3.first, corner4.first).min() &&
                    it.x < listOf(corner1.first, corner2.first, corner3.first, corner4.first).max() &&
                    it.y > listOf(corner1.second, corner2.second, corner3.second, corner4.second).min() &&
                    it.y < listOf(corner1.second, corner2.second, corner3.second, corner4.second).max()
        } && switchableTiles.none {
            it.first > listOf(corner1.first, corner2.first, corner3.first, corner4.first).min() &&
                    it.first < listOf(corner1.first, corner2.first, corner3.first, corner4.first).max() &&
                    it.second > listOf(corner1.second, corner2.second, corner3.second, corner4.second).min() &&
                    it.second < listOf(corner1.second, corner2.second, corner3.second, corner4.second).max()
        }
        return noRedTileInside && compatibleCorners
    }

    fun startVisualMode() {
        val config = LwjglApplicationConfiguration().apply {
            LwjglApplicationConfiguration.getDesktopDisplayMode()
            width = 1080
            height = 1080
            fullscreen = false
            resizable = false
        }
        config.title = "Day 09"
        LwjglApplication(Day09y2025Application(this), config);
    }


}

class Day09y2025Application(private val day9: Day09) : ApplicationAdapter() {

    private lateinit var shapeRenderer: ShapeRenderer
    override fun create() {
        shapeRenderer = ShapeRenderer()
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        val minX = day9.redTiles.minOf { it.x } + 400
        val minY = day9.redTiles.minOf { it.y } + 400
        day9.redTiles.forEach { tile ->
            shapeRenderer.color = Color.RED
            shapeRenderer.rect((tile.x - minX) * 0.009f, (tile.y - minY) * 0.009f, 2f, 2f)
        }
        day9.switchableTiles.forEach { (x, y) ->
            shapeRenderer.color = Color.GREEN
            shapeRenderer.rect((x - minX) * 0.009f, (y - minY) * 0.009f, 2f, 2f)
        }
        if (day9.tile1 != null) {
            shapeRenderer.color = Color.BLUE
            shapeRenderer.rect((day9.tile1!!.x - minX) * 0.009f, (day9.tile1!!.y - minY) * 0.009f, 3f, 3f)
            shapeRenderer.rect((day9.tile2!!.x - minX) * 0.009f, (day9.tile2!!.y - minY) * 0.009f, 3f, 3f)
            shapeRenderer.color = Color.CHARTREUSE
            shapeRenderer.rect((day9.tile1!!.x - minX) * 0.009f, (day9.tile2!!.y - minY) * 0.009f, 3f, 3f)
            shapeRenderer.rect((day9.tile2!!.x - minX) * 0.009f, (day9.tile1!!.y - minY) * 0.009f, 3f, 3f)
        }
        shapeRenderer.end()
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

}



