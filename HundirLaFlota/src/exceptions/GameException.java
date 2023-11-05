package exceptions;

public class GameException extends Exception {
    private String message = "";
    private final Throwable cause = null;
    public GameException() {}
    public GameException(String message){ this.message = message; }
    public GameException(String message, Throwable cause){
        super(message, cause);
    }
    public GameException(Throwable cause){ super(cause); }
    public GameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    public String getMessage() {
        return this.message;
    }
}
