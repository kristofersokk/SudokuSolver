import java.io.File;
import java.util.ArrayList;

/**
 * Created by Kristofer on 02/06/2017.
 */
public class Game {

    public Kast[] kastid;
    public ArrayList<String> messages;

    public Game() {
        this.kastid = Loogika.newKastid();
        this.messages = new ArrayList<>();
    }

    public Game(Kast[] kastid, ArrayList<String> messages) {
        this.kastid = kastid;
        this.messages = messages;
    }

    public void importInfo(String info){
        Loogika.loadFromString(this, info);
    }

    /**
     *
     * @param file
     * @param index 1-...
     */
    public void importInfo(File file, int index){
        Loogika.loadFromFile(this, file, index-1);
    }

    public void solve(){
        Loogika.startFilling(this);
    }
}
