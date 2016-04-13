package jscope.parser;

import java.io.File;

import jscope.model.Unit;


public class ParseError {

    private File file;

    private int line;

    private Unit unit;

    private String message;


    public ParseError(final File file, final int line, final Unit unit, final String message) {
        super();
        this.file = file;
        this.line = line;
        this.unit = unit;
        this.message = message;
    }


    public File getFile() {
        return this.file;
    }


    public void setFile(final File file) {
        this.file = file;
    }


    public int getLine() {
        return this.line;
    }


    public void setLine(final int line) {
        this.line = line;
    }


    public Unit getUnit() {
        return this.unit;
    }


    public void setUnit(final Unit unit) {
        this.unit = unit;
    }


    public String getMessage() {
        return this.message;
    }


    public void setMessage(final String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ParseError [")
        .append("file=").append(this.file)
        .append(", line=").append(this.line)
        .append(", message=").append(this.message)
        .append("]");
        return builder.toString();
    }


}
