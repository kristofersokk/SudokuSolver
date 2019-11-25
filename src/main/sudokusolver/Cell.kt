package sudokusolver

import sudokusolver.Main.dim

class Cell(val locX: Int, val locY: Int, val box: Box, value: Int = 0, var numbers: HashSet<Int> = Logic.numberSet)

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

    var value: Int = value
        set(value) {
            field = value
            numbers = HashSet<Int>()
        }

    fun toPrettyString(): String = "{}\n{}\n{}".format(
        " ".repeat(dim + 2),
        (1..dim).joinToString(separator = "\n") { y ->
            " {} ".format(
                (1..dim).joinToString { x ->
                    val arv = (y - 1) * dim + x
                    if (arv in numbers) arv.toString() else " "
                }
            )

        },
        " ".repeat(dim + 2)
    )

    val globalCoords: GlobalCoords
        get() = GlobalCoords((box.locX - 1) * dim + locX, (box.locY - 1) * dim + locY)

}
