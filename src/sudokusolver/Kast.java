package sudokusolver;

public class Kast {

    int locX;
    int locY;
    int dim;
    private Lahter[] lahtrid;

    /**
     *
     * @param locX 1-dim
     * @param locY 1-3
     * @param lahtrid [Lahter, Lahter...]
     *               1.rida vasakult paremale, 2. -||-, 3. -||-, ...
     * @param dim the dimensions of kast
     */
    public Kast(int locX, int locY, Lahter[] lahtrid, int dim) {
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

    public Lahter[] getLahtrid() {
        return lahtrid;
    }

    public void setLahtrid(Lahter[] lahtrid) {
        this.lahtrid = lahtrid;
    }
}
