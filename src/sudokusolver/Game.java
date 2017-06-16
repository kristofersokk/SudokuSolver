package sudokusolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    Kast[] kastid;
    ArrayList<String> messages;
    boolean solved = false;

    Game() {
        this.kastid = Loogika.newKastid();
        this.messages = new ArrayList<>();
    }

    Game(Kast[] kastid, ArrayList<String> messages) {
        this.kastid = kastid;
        this.messages = messages;
    }

    public static void solveMultipleFromFile(int level, int start, int stop) {
        File file = new File(MainClass.fileLocation + "level" + level + "-10000.txt");
        String info = "";
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
                if (i % 100 == 0) {
                    Loogika.println(i);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void solveTestString() {
        Game game = new Game();
        game.importFromTestString(MainClass.testString);
        game.solve();
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
    void importFromFile(int level, int index) {
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
    void importFromTestString(String info) {
        Kast[] kastid = this.kastid;
        for (int rida = 1; rida <= Loogika.dim2; rida++) {
            for (int index = 1; index <= Loogika.dim2; index++) {
                Lahter lahter = Loogika.getLahter(index, rida, kastid);
                char ch = info.charAt((rida - 1) * Loogika.dim2 + index - 1);
                if (Character.isDigit(ch) && Integer.parseInt(Character.toString(ch)) > 0) {
                    lahter.setValue(Integer.parseInt(Character.toString(ch)));
                }
            }
        }
    }

    void solve() {
        Loogika.startFilling(this);
    }
}
