package sudokusolver

class FinishedException(val game: Game, val filled: Boolean) : Exception()