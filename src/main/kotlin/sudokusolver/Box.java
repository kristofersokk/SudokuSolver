package sudokusolver;

import java.util.ArrayList;

import static sudokusolver.Game.renderScale;

public class Box {

    int locX;
    int locY;
    private int dim;
    private Lahter[] lahtrid;

    /**
     *
     * @param locX 1-dim
     * @param locY 1-3
     * @param lahtrid [Lahter, Lahter...]
     *               1.rida vasakult paremale, 2. -||-, 3. -||-, ...
     * @param dim the dimensions of box
     */
    Box(int locX, int locY, Lahter[] lahtrid, int dim) {
        this.locX = locX;
        this.locY = locY;
        this.lahtrid = lahtrid;
        this.dim = dim;
    }

    Lahter getLahter(int locX, int locY){
        return lahtrid[(locY-1)*dim+locX-1];
    }

    Lahter getLahter(int[] loc){
        return lahtrid[(loc[1]-1)*dim+loc[0]-1];
    }

    boolean hasValue(int value){
        for (Lahter lah : lahtrid){
            int val = lah.getValue();
            if (val == value){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        ArrayList<String[]> stringid = new ArrayList<>();
        for (Lahter lahter : lahtrid) {
            stringid.add(lahter.toString().split("\n"));
        }
        StringBuilder result = new StringBuilder();
        int lahterX = 1;
        int lahterY = 1;
        int rida = 1;
        while (lahterX <= dim && lahterY <= dim) {
            result.append(stringid.get((lahterY - 1) * dim + lahterX - 1)[rida - 1]);
            lahterX++;
            while (lahterX <= dim) {
                result.append("¦");
                result.append(stringid.get((lahterY - 1) * dim + lahterX - 1)[rida - 1]);
                lahterX++;
            }
            rida++;
            lahterX = 1;
            result.append("\n");
            while (rida <= dim) {
                result.append(stringid.get((lahterY - 1) * dim + lahterX - 1)[rida - 1]);
                lahterX++;
                while (lahterX <= dim) {
                    result.append("¦");
                    result.append(stringid.get((lahterY - 1) * dim + lahterX - 1)[rida - 1]);
                    lahterX++;
                }
                rida++;
                lahterX = 1;
                result.append("\n");
            }
            lahterY++;
            lahterX = 1;
            rida = 1;

            while (lahterY <= dim) {
                result.append(new String(new char[Math.round(dim * renderScale)]).replace("\0", "-"));
                for (int i = 0; i < dim - 1; i++)
                    result.append("+").append(new String(new char[Math.round(dim * renderScale)]).replace("\0", "-"));
                result.append("\n");

                result.append(stringid.get((lahterY - 1) * dim + lahterX - 1)[rida - 1]);
                lahterX++;
                while (lahterX <= dim) {
                    result.append("¦");
                    result.append(stringid.get((lahterY - 1) * dim + lahterX - 1)[rida - 1]);
                    lahterX++;
                }
                rida++;
                lahterX = 1;
                result.append("\n");
                while (rida <= stringid.get(0).length) {
                    result.append(stringid.get((lahterY - 1) * dim + lahterX - 1)[rida - 1]);
                    lahterX++;
                    while (lahterX <= dim) {
                        result.append("¦");
                        result.append(stringid.get((lahterY - 1) * dim + lahterX - 1)[rida - 1]);
                        lahterX++;
                    }
                    rida++;
                    lahterX = 1;
                    result.append("\n");
                }
                lahterY++;
                lahterX = 1;
                rida = 1;
            }
        }
        return result.toString();
    }

    Lahter[] getLahtrid() {
        return lahtrid;
    }

    public void setLahtrid(Lahter[] lahtrid) {
        this.lahtrid = lahtrid;
    }
}
