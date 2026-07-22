package model.game_exceptions;

public class GameException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }
    public GameException(Throwable cause) {
        super(cause);
    }
}
