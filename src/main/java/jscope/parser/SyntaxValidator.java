package jscope.parser;

import java.util.List;

import jscope.exception.ParseException;
import jscope.model.FileUnit;


public interface SyntaxValidator {

    void setLenient(final boolean lenient);

    List<ParseError> validate(FileUnit unit) throws ParseException;

}
