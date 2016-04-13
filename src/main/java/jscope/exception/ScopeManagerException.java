/**
 * 
 */
package jscope.exception;


/**
 * @author Alexandre Dutra
 *
 */
public class ScopeManagerException extends JScopeException {

    /**
     * 
     */
    private static final long serialVersionUID = 7969499282962697315L;

    public ScopeManagerException() {
        super();
    }

    public ScopeManagerException(final String message) {
        super(message);
    }

    public ScopeManagerException(final Throwable cause) {
        super(cause);
    }

    public ScopeManagerException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
