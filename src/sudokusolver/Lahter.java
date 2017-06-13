package sudokusolver;

import com.sun.istack.internal.Nullable;

import java.util.ArrayList;

public class Lahter {

    int locX;
    int locY;
    Kast kast;
    String message;
    private ArrayList numbers;
    private int value;

    /**
     *
     * @param locX 1-dim
     * @param locY 1-dim
     * @param numbers [1-dim2] või [0],
     *                1.rida vasakult paremale, 2. -||-, 3. -||-, ...
     * @param value null või väärtus
     */
    public Lahter(int locX, int locY, ArrayList numbers, @Nullable int value, Kast kast, @Nullable String message) {
        this.numbers = numbers;
        this.value = value;
        this.locX = locX;
        this.locY = locY;
        this.kast = kast;
        this.message = message;
    }


    public ArrayList<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(ArrayList numbers) {
        this.numbers = numbers;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        numbers = new ArrayList();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
