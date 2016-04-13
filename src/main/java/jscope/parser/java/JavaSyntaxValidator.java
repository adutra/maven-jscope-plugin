package jscope.parser.java;

import jscope.antlr.JavaLexer;
import jscope.exception.ParseException;
import jscope.model.FileUnit;
import jscope.parser.AbstractSyntaxValidator;
import jscope.parser.ParseError;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

import java.util.List;


public class JavaSyntaxValidator extends AbstractSyntaxValidator {

    public List<ParseError> validate(final FileUnit fileUnit) throws ParseException {
        this.validateJava(fileUnit);
        return this.errors;
    }

    public void validateJava(final FileUnit fileUnit) throws ParseException {
        final ANTLRStringStream input = new ANTLRStringStream(fileUnit.toString());
        final JavaLexer lexer = new JavaLexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final jscope.antlr.JavaParser parser = new jscope.antlr.JavaParser(tokens);
        lexer.enableErrorMessageCollection(true);
        parser.enableErrorMessageCollection(true);
        lexer.preserveWhitespacesAndComments = true;
        try {
            parser.javaSource();
        } catch (final Exception e) {
            this.handleError(fileUnit, e.getMessage(), e);
        }

        for (final String msg : lexer.getMessages()) {
            this.handleError(fileUnit, msg, null);
        }
        for (final String msg : parser.getMessages()) {
            this.handleError(fileUnit, msg, null);
        }
    }


}
