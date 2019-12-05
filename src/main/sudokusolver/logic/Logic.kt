package sudokusolver.logic

import sudokusolver.*
import sudokusolver.Main.dim2
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.WindowConstants

object Logic {

    var solutionsAmount = 0
    var gamesAmount = 0
    private val collectionNames = arrayOf("pair", "triplet", "quadruplet", "quintuplet", "sextuplet", "septuplet")
    private val puzzles = ArrayList<Game>()
    private val solutions = ArrayList<Game>()

    private fun createAndShowGUI() {
        val frame = JFrame("Logic")
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        val label = JLabel("A new label")
        label.preferredSize = Dimension(400, 200)
        label.horizontalAlignment = SwingConstants.CENTER
        frame.contentPane.add(label, BorderLayout.CENTER)
        frame.pack()
        frame.isVisible = true
    }

    @Throws(FillingException::class, FinishedException::class)
    private fun continuousFilling(game: Game) {
        while (true) {
            game.checkForErrors()
            //level 1
            if (hiddenSingle(game)) {
                continue
            } else {
                if (game.checkFilled()) {
                    throw FinishedException(game, true)
                }
            }
            if (nakedSingle(game)) {
                continue
            }

            //Level 2
            if (intersectionRemoval(game)) {
                continue
            }

            //Level 3
            if (nakedHiddenCombinations(game)) {
                continue
            }
            throw FinishedException(game, false)
        }
    }

    /**
     * @param unsolved the original boxes after reading in the information
     * updates puzzles, solutions, gamesAmount and solutionsAmount
     */
    fun startFilling(unsolved: Game): Game {
        initialLoading(unsolved)
        val solvedGame = unsolved.deepcopy()
        //println(solved.toString());
        try {
            continuousFilling(solvedGame)
        } catch (e: FillingException) {
            println("There was an error while solving, sudoku probably doesn't have a solution")
            e.printStackTrace()
            //            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
            println(listOf(unsolved, e.game).toSideWaysSimpleString())
            e.game.messages.forEach(::println)
            println(e.game.toPrettyString(true))
        } catch (finishedException: FinishedException) {
            puzzles.add(unsolved)
            solutions.add(solvedGame)
            gamesAmount++
            if (gamesAmount % 1000 == 0)
                println(gamesAmount)
            if (finishedException.filled) {
                solutionsAmount++
                solvedGame.isSolved = true
            } else {
//                printSidewaysGrid(unsolved, solvedGame)
//                println(solvedGame.toPrettyString(true))
            }
        }
        return solvedGame
    }

    private fun initialLoading(game: Game) {
        game.allCells.forEach { cell ->
            if (cell.value > 0) {
                afterNumber(cell, game, false, null, false)
            }
        }
    }

    /**
     * @param cell
     * @param game
     * @param showMessage
     * @param messageSuffix
     * @param findNextOnes look for boxes with only one choice remaining, ca 3x times faster for computer, less intuitive for people, but more intuitive, if trying to fill
     */
    internal fun afterNumber(cell: Cell, game: Game, showMessage: Boolean, messageSuffix: String?, findNextOnes: Boolean) {
        val (x, y) = cell.globalCoords
        if (showMessage) game.addMessage("x:$x y:$y value: ${cell.value}$messageSuffix")
        (cell.box(game).cells + (game row y) + (game column x)).distinct().applyAll {
            numbers.remove(cell.value)
        }

        if (findNextOnes) {
            listOf(cell.box(game).cells, game row y, game column x).forEach {
                val unfilled = it.filter { !it.filled }
                if (unfilled.size <= 3) {
                    unfilled.applyAll {
                        if (value == 0 && numbers.size == 1) {
                            val onlyValue = numbers.first()
                            game.addMessage("x:$x y:$y value: ${cell.value}$messageSuffix naked single")
                            value = onlyValue
                            afterNumber(this, game, showMessage, null, findNextOnes)
                        }
                    }
                }
            }
        }
    }

    fun getCollectionName(a: Int): String {
        return if (a <= collectionNames.size + 1) collectionNames[a - 2] else "$a-uplet"
    }

    /**
     * @param printSolutionSteps  --
     * @param printOnlyUnsolvable --
     */
    fun printSolutions(printSolutionSteps: Boolean, printOnlyUnsolvable: Boolean) {
        for (i in 0 until gamesAmount) {
            if (!printOnlyUnsolvable || printOnlyUnsolvable && !solutions[i].isSolved) {
                if (printSolutionSteps) {
                    for (j in solutions[i].messages.indices) {
                        val message = solutions[i].messages[j]
                        if (!message.contains("Logic")) {
                            val index = j.toString()
                            print("${" ".repeat(5 - index.length)}$index: ")
                        }
                        println(message)
                    }
                }
                println()
                println(listOf(puzzles[i], solutions[i]).toSideWaysSimpleString())
                println()
                println("----------------------------------------------------------------")
            }
        }
    }

    val numberSet: HashSet<Int>
        get() = (1..dim2).toHashSet()

    val numberList: MutableList<Int>
        get() = (1..dim2).toMutableList()

}
