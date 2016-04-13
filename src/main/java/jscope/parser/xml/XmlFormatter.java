package jscope.parser.xml;

import jscope.model.Line;
import jscope.parser.Formatter;
import org.apache.commons.lang.StringUtils;

public class XmlFormatter implements Formatter {

    private final static String COMMENT_BEGIN = "<!--";

    private final static String JSCOPE_COMMENT_BEGIN = XmlFormatter.COMMENT_BEGIN + UNSCOPE_MARKER;

    private final static String COMMENT_END = "-->";

    private final static String REPLACED_COMMENT_BEGIN = "@COMMENTBEGIN@";

    private final static String REPLACED_COMMENT_END = "@COMMENTEND@";

    public Line scope(final Line line) {
        if(this.isScoped(line)){
            return line;
        }
        String text = line.getText();
        text = StringUtils.substringBetween(
            text,
            XmlFormatter.JSCOPE_COMMENT_BEGIN,
            XmlFormatter.COMMENT_END);
        text = text.replace(XmlFormatter.REPLACED_COMMENT_BEGIN, XmlFormatter.COMMENT_BEGIN);
        text = text.replace(XmlFormatter.REPLACED_COMMENT_END, XmlFormatter.COMMENT_END);
        return new Line(
            line.getStartLineNumber(),
            text);
    }

    public Line unscope(final Line line) {
        if(this.isScoped(line)){
            String text = line.getText();
            text = text.replace(XmlFormatter.COMMENT_BEGIN, XmlFormatter.REPLACED_COMMENT_BEGIN);
            text = text.replace(XmlFormatter.COMMENT_END, XmlFormatter.REPLACED_COMMENT_END);
            text = XmlFormatter.JSCOPE_COMMENT_BEGIN + text + XmlFormatter.COMMENT_END;
            return new Line(line.getStartLineNumber(), text);
        }
        return line;
    }

    public boolean isScoped(final Line line) {
        return ! line.getText().trim().startsWith(XmlFormatter.JSCOPE_COMMENT_BEGIN);
    }

}

