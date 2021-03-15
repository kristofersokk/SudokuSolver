package sudokusolver

data class Box(val index: Int, val cells: List<Cell> = (1..dim2).map { Cell(it.modulo, it.partition, index.modulo, index.partition) })
{
    val coords: Coords
        get() = Coords(index.modulo, index.partition)

    val locX: Int
        get() = index.modulo

    val locY: Int
        get() = index.partition

    fun getCell(coords: Coords): Cell = getCell(coords.linear)

    private fun getCell(index: Int): Cell = cells[index - 1]

    operator fun get(index: Int) = getCell(index)

    operator fun contains(value: Int): Boolean = cells.any { it.value == value }

    operator fun contains(cell: Cell): Boolean = cell in cells

    fun toPrettyString(cellPossibilities: Boolean = false, border: Boolean = false): String {
        val rows = if (cellPossibilities) {
            val rows = cells.map { it.toPrettyString().split("\n").toTypedArray() }
            rows
        } else {
            cells.map { cell -> arrayOf(" ".repeat(dim), " ${cell.value.toString().replace("0", " ")} ", " ".repeat(dim)) }
        }

        val inside = (1..dim).joinToString("\n${"─".repeat(dim) * dim joinWith "┼"}\n") { cellY ->
            (1..dim).joinToString("\n") { matrixY ->
                (1..dim).joinToString("│") { cellX ->
                    rows[(cellY - 1) * dim + cellX - 1][matrixY - 1]
                }
            }
        }

        return if (!border) {
            inside
        } else {
            "%s\n%s\n%s".format(
                "╔${"═".repeat(dim) * dim joinWith "╤"}╗",
                inside.split("\n").mapIndexed { index, line ->
                    if ((index + 1) % (dim + 1) == 0) "╟$line╢" else "║$line║"
                } joinWith "\n",
                "╚${"═".repeat(dim) * dim joinWith "╧"}╝"
            )
        }

    }

    fun deepCopy(): Box = copy(cells = cells.map { it.deepCopy() })

}
