/**
 * 
 */
package jscope.exception;


/**
 * @author Alexandre Dutra
 *
 */
public class ScopeNotFoundException extends JScopeException {

    /**
     * 
     */
    private static final long serialVersionUID = 7969499282962697315L;

    public ScopeNotFoundException() {
        super();
    }

    public ScopeNotFoundException(final String message) {
        super(message);
    }

    public ScopeNotFoundException(final Throwable cause) {
        super(cause);
    }

    public ScopeNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
