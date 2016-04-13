/**
 * 
 */
package jscope.parser;

import java.io.File;
import java.io.Reader;

import jscope.exception.ParseException;


/**
 * @author Alexandre Dutra
 *
 */
public interface Parser {

    public final static String JSCOPE_MARKER = "@SCOPE";

    void setLenient(boolean lenient);

    ParseResult parse(final Reader reader, final String encoding) throws ParseException;

    ParseResult parse(final File file, String encoding) throws ParseException;

}
