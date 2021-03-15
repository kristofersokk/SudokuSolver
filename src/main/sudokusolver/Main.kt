package sudokusolver

import sudokusolver.logic.Logic

object Main {
    const val testString3 = "__ge26___78cad__" +
            "__6__g____a__8__" +
            "f4c__7da6bg__935" +
            "3______45______7" +
            "d______g7______e" +
            "4e5__12ca89__gb6" +
            "__7__e____b__4__" +
            "__129b____c63f__" +
            "__8915____e743__" +
            "__b__d____4__1__" +
            "e7a__9f2d31__6cb" +
            "1______8g______9" +
            "5______7b______3" +
            "6a2__fbd417__e8g" +
            "__e__3____f__5__" +
            "__f1a4____59c2__"
    const val testString = ".6....3.......2.6..8..1.5.9.....4.8...3...7...2.6.....1.5.9..2..4.8.......7....1."
    private const val testString2 = "..16.4......8932...3......678.....63.2..6..8.19.....244......5...8437......2.61.."
    const val fileLocation = "C:/Users/krist/Google Drive/Proge/SudokuSolver/SudokuSolver/src/sudokusolver/"
    //settings
    const val dim = 3
    const val dim2 = dim * dim
    private const val level = 1
    private const val choice = 3
    private const val printSolutionSteps = false
    private const val printOnlyUnsolvable = false
    private val measure: TimeMeasure = TimeMeasure()
    const val findNextOnes = false
    const val sameBoxFirst = true

    @JvmStatic
    fun main(args: Array<String>) { //TODO vs10 add graphics ps! in the end probably
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                createAndShowGUI();
//            }
//        });
        measure.start()
//        val game = if (args.isNotEmpty()) {
//            val sudokuString = args[1]
//            Game.solveSudokuString(sudokuString)
//        } else { //        Game.solveMultipleFromFile(level, 1, 10000);
//            Game.solveSudokuString(testString2)
//        }
//        println(game.toPrettyString(true))
//        println(game.toPrettyString(false))
//        val games = Game.solveMultipleFromFile(1, 1, 10000)
        val game = Game.solveSudokuString(testString2)
        println(game.nonBorderedString)
        println(game.messages.joinToString("\n"))
        //        Game.solveMultipleFromFile(1, 1, 10000);
//        Game.solveMultipleFromFile(2, 1, 10000);
//        Game.solveMultipleFromFile(3, 1, 10000);
//        Game.solveMultipleFromFile(4, 1, 10000);
//        Game.solveMultipleFromFile(6, 1, 10000)
        measure.stop()
        //Logic.printSolutions(printSolutionSteps, printOnlyUnsolvable);
        println("Solutions: ${Logic.solutionsAmount}/${Logic.gamesAmount}")
        println(measure.durationString)
        //        fileChoose.openFile();
    }

}
