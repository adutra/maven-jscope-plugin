package jscope.parser.properties;

import jscope.model.Line;
import jscope.parser.Formatter;
import org.apache.commons.lang.StringUtils;

public class PropertiesFormatter implements Formatter {

    private final static String JSCOPE_COMMENT = "#" + UNSCOPE_MARKER;

    public Line scope(final Line line) {
        if(this.isScoped(line)){
            return line;
        }
        return new Line(line.getStartLineNumber(), StringUtils.substringAfter(line.getText(), JSCOPE_COMMENT));
    }

    public Line unscope(final Line line) {
        if(this.isScoped(line)){
            return new Line(line.getStartLineNumber(), JSCOPE_COMMENT + line);
        }
        return line;
    }

    public boolean isScoped(final Line line) {
        return ! line.getText().trim().startsWith(JSCOPE_COMMENT);
    }

}

