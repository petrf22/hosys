package cz.pfservis.hosys.exception;

/**
 * Created by petr on 8.10.16.
 */

public class NotSetCookieException extends Exception {
    public NotSetCookieException() {
    }

    public NotSetCookieException(String message) {
        super(message);
    }

    public NotSetCookieException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSetCookieException(Throwable cause) {
        super(cause);
    }
}
