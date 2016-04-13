/**
 * 
 */
package jscope.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jscope.model.FileUnit;


/**
 * @author Alexandre Dutra
 *
 */
public class ParseResult {

    private final FileUnit fileUnit;

    private final List<ParseError> errors = new ArrayList<ParseError>();

    public ParseResult(final FileUnit fileUnit) {
        this.fileUnit = fileUnit;
    }

    public FileUnit getFileUnit() {
        return this.fileUnit;
    }

    public List<ParseError> getErrors() {
        Collections.sort(this.errors, new Comparator<ParseError>() {
            public int compare(final ParseError o1, final ParseError o2) {
                return Integer.valueOf(o1.getLine()).compareTo(o2.getLine());
            }
        });
        return this.errors;
    }

    public void addParseError(final ParseError o) {
        this.errors.add(o);
    }

    public void addParseErrors(final Collection<ParseError> errors) {
        this.errors.addAll(errors);
    }

    public boolean isSuccessful() {
        return this.errors.isEmpty();
    }

}
