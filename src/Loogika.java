import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Loogika {

    //settings
    static final int dim = 4;
    static final int dim2 = 16;
    static final boolean findNextOnes = false;
    static final boolean sameBoxFirst = true;
    static int solutionsAmount = 0;
    static int gamesAmount = 0;
    static ArrayList<Game> puzzles = new ArrayList<>();
    static ArrayList<Game> solutions = new ArrayList<>();
    static FileChooser fileChoose = new FileChooser();
    static TimeMeasure measure = new TimeMeasure();
    static ArrayList<String> solutionSteps = new ArrayList<>();


    /**
     * @param unsolved the original kastid after reading in the information
     * updates puzzles, solutions, gamesAmount and solutionsAmount
     */
    public static void startFilling(Game unsolved){
        Game solved = copyGame(unsolved);
        initialLoading(solved);
        try {
            continuousFilling(solved);
        } catch (FillingException e) {
            e.printStackTrace();
        } catch (FinishedException e) {
            puzzles.add(unsolved);
            solutions.add(solved);
            gamesAmount++;
            if (e.filled){
                solutionsAmount++;
            }
        }
    }

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
     * @param game
     * @return completed sudoku
     * @throws FillingException, FinishedException
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
        } catch (FillingException e) {
            throw e;
        } catch (FinishedException e) {
            throw e;
        }
    }

    private static void initialLoading(Game game){
        for (Kast kast : game.kastid){
            for (Lahter lahter : kast.getLahtrid()){
                if (lahter.getValue() != 0){
                    afterNumber(lahter, game, false, null, findNextOnes);
                }
            }
        }
    }

    private static boolean fillingNormal(Game game) throws FillingException {
        boolean somethingDone = false;

        //TODO p''ra ringi, et ta otsiks sama numbrit teistest kastidest enne, siis teisi numbreid

        //box

        if (sameBoxFirst){
            int i = 1;
            while (i <= dim2) {
                somethingDone = false;
                Kast kast = getKast(i, game.kastid);
                ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<ArrayList<Integer>>();
                ArrayList<Lahter> lahtridInBox = arrayToArrayList(kast.getLahtrid());
                ArrayList<Integer> olemasNumbrid = new ArrayList<>();
                for (int j = 1; j <= dim2; j++) {
                    availableSlots.add(new ArrayList<Integer>());
                }
                for (int j = 0; j < dim2; j++) {
                    Lahter lahter = lahtridInBox.get(j);
                    ArrayList<Integer> numbers = lahter.getNumbers();
                    if (numbers.size() == 0 && lahter.getValue() == 0) {
                        throw new FillingException();
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
                        throw new FillingException();
                    }
                    if (arvud.size() == 1) {
                        Lahter lahter = kast.getLahtrid()[arvud.get(0) - 1];
                        lahter.setValue(j + 1);
                        afterNumber(lahter, game, true, " (box " + getIntFromLocalLocs(new int[]{kast.locX, kast.locY}) + ")", findNextOnes);
                        somethingDone = true;
//                    println("box",j+1);
                    } else if (arvud.size() == 0 && !olemasNumbrid.contains(j + 1)) {
                        throw new FillingException();
                    }
                }
                if (!somethingDone){
                    i++;
                }
            }
        }else{
            for (Kast kast : game.kastid) {
                ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<>();
                ArrayList<Lahter> lahtridInBox = arrayToArrayList(kast.getLahtrid());
                ArrayList<Integer> olemasNumbrid = new ArrayList<>();
                for (int j = 1; j <= dim2; j++) {
                    availableSlots.add(new ArrayList<Integer>());
                }
                for (int j = 0; j < dim2; j++) {
                    Lahter lahter = lahtridInBox.get(j);
                    ArrayList<Integer> numbers = lahter.getNumbers();
                    if (numbers.size() == 0 && lahter.getValue() == 0) {
                        throw new FillingException();
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
                        throw new FillingException();
                    }
                    if (arvud.size() == 1) {
                        Lahter lahter = kast.getLahtrid()[arvud.get(0) - 1];
                        lahter.setValue(j + 1);
                        afterNumber(lahter, game, true, " (box " + getIntFromLocalLocs(new int[]{kast.locX, kast.locY}) + ")", findNextOnes);
                        return true;
//                    println("box",j+1);
                    } else if (arvud.size() == 0 && !olemasNumbrid.contains(j + 1)) {
                        throw new FillingException();
                    }
                }
            }
        }

        if (somethingDone){
            return true;
        }

        //rows
        for (int row = 1; row <= dim2; row++) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<ArrayList<Integer>>();
            ArrayList<Lahter> lahtridInRow = getRow(row, game.kastid);
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<Integer>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInRow.get(j);
                ArrayList<Integer> numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    throw new FillingException();
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
                    throw new FillingException();
                }
                if (arvud.size() == 1) {
                    Lahter lahter = getLahter(arvud.get(0), row, game.kastid);
                    lahter.setValue(j + 1);
                    afterNumber(lahter, game, true, " (row " + row + ")", findNextOnes);
                    return true;
//                    println("row",j+1);
                } else if (arvud.size() == 0 && !olemasNumbrid.contains(j + 1)) {
                    println(row, arvud.toString(), j + 1, olemasNumbrid.toString());
                    throw new FillingException();
                }
            }

        }

        //columns
        for (int column = 1; column <= dim2; column++) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<ArrayList<Integer>>();
            ArrayList<Lahter> lahtridInColumn = getColumn(column, game.kastid);
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<Integer>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInColumn.get(j);
                ArrayList<Integer> numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    throw new FillingException();
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
                    throw new FillingException();
                }
                if (arvud.size() == 1) {
                    Lahter lahter = getLahter(column, arvud.get(0), game.kastid);
                    lahter.setValue(j + 1);
                    afterNumber(lahter, game, true, " (column " + column + ")", findNextOnes);
                    return true;
//                    println("column",j+1);
                } else if (arvud.size() == 0 && !olemasNumbrid.contains(j + 1)) {
                    throw new FillingException();
                }
            }
        }

        return somethingDone;
    }

    private static boolean filling1(Game game) throws FillingException {
        boolean somethingDone = false;

        for (Kast kast : game.kastid){
            for (Lahter lahter : kast.getLahtrid()){
                ArrayList<Integer> numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    throw new FillingException();
                } else if (numbers.size() == 1) {
                    lahter.setValue(numbers.get(0));
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

        println("logic level 2");

        //box
        for (Kast kast : game.kastid) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<>();
            ArrayList<Lahter> lahtridInBox = arrayToArrayList(kast.getLahtrid());
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<Integer>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInBox.get(j);
                ArrayList<Integer> numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    throw new FillingException();
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
                    throw new FillingException();
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
                        for (Lahter lahter : getRow((kast.locY - 1) * dim + moduleList.get(0), game.kastid)) {
                            if (lahter.kast != kast) {
                                if (lahter.getNumbers().remove((Integer) (j + 1))) {
                                    somethingDone = true;
//                                    println("vs3 row", (kast.locY-1)*3+moduleList.get(0));
                                }
                            }
                        }
                        if (somethingDone){
                            game.messages.add("row " + ((kast.locY - 1) * dim + moduleList.get(0)) + ": "+ (j+1));
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
                        for (Lahter lahter : getColumn((kast.locX - 1) * dim + moduleList.get(0), game.kastid)) {
                            if (lahter.kast != kast) {
                                if (lahter.getNumbers().remove((Integer) (j + 1))) {
                                    somethingDone = true;
//                                    println("vs3 column", (kast.locX-1)*3+moduleList.get(0));
                                }
                            }
                        }
                        if (somethingDone){
                            game.messages.add("row " + ((kast.locX - 1) * dim + moduleList.get(0)) + ": "+ (j+1));
                            return true;
                        }
                    }
                }
            }
        }

        //rows
        for (int row = 1; row <= dim2; row++) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<>();
            ArrayList<Lahter> lahtridInRow = getRow(row, game.kastid);
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<Integer>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInRow.get(j);
                ArrayList<Integer> numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    throw new FillingException();
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
                    throw new FillingException();
                }
                if (arvud.size() >= 2 && arvud.size() <= dim) {
                    HashSet<Integer> module = new HashSet<>();
                    for (int arv : arvud) {
                        module.add(getIntFromLinearInt(arv));
                    }
                    // multiple lines is active
                    if (module.size() == 1) {
                        ArrayList<Integer> moduleList = new ArrayList<>(module);
                        for (Lahter lahter : getKast(moduleList.get(0), getIntFromLinearInt(row), game.kastid).getLahtrid()) {
                            if (lahter.locY != getModuleFromLinearInt(row)) {
                                if (lahter.getNumbers().remove((Integer) (j + 1))) {
                                    somethingDone = true;
//                                    println("vs4 row", j+1);
                                }
                            }
                        }
                        if (somethingDone){
                            game.messages.add("row " + row + ": "+ (j+1));
                            return true;
                        }
                    }
                }
            }
        }

        //columns
        for (int column = 1; column <= dim2; column++) {
            ArrayList<ArrayList<Integer>> availableSlots = new ArrayList<>();
            ArrayList<Lahter> lahtridInColumn = getColumn(column, game.kastid);
            ArrayList<Integer> olemasNumbrid = new ArrayList<>();
            for (int j = 1; j <= dim2; j++) {
                availableSlots.add(new ArrayList<Integer>());
            }
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtridInColumn.get(j);
                ArrayList<Integer> numbers = lahter.getNumbers();
                if (numbers.size() == 0 && lahter.getValue() == 0) {
                    throw new FillingException();
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
                    throw new FillingException();
                }
                if (arvud.size() >= 2 && arvud.size() <= dim) {
                    HashSet<Integer> module = new HashSet<>();
                    for (int arv : arvud) {
                        module.add(getIntFromLinearInt(arv));
                    }
                    //multiple lines is active
                    if (module.size() == 1) {
                        ArrayList<Integer> moduleList = new ArrayList<Integer>(module);
                        for (Lahter lahter : getKast(getIntFromLinearInt(column), moduleList.get(0), game.kastid).getLahtrid()) {
                            if (lahter.locX != getModuleFromLinearInt(column)) {
                                if (lahter.getNumbers().remove((Integer) (j + 1))) {
                                    somethingDone = true;
//                                    println("vs4 column", j+1);
                                }
                            }
                        }
                        if (somethingDone){
                            game.messages.add("column " + column + ": "+ (j+1));
                            return somethingDone;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean filling3(Game game) throws FillingException {
        boolean somethingDone = false;

        println("logic level 3");

        //naked and hidden pairs, triplets, quarters

        //rows
        for (int row = 1; row <= dim2; row++) {
            ArrayList<Lahter> lahtridInRow = getRow(row, game.kastid);
            ArrayList<Lahter> emptyLahtrid = new ArrayList<>();
            for (Lahter lahter : lahtridInRow) {
                if (lahter.getValue() != 0) {
                    emptyLahtrid.add(lahter);
                }
            }
            if (emptyLahtrid.size()>2){
                for (int i = 2; i < dim2 ; i++){
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
                            for (int usedNumber : numbers){
                                if (lahter.getNumbers().remove((Object)usedNumber)){
                                    somethingDone = true;
                                }
                            }
                        }
                        if (somethingDone){
                            return true;
                        }
                    }

                }
            }
        }

        //columns
        for (int column = 1; column <= dim2; column++) {
            ArrayList<Lahter> lahtridInColumn = getColumn(column, game.kastid);
            ArrayList<Lahter> emptyLahtrid = new ArrayList<>();
            for (Lahter lahter : lahtridInColumn) {
                if (lahter.getValue() != 0) {
                    emptyLahtrid.add(lahter);
                }
            }
            if (emptyLahtrid.size()>2){
                for (int i = 2; i < dim2 ; i++){
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
                            for (int usedNumber : numbers){
                                if (lahter.getNumbers().remove((Object)usedNumber)){
                                    somethingDone = true;
                                }
                            }
                        }
                        if (somethingDone){
                            return true;
                        }
                    }
                }
            }
        }

        //box
        for (Kast kast : game.kastid) {
            ArrayList<Lahter> lahtridInBox = arrayToArrayList(kast.getLahtrid());
            ArrayList<Lahter> emptyLahtrid = new ArrayList<>();
            for (Lahter lahter : lahtridInBox) {
                if (lahter.getValue() != 0) {
                    emptyLahtrid.add(lahter);
                }
            }
            if (emptyLahtrid.size()>2) {
                for (int i = 2; i < dim2; i++) {
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
                                if (lahter.getNumbers().remove((Object) usedNumber)) {
                                    somethingDone = true;
                                }
                            }
                        }
                        if (somethingDone) {
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
        int[] XY = getXYFromLocs(new int[][]{{antudLahter.kast.locX, antudLahter.kast.locY},{antudLahter.locX, antudLahter.locY}});
        int x = XY[0];
        int y = XY[1];

        int value = antudLahter.getValue();

        ArrayList<Lahter> nextLahtrid = new ArrayList<>();

        if (showXY) game.messages.add("x:  " + x + " y:  " + y + " value:  " +  value + " " +  message);

        //remove possible numbers in the same row, column and the 3x3 box

        //the same 3x3 box
        for (Lahter lahter : getLahter(x, y, game.kastid).kast.getLahtrid()) {
            lahter.getNumbers().remove((Integer) value);
            if (findNextOnes){
                if (lahter.getNumbers().size() == 1 && lahter.getValue() == 0) {
                    lahter.setMessage(" (box " + y + ", only choice " + lahter.getNumbers().get(0) + ")");
                    lahter.setValue(lahter.getNumbers().get(0));
                    nextLahtrid.add(lahter);
                }
            }
        }

        //same row
        for (Lahter lahter : getRow(y, game.kastid)) {
            lahter.getNumbers().remove((Integer) value);
            if (findNextOnes) {
                if (lahter.getNumbers().size() == 1 && lahter.getValue() == 0) {
                    lahter.setMessage(" (row " + y + ", only choice " + lahter.getNumbers().get(0) + ")");
                    lahter.setValue(lahter.getNumbers().get(0));
                    nextLahtrid.add(lahter);
                }
            }
        }
        //same column
        for (Lahter lahter : getColumn(x, game.kastid)) {
            lahter.getNumbers().remove((Integer) value);
            if (findNextOnes) {
                if (lahter.getNumbers().size() == 1 && lahter.getValue() == 0) {
                    lahter.setMessage(" (column " + x + ", only choice " + lahter.getNumbers().get(0) + ")");
                    lahter.setValue(lahter.getNumbers().get(0));
                    nextLahtrid.add(lahter);
                }
            }
        }

        for (Lahter lahter : nextLahtrid){
            afterNumber(lahter, game, true, lahter.getMessage(), findNextOnes);
        }
    }


    public static Game copyGame(Game game) {
        Kast[] clone = newKastid();

        for (int i = 0; i < dim2; i++) {
            for (int j = 0; j < dim2; j++) {
                clone[i].getLahtrid()[j].setValue(game.kastid[i].getLahtrid()[j].getValue());
                clone[i].getLahtrid()[j].setNumbers(copyList(game.kastid[i].getLahtrid()[j].getNumbers()));
            }
        }

        return new Game(clone, game.messages);
    }

    static ArrayList copyList(ArrayList<Integer> in) {
        ArrayList out = new ArrayList();
        for (int a : in) {
            out.add(a);
        }
        return out;
    }


    private static void printGrid(Kast[] difKastid) {
        for (int kastY = 1; kastY <= dim; kastY++) {
            for (int lahterY = 1; lahterY <= dim; lahterY++) {
                for (int kastX = 1; kastX <= dim; kastX++) {
                    for (int lahterX = 1; lahterX <= dim; lahterX++) {
                        int value = 0;
                        value = getKast(kastX, kastY, difKastid).getLahter(lahterX, lahterY).getValue();
                        print(value == 0 ? "*" : (value > dim2 ? String.valueOf((char) (value - dim2 - 1 + 97)) : Integer.toString(value)));
                    }
                    print(" ");
                }
                newLine();
            }
            newLine();
        }
        newLine();
        newLine();
    }

    static void printSidewaysGrid(Game game1, Game game2) {
        for (int kastY = 1; kastY <= dim; kastY++) {
            for (int lahterY = 1; lahterY <= dim; lahterY++) {
                for (int kastX = 1; kastX <= dim; kastX++) {
                    for (int lahterX = 1; lahterX <= dim; lahterX++) {
                        int value = 0;
                        value = getKast(kastX, kastY, game1.kastid).getLahter(lahterX, lahterY).getValue();
                        print(value == 0 ? "*" : (value > 9 ? String.valueOf((char) (value - dim2 - 1 + 97 + 7)) : Integer.toString(value)));
                    }
                    print(" ");
                }
                print("        ");
                for (int kastX = 1; kastX <= dim; kastX++) {
                    for (int lahterX = 1; lahterX <= dim; lahterX++) {
                        int value = 0;
                        value = getKast(kastX, kastY, game2.kastid).getLahter(lahterX, lahterY).getValue();
                        print(value == 0 ? "*" : (value > 9 ? String.valueOf((char) (value - dim2 - 1 + 97 + 7)) : Integer.toString(value)));
                    }
                    print(" ");
                }
                newLine();
            }
            newLine();
        }
    }

    static void printSolutions(){
        for (int i = 0;i < solutionsAmount; i++){
            for (String message : solutions.get(i).messages){
                println(message);
            }
            newLine();
            printSidewaysGrid(puzzles.get(i), solutions.get(i));
            newLine();
        }
    }

    /**
     *
     * @param game
     * @param sis
     */
    static void loadFromString(Game game, String sis) {

        HashSet<Character> characters = new HashSet<>();
        characters.add(' ');
        characters.add('*');
        characters.add('0');

        for (int i = 1; i <= dim2; i++) {
            ArrayList<Lahter> lahtrid = getRow(i, game.kastid);
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = lahtrid.get(j);
                String sisString = String.valueOf(sis.charAt((i-1)*dim2+j));
//                println(sisString);
                //print(sisString+" ");
                if (characters.contains(sis.charAt((i-1)*dim2+j))) {//tühi Lahter
                    lahter.setValue(0);
                    lahter.setNumbers(getNumberList());
                } else {//lahtris on number
                    int intSis = 0;
                    try {
                        intSis = Integer.parseInt(sisString);
                        lahter.setValue(intSis);
                    } catch (NumberFormatException e) {
                        char ch = sisString.toLowerCase().charAt(0);
                        intSis = dim2 + (int) ch - 103;
                        println(ch, intSis);
                        lahter.setValue(intSis);
                    }
                    lahter.setNumbers(new ArrayList());
                }
            }
        }
    }

    /**
     *
     * @param game
     * @param file
     * @param a
     */
    static void loadFromFile(Game game, File file, int a) {

        ArrayList<String> sudokusString = new ArrayList<>();
        try {
            sudokusString = readTextFromFile(file);
        }catch (FileNotFoundException e){
            println("File not found");
            e.printStackTrace();
        }

        for (int i = 1; i <= dim2; i++) {
            ArrayList<Lahter> rows = getRow(i, game.kastid);
            String infoRow = sudokusString.get(i + 10 * a);
            for (int j = 0; j < dim2; j++) {
                Lahter lahter = rows.get(j);
                String sis = String.valueOf(infoRow.charAt(j));
                //println(sisString+" ");
                if (sis.contains("0")) {//tühi Lahter
                    lahter.setNumbers(getNumberList());
                } else {//lahtris on number
                    int sisInt = 0;
                    try {
                        sisInt = Integer.parseInt(sis);
                        lahter.setValue(sisInt);
                    } catch (NumberFormatException e) {
                        sisInt = dim2 + (int) sis.charAt(0) - 96;
                        lahter.setValue(sisInt);
                    }
                    lahter.setNumbers(new ArrayList());
                }
            }
        }
    }

    /**
     * @param file for example: /src/50sudokus.txt
     * @return string of contents
     */
    static ArrayList<String> readTextFromFile(File file) throws FileNotFoundException{
        try {
            Scanner scan = new Scanner(file);
            ArrayList<String> lines = new ArrayList<>();
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine());
            }
//            println("File read successfully!");
            return lines;
        }catch (FileNotFoundException e){
            throw e;
        }
    }


    static Kast[] newKastid() {
        Kast[] kastid = new Kast[dim2];
        for (int i = 1; i <= dim2; i++) {//dim2 korda tee Kast
            kastid[i - 1] = genKast(getXYFromBoxInt(i)[0], getXYFromBoxInt(i)[1]);
        }
        return kastid;
    }

    /**
     * @param locX 1-3
     * @param locY 1-3
     * @return Kast
     */
    static Kast genKast(int locX, int locY) {
        Lahter[] lahtrid = new Lahter[dim2];
        for (int i = 0; i < dim2; i++) {//dim2 korda tee Lahter
            lahtrid[i] = new Lahter(i % dim + 1, i / dim + 1, getNumberList(), 0,null, null);
        }
        Kast kast = new Kast(locX, locY, lahtrid, dim);
        for (Lahter lahter : lahtrid) {
            lahter.kast = kast;
        }
        return kast;
    }

    static ArrayList<Integer> getNumberList() {
        ArrayList<Integer> result = new ArrayList<Integer>(dim2);
        for (int i = 1; i <= dim2; i++) {
            result.add(i);
        }
        return result;
    }

    private static ArrayList<Lahter> getRow(int y, Kast[] difKastid) {
        ArrayList<Lahter> lahtrid = new ArrayList<>();
        for (Kast kast : difKastid) {
            for (Lahter lahter : kast.getLahtrid()) {
                if (kast.locY == (y - 1) / dim + 1 && lahter.locY == (y - 1) % dim + 1) {
                    lahtrid.add(lahter);
                }
            }
        }
        return lahtrid;
    }

    static ArrayList<Lahter> getColumn(int x, Kast[] difKastid) {
        ArrayList<Lahter> lahtrid = new ArrayList<>();
        for (Kast kast : difKastid) {
            for (Lahter lahter : kast.getLahtrid()) {
                if (kast.locX == (x - 1) / dim + 1 && lahter.locX == (x - 1) % dim + 1) {
                    lahtrid.add(lahter);
                }
            }
        }
        return lahtrid;
    }

    static boolean checkFilled(Game game) {
        for (Kast kast : game.kastid) {
            for (Lahter lahter : kast.getLahtrid()) {
                if (lahter.getValue() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean checkAll(Kast[] difKastid) {
        if (!checkAllRows(difKastid)) {
            return false;
        }
        if (!checkAllColumns(difKastid)) {
            return false;
        }
        return checkAllBoxes(difKastid);
    }

    private static boolean checkAllRows(Kast[] difKastid) {
        for (int i = 1; i <= dim2; i++) {
            if (!checkRow(i, difKastid)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkAllColumns(Kast[] difKastid) {
        for (int i = 1; i <= dim2; i++) {
            if (!checkColumn(i, difKastid)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkAllBoxes(Kast[] difKastid) {
        for (int i = 1; i <= dim2; i++) {
            if (!checkBox(i, difKastid)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkRow(int ind, Kast[] difKastid) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (Lahter lahter : getRow(ind, difKastid)) {
            if (lahter.getValue() != 0) {
                if (numbers.contains(lahter.getValue())) {
                    return false;
                } else {
                    numbers.add(lahter.getValue());
                }
            }
        }
        return true;
    }

    private static boolean checkColumn(int ind, Kast[] difKastid) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (Lahter lahter : getColumn(ind, difKastid)) {
            if (lahter.getValue() != 0) {
                if (numbers.contains(lahter.getValue())) {
                    return false;
                } else {
                    numbers.add(lahter.getValue());
                }
            }
        }
        return true;
    }

    private static boolean checkBox(int ind, Kast[] difKastid) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (Lahter lahter : getKast(getXYFromBoxInt(ind), difKastid).getLahtrid()) {
            if (lahter.getValue() != 0) {
                if (numbers.contains(lahter.getValue())) {
                    return false;
                } else {
                    numbers.add(lahter.getValue());
                }
            }
        }
        return true;
    }

    static ArrayList<int[]> combinations(int sample, int maxLen) {
        ArrayList results = new ArrayList();
        return _combsFindNext(new ArrayList(), -1, results, sample, -1, maxLen);
    }

    private static ArrayList _combsFindNext(ArrayList last, int lastInt, ArrayList results, int sample, int level, int maxLevel) {
        level++;
        if (level < maxLevel) {
            for (int i = lastInt + 1; i < sample; i++) {
                ArrayList send = copyList(last);
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
     * @return Kast
     */
    static Kast getKast(int locX, int locY, Kast[] difKastid) {
        return difKastid[(locY - 1) * dim + locX - 1];
    }

    static Kast getKast(int[] loc, Kast[] difKastid) {
        return difKastid[(loc[1] - 1) * dim + loc[0] - 1];
    }

    static Kast getKast(int ind, Kast[] difKastid) {return difKastid[ind-1];}

    /**
     * @param x 1-9
     * @param y 1-9
     * @return
     */
    static Lahter getLahter(int x, int y, Kast[] difKastid) {
        int[][] locs = getLocsFromXY(x, y);
        int[] locsKast = locs[0];
        int[] locsLahter = locs[1];
        return getKast(locsKast, difKastid).getLahter(locsLahter);
    }

    /**
     * @param XY [1-9,1-9]
     * @return lahter
     */
    static Lahter getLahter(int[] XY, Kast[] difKastid) {
        int[][] locs = getLocsFromXY(XY[0], XY[1]);
        int[] locsKast = locs[0];
        int[] locsLahter = locs[1];
        return getKast(locsKast, difKastid).getLahter(locsLahter);
    }

    /**
     * @param locs [[kastX 1-3, kastY 1-3],[lahterX 1-3, lahterY 1-3]]
     * @return
     */
    static Lahter getLahter(int[][] locs, Kast[] difKastid) {
        int[] locsKast = locs[0];
        int[] locsLahter = locs[1];
        return getKast(locsKast, difKastid).getLahter(locsLahter);
    }

    /**
     * @param val 1-9
     * @return x = 1-3, y = 1-3
     */
    static int[] getXYFromBoxInt(int val) {
        int[] retVal = {(val - 1) % dim + 1, (val - 1) / dim + 1};
        return retVal;
    }

    /**
     * @param val 1-9
     *            1 2 3, 4 5 6, 7 8 9
     *            1,     2,     3
     * @return 1-3
     */
    static int getIntFromLinearInt(int val) {
        return (val - 1) / dim + 1;
    }

    /**
     * @param val 1-9
     *            1 2 3, 4 5 6, 7 8 9
     *            1 2 3, 1 2 3, 1 2 3
     * @return
     */
    static int getModuleFromLinearInt(int val) {
        return (val - 1) % dim + 1;
    }

    /**
     *
     * @param locs [x 1-dim, y 1-dim]
     * @return 1-dim2
     */
    static int getIntFromLocalLocs(int[] locs){
        return (locs[1]-1)*dim + locs[0];
    }

    /**
     * @param x 1-9
     * @param y 1-9
     * @return [[kastX 1-3, kastY 1-3],[lahterX 1-3, lahterY 1-3]]
     */
    static int[][] getLocsFromXY(int x, int y) {
        int kastX = (x - 1) / dim + 1;
        int kastY = (y - 1) / dim + 1;
        int lahterX = x - (kastX - 1) * dim;
        int lahterY = y - (kastY - 1) * dim;
        int[][] result = {{kastX, kastY}, {lahterX, lahterY}};
        return result;
    }

    static int[] getXYFromLocs(int[][] locs) {
        int[] kastXY = locs[0];
        int[] lahterXY = locs[1];
        return new int[]{(kastXY[0] - 1) * dim + lahterXY[0], (kastXY[1] - 1) * dim + lahterXY[1]};
    }

    static void print(Object... o) {
        for (Object i : o) {
            System.out.print(i + " ");
        }
    }

    static void println(Object... o) {
        for (Object i : o) {
            System.out.print(i + " ");
        }
        newLine();
    }

    static ArrayList arrayToArrayList(Object[] set) {
        ArrayList list = new ArrayList();
        for (Object o : set) {
            list.add(o);
        }
        return list;
    }

    static ArrayList<Integer> arrayToArrayList(int[] set) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i : set) {
            list.add(i);
        }
        return list;
    }

    static void newLine() {
        System.out.println();
    }

    static void newLine(int a) {
        for (int i = 0; i < a; i++) {
            System.out.println();
        }
    }

    /**
     *
     * @param message information to be written
     * @param resource the file name
     */
    static void write(ArrayList<String> message, String resource) throws FileNotFoundException{
        try {
            PrintWriter writer = new PrintWriter(new File(resource));
            for (String rida : message){
                writer.write(rida+"\n");
            }
        } catch (FileNotFoundException e) {
            throw e;
        }
    }

    /**
     * to return a
     * @param a
     * @param places
     * @return
     */
    static String round(double a, int places){
        return String.format("%."+Integer.toString(places)+"f", a).replace(",",".");
    }
}


      