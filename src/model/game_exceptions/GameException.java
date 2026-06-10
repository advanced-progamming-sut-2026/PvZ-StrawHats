package model.game_exceptions;

public abstract class GameException extends RuntimeException {
    public GameException(String message) {
        super(message);
    }
    public GameException(Throwable cause) {}
}
