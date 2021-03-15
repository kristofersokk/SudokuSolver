package sudokusolver.logic

import sudokusolver.*
import sudokusolver.CellCollectionType.COLUMN
import sudokusolver.CellCollectionType.ROW
import sudokusolver.Main.dim2

@Throws(FillingException::class)
internal fun intersectionRemoval(game: Game): Boolean {
    game.level = 2
    //possibleCells in box are in one row or column
    game.boxes.forEachIndexed { index, box ->
        val boxIndex = index + 1
        //get the frequency of possibilities of numbers 1-dim2
        val possibilities = box.cells.possibilities()
        //check the different numbers and the number of the possibilities
        possibilities.forEach { (number, possibleCells) ->
            if (possibleCells.size > 1) {
                //check number of rows
                val rows = possibleCells.map { it.globalCoords.y }.distinct()
                val columns = possibleCells.map { it.globalCoords.x }.distinct()
                // candidate line (row) is active
                if (rows.size == 1) {
                    val rowCells = game row rows.first()
                    val somethingDone = (rowCells - possibleCells).applyAllWithResult {
                        numbers.remove(number).result
                    }.anyTrue()
                    if (somethingDone) {
                        game.addMessage("box $boxIndex: intersection of number $number in row : ${rows.first()}")
                        return true
                    }
                } else if (columns.size == 1) {
                    val columnCells = game column columns.first()
                    val somethingDone = (columnCells - possibleCells).applyAllWithResult {
                        numbers.remove(number).result
                    }.anyTrue()
                    if (somethingDone) {
                        game.addMessage("box $boxIndex: intersection of number $number in column ${columns.first()}")
                        return true
                    }
                }
            }
        }
    }

    Game.funcsGetCells(ROW, COLUMN).forEach { (func, type) ->
        (1..dim2).forEach { index ->
            val cells = func(game, index)
            cells.checkForErrors(game, type, index)
            val possibilities = cells.possibilities()
            possibilities.forEach { (number, possibleCells) ->
                //get the cells where a number could go
                val boxes = possibleCells.map { it.box(game) }.distinct()
                if (boxes.size == 1) {
                    val box = boxes.first()
                    val somethingDone = (box.cells - possibleCells).applyAllWithResult {
                        numbers.remove(number).result
                    }.anyTrue()
                    if (somethingDone) {
                        game.addMessage("${type.name} $index: intersection of number $number in box $box")
                        return true
                    }
                }
            }
        }
    }
    return false
}
