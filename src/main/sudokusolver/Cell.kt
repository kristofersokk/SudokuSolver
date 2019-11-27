package sudokusolver

import sudokusolver.Main.dim
import sudokusolver.Main.dim2

data class Cell(val locX: Int, val locY: Int, val box: Box, val givenValue: Int = 0, var numbers: HashSet<Int> = Logic.numberSet)

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

    val globalCoords: GlobalCoords
        get() = GlobalCoords((box.locX - 1) * dim + locX, (box.locY - 1) * dim + locY)

}

fun Collection<Cell>.checkForErrors(game: Game) {
    this.forEach { cell ->
        if (cell.numbers.size == 0 && cell.value == 0) {
            val globalCoords = cell.globalCoords
            throw FillingException("cell at x: ${globalCoords.x}, y: ${globalCoords.y} is empty and without possibilities", game)
        }
    }
}

operator fun Collection<Cell>.contains(number: Int) =
    number in this.map { it.value }

fun Collection<Cell>.possibilities() : Map<Int, List<Cell>> =
    (1..dim2).associateWith { number -> this.filter { number in it } }
