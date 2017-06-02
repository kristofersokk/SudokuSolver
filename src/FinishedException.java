/**
 * Created by Kristofer-PC2 on 30/08/2016.
 */
public class FinishedException extends Exception {

    public Game game;
    public boolean filled;

    public FinishedException(Game game, boolean filled) {
        this.game = game;
        this.filled = filled;
    }

    public FinishedException(String message, Game game) {
        super(message);
        this.game = game;
    }

    public FinishedException(String message, Throwable cause, Game game) {
        super(message, cause);
        this.game = game;
    }

    public FinishedException(Throwable cause, Game game) {
        super(cause);
        this.game = game;
    }
}
