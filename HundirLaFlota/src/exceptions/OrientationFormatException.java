package exceptions;

public class OrientationFormatException extends CommandParseException {
    public OrientationFormatException() { super(); }
    public OrientationFormatException(String message){ super(message); }
    public OrientationFormatException(String message, Throwable cause){ super(message, cause); }
    public OrientationFormatException(Throwable cause){ super(cause); }
    public OrientationFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
