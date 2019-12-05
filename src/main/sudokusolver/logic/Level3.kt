package sudokusolver.logic

import com.marcinmoskala.math.combinations
import sudokusolver.*
import sudokusolver.CellCollectionType.*
import sudokusolver.logic.Logic.getCollectionName

fun nakedHiddenCombinations(game: Game): Boolean {
    var somethingDone = false
    //naked and hidden pairs, triplets, quarters
    game.level = 3

    listOf(ROW, COLUMN, BOX).forEach {type ->
        val cellCollections = game.allCellCollectionsByType(type)
        cellCollections.map { cells ->
            val unfilled = cells.filter { !it.filled }
            identifyNakedOrSingleCombinations(unfilled).forEach { combination ->
                if (combination.nakedOrHiddenDetected(game)) {
                    return true
                }
            }
        }
    }
    return somethingDone
}

private fun identifyNakedOrSingleCombinations(unfilledCells: List<Cell>) : List<Set<Cell>> {
    val cellsSet = unfilledCells.toSet()
    if (unfilledCells.size >= 4) {
        return (2..unfilledCells.size - 2).inwardsAlternating().flatMap {amount ->
            cellsSet.combinations(amount).filter {
                it.allPossibleNumbers.size == it.size
            }
        }
    }
    return listOf()
}

private fun Iterable<Int>.inwardsAlternating() : List<Int> {
    val list = toList()
    val count = count()
    val indexes = (0 until (count + 1) / 2).flatMap { listOf(count - it - 1, it) }.distinct().toList()
    return indexes.map { list[it] }
}

private fun Iterable<Cell>.nakedOrHiddenDetected(game: Game) : Boolean {
    val removedNumbers = allPossibleNumbers
    val rows = map {it.globalCoords.y}.distinct()
    val columns = map {it.globalCoords.x}.distinct()
    val boxes = map { it.box(game) }.distinct()
    val locations = listOf(
        rows.size == 1 && game.row(rows.first()).filter { it !in this }.map { it.numbers.removeAll(removedNumbers).result }.anyTrue(),
        columns.size == 1 && game.column(columns.first()).filter { it !in this }.map { it.numbers.removeAll(removedNumbers).result }.anyTrue(),
        boxes.size == 1 && boxes.first().cells.filter { it !in this }.map { it.numbers.removeAll(removedNumbers).result }.anyTrue()
    )
    val possibleLocationStrings = listOf(
        "row ${rows.first()}",
        "column ${rows.first()}",
        "box ${boxes.first().index}"
    )
    val locationString = locations.mapIndexed { index, locationIsValid ->
        if (locationIsValid) possibleLocationStrings[index] else ""
    }.joinToString(separator = " and ")
    if (locationString.isNotEmpty()) {
        val isHidden = removedNumbers.size <= (count() + 1) / 2
        val type = if (isHidden) "Hidden" else "Naked"
        val collectionName = getCollectionName(if (isHidden) count() - removedNumbers.size else removedNumbers.size)
        game.addMessage("$type $collectionName in $possibleLocationStrings with numbers $removedNumbers")
    }
    return locationString.isNotEmpty()
}
