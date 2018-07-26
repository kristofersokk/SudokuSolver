package sudokusolver;

/**
 * Created by Kristofer-PC2 on 28/08/2016.
 */
public class FillingException extends Exception {

    Game game;

    FillingException(String message, Game game) {
        super(message);
        this.game = game;
    }

    public FillingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FillingException(Throwable cause) {
        super(cause);
    }


}
