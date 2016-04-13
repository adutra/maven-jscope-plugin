package jscope.parser.java;

import jscope.model.Line;
import jscope.parser.Formatter;
import org.apache.commons.lang.StringUtils;

public class JavaFormatter implements Formatter {

    private final static String JSCOPE_COMMENT = "//" + UNSCOPE_MARKER + "//";

    public Line scope(final Line line) {
        if(this.isScoped(line)) {
            return line;
        }
        return new Line(line.getStartLineNumber(), StringUtils.substringAfter(line.getText(), JavaFormatter.JSCOPE_COMMENT));
    }

    public Line unscope(final Line line) {
        if(this.isScoped(line)){
            return new Line(line.getStartLineNumber(), JavaFormatter.JSCOPE_COMMENT + line);
        }
        return line;
    }

    public boolean isScoped(final Line line) {
        return ! line.getText().trim().startsWith(JavaFormatter.JSCOPE_COMMENT);
    }

}

