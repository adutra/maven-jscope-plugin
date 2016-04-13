/**
 * 
 */
package jscope.exception;


/**
 * @author Alexandre Dutra
 *
 */
public class TaskFailedException extends JScopeException {

    /**
     * 
     */
    private static final long serialVersionUID = -724185986938357089L;

    /**
     * 
     */
    public TaskFailedException() {
    }

    /**
     * @param message
     * @param cause
     */
    public TaskFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public TaskFailedException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public TaskFailedException(final Throwable cause) {
        super(cause);
    }

}
