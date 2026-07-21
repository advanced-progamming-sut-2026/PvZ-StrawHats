package model.game_exceptions;

public class GameException extends RuntimeException {
    public GameException(String message) {
        super(message);
    }
    public GameException(Throwable cause) {
        super(cause);
    }
}