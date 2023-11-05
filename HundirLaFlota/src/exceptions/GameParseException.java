package exceptions;

public class GameParseException extends GameException {
    public GameParseException() { super(); }
    public GameParseException(String message){ super(message); }
    public GameParseException(String message, Throwable cause){ super(message, cause); }
    public GameParseException(Throwable cause){ super(cause); }
    public GameParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}