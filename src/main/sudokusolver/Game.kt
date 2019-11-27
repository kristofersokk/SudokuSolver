package sudokusolver

import sudokusolver.Main.dim
import sudokusolver.Main.dim2
import java.io.File

data class Game(val boxes: List<Box> = generateBoxes(), val messages: ArrayList<String> = ArrayList()) {

    var isSolved = false

    private var prevLevel = -1
    internal var level = 1

    val allCells: List<Cell>
        get() = boxes.flatMap { it.cells }

    fun solve() = Logic.startFilling(this)

    fun addMessage(message: String) {
        if (prevLevel != level) {
            messages.add("Logic level $level")
            prevLevel = level
        }
        messages.add(message)
        println(message)
        println(toPrettyString(true))
    }

    fun checkFilled(): Boolean {
        boxes.forEach {
            it.cells.forEach { cell ->
                if (cell.value == 0) return false
            }
        }
        return true
    }

    infix fun row(y: Int): List<Cell> {
        return allCells.filter { it.globalCoords.y == y }
    }

    infix fun column(x: Int): List<Cell> {
        return allCells.filter { it.globalCoords.x == x }
    }

    companion object {

        val renderScale = if (dim2 <= 9) 1.1f else 2.1f

        fun generateBoxes(): List<Box> {
            return (1..dim2).map { Box(it) }.toList()
        }

        fun solveSudokuString(sudoku: String): Game {
            val game = Game()
            game.importFromString(sudoku)
            return game.solve()
        }

        fun solveSudokuFromFile(level: Int, index: Int): Game {
            val game = Game()
            game.importFromFile(level, index)
            return game.solve()
        }

        fun solveMultipleFromFile(level: Int, start: Int, stop: Int): List<Game> {
            val file = File("../src/main/sudokusolver/level$level-10000.txt")
            val lines = file.readLines()
            return (stop..start).map {
                val game = Game()
                game.importFromString(lines[it])
                game.solve()
            }
        }
    }

    fun toPrettyString(cellPossibilities: Boolean): String {
        val boxesRows = boxes.map { it.toPrettyString(cellPossibilities = cellPossibilities, border = true).split("\n") }
        val width = boxesRows[0].size
        println(width)
        return (0 until dim).joinToString("\n") { boxY ->
            (0 until width).joinToString("\n") { rowY ->
                boxesRows.subList(boxY * dim, boxY * dim + dim).joinToString("") { it[rowY] }
            }
        }
    }

    /**
     *
     * @param level level 1-6
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

    operator fun get(index: Int) = getBox(index)

    fun getBox(locX: Int, locY: Int): Box = boxes[Coords(locX, locY).linear - 1]

    fun getBox(coords: Coords): Box = boxes[coords.linear - 1]

    fun getBox(index: Int): Box = boxes[index - 1]

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

    fun deepcopy(): Game =
        Game(boxes = boxes.map { it.deepCopy() }.toList(), messages = messages.map { String(it.toCharArray()) }.toMutableList() as ArrayList<String>)

}
