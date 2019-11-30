package sudokusolver

import sudokusolver.Main.dim

val workingDirectory = System.getProperty("user.dir")

inline operator fun <reified T> T.times(count: Int): Array<T> = Array(count) { this }

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

inline fun <T> Iterable<T>.applyAll(func: T.(index: Int) -> Unit) = forEachIndexed { index, t -> t.func(index) }
inline fun <T> Iterable<T>.applyAllWithResult(func: T.() -> Result): List<Result> = map { it.func() }

fun Iterable<Result>.allTrue() = all { it.result }
fun Iterable<Result>.anyTrue() = any { it.result }

inline fun <T> Iterable<T>.applyEnsureSuccess(func: T.(index: Int) -> Result) {
    var index = 0
    forEach {
        var success = false
        while (!success) {
            success = it.func(index).result
            index++
        }
    }
}

class Result private constructor(val result: Boolean) {
    companion object {
        val SUCCESS = Result(true)
        val FAILURE = Result(false)
    }
}

val Boolean.result: Result
    get() = if (this) Result.SUCCESS else Result.FAILURE
