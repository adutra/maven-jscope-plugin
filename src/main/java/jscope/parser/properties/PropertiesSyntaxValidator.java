package jscope.parser.properties;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import jscope.parser.ParseError;
import jscope.exception.ParseException;
import jscope.model.FileUnit;
import jscope.parser.AbstractSyntaxValidator;


public class PropertiesSyntaxValidator extends AbstractSyntaxValidator {

    public List<ParseError> validate(final FileUnit fileUnit) throws ParseException {
        this.validateProperties(fileUnit);
        return this.errors;
    }

    private void validateProperties(final FileUnit fileUnit) throws ParseException {
        final Properties test = new Properties();
        try {
            test.load(fileUnit.getInputStream("ISO-8859-1"));
        } catch (final UnsupportedEncodingException e) {
            this.handleError(fileUnit, e.getMessage(), e);
        } catch (final IOException e) {
            this.handleError(fileUnit, e.getMessage(), e);
        }
    }

}
