package sudokusolver

import sudokusolver.Main.dim
import sudokusolver.Main.dim2
import sudokusolver.logic.Logic

data class Cell(val locX: Int, val locY: Int, val box_locX: Int, val box_locY: Int, val givenValue: Int = 0, var numbers: HashSet<Int> = Logic.numberSet)

/**
 *
 * @param locX 1-dim
 * @param locY 1-dim
 * @param numbers [1-dim2] or [0],
 * 1st row from left to right, 2. -||-, 3. -||-, ...
 * @param value null or value
 */
{

    val index: Int
        get() = (locY - 1) * dim + locX

    val coords: Coords
        get() = Coords(locX, locY)

    var value: Int = givenValue
        set(value) {
            field = value
            numbers = HashSet()
        }

    val filled = value > 0

    val globalCoords: GlobalCoords
        get() = GlobalCoords((box_locX - 1) * dim + locX, (box_locY - 1) * dim + locY)

    fun box(game: Game): Box = game.box(box_locX, box_locY)

    operator fun contains(number: Int) = number in numbers

    fun toPrettyString(): String {
        val second = (1..dim).joinToString("\n") { y ->
            (1..dim).joinToString("") { x ->
                val arv = (y - 1) * dim + x
                if (arv in numbers) arv.toString() else " "
            }
        }
        return second
    }

    fun deepCopy(): Cell = copy(numbers = numbers.toHashSet(), givenValue = value)

}

@Throws(FillingException::class)
fun Collection<Cell>.checkForErrors(game: Game, type: CellCollectionType, index: Int) {
    val possibilities = possibilities()
    possibilities.forEach { (number, possibleCells) ->
        if (possibleCells.isEmpty() && number !in this) {
            throw FillingException("${type.name} $index doesn't have number $number and no possibilities exist", game)
        } else if (possibleCells.isNotEmpty() && number in this) {
            throw FillingException("${type.name} $index already has number $number, but possibilities still exist", game)
        }
    }
}

operator fun Collection<Cell>.contains(number: Int) =
    number in this.map { it.value }

fun Collection<Cell>.possibilities() : Map<Int, List<Cell>> =
    (1..dim2).associateWith { number -> this.filter { number in it.numbers } }

fun Collection<Cell>.possibleCellsFor(number: Int) : List<Cell> =
    this.filter { number in it.numbers }

enum class CellCollectionType(lambda: (game: Game, index: Int) -> List<Cell>) {
    BOX({ game: Game, index: Int -> game.box(index).cells }),
    ROW({ game: Game, index: Int -> game row index }),
    COLUMN({ game: Game, index: Int -> game column index });

    val cells: (game: Game, index: Int) -> List<Cell> = lambda
}
