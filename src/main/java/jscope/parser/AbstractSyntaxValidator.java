package jscope.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jscope.exception.ParseException;
import jscope.model.Unit;


public abstract class AbstractSyntaxValidator implements SyntaxValidator {

    protected boolean lenient = false;

    protected final List<ParseError> errors = new ArrayList<ParseError>();

    public void setLenient(final boolean lenient) {
        this.lenient = lenient;
    }

    protected void handleError(final Unit unit, final String message, final Throwable cause) throws ParseException {
        this.handleError(unit, unit.getFile(), unit.getStartLineNumber(), message, cause);
    }

    protected void handleError(final Unit unit, final File file, final int line, final String message, final Throwable cause) throws ParseException {
        final ParseError error = new ParseError(file, line, unit, message);
        if(this.lenient) {
            this.errors.add(error);
        } else {
            if(cause == null) {
                throw new ParseException(error);
            }
            throw new ParseException(error, cause);
        }
    }

}
