package exception;

/**
 *
 * @author Daniele Moro
 */
public class DateConsistencyException extends Exception {

    /**
     * Creates a new instance of <code>DateConsistencyException</code> without
     * detail message.
     */
    public DateConsistencyException() {
    }

    /**
     * Constructs an instance of <code>DateConsistencyException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DateConsistencyException(String msg) {
        super(msg);
    }
}
