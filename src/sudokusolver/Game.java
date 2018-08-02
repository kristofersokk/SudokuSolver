package sudokusolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static sudokusolver.MainClass.dim;
import static sudokusolver.MainClass.dim2;

public class Game {

    //1.2 for 9x9
    //2.1 for 16x16
    static final float renderScale = dim2 <= 9 ? 1.1f : 2.1f;
    ArrayList<String> messages;
    boolean solved = false;
    Box[] kastid;

    private Game() {
        this.kastid = Loogika.newKastid();
        this.messages = new ArrayList<>();
    }

    Game(Box[] kastid, ArrayList<String> messages) {
        this.kastid = kastid;
        this.messages = messages;
    }

    public static void solveMultipleFromFile(int level, int start, int stop) {
        File file = new File(MainClass.fileLocation + "level" + level + "-10000.txt");
        String info;
        try {
            Scanner scanner = new Scanner(file);
            int i = 1;
            while (scanner.hasNextLine()) {
                info = scanner.nextLine();
                if (start <= i && i <= stop) {
                    Game game = new Game();
                    game.importFromTestString(info);
                    game.solve();
                }
                i++;
//                if (i % 100 == 0) {
//                    Loogika.println(i);
//                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void solveTestString() {
        Game game = new Game();
        game.importFromTestString(MainClass.testString);
        game.solve();
    }

    static void solveSudokuString(String sudoku) {
        Game game = new Game();
        game.importFromTestString(sudoku);
        game.solve();
    }

    @Override
    public String toString() {
        ArrayList<String[]> stringid = new ArrayList<>();
        for (Box box : kastid) {
            stringid.add(box.toString().split("\n"));
        }
        StringBuilder result = new StringBuilder();
        int boxX = 1;
        int boxY = 1;
        int rida = 1;
        final int ridalen = dim * (dim + 1) - 1;

        result.append("╔").append(new String(new char[Math.round(ridalen * renderScale)]).replace("\0", "═"));
        for (int i = 0; i < dim - 1; i++)
            result.append("╤").append(new String(new char[Math.round(ridalen * renderScale)]).replace("\0", "═"));
        result.append("╗\n");

        while (boxX <= dim && boxY <= dim) {
            result.append("║");
            result.append(stringid.get((boxY - 1) * dim + boxX - 1)[rida - 1]);
            boxX++;
            while (boxX <= dim) {
                result.append("║");
                result.append(stringid.get((boxY - 1) * dim + boxX - 1)[rida - 1]);
                boxX++;
            }
            rida++;
            boxX = 1;
            result.append("║\n");
            while (rida <= ridalen) {
                result.append("║");
                result.append(stringid.get((boxY - 1) * dim + boxX - 1)[rida - 1]);
                boxX++;
                while (boxX <= dim) {
                    result.append("║");
                    result.append(stringid.get((boxY - 1) * dim + boxX - 1)[rida - 1]);
                    boxX++;
                }
                rida++;
                boxX = 1;
                result.append("║\n");
            }
            boxY++;
            boxX = 1;
            rida = 1;

            while (boxY <= dim) {
                result.append("║");
                result.append(new String(new char[Math.round(ridalen * renderScale)]).replace("\0", "═"));
                for (int i = 0; i < dim - 1; i++)
                    result.append("+").append(new String(new char[Math.round(ridalen * renderScale)]).replace("\0", "═"));
                result.append("║\n");

                result.append("║");
                result.append(stringid.get((boxY - 1) * dim + boxX - 1)[rida - 1]);
                boxX++;
                while (boxX <= dim) {
                    result.append("║");
                    result.append(stringid.get((boxY - 1) * dim + boxX - 1)[rida - 1]);
                    boxX++;
                }
                rida++;
                boxX = 1;
                result.append("║\n");
                while (rida <= ridalen) {
                    result.append("║");
                    result.append(stringid.get((boxY - 1) * dim + boxX - 1)[rida - 1]);
                    boxX++;
                    while (boxX <= dim) {
                        result.append("║");
                        result.append(stringid.get((boxY - 1) * dim + boxX - 1)[rida - 1]);
                        boxX++;
                    }
                    rida++;
                    boxX = 1;
                    result.append("║\n");
                }
                boxY++;
                boxX = 1;
                rida = 1;
            }
        }

        result.append("╚").append(new String(new char[Math.round(ridalen * renderScale)]).replace("\0", "═"));
        for (int i = 0; i < dim - 1; i++)
            result.append("╧").append(new String(new char[Math.round(ridalen * renderScale)]).replace("\0", "═"));
        result.append("╝");

        return result.toString();
    }

    public static void solveSudokuFromFile(int level, int index) {
        Game game = new Game();
        game.importFromFile(level, index);
        game.solve();
    }

    /**
     *
     * @param level level 1-7
     * @param index index 1-10000
     */
    private void importFromFile(int level, int index) {
        File file = new File(MainClass.fileLocation + "level" + level + "-10000.txt");
        String info = "";
        try {
            Scanner scanner = new Scanner(file);
            int i = 1;
            while (scanner.hasNextLine()) {
                info = scanner.nextLine();
                if (i == index) {
                    break;
                } else {
                    i++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        importFromTestString(info);
    }

    /**
     * @param info line by line
     */
    private void importFromTestString(String info) {
        for (int rida = 1; rida <= MainClass.dim2; rida++) {
            for (int index = 1; index <= MainClass.dim2; index++) {
                Lahter lahter = Loogika.getLahter(index, rida, this);
                char ch = info.charAt((rida - 1) * MainClass.dim2 + index - 1);
                if (Character.isDigit(ch) && Integer.parseInt(Character.toString(ch)) > 0) {
                    lahter.setValue(Integer.parseInt(Character.toString(ch)));
                } else {
                    if (Character.isAlphabetic(ch)) {
                        ch = Character.toLowerCase(ch);
                        int ascii = (int) ch;
                        ascii -= 87;
//                        println(ch, ascii);
                        // a,  b,  c,  d...
                        //10, 11, 12, 13...
                        lahter.setValue(ascii);
                    }
                }
            }
        }
    }

    private void solve() {
        Loogika.startFilling(this);
    }
}
