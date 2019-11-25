package sudokusolver

import sudokusolver.Main.dim

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
