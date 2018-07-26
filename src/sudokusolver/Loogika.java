package sudokusolver;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Loogika {

    private static final boolean findNextOnes = false;
    private static final boolean sameBoxFirst = true;
    static int solutionsAmount = 0;
    static int gamesAmount = 0;
    private static final String[] collectionNames = new String[]{"pair", "triplet", "quadruplet", "quintuplet", "sextuplet", "septuplet"};
    //settings
    static int dim = 4;
    private static ArrayList<Game> puzzles = new ArrayList<>();
    private static ArrayList<Game> solutions = new ArrayList<>();
    static int dim2 = 16;
    public static final ArrayList<Integer> allNumbers = getNumberList();

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Loogika");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        JLabel label = new JLabel("A new label");
        label.setPreferredSize(new Dimension(400, 200));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @param game game
     * @throws FillingException FinishedException
     */
    private static void continuousFilling(Game game) throws FillingException, FinishedException {
        try {
            while (true){
                if (fillingNormal(game)){
                    continue;
                }else{
                    if (checkFilled(game)){
                        throw new FinishedException(game,true);
                    }
                }
                if (filling1(game)){
                    continue;
                }
                if (filling2(game)){
                    continue;
                }
                if (filling3(game)){
                    continue;
                }
                throw new FinishedException(game,false);
            }
        } catch (FillingException | FinishedException e) {
            throw e;
        }
    }

    private static int prevLevel = -1;
    private static int level = 1;

    /**
     * @param unsolved the original kastid after reading in the information
     *                 updates puzzles, solutions, gamesAmount and solutionsAmount
     */
    static void startFilling(Game unsolved) {
        Game solved = copyGame(unsolved);
        initialLoading(solved);
        println(solved.toString());
        try {
            continuousFilling(solved);
        } catch (FillingException e) {
            e.printStackTrace();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            printSidewaysGrid(unsolved, e.game);
            for (String message : e.game.messages) {
                println(message);
            }
        } catch (FinishedException e) {
            puzzles.add(unsolved);
            solutions.add(solved);
            gamesAmount++;
            if (gamesAmount % 1000 == 0)
                println(gamesAmount);
            prevLevel = -1;//to print the first logic level
            if (e.filled) {
                solutionsAmount++;
                solved.solved = true;
            }
//            println(solved.kastid[0]);
//            println(solved);
        }
    }

    private static void initialLoading(Game game) {
        for (Box box : game.kastid) {
            for (Lahter lahter : box.getLahtrid()) {
                if (lahter.getValue() != 0){
                    afterNumber(lahter, game, false, null, findNextOnes);
                }
            }
        }
    }

    private static boolean fillingNormal(Game game) throws FillingException {
        boolean somethingDone = false;

        //TODO p''ra ringi, et ta otsiks sama numbrit teistest kastidest enne, siis teisi numbreid

        setLevel(1);
        //box
        if (sameBoxFirst){
            int i = 1;
            while (i <= dim2) {
                somethingDone = false;
                Box box = getKast(i, game);
                ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<>();
                ArrayList<Lahter> lahtridInBox = arrayToArrayList(box.getLahtrid());
                ArrayList<Integer> olemasNumbrid = new ArrayList<>();
                for (int j = 1; j <= dim2; j++) {
                    availableSlots.add(new ArrayList<>());
                }
                for (int j = 0; j < dim2; j++) {
                    Lahter lahter = lahtridInBox.get(j);
                    HashSet numbers = lahter.getNumbers();
                    if (numbers.size() == 0 && lahter.getValue() == 0) {
                        int[] XY = lahter.getXYOnBoard();
                        throw new FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game);
                    }
                }
                //get the frequency of possibilities of numbers 1-dim2
                for (int j = 0; j < dim2; j++) {
                    Lahter lahter = lahtridInBox.get(j);
                    for (int a : lahter.getNumbers()) {
                        availableSlots.get(a - 1).add(j + 1);
                    }
                    olemasNumbrid.add(lahter.getValue());
                }
                //check the different numbers and the number of possibilities
                for (int j = 0; j < dim2; j++) {
                    ArrayList<Integer> arvud = availableSlots.get(j);
                    if (arvud.size() > 0 && olemasNumbrid.contains(j + 1)) {
                        throw new FillingException("box " + i + " already has number " + (j + 1) + ", but possibilities exist", game);
                    }
                    if (arvud.size() == 1) {
                        Lahter lahter = box.getLahtrid()[arvud.get(0) - 1];
                        lahter.setValue(j + 1);
                        afterNumber(lahter, game, true, " (box " + i + ")", findNextOnes);
                        somethingDone = true;
//                    println("box",j+1);
                    } else if (arvud.size() == 0 && !olemasNumbrid.contains(j + 1)) {
                        throw new FillingException("box " + i + " doesn't have number " + (j + 1) + " and no possibilities exist", game);
                    }
                }
                if (!somethingDone){
                    i++;
                }
            }
        }
//        else {
//            for (Box box : game.kastid) {
//                ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<>();
//                ArrayList<Lahter> lahtridInBox = arrayToArrayList(box.getLahtrid());
//                ArrayList<Integer> olemasNumbrid = new ArrayList<>();
//                for (int j = 1; j <= dim2; j++) {
//                    availableSlots.add(new ArrayList<Integer>());
//                }
//                for (int j = 0; j < dim2; j++) {
//                    Lahter lahter = lahtridInBox.get(j);
//                    HashSet numbers = lahter.getNumbers();
//                    if (numbers.size() == 0 && lahter.getValue() == 0) {
//                        int[] XY = lahter.getXYOnBoard();
//                        throw new FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game);
//                    }
//                }
//                //get the frequency of possibilities of numbers 1-dim2
//                for (int j = 0; j < dim2; j++) {
//                    Lahter lahter = lahtridInBox.get(j);
//                    for (int a : lahter.getNumbers()) {
//                        availableSlots.get(a - 1).add(j + 1);
//                    }
//                    olemasNumbrid.add(lahter.getValue());
//                }
//                //check the different numbers and the number of the possibilities
//                for (int j = 0; j < dim2; j++) {
//                    ArrayList<Integer> arvud = availableSlots.get(j);
//                    if (arvud.size() > 0 && olemasNumbrid.contains(j + 1)) {
//                        throw new FillingException("box " + getIntFromLocalLocs(new int[]{box.locX, box.locY}) + " already has number " + (j + 1) + ", but possibilities exist", game);
//                    }
//                    if (arvud.size() == 1) {
//                        Lahter lahter = box.getLahtrid()[arvud.get(0) - 1];
//                        lahter.setValue(j + 1);
//                        afterNumber(lahter, game, true, " (box " + getIntFromLocalLocs(new int[]{box.locX, box.locY}) + ")", findNextOnes);
//                        return true;
////                    println("box",j+1);
//                    } else if (arvud.size() == 0 && !olemasNumbrid.contains(j + 1)) {
//                        throw new FillingException("box " + getIntFromLocalLocs(new int[]{box.locX, box.locY}) + " doesn't have number " + (j + 1) + " and no possibilities exist", game);
//                    }
//                }
//            }
//        }

        if (somethingDone){
            return true;
        }

        //rows
        for (int row = 1; row <= dim2; row++) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<ArrayList<Integer>>();
            ArrayList<Lahter> lahtridInRow = getRow(row, game);
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<Integer>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInRow.get(j);
                HashSet numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    int[] XY = lahter.getXYOnBoard();
                    throw new FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game);
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInRow.get(j);
                for (int a : lahter.getNumbers()) {
                    availableSlots.get(a - 1).add(j + 1);
                }
                olemasNumbrid.add(lahter.getValue());
            }
            //check the different numbers and the number of the possibilities
            for (int j = 0; j < dim2; j++) {
                ArrayList<Integer> arvud = availableSlots.get(j);
                if (arvud.size() > 0 && olemasNumbrid.contains(j + 1)) {
                    throw new FillingException("row " + row + " already has number " + (j + 1) + ", but possibilities exist", game);
                }
                if (arvud.size() == 1) {
                    Lahter lahter = getLahter(arvud.get(0), row, game);
                    lahter.setValue(j + 1);
                    afterNumber(lahter, game, true, " (row " + row + ")", findNextOnes);
                    return true;
//                    println("row",j+1);
                } else if (arvud.size() == 0 && !olemasNumbrid.contains(j + 1)) {
                    throw new FillingException("row " + row + " doesn't have number " + (j + 1) + " and no possibilities exist", game);
                }
            }

        }


        //columns
        for (int column = 1; column <= dim2; column++) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<ArrayList<Integer>>();
            ArrayList<Lahter> lahtridInColumn = getColumn(column, game);
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<Integer>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInColumn.get(j);
                HashSet numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    int[] XY = lahter.getXYOnBoard();
                    throw new FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game);
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInColumn.get(j);
                for (int a : lahter.getNumbers()) {
                    availableSlots.get(a - 1).add(j + 1);
                }
                olemasNumbrid.add(lahter.getValue());
            }
            //check the different numbers and the number of the possibilities
            for (int j = 0; j < dim2; j++) {
                ArrayList<Integer> arvud = availableSlots.get(j);
                if (arvud.size() > 0 && olemasNumbrid.contains(j + 1)) {
                    throw new FillingException("column " + column + " already has number " + (j + 1) + ", but possibilities exist", game);
                }
                if (arvud.size() == 1) {
                    Lahter lahter = getLahter(column, arvud.get(0), game);
                    lahter.setValue(j + 1);
                    afterNumber(lahter, game, true, " (column " + column + ")", findNextOnes);
                    return true;
//                    println("column",j+1);
                } else if (arvud.size() == 0 && !olemasNumbrid.contains(j + 1)) {
                    throw new FillingException("column " + column + " doesn't have number " + (j + 1) + " and no possibilities exist", game);
                }
            }
        }

        return false;
    }

    private static boolean filling1(Game game) throws FillingException {

        setLevel(1);

        for (Box box : game.kastid) {
            for (Lahter lahter : box.getLahtrid()) {
                HashSet<Integer> numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    int[] XY = lahter.getXYOnBoard();
                    throw new FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game);
                } else if (numbers.size() == 1) {
                    lahter.setValue((int) numbers.toArray()[0]);
                    numbers.clear();
                    afterNumber(lahter, game, true, " (only choice)", findNextOnes);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean filling2(Game game) throws FillingException {
        boolean somethingDone = false;

        setLevel(2);

        //box
        for (Box box : game.kastid) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<>();
            ArrayList<Lahter> lahtridInBox = arrayToArrayList(box.getLahtrid());
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInBox.get(j);
                HashSet numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    int[] XY = lahter.getXYOnBoard();
                    throw new FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game);
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInBox.get(j);
                for (int a : lahter.getNumbers()) {
                    availableSlots.get(a - 1).add(j + 1);
                }
                olemasNumbrid.add(lahter.getValue());
            }
            //check the different numbers and the number of the possibilities
            for (int j = 0; j < dim2; j++) {
                ArrayList<Integer> arvud = availableSlots.get(j);
                if (arvud.size() > 0 && olemasNumbrid.contains(j + 1)) {
                    throw new FillingException("box " + getIntFromLocalLocs(new int[]{box.locX, box.locY}) + " already has number " + (j + 1) + ", but possibilities exist", game);
                }
                if (arvud.size() >= 2 && arvud.size() <= dim) {

                    //check number of rows
                    HashSet<Integer> moduleRow = new HashSet<>();
                    for (int arv : arvud) {
                        moduleRow.add(getIntFromLinearInt(arv));
                    }
                    // candidate line (row) is active
                    if (moduleRow.size() == 1) {
                        ArrayList<Integer> moduleList = new ArrayList<>(moduleRow);
                        for (Lahter lahter : getRow((box.locY - 1) * dim + moduleList.get(0), game)) {
                            if (lahter.box != box) {
                                if (lahter.getNumbers().remove((Integer) (j + 1))) {
                                    somethingDone = true;
//                                    println("vs3 row", (box.locY-1)*3+moduleList.get(0));
                                }
                            }
                        }
                        if (somethingDone) {
                            addMessage(game, "row " + ((box.locY - 1) * dim + moduleList.get(0)) + ": " + (j + 1));
                            return true;
                        }
                    }
                    //check number of columns
                    HashSet<Integer> moduleColumn = new HashSet<>();
                    for (int arv : arvud) {
                        moduleColumn.add(getModuleFromLinearInt(arv));
                    }
                    // candidate line (column) is active
                    if (moduleColumn.size() == 1) {
                        ArrayList<Integer> moduleList = new ArrayList<>(moduleColumn);
                        for (Lahter lahter : getColumn((box.locX - 1) * dim + moduleList.get(0), game)) {
                            if (lahter.box != box) {
                                if (lahter.getNumbers().remove(j + 1)) {
                                    somethingDone = true;
//                                    println("vs3 column", (box.locX-1)*3+moduleList.get(0));
                                }
                            }
                        }
                        if (somethingDone) {
                            addMessage(game, "column " + ((box.locX - 1) * dim + moduleList.get(0)) + ": " + (j + 1));
                            return true;
                        }
                    }
                }
            }
        }

        //rows
        for (int row = 1; row <= dim2; row++) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<>();
            ArrayList<Lahter> lahtridInRow = getRow(row, game);
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInRow.get(j);
                HashSet numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    int[] XY = lahter.getXYOnBoard();
                    throw new FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game);
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInRow.get(j);
                for (int a : lahter.getNumbers()) {
                    availableSlots.get(a - 1).add(j + 1);
                }
                olemasNumbrid.add(lahter.getValue());
            }
            //check the different numbers and the number of the possibilities
            for (int j = 0; j < dim2; j++) {
                ArrayList<Integer> arvud = availableSlots.get(j);
                if (arvud.size() > 0 && olemasNumbrid.contains(j + 1)) {
                    throw new FillingException("row " + row + " already has number " + (j + 1) + ", but possibilities exist", game);
                }
                if (arvud.size() >= 2 && arvud.size() <= dim) {
                    HashSet<Integer> module = new HashSet<>();
                    for (int arv : arvud) {
                        module.add(getIntFromLinearInt(arv));
                    }
                    // multiple lines is active, one number in a row resides in one box
                    if (module.size() == 1) {
                        ArrayList<Integer> moduleList = new ArrayList<>(module);
                        for (Lahter lahter : getKast(moduleList.get(0), getIntFromLinearInt(row), game).getLahtrid()) {
                            if (lahter.locY != getModuleFromLinearInt(row)) {
                                if (lahter.getNumbers().remove(j + 1)) {
                                    somethingDone = true;
//                                    println("vs4 row", j+1);
                                }
                            }
                        }
                        if (somethingDone) {
                            addMessage(game, "row " + row + ": " + (j + 1));
                            return true;
                        }
                    }
                }
            }
        }

        //columns
        for (int column = 1; column <= dim2; column++) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<>();
            ArrayList<Lahter> lahtridInColumn = getColumn(column, game);
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInColumn.get(j);
                HashSet numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    int[] XY = lahter.getXYOnBoard();
                    throw new FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game);
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInColumn.get(j);
                for (int a : lahter.getNumbers()) {
                    availableSlots.get(a - 1).add(j + 1);
                }
                olemasNumbrid.add(lahter.getValue());
            }
            //check the different numbers and the number of the possibilities
            for (int j = 0; j < dim2; j++) {
                ArrayList<Integer> arvud = availableSlots.get(j);
                if (arvud.size() > 0 && olemasNumbrid.contains(j + 1)) {
                    throw new FillingException("column " + column + " already has number " + (j + 1) + ", but possibilities exist", game);
                }
                if (arvud.size() >= 2 && arvud.size() <= dim) {
                    HashSet<Integer> module = new HashSet<>();
                    for (int arv : arvud) {
                        module.add(getIntFromLinearInt(arv));
                    }
                    //multiple lines is active
                    if (module.size() == 1) {
                        ArrayList<Integer> moduleList = new ArrayList<Integer>(module);
                        for (Lahter lahter : getKast(getIntFromLinearInt(column), moduleList.get(0), game).getLahtrid()) {
                            if (lahter.locX != getModuleFromLinearInt(column)) {
                                if (lahter.getNumbers().remove(j + 1)) {
                                    somethingDone = true;
//                                    println("vs4 column", j+1);
                                }
                            }
                        }
                        if (somethingDone) {
                            addMessage(game, "column " + column + ": " + (j + 1));
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static String getCollectionName(int a) {
        return a <= collectionNames.length + 1 ? collectionNames[a - 2] : a + "-uplet";
    }

    private static boolean filling3(Game game) {
        boolean somethingDone = false;

        //naked and hidden pairs, triplets, quarters

        setLevel(3);

        //box
        for (Box box : game.kastid) {
            ArrayList<Lahter> lahtridInBox = arrayToArrayList(box.getLahtrid());
            ArrayList<Lahter> emptyLahtrid = new ArrayList<>();
            for (Lahter lahter : lahtridInBox) {
                if (lahter.getValue() == 0) {
                    emptyLahtrid.add(lahter);
                }
            }
            if (emptyLahtrid.size() > 3) {

                //naked
                for (int i = 2; i <= Math.ceil(emptyLahtrid.size() / 2); i++) {
                    combinations:
                    for (Object a : combinations(emptyLahtrid.size(), i)) {
                        //you've got the combinations, what now?
                        ArrayList<Lahter> chosenOnes = new ArrayList<>();
                        for (int index : (ArrayList<Integer>) a) {
                            chosenOnes.add(emptyLahtrid.get(index));
                        }
                        //finding naked pairs, triplets, ...
                        HashSet<Integer> numbers = new HashSet<>();
                        for (Lahter lahter : chosenOnes) {
                            for (int index : lahter.getNumbers()) {
                                numbers.add(index);
                                if (numbers.size() > i) {
                                    //not a hidden pair
                                    continue combinations;
                                }
                            }
                        }
                        //remove the numbers from the other lahtrid in the same box
                        lahtrid:
                        for (Lahter lahter : emptyLahtrid) {
                            for (Lahter lahter1 : chosenOnes) {
                                if (lahter.equals(lahter1)) {
                                    continue lahtrid;
                                }
                            }
                            for (int usedNumber : numbers) {
                                if (lahter.getNumbers().remove(usedNumber)) {
                                    somethingDone = true;
                                }
                            }
                        }
                        //TODO check if in a row or a column

                        if (somethingDone) {
                            addMessage(game, "box " + String.valueOf(getIntFromLocalLocs(new int[]{box.locX, box.locY})) + ": naked " + getCollectionName(i) + " with numbers " + numbers.toString());
                            return true;
                        }
                    }
                }

                //hidden

                //get unfilled numbers
                HashSet unfillednumbers = getNumberSet();
                for (Lahter lahter : box.getLahtrid()) {
                    unfillednumbers.remove(lahter.getValue());
                }

                ArrayList<ArrayList<Integer>> numberscontainedinlahters = new ArrayList();
                for (int i = 0; i < dim2; i++) {
                    numberscontainedinlahters.add(new ArrayList<>());
                }

                for (int index = 0; index < emptyLahtrid.size(); index++) {
                    Lahter lahter = emptyLahtrid.get(index);
                    if (lahter.getValue() == 0) {
                        for (int numbrid : lahter.getNumbers()) {
                            numberscontainedinlahters.get(numbrid - 1).add(index);
                        }
                    }
                }
                ArrayList<Integer> numberlist = getNumberList();

                //removes unneeded empty lists (of those numbers that are already filled)
                int b = 0;
                while (b < numberscontainedinlahters.size()) {
                    if (numberscontainedinlahters.get(b).isEmpty()) {
                        numberscontainedinlahters.remove(b);
                        numberlist.remove(b);
                    } else {
                        b++;
                    }
                }

                //taking combinations of fillable numbers and analyzing if total lahters needed by them is more than the amount of numbers being analyzed
                for (int i = 2; i < Math.ceil(emptyLahtrid.size() / 2); i++) {
                    for (Object a : combinations(unfillednumbers.size(), i)) {
                        //you've got the combinations, what now?
                        ArrayList<Integer> chosenOnesIndexes = new ArrayList<>((ArrayList<Integer>) a);

                        //collecting lahters needed
                        HashSet<Integer> chosenLahtersIndexes = new HashSet<>();
                        for (int query : chosenOnesIndexes)
                            chosenLahtersIndexes.addAll(numberscontainedinlahters.get(query));
                        //not a hidden pair, triplet,...
                        if (chosenLahtersIndexes.size() > chosenOnesIndexes.size())
                            continue;

                        ArrayList<Lahter> chosenLahtrid = new ArrayList<>();
                        for (int index : chosenLahtersIndexes)
                            chosenLahtrid.add(emptyLahtrid.get(index));

                        HashSet<Integer> chosenOnes = new HashSet();
                        for (int arv : chosenOnesIndexes)
                            chosenOnes.add(numberlist.get(arv));


                        //remove other numbers from chosen lahters
                        for (Lahter lahter : chosenLahtrid) {
                            for (int arv : allNumbers)
                                if (!chosenOnes.contains(arv))
                                    if (lahter.getNumbers().remove(arv))
                                        somethingDone = true;

                        }

                        //TODO add check if inside a row or a column

                        //remove the numbers from the other lahtrid in the same row
                        lahtrid:
                        for (Lahter lahter : emptyLahtrid) {
                            for (Lahter lahter1 : chosenLahtrid) {
                                if (lahter.equals(lahter1))
                                    continue lahtrid;
                            }
                            for (int usedNumber : chosenOnes)
                                if (lahter.getNumbers().remove(usedNumber))
                                    somethingDone = true;
                        }
                        if (somethingDone) {
                            addMessage(game, "box " + String.valueOf(getIntFromLocalLocs(new int[]{box.locX, box.locY})) + ": hidden " + getCollectionName(i) + " with numbers " + chosenOnes.toString());
                            return true;
                        }
                    }
                }
            }
        }

        //TODO add puzzle level to the game, after that use filtering to filter out more complex

        //rows
        for (int row = 1; row <= dim2; row++) {
            ArrayList<Lahter> lahtridInRow = getRow(row, game);
            ArrayList<Lahter> emptyLahtrid = new ArrayList<>();
            for (Lahter lahter : lahtridInRow) {
                if (lahter.getValue() == 0) {
                    emptyLahtrid.add(lahter);
                }
            }
            if (emptyLahtrid.size() > 3) {

                //naked
                for (int i = 2; i <= Math.ceil(emptyLahtrid.size() / 2); i++) {
                    combinations:
                    for (Object a : combinations(emptyLahtrid.size(),i)){
                        //you've got the combinations, what now?
                        ArrayList<Lahter> chosenOnes = new ArrayList<>();
                        for (int index : (ArrayList<Integer>)a){
                            chosenOnes.add(emptyLahtrid.get(index));
                        }
                        //finding naked pairs, triplets, ...
                        HashSet<Integer> numbers = new HashSet<>();
                        for (Lahter lahter : chosenOnes){
                            for (int index : lahter.getNumbers()){
                                numbers.add(index);
                                if (numbers.size()>i){
                                    //not a hidden pair
                                    continue combinations;
                                }
                            }
                        }
                        //remove the numbers from the other lahtrid in the same row
                        lahtrid:
                        for (Lahter lahter : emptyLahtrid){
                            for (Lahter lahter1 : chosenOnes){
                                if (lahter.equals(lahter1)){
                                    continue lahtrid;
                                }
                            }
                            for (int usedNumber : numbers) {
                                if (lahter.getNumbers().remove(usedNumber)) {
                                    somethingDone = true;
                                }
                            }
                        }
                        if (somethingDone) {
                            addMessage(game, "row " + row + ": naked " + getCollectionName(i) + " with numbers " + numbers.toString());
                            return true;
                        }
                    }
                }
                //hidden

                //get unfilled numbers
                HashSet unfillednumbers = getNumberSet();
                for (Lahter lahter : getRow(row, game)) {
                    unfillednumbers.remove(lahter.getValue());
                }

                ArrayList<ArrayList<Integer>> numberscontainedinlahters = new ArrayList();
                for (int i = 0; i < dim2; i++) {
                    numberscontainedinlahters.add(new ArrayList<Integer>());
                }

                for (int index = 0; index < emptyLahtrid.size(); index++) {
                    Lahter lahter = emptyLahtrid.get(index);
                    if (lahter.getValue() == 0) {
                        for (int numbrid : lahter.getNumbers()) {
                            numberscontainedinlahters.get(numbrid - 1).add(index);
                        }
                    }
                }
                ArrayList<Integer> numberlist = getNumberList();

                //removes unneeded empty lists (of those numbers that are already filled)
                int b = 0;
                while (b < numberscontainedinlahters.size()) {
                    if (numberscontainedinlahters.get(b).isEmpty()) {
                        numberscontainedinlahters.remove(b);
                        numberlist.remove(b);
                    } else {
                        b++;
                    }
                }

                //taking combinations of fillable numbers and analyzing if total lahters needed by them is more than the amount of numbers being analyzed
                for (int i = 2; i < Math.ceil(emptyLahtrid.size() / 2); i++) {
                    for (Object a : combinations(unfillednumbers.size(), i)) {
                        //you've got the combinations, what now?
                        ArrayList<Integer> chosenOnesIndexes = new ArrayList<>((ArrayList<Integer>) a);

                        //collecting lahters needed
                        HashSet<Integer> chosenLahtersIndexes = new HashSet<>();
                        for (int query : chosenOnesIndexes)
                            chosenLahtersIndexes.addAll(numberscontainedinlahters.get(query));
                        //not a hidden pair, triplet,...
                        if (chosenLahtersIndexes.size() > chosenOnesIndexes.size())
                            continue;

                        ArrayList<Lahter> chosenLahtrid = new ArrayList<>();
                        for (int index : chosenLahtersIndexes)
                            chosenLahtrid.add(emptyLahtrid.get(index));

                        HashSet<Integer> chosenOnes = new HashSet();
                        for (int arv : chosenOnesIndexes)
                            chosenOnes.add(numberlist.get(arv));


                        //remove other numbers from chosen lahters
                        for (Lahter lahter : chosenLahtrid) {
                            for (int arv : allNumbers)
                                if (!chosenOnes.contains(arv))
                                    if (lahter.getNumbers().remove(arv))
                                        somethingDone = true;

                        }

                        //remove the numbers from the other lahtrid in the same row
                        lahtrid:
                        for (Lahter lahter : emptyLahtrid) {
                            for (Lahter lahter1 : chosenLahtrid) {
                                if (lahter.equals(lahter1))
                                    continue lahtrid;
                            }
                            for (int usedNumber : chosenOnes)
                                if (lahter.getNumbers().remove(usedNumber))
                                    somethingDone = true;
                        }

                        //TODO check if inside a box

                        if (somethingDone) {
                            addMessage(game, "row " + String.valueOf(row) + ": hidden " + getCollectionName(i) + " with numbers " + chosenOnes.toString());
                            return true;
                        }
                    }
                }
            }
        }

        //columns
        for (int column = 1; column <= dim2; column++) {
            ArrayList<Lahter> lahtridInColumn = getColumn(column, game);
            ArrayList<Lahter> emptyLahtrid = new ArrayList<>();
            for (Lahter lahter : lahtridInColumn) {
                if (lahter.getValue() == 0) {
                    emptyLahtrid.add(lahter);
                }
            }
            if (emptyLahtrid.size() > 3) {

                //naked
                for (int i = 2; i <= Math.ceil(emptyLahtrid.size() / 2); i++) {
                    combinations:
                    for (Object a : combinations(emptyLahtrid.size(), i)) {
                        //you've got the combinations, what now?
                        ArrayList<Lahter> chosenOnes = new ArrayList<>();
                        for (int index : (ArrayList<Integer>) a) {
                            chosenOnes.add(emptyLahtrid.get(index));
                        }
                        //finding naked pairs, triplets, ...
                        HashSet<Integer> numbers = new HashSet<>();
                        for (Lahter lahter : chosenOnes) {
                            for (int index : lahter.getNumbers()) {
                                numbers.add(index);
                                if (numbers.size() > i) {
                                    //not a hidden pair
                                    continue combinations;
                                }
                            }
                        }
                        //remove the numbers from the other lahtrid in the same row
                        lahtrid:
                        for (Lahter lahter : emptyLahtrid) {
                            for (Lahter lahter1 : chosenOnes) {
                                if (lahter.equals(lahter1)) {
                                    continue lahtrid;
                                }
                            }
                            for (int usedNumber : numbers) {
                                if (lahter.getNumbers().remove(usedNumber)) {
                                    somethingDone = true;
                                }
                            }
                        }
                        if (somethingDone) {
                            addMessage(game, "column " + column + ": naked " + getCollectionName(i) + " with numbers " + numbers.toString());
                            return true;
                        }
                    }
                }

                //hidden

                //get unfilled numbers
                HashSet unfillednumbers = getNumberSet();
                for (Lahter lahter : getColumn(column, game)) {
                    unfillednumbers.remove(lahter.getValue());
                }

                ArrayList<ArrayList<Integer>> numberscontainedinlahters = new ArrayList();
                for (int i = 0; i < dim2; i++) {
                    numberscontainedinlahters.add(new ArrayList<Integer>());
                }

                for (int index = 0; index < emptyLahtrid.size(); index++) {
                    Lahter lahter = emptyLahtrid.get(index);
                    if (lahter.getValue() == 0) {
                        for (int numbrid : lahter.getNumbers()) {
                            numberscontainedinlahters.get(numbrid - 1).add(index);
                        }
                    }
                }
                ArrayList<Integer> numberlist = getNumberList();

                //removes unneeded empty lists (of those numbers that are already filled)
                int b = 0;
                while (b < numberscontainedinlahters.size()) {
                    if (numberscontainedinlahters.get(b).isEmpty()) {
                        numberscontainedinlahters.remove(b);
                        numberlist.remove(b);
                    } else {
                        b++;
                    }
                }

                //taking combinations of fillable numbers and analyzing if total lahters needed by them is more than the amount of numbers being analyzed
                for (int i = 2; i < Math.ceil(emptyLahtrid.size() / 2); i++) {
                    for (Object a : combinations(unfillednumbers.size(), i)) {
                        //you've got the combinations, what now?
                        ArrayList<Integer> chosenOnesIndexes = new ArrayList<>((ArrayList<Integer>) a);

                        //collecting lahters needed
                        HashSet<Integer> chosenLahtersIndexes = new HashSet<>();
                        for (int query : chosenOnesIndexes)
                            chosenLahtersIndexes.addAll(numberscontainedinlahters.get(query));
                        //not a hidden pair, triplet,...
                        if (chosenLahtersIndexes.size() > chosenOnesIndexes.size())
                            continue;

                        ArrayList<Lahter> chosenLahtrid = new ArrayList<>();
                        for (int index : chosenLahtersIndexes)
                            chosenLahtrid.add(emptyLahtrid.get(index));

                        HashSet<Integer> chosenOnes = new HashSet();
                        for (int arv : chosenOnesIndexes)
                            chosenOnes.add(numberlist.get(arv));


                        //remove other numbers from chosen lahters
                        for (Lahter lahter : chosenLahtrid)
                            for (int arv : allNumbers)
                                if (!chosenOnes.contains(arv))
                                    if (lahter.getNumbers().remove(arv))
                                        somethingDone = true;


                        //remove the numbers from the other lahtrid in the same row
                        lahtrid:
                        for (Lahter lahter : emptyLahtrid) {
                            for (Lahter lahter1 : chosenLahtrid)
                                if (lahter.equals(lahter1))
                                    continue lahtrid;
                            for (int usedNumber : chosenOnes)
                                if (lahter.getNumbers().remove(usedNumber))
                                    somethingDone = true;
                        }

                        //TODO check if inside a box

                        if (somethingDone) {
                            addMessage(game, "column " + String.valueOf(column) + ": hidden " + getCollectionName(i) + " with numbers " + chosenOnes.toString());
                            return true;
                        }
                    }
                }
            }
        }
        return somethingDone;
    }

    /**
     * @param antudLahter
     * @param game
     * @param showXY
     * @param message
     * @param findNextOnes look for lahtrid with only one choice remaining, ca 3x times faster for computer, less intuitive for people, but more intuitive, if trying to fill
     */
    private static void afterNumber(Lahter antudLahter, Game game, boolean showXY, String message, boolean findNextOnes) {
        int[] XY = getXYFromLocs(new int[][]{{antudLahter.box.locX, antudLahter.box.locY}, {antudLahter.locX, antudLahter.locY}});
        int x = XY[0];
        int y = XY[1];

        int value = antudLahter.getValue();

        ArrayList<Lahter> nextLahtrid = new ArrayList<>();

        if (showXY) addMessage(game, "x:" + x + " y:" + y + " value: " + value + message);

        //remove possible numbers in the same row, column and the 3x3 box

        //the same 3x3 box
        for (Lahter lahter : getLahter(x, y, game).box.getLahtrid()) {
            lahter.getNumbers().remove(value);
            if (findNextOnes){
                if (lahter.getNumbers().size() == 1 && lahter.getValue() == 0) {
                    int onlyValue = (int) lahter.getNumbers().toArray()[0];
                    addMessage(game, " (box " + y + ", only choice " + onlyValue + ")");
                    lahter.setValue(onlyValue);
                    nextLahtrid.add(lahter);
                }
            }
        }

        //same row
        for (Lahter lahter : getRow(y, game)) {
            lahter.getNumbers().remove(value);
            if (findNextOnes) {
                if (lahter.getNumbers().size() == 1 && lahter.getValue() == 0) {
                    int onlyValue = (int) lahter.getNumbers().toArray()[0];
                    addMessage(game, " (row " + y + ", only choice " + onlyValue + ")");
                    lahter.setValue(onlyValue);
                    nextLahtrid.add(lahter);
                }
            }
        }
        //same column
        for (Lahter lahter : getColumn(x, game)) {
            lahter.getNumbers().remove(value);
            if (findNextOnes) {
                if (lahter.getNumbers().size() == 1 && lahter.getValue() == 0) {
                    int onlyValue = (int) lahter.getNumbers().toArray()[0];
                    addMessage(game, " (column " + x + ", only choice " + onlyValue + ")");
                    lahter.setValue(onlyValue);
                    nextLahtrid.add(lahter);
                }
            }
        }

        for (Lahter lahter : nextLahtrid){
            afterNumber(lahter, game, true, lahter.getMessage(), findNextOnes);
        }
    }

    private static void setLevel(int level) {
        Loogika.level = level;
    }

    private static void addMessage(Game game, String message) {
        if (prevLevel != level) {
            game.messages.add("Logic level " + level);
            prevLevel = level;
        }
        game.messages.add(message);
    }

    private static Game copyGame(Game game) {
        Box[] clone = newKastid();

        for (int i = 0; i < dim2; i++) {
            for (int j = 0; j < dim2; j++) {
                clone[i].getLahtrid()[j].setValue(game.kastid[i].getLahtrid()[j].getValue());
                clone[i].getLahtrid()[j].setNumbers(copyHashSet(game.kastid[i].getLahtrid()[j].getNumbers()));
            }
        }

        return new Game(clone, game.messages);
    }

    private static ArrayList<Integer> copyList(ArrayList<Integer> in) {
        ArrayList<Integer> out = new ArrayList<>();
        out.addAll(in);
        return out;
    }

    private static HashSet<Integer> copyHashSet(HashSet<Integer> set) {
        return new HashSet(Arrays.asList(set.toArray(new Integer[]{})));
    }

    private static void printSidewaysGrid(Game game1, Game game2) {
        for (int kastY = 1; kastY <= dim; kastY++) {
            for (int lahterY = 1; lahterY <= dim; lahterY++) {
                for (int kastX = 1; kastX <= dim; kastX++) {
                    for (int lahterX = 1; lahterX <= dim; lahterX++) {
                        int value;
                        value = getKast(kastX, kastY, game1).getLahter(lahterX, lahterY).getValue();
                        if (value == 0) {
                            printSpaceBefore("*");
                        } else if (value > 9) {
                            printNoSpace(value);
                        } else {
                            printSpaceBefore(value);
                        }
                    }
                    print(" ");
                }
                print("        ");
                for (int kastX = 1; kastX <= dim; kastX++) {
                    for (int lahterX = 1; lahterX <= dim; lahterX++) {
                        int value;
                        value = getKast(kastX, kastY, game2).getLahter(lahterX, lahterY).getValue();
                        if (value == 0) {
                            printSpaceBefore("*");
                        } else if (value > 9) {
                            printNoSpace(value);
                        } else {
                            printSpaceBefore(value);
                        }
                    }
                    print(" ");
                }
                newLine();
            }
            newLine();
        }
    }

    /**
     * @param printSolutionSteps  --
     * @param printOnlyUnsolvable --
     */
    static void printSolutions(boolean printSolutionSteps, boolean printOnlyUnsolvable) {
        for (int i = 0; i < gamesAmount; i++) {
            if (!printOnlyUnsolvable || (printOnlyUnsolvable && !solutions.get(i).solved)) {
                if (printSolutionSteps) {
                    for (int j = 0; j < solutions.get(i).messages.size(); j++) {
                        String message = solutions.get(i).messages.get(j);
                        if (!message.contains("Logic")) {
                            String index = Integer.toString(j);
                            print(new String(new char[5 - index.length()]).replace('\0', ' ') + index + ": ");
                        }
                        println(message);
                    }
                }
                newLine();
                printSidewaysGrid(puzzles.get(i), solutions.get(i));
                newLine();
                println("----------------------------------------------------------------");
            }
        }
    }


    /**
     * @param file for example: /src/level4-10000.txt
     * @return string of contents
     */
    private static ArrayList<String> readTextFromFile(File file) throws FileNotFoundException {
        try {
            Scanner scan = new Scanner(file);
            ArrayList<String> lines = new ArrayList<>();
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine());
            }
//            println("File read successfully!");
            return lines;
        }catch (FileNotFoundException e){
            throw new FileNotFoundException();
        }
    }


    static Box[] newKastid() {
        Box[] kastid = new Box[dim2];
        for (int i = 1; i <= dim2; i++) {//dim2 korda tee Box
            kastid[i - 1] = genKast(getXYFromInt(i)[0], getXYFromInt(i)[1]);
        }
        return kastid;
    }

    /**
     * @param locX 1-3
     * @param locY 1-3
     * @return Box
     */
    private static Box genKast(int locX, int locY) {
        Lahter[] lahtrid = new Lahter[dim2];
        for (int i = 0; i < dim2; i++) {//dim2 korda tee Lahter
            lahtrid[i] = new Lahter(i % dim + 1, i / dim + 1, getNumberSet(), 0, null, null);
        }
        Box box = new Box(locX, locY, lahtrid, dim);
        for (Lahter lahter : lahtrid) {
            lahter.box = box;
        }
        return box;
    }

    private static HashSet getNumberSet() {
        HashSet result = new HashSet();
        for (int i = 1; i <= dim2; i++) {
            result.add(i);
        }
        return result;
    }

    private static ArrayList<Integer> getNumberList() {
        ArrayList result = new ArrayList();
        for (int i = 1; i <= dim2; i++) {
            result.add(i);
        }
        return result;
    }

    private static ArrayList<Lahter> getRow(int y, Game game) {
        ArrayList<Lahter> lahtrid = new ArrayList<>();
        for (Box box : game.kastid) {
            for (Lahter lahter : box.getLahtrid()) {
                if (box.locY == (y - 1) / dim + 1 && lahter.locY == (y - 1) % dim + 1) {
                    lahtrid.add(lahter);
                }
            }
        }
        return lahtrid;
    }

    private static ArrayList<Lahter> getColumn(int x, Game game) {
        ArrayList<Lahter> lahtrid = new ArrayList<>();
        for (Box box : game.kastid) {
            for (Lahter lahter : box.getLahtrid()) {
                if (box.locX == (x - 1) / dim + 1 && lahter.locX == (x - 1) % dim + 1) {
                    lahtrid.add(lahter);
                }
            }
        }
        return lahtrid;
    }

    private static boolean checkFilled(Game game) {
        for (Box box : game.kastid) {
            for (Lahter lahter : box.getLahtrid()) {
                if (lahter.getValue() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static ArrayList combinations(int sample, int maxLen) {
        ArrayList results = new ArrayList();
        return _combsFindNext(new ArrayList<>(), -1, results, sample, -1, maxLen);
    }

    private static ArrayList<Integer> _combsFindNext(ArrayList<Integer> last, int lastInt, ArrayList results, int sample, int level, int maxLevel) {
        level++;
        if (level < maxLevel) {
            for (int i = lastInt + 1; i < sample; i++) {
                ArrayList<Integer> send = copyList(last);
                send.add(i);
                _combsFindNext(send, i, results, sample, level, maxLevel);
            }
        } else {
            results.add(last);
        }
        return results;
    }

    /**
     * @param locX 1-3
     * @param locY 1-3
     * @return Box
     */
    private static Box getKast(int locX, int locY, Game game) {
        return game.kastid[(locY - 1) * dim + locX - 1];
    }

    private static Box getKast(int[] loc, Game game) {
        return game.kastid[(loc[1] - 1) * dim + loc[0] - 1];
    }

    private static Box getKast(int ind, Game game) {
        return game.kastid[ind - 1];
    }

    /**
     * @param x 1-9
     * @param y 1-9
     * @return lahter
     */
    static Lahter getLahter(int x, int y, Game game) {
        int[][] locs = getLocsFromXY(x, y);
        int[] locsKast = locs[0];
        int[] locsLahter = locs[1];
        return getKast(locsKast, game).getLahter(locsLahter);
    }

    /**
     * @param val 1-9
     * @return x = 1-3, y = 1-3
     */
    private static int[] getXYFromInt(int val) {
        return new int[]{(val - 1) % dim + 1, (val - 1) / dim + 1};
    }

    /**
     * @param val 1-9
     *            1 2 3, 4 5 6, 7 8 9
     *            1,     2,     3
     * @return 1-3
     */
    private static int getIntFromLinearInt(int val) {
        return (val - 1) / dim + 1;
    }

    /**
     * @param val 1-9
     *            1 2 3, 4 5 6, 7 8 9
     *            1 2 3, 1 2 3, 1 2 3
     * @return the modulo
     */
    private static int getModuleFromLinearInt(int val) {
        return (val - 1) % dim + 1;
    }

    /**
     *
     * @param locs [x 1-dim, y 1-dim]
     * @return 1-dim2
     */
    private static int getIntFromLocalLocs(int[] locs) {
        return (locs[1]-1)*dim + locs[0];
    }

    /**
     * @param x 1-9
     * @param y 1-9
     * @return [[kastX 1-3, kastY 1-3],[lahterX 1-3, lahterY 1-3]]
     */
    private static int[][] getLocsFromXY(int x, int y) {
        int kastX = (x - 1) / dim + 1;
        int kastY = (y - 1) / dim + 1;
        int lahterX = x - (kastX - 1) * dim;
        int lahterY = y - (kastY - 1) * dim;
        return new int[][]{{kastX, kastY}, {lahterX, lahterY}};
    }

    private static int[] getXYFromLocs(int[][] locs) {
        int[] kastXY = locs[0];
        int[] lahterXY = locs[1];
        return new int[]{(kastXY[0] - 1) * dim + lahterXY[0], (kastXY[1] - 1) * dim + lahterXY[1]};
    }

    public static void print(Object... o) {
        for (Object i : o) {
            System.out.print(i + " ");
        }
    }

    public static void printSpaceBefore(Object o) {
        System.out.print(" " + o);
    }

    public static void printNoSpace(Object o) {
        System.out.print(o);
    }

    public static void println(Object... o) {
        for (Object i : o) {
            System.out.print(i + " ");
        }
        newLine();
    }

    public static ArrayList arrayToArrayList(Object[] set) {
        ArrayList list = new ArrayList<>();
        Collections.addAll(list, set);
        return list;
    }

    public static void newLine() {
        System.out.println();
    }

}


      