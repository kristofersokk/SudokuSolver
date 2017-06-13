package sudokusolver;

import java.io.File;
import java.util.ArrayList;

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

    public void importInfo(int stringIndex) {
        Loogika.loadFromString(this, MainClass.testStrings.get(stringIndex - 1));
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
