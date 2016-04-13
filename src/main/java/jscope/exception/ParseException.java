package jscope.exception;

import jscope.parser.ParseError;


public class ParseException extends JScopeException {

    /**
     * 
     */
    private static final long serialVersionUID = -8166734289357069416L;

    private final ParseError error;

    public ParseException(final ParseError error, final Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }

    public ParseException(final ParseError error) {
        super(error.getMessage());
        this.error = error;
    }

    public ParseError getError() {
        return this.error;
    }

    @Override
    public String getLocalizedMessage() {
        return
        new StringBuilder().
        append(super.getLocalizedMessage()).
        append("\nFile: ").
        append(this.error.getFile()).
        append("\nLine: ").
        append(this.error.getLine()).
        toString();
    }

}
