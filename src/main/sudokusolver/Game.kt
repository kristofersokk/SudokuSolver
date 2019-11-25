package sudokusolver

import sudokusolver.Main.dim
import sudokusolver.Main.dim2
import java.io.File
import kotlin.collections.ArrayList

val renderScale = if (dim2 <= 9) 1.1f else 2.1f


class Game(val boxes: Array<Box> = generateBoxes(), val messages: ArrayList<String> = ArrayList()) {

    var solved = false

    private var prevLevel = -1
    private var level = 1
        set(value) {
            field = value
        }

    fun solve() {
        Logic.startFilling(this)
    }

    fun addMessage(message: String) {
        if (prevLevel != level) {
            messages.add("Logic level $level")
            prevLevel = level
        }
        messages.add(message)
    }

    companion object {

        fun generateBoxes(): Array<Box> {
            return (1..dim2).map { Box(it) }.toTypedArray()
        }

        fun solveSudokuString(sudoku: String): Game {
            val game = Game()
            game.importFromString(sudoku)
            game.solve()
            return game
        }

        fun solveSudokuFromFile(level: Int, index: Int): Game {
            val game = Game()
            game.importFromFile(level, index)
            game.solve()
            return game
        }

        fun solveMultipleFromFile(level: Int, start: Int, stop: Int): List<Game> {
            val file = File(Main.fileLocation + "level" + level + "-10000.txt")
            val lines = file.readLines()
            return (stop..start).map {
                val game = Game()
                game.importFromString(lines[it])
                game.solve()
                game
            }
        }
    }

    override fun toString(): String {
        val boxesRows = boxes.map { it.toPrettyString(cellPossibilities = true, border = true).split("\n") }
        return (1..dim).joinToString(separator = "\n") { boxY ->
            (1..boxesRows[0].size).joinToString(separator = "\n") { rowY ->
                boxesRows.subList(boxY * dim, boxY * dim + dim).joinToString { it[rowY] }
            }
        }
    }

    /**
     *
     * @param level level 1-7
     * @param index index 1-10000
     */
    private fun importFromFile(level: Int, index: Int) {
        val file = File(Main.fileLocation + "level" + level + "-10000.txt")
        importFromString(file.readLines()[index])
    }

    /**
     * @param info line by line
     */
    private fun importFromString(info: String) {
        for (y in 1..dim2) {
            for (x in 1..dim2) {
                val cell = getCell(GlobalCoords(x, y))
                val ch = info[(y - 1) * dim2 + x - 1].toLowerCase()
                if (ch.isDigit()) {
                    cell.value = ch.numericValue
                } else {
                    if (ch.isLetter()) {
                        cell.value = ch.toInt() - 87
                    }
                }
            }
        }
    }

    fun getBox(coords: Coords) : Box = boxes[coords.linear]

    fun getCell(bLoc: Coords, cLoc: Coords) : Cell = getBox(bLoc).getCell(cLoc)

    /**
     * @param x 1-9
     * @param y 1-9
     * @return lahter
     */
    fun getCell(globalCoords: GlobalCoords): Cell {
        val (boxCoords, cellCoords) = globalCoords.toBoxAndCellCoords()
        return getCell(boxCoords, cellCoords)
    }

}
