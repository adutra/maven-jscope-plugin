package jscope.parser;

import java.util.ArrayList;
import java.util.List;

import jscope.configuration.ScopeManager;
import jscope.model.FileUnit;
import jscope.exception.ParseException;

public class SyntaxValidationHelper {

    public static List<ParseError> checkSyntax(final FileUnit fileUnit, final ScopeManager scopeManager, final boolean lenient) throws ParseException {
        final List<ParseError> errors = new ArrayList<ParseError>();
        checkJScopeSyntax(fileUnit, errors, lenient);
        checkJScopeIntegrity(fileUnit, errors, scopeManager, lenient);
        checkUnderlyingSyntax(fileUnit, errors, lenient);
        return errors;
    }

    private static void checkJScopeIntegrity(final FileUnit fileUnit, final List<ParseError> errors, final ScopeManager scopeManager, final boolean lenient) throws ParseException {
        final SyntaxValidator validator = new JScopeIntegrityChecker(scopeManager);
        validator.setLenient(lenient);
        final List<ParseError> jscopeErrors = validator.validate(fileUnit);
        if(jscopeErrors != null) {
            errors.addAll(jscopeErrors);
        }
    }

    private static void checkJScopeSyntax(final FileUnit fileUnit, final List<ParseError> errors, final boolean lenient) throws ParseException {
        final SyntaxValidator validator = new JScopeSyntaxValidator();
        validator.setLenient(lenient);
        final List<ParseError> jscopeErrors = validator.validate(fileUnit);
        if(jscopeErrors != null) {
            errors.addAll(jscopeErrors);
        }
    }

    private static void checkUnderlyingSyntax(final FileUnit fileUnit, final List<ParseError> errors, final boolean lenient) throws ParseException {
        final SyntaxValidator underlyingSyntaxValidator = fileUnit.getFileType().newSyntaxValidator();
        if(underlyingSyntaxValidator != null) {
            underlyingSyntaxValidator.setLenient(lenient);
            final List<ParseError> underlyingSyntaxErrors = underlyingSyntaxValidator.validate(fileUnit);
            if(underlyingSyntaxErrors != null && ! underlyingSyntaxErrors.isEmpty()) {
                errors.addAll(underlyingSyntaxErrors);
            }
        }
    }

}

