/**
 * Created by Kristofer on 02/06/2017.
 */
public class Solve {


    static final String testString = "  1 4 3   8  96  4  48  9        5 34  5 7  98 9        8  27  7  43  8   6 8 4  ";
    static final String fileLocation = "C:/Users/Kristofer.DESKTOP-4AMDEPH/Google Drive/Proge/Sudoku/src/50sudokus.txt";
    static final TimeMeasure measure = new TimeMeasure();

    public static void main(String[] args){
        //TODO vs10 add graphics ps! in the end probably
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                createAndShowGUI();
//            }
//        });
        measure.start();

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

        Game unsolved = new Game();
        unsolved.importInfo(testString);
        Game solved = Loogika.copyGame(unsolved);
        solved.solve();

        measure.stop();

        Loogika.printSolutions();

        println("Solutions: " + Integer.toString(Loogika.solutionsAmount)+"/"+Integer.toString(Loogika.gamesAmount));
        println(measure.getDurationString());

//        fileChoose.openFile();
    }


    static void println(Object... o) {
        for (Object i : o) {
            System.out.print(i + " ");
        }
        newLine();
    }

    static void newLine(){
        System.out.println();
    }
}
