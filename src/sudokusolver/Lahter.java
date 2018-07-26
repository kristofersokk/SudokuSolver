package sudokusolver;

import com.sun.istack.internal.Nullable;

import java.util.HashSet;

public class Lahter {

    int locX;
    int locY;
    Box box;
    private String message;
    private HashSet<Integer> numbers;
    private int value;

    /**
     *
     * @param locX 1-dim
     * @param locY 1-dim
     * @param numbers [1-dim2] või [0],
     *                1.rida vasakult paremale, 2. -||-, 3. -||-, ...
     * @param value null või väärtus
     */
    Lahter(int locX, int locY, HashSet numbers, @Nullable int value, Box box, @Nullable String message) {
        this.numbers = numbers;
        this.value = value;
        this.locX = locX;
        this.locY = locY;
        this.box = box;
        this.message = message;
    }


    HashSet<Integer> getNumbers() {
        return numbers;
    }

    void setNumbers(HashSet numbers) {
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        int dim = Loogika.dim;
        int dim2 = Loogika.dim2;
        StringBuilder result = new StringBuilder();
        if (value == 0) {
            for (int arv = 1; arv <= dim2; arv++) {
                result.append(numbers.contains(arv) ? ((arv <= 9) ? " " + arv : Integer.toString(arv)) : "  ");
                if (arv % dim == 0)
                    result.append("\n");
            }
        } else {
            for (int arv = 1; arv <= dim2; arv++) {
                result.append("  ");
                if (arv % dim == 0)
                    result.append("\n");
            }
        }
        return result.toString();
    }

    int[] getXYOnBoard() {
        int dim = Loogika.dim;
        return new int[]{(box.locX - 1) * dim + locX, (box.locY - 1) * dim + locY};
    }

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
        numbers = new HashSet<>();
    }

    String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
