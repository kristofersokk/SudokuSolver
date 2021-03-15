package sudokusolver

import sudokusolver.logic.Logic
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
//        println(message)
//        println(toPrettyString(true))
    }

    fun checkFilled(): Boolean {
        boxes.forEach {
            it.cells.forEach { cell ->
                if (cell.value == 0) return false
            }
        }
        return true
    }

    @Throws(FillingException::class)
    fun checkForErrors() {
        allCells.forEach { cell ->
            if (cell.numbers.size == 0 && cell.value == 0) {
                val globalCoords = cell.globalCoords
                throw FillingException("cell at x: ${globalCoords.x}, y: ${globalCoords.y} is empty and without possibilities", this)
            }
        }
    }

    infix fun row(y: Int): List<Cell> = allCells.filter { it.globalCoords.y == y }

    infix fun column(x: Int): List<Cell> = allCells.filter { it.globalCoords.x == x }

    private fun allRows(): Collection<List<Cell>> = allCells.groupBy { it.globalCoords.y }.values

    private fun allColumns(): Collection<List<Cell>> = allCells.groupBy { it.globalCoords.x }.values

    private fun allBoxes(): Collection<List<Cell>> = boxes.map { it.cells }

    fun allCellCollectionsByType(type: CellCollectionType): Collection<List<Cell>> =
        when (type) {
            CellCollectionType.ROW -> allRows()
            CellCollectionType.COLUMN -> allColumns()
            CellCollectionType.BOX -> allBoxes()
        }

    companion object {

        val funcsGetCells: List<Pair<(game: Game, index: Int) -> List<Cell>, CellCollectionType>> = CellCollectionType.values().map { it.cells to it }

        fun funcsGetCells(vararg types: CellCollectionType) = types.map { it.cells to it }

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
            val file = File("${workingDirectory}/level$level-10000.txt")
            val lines = file.readLines()
            println(lines)
            return (start..stop).map {
                val game = Game()
                game.importFromString(lines[it - 1])
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

    val nonBorderedString: String
        get() = (1..dim).joinToString("\n\n") { boxY ->
            (1..dim).joinToString("\n") { cellY ->
                (1..dim).joinToString(" ", postfix = " ") { boxX ->
                    (1..dim).joinToString("") { cellX ->
                        val value = getCell(Coords(boxX, boxY), Coords(cellX, cellY)).value
                        when {
                            value == 0 -> " *"
                            value > 9 -> value.toString()
                            else -> " $value"
                        }
                    }
                }
            }
        }

    /**
     *
     * @param level level 1-6
     * @param index index 1-10000
     */
    private fun importFromFile(level: Int, index: Int) {
        val file = File(fileLocation + "level" + level + "-10000.txt")
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

    operator fun get(index: Int) = box(index)

    fun box(locX: Int, locY: Int): Box = boxes[Coords(locX, locY).linear - 1]

    private infix fun box(coords: Coords): Box = boxes[coords.linear - 1]

    infix fun box(index: Int): Box = boxes[index - 1]

    private fun getCell(bLoc: Coords, cLoc: Coords): Cell = box(bLoc).getCell(cLoc)

    /**
     * @return lahter
     */
    private fun getCell(globalCoords: GlobalCoords): Cell {
        val (boxCoords, cellCoords) = globalCoords.toBoxAndCellCoords()
        return getCell(boxCoords, cellCoords)
    }

    fun deepcopy(): Game =
        Game(boxes = boxes.map { it.deepCopy() }.toList(), messages = messages.map { String(it.toCharArray()) }.toMutableList() as ArrayList<String>)

}
