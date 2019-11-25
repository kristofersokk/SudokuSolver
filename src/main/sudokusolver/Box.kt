package sudokusolver

import sudokusolver.Main.dim
import sudokusolver.Main.dim2

class Box(index: Int)
/**
 *
 * @param locX 1-dim
 * @param locY 1-3
 * @param cells [Cell, Cell...]
 * 1st row from left to right, 2. -||-, 3. -||-, ...
 * @param dim the dimensions of box
 */
{

    val locX: Int = index.modulo
    val locY: Int = index.partition
    val cells: Array<Cell> = (1..dim2).map { Cell(it.modulo, it.partition, this) }.toTypedArray()

    val index: Int
        get() = coords.linear

    val coords: Coords
        get() = Coords(locX, locY)

    fun getCell(coords: Coords): Cell = getCell(coords.linear)

    fun getCell(index: Int): Cell = cells[index]

    operator fun contains(value: Int): Boolean = cells.any { it.value == value }

    fun toPrettyString(cellPossibilities: Boolean = false, border: Boolean = false): String {
        val width: Int
        val rows = if (cellPossibilities) {
            width = 3
            cells.map { cell -> arrayOf("   ", " ${cell.value.toString().replace("0", " ")} ", "   ") }
        } else {
            val rows = cells.map { it.toPrettyString().split("\n").toTypedArray() }
            width = rows[0][0].length
            rows
        }

        val inside = (1..dim).joinToString(separator = "\n{}\n".format(
            "-".repeat(width) * dim joinWith "+"
        )) { cellY ->
            (1..width).joinToString(separator = "\n") { matrixY ->
                (1..dim).joinToString(separator = "¦") { cellX ->
                    rows[(cellY - 1) * dim + cellX - 1][matrixY - 1]
                }
            }
        }

        return if (!border) {
            inside
        } else {
            "{}\n{}\n{}".format(
                "╔{}╗\n".format(
                    "=".repeat(width) * dim joinWith "╤"
                ),
                inside.split("\n").mapIndexed { index, line ->
                    if (index % width == 0) "╟$line╢" else "║$line║"
                } joinWith "\n",
                "\n╚{}╝".format(
                    "=".repeat(width) * dim joinWith "╧"
                )
            )
        }

    }

    fun generateCells() : Array<Cell> = (1..dim2).map { Cell(it.modulo, it.partition, this) }.toTypedArray()
}
