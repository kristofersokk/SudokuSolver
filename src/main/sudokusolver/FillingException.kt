package sudokusolver

/**
 * Created by Kristofer-PC2 on 28/08/2016.
 */
class FillingException(override val message: String, val game: Game) : Exception(message)