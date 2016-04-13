package jscope.parser;

import jscope.model.Line;


public interface Formatter {

    public static final String UNSCOPE_MARKER = "@UNSCOPED@";

    Line scope(final Line line);

    Line unscope(final Line line);

    boolean isScoped(Line line);
}

