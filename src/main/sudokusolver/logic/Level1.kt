package sudokusolver.logic

import sudokusolver.*
import sudokusolver.CellCollectionType.*

@Throws(FillingException::class)
internal fun nakedSingle(game: Game): Boolean {
    game.level = 1
    game.allCells.applyAll {
        if (numbers.size == 0 && value == 0) {
            throw FillingException("cell at x: " + globalCoords.x + ", y: " + globalCoords.y + " is empty and without possibilities", game)
        } else if (numbers.size == 1) {
            value = numbers.first()
            Logic.afterNumber(this, game, true, " (only choice)", findNextOnes)
            return true
        }
    }
    return false
}

@Throws(FillingException::class)
internal fun hiddenSingle(game: Game): Boolean {
    var somethingDone = false
    game.level = 1

    game.boxes.map { it.cells }.applyAll { i ->
        val index = i + 1
        (1..dim2).forEach { number ->
            checkForErrors(game, BOX, index)
            //get the cells where a number could go
            val possibleCells = possibleCellsFor(number)
            //check the number of possibilities
            if (possibleCells.size == 1) {
                val cell = possibleCells.first()
                cell.value = number
                Logic.afterNumber(cell, game, true, " (box $index)", findNextOnes)
                somethingDone = true
            }
        }
    }

    if (somethingDone) {
        return true
    }

    (1..dim2).forEach { number ->
        Game.funcsGetCells(ROW, COLUMN).forEach { (func, type) ->
            somethingDone = false
            (1..dim2).map { func(game, it) }.applyEnsureSuccess { i ->
                val index = i + 1
                checkForErrors(game, type, index)
                //get the cells where a number could go
                val possibleCells = possibleCellsFor(number)
                //check the number of possibilities
                if (possibleCells.size == 1) {
                    val cell = possibleCells.first()
                    cell.value = number
                    Logic.afterNumber(cell, game, true, " (${type.name} $index)", findNextOnes)
                    somethingDone = true
                    Result.FAILURE
                } else {
                    Result.SUCCESS
                }
            }
            if (somethingDone) {
                return true
            }
        }
    }
    return false
}


