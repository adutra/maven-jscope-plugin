/**
 * 
 */
package jscope.exception;


/**
 * @author Alexandre Dutra
 *
 */
public class JScopeException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4857680192584779952L;

    public JScopeException() {
        super();
    }

    public JScopeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JScopeException(final String message) {
        super(message);
    }

    public JScopeException(final Throwable cause) {
        super(cause);
    }

}
