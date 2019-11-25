package sudokusolver

import sudokusolver.Main.dim

inline operator fun <reified T> T.times(count: Int) : Array<T> = Array(count) {this}

infix fun Array<String>.joinWith(separator: String): String = this.joinToString(separator = separator)

infix fun Iterable<String>.joinWith(separator: String): String = this.joinToString(separator = separator)

data class Coords(val locX: Int, val locY: Int) {
    val linear = (locY - 1) * dim + locX
}

data class GlobalCoords(val x: Int, val y: Int) {

    fun toBoxAndCellCoords(): Pair<Coords, Coords> =
        Coords(x.partition, y.partition) to Coords(x.modulo, y.modulo)
}

val Char.numericValue
    get() = Character.getNumericValue(this)

/**
 * 1 2 3 4 5 6 7 8 9
 * 1     2     3
 * @return 1-3
 */
val Int.partition: Int
    get() = (this - 1) / dim + 1

/**
 * input:  1 2 3 4 5 6 7 8 9
 * output: 1 2 3 1 2 3 1 2 3
 * @return the modulo
 */
val Int.modulo: Int
    get() = (this - 1) % dim + 1

val Int.coords: Coords
    get() = Coords(modulo, partition)

val Pair<Int, Int>.linear: Int
    get() = (second - 1) * dim + first

fun <T> arrayToArrayList(array: Array<T>): List<T> = array.toList()
