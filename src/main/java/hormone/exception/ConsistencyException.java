package hormone.exception;

public class ConsistencyException extends ORMException {
    public ConsistencyException() {
    }

    public ConsistencyException(String message) {
        super(message);
    }

    public ConsistencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsistencyException(Throwable cause) {
        super(cause);
    }

    public ConsistencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
