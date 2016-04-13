/**
 * 
 */
package jscope.exception;


/**
 * @author Alexandre Dutra
 *
 */
public class SyntaxValidationException extends JScopeException {

    /**
     * 
     */
    private static final long serialVersionUID = 576427314912118308L;

    /**
     * 
     */
    public SyntaxValidationException() {
    }

    /**
     * @param message
     */
    public SyntaxValidationException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public SyntaxValidationException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public SyntaxValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
