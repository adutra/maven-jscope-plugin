/**
 * 
 */
package jscope.parser.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jscope.parser.AbstractParser;
import jscope.parser.FileType;


/**
 * @author Alexandre Dutra
 *
 */
public class XmlParser extends AbstractParser {

    private final static Pattern JSCOPE_BEGIN_LINE =
        Pattern.compile("\\s*<\\!--\\s*" + JSCOPE_MARKER + "\\s+(.+)\\s+BEGIN\\s*-->\\s*");

    private final static Pattern JSCOPE_END_LINE =
        Pattern.compile("\\s*<\\!--\\s*" + JSCOPE_MARKER + "\\s+END\\s*-->\\s*");

    @Override
    protected FileType getFileType() {
        return FileType.XML;
    }

    @Override
    protected boolean isScopeEnd(final String line) {
        return XmlParser.JSCOPE_END_LINE.matcher(line).matches();
    }

    @Override
    protected String detectScopeBegin(final String line) {
        final Matcher matcher = XmlParser.JSCOPE_BEGIN_LINE.matcher(line);
        if(matcher.matches()){
            return matcher.group(1);
        }
        return null;
    }

}
