package sudokusolver;

public class MainClass {


    public static final String testString = " 6 9  17 5713   69 9 671  87 516 9  68972 4 11 38 97 691 437    5 218 97  7596 1 ";
    public static final String fileLocation = "C:/Users/krist/GitHub projects/SudokuSolver/SudokuSolver/src/sudokusolver/";
    private static final int level = 1;
    private static final int choice = 3;
    private static final boolean printSolutionSteps = false;
    private static final boolean printOnlySolvable = true;
    private static final TimeMeasure measure = new TimeMeasure();

    public static void main(String[] args){
        //TODO vs10 add graphics ps! in the end probably
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                createAndShowGUI();
//            }
//        });

        /*
        for (int i = 0; i < 50; i++) {
            Kast[] antud = newKastid();
            loadFromText(i, antud);
            Kast[] lopp = copyKastid(antud);
            //TODO vs20 add the possibility to play the game yourself

            //add all the numbers at the start before starting to
            for (Kast kast : lopp) {
                for (Lahter lahter : kast.getLahtrid()) {
                    int[] XY = getXYFromLocs(new int[][]{{kast.locX, kast.locY}, {lahter.locX, lahter.locY}});
                    if (lahter.getValue() != 0) {
                        afterNumber(XY[0], XY[1], lopp);
                    }
                }
            }
//        for (Lahter lahter : getKast(3,3,kastid).getLahtrid()){
//            println(lahter.getValue(),lahter.getNumbers());
//        }
            try {
                continuousFilling(lopp);
            } catch (FillingException e) {
                e.printStackTrace();
            } catch (FinishedException e) {
                puzzles.add(antud);
                solutions.add(lopp);
//                printSidewaysGrid(antud, lopp);
                if (e.filled) {
                    solutionsAmount++;
                }
                gamesAmount++;
            }
        }
        */
        measure.start();

//        Game game = new Game();
//        game.importFromFile(level, choice);
//        game.solve();
        Game.solveMultipleFromFile(level, 1, 10000);
//        Game.solveTestString();

        measure.stop();

        Loogika.printSolutions(printSolutionSteps, printOnlySolvable);

        println("Solutions: " + Integer.toString(Loogika.solutionsAmount)+"/"+Integer.toString(Loogika.gamesAmount));
        println(measure.getDurationString());

//        fileChoose.openFile();
    }

    private static void println(Object... o) {
        for (Object i : o) {
            System.out.print(i + " ");
        }
        newLine();
    }

    private static void newLine(){
        System.out.println();
    }
}
