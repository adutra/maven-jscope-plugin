/**
 * 
 */
package jscope.parser.java;

import jscope.parser.AbstractParser;
import jscope.parser.FileType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Alexandre Dutra
 *
 */
public class JavaParser extends AbstractParser {

    private final static Pattern JSCOPE_BEGIN_LINE =
        Pattern.compile("\\s*//\\s*" + JSCOPE_MARKER + "\\s+(.+)\\s+BEGIN\\s*");

    private final static Pattern JSCOPE_END_LINE =
        Pattern.compile("\\s*//\\s*" + JSCOPE_MARKER + "\\s+END\\s*");

    @Override
    protected FileType getFileType() {
        return FileType.JAVA;
    }

    @Override
    protected boolean isScopeEnd(final String line) {
        return JavaParser.JSCOPE_END_LINE.matcher(line).matches();
    }

    @Override
    protected String detectScopeBegin(final String line) {
        final Matcher matcher = JavaParser.JSCOPE_BEGIN_LINE.matcher(line);
        if(matcher.matches()){
            return matcher.group(1);
        }
        return null;
    }

}
