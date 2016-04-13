/**
 * 
 */
package jscope.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import jscope.exception.ParseException;
import jscope.model.FileUnit;
import jscope.model.Line;
import jscope.model.ScopeUnit;
import jscope.model.ScopeUnitBeginLine;
import jscope.model.ScopeUnitEndLine;
import jscope.model.Unit;
import jscope.model.UnitContainer;


/**
 * @author Alexandre Dutra
 *
 */
public abstract class AbstractParser implements Parser {

    private boolean lenient = false;

    public void setLenient(final boolean lenient) {
        this.lenient = lenient;
    }

    public ParseResult parse(final File file, final String encoding) throws ParseException {
        LineNumberReader br;
        try {
            br = new LineNumberReader(new InputStreamReader(new FileInputStream(file), encoding));
        } catch (final FileNotFoundException e) {
            final ParseError error = new ParseError(file, -1, null, "File does not exist: " + file);
            throw new ParseException(error, e);
        } catch (final UnsupportedEncodingException e) {
            final ParseError error = new ParseError(file, -1, null, "Unsupported encoding: " + encoding);
            throw new ParseException(error, e);
        }
        return this.parseInternal(file, encoding, br);
    }

    public ParseResult parse(final Reader reader, final String encoding) throws ParseException {
        final LineNumberReader br = new LineNumberReader(reader);
        return this.parseInternal(null, encoding, br);
    }

    private ParseResult parseInternal(final File file, final String encoding, final LineNumberReader br) throws ParseException {

        final FileUnit fileUnit = new FileUnit(file, this.getFileType(), encoding);
        final ParseResult result = new ParseResult(fileUnit);

        String line = null;
        UnitContainer container = fileUnit;

        try {
            while((line = br.readLine()) != null) {

                if(this.isScopeLine(line)) {

                    final String expression = this.detectScopeBegin(line);

                    if(expression != null) {

                        final ScopeUnit block = this.newScopeBlock(br.getLineNumber(), line, expression);
                        container.addUnit(block);
                        container = block;

                    } else if(this.isScopeEnd(line)) {

                        if(container instanceof ScopeUnit) {
                            container = this.closeScopeBlock((ScopeUnit) container, br.getLineNumber(), line);
                        } else {
                            this.handleError(result, file, br.getLineNumber(), container, "Found end scope line without begin: " + line);
                        }

                    } else {
                        this.handleError(result, file, br.getLineNumber(), container, "Unrecognized scope line: " + line);
                    }

                } else {

                    final Line lineUnit = this.newLine(line, br.getLineNumber());
                    container.addUnit(lineUnit);

                }
            }

        } catch (final IOException e) {
            this.handleError(result, file, br.getLineNumber(), container, "Unexpected IO exception: " + e.getLocalizedMessage() + ", last line read was: " + line);
        }

        return result;
    }

    protected abstract FileType getFileType();

    protected abstract boolean isScopeEnd(final String line);

    protected abstract String detectScopeBegin(final String line);

    protected boolean isScopeLine(final String line) {
        return line.contains(JSCOPE_MARKER);
    }

    protected void handleError(final ParseResult result, final File file, final int number, final Unit unit, final String message) throws ParseException {
        final ParseError error = new ParseError(file, number, unit, message);
        if(this.lenient) {
            result.addParseError(error);
        } else {
            throw new ParseException(error);
        }
    }

    protected ScopeUnit newScopeBlock(final int number, final String line, final String expression) {
        final ScopeUnitBeginLine begin = new ScopeUnitBeginLine(number, line, expression);
        final ScopeUnit block = new ScopeUnit(number);
        block.setBegin(begin);
        return block;
    }

    protected UnitContainer closeScopeBlock(final ScopeUnit block, final int number, final String line) {
        final ScopeUnitEndLine end = new ScopeUnitEndLine(number, line);
        block.setEnd(end);
        return (UnitContainer) block.getParent();
    }

    protected Line newLine(final String line, final int number) {
        final Line lineUnit = new Line(number, line);
        return lineUnit;
    }

    protected SyntaxValidator newUnitSyntaxValidator() {
        final SyntaxValidator syntaxValidator = this.getFileType().newSyntaxValidator();
        syntaxValidator.setLenient(this.lenient);
        return syntaxValidator;
    }

}
