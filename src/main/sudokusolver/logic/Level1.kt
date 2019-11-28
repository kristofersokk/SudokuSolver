package sudokusolver.logic

import sudokusolver.*
import sudokusolver.Main.dim2
import sudokusolver.Main.findNextOnes

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

    (1..dim2).forEach { number ->
        Game.funcsGetCells.forEach { (func, type) ->
            var index = 1
            while (index <= dim2) {
                somethingDone = false
                val cells = func(game, index)
                cells.checkForErrors(game)
                //get the cells where a number could go
                val possibleCells = cells.possibleCellsFor(number)
                //check the number of possibilities
                if (possibleCells.isNotEmpty() && number in cells) {
                    throw FillingException("box $index already has number $number, but possibilities exist", game)
                }
                if (possibleCells.size == 1) {
                    val cell = possibleCells.first()
                    cell.value = number
                    Logic.afterNumber(cell, game, true, " (${type.name} $index)", findNextOnes)
                    somethingDone = true
                } else if (possibleCells.isEmpty() && number !in cells) {
                    throw FillingException("${type.name} $index doesn't have number $number and no possibilities exist", game)
                }
                if (!somethingDone) {
                    index++
                }
            }
            if (somethingDone) {
                return true
            }
        }
    }
    return false
}


