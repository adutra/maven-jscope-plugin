/**
 * 
 */
package jscope.exception;


/**
 * @author Alexandre Dutra
 *
 */
public class TaskErrorException extends JScopeException {

    /**
     * 
     */
    private static final long serialVersionUID = -724185986938357089L;

    /**
     * 
     */
    public TaskErrorException() {
    }

    /**
     * @param message
     * @param cause
     */
    public TaskErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public TaskErrorException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public TaskErrorException(final Throwable cause) {
        super(cause);
    }

}
