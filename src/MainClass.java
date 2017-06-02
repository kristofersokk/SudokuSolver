public class MainClass {


    private static final String testString = " g97 e 6      ad4  2bc3    e9   5 bea d1 79 gc26   8g 7 2 5c e  9    b 3 a2  g   6eg8  4  cd    7 d 9 6ag1e b 42 1    ce8  9a6d db   8g2 f 4 56  5c4   d7 3g8 e1 26 e f7d  5c  a e   3 bc 8a2df bf d   83  6e91   2 36 c ed7   4 7ac5     f   b  95  f4  c   78 ";
    private static final String fileLocation = "C:/Users/Kristofer.DESKTOP-4AMDEPH/Google Drive/Proge/Sudoku/src/50sudokus.txt";
    private static final TimeMeasure measure = new TimeMeasure();

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
        Loogika.printSidewaysGrid(unsolved, unsolved);
        solved.solve();

        measure.stop();

        Loogika.printSolutions();

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
