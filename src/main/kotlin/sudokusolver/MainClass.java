package sudokusolver;

public class MainClass {

    static final String testString2 = ".5...12.9.6.29....4...6....8.....4.1.73........4............6.4..5......7...8...2";
    static final String testString3 =
            "__ge26___78cad__" +
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
                    "__f1a4____59c2__";
    static final String testString = ".6....3.......2.6..8..1.5.9.....4.8...3...7...2.6.....1.5.9..2..4.8.......7....1.";
    static final String fileLocation = "C:/Users/krist/Google Drive/Proge/SudokuSolver/SudokuSolver/src/sudokusolver/";
    //settings
    static final int dim = 3;
    static final int dim2 = dim * dim;
    private static final int level = 1;
    private static final int choice = 3;
    private static final boolean printSolutionSteps = true;
    private static final boolean printOnlyUnsolvable = false;
    private static final TimeMeasure measure = new TimeMeasure();
    static final boolean findNextOnes = false;
    static final boolean sameBoxFirst = true;

    public static void main(String[] args){
        //TODO vs10 add graphics ps! in the end probably
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                createAndShowGUI();
//            }
//        });

        measure.start();

        if (args != null && args.length > 0) {
            String sudokuString = args[0];
            Game.solveSudokuString(sudokuString);
        } else {
            //        Game.solveMultipleFromFile(level, 1, 10000);
            Game.solveTestString();
        }

        measure.stop();

        Loogika.printSolutions(printSolutionSteps, printOnlyUnsolvable);

        println("Solutions: " + Integer.toString(Loogika.solutionsAmount)+"/"+Integer.toString(Loogika.gamesAmount));
        println(measure.getDurationString());

//        fileChoose.openFile();
    }

    private static void println(Object... o) {
        for (Object i : o) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
