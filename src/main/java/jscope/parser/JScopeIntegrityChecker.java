package jscope.parser;

import java.util.List;

import jscope.configuration.ScopeManager;
import jscope.exception.ParseException;
import jscope.exception.ScopeNotFoundException;
import jscope.model.FileUnit;
import jscope.model.Line;
import jscope.model.ScopeUnit;
import jscope.model.ScopeUnitBeginLine;
import jscope.model.ScopeUnitEndLine;
import jscope.model.Unit;


public class JScopeIntegrityChecker extends AbstractSyntaxValidator implements UnitVisitor {

    private final class VisitException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private VisitException(final ParseException cause) {
            super(cause);
        }

    }

    private final ScopeManager scopeManager;

    public JScopeIntegrityChecker(final ScopeManager scopeManager) {
        super();
        this.scopeManager = scopeManager;
    }

    public List<ParseError> validate(final FileUnit unit) throws ParseException {
        try {
            this.visit(unit);
        } catch (final VisitException e) {
            throw (ParseException) e.getCause();
        }
        return this.errors;
    }

    public void visit(final FileUnit fileUnit) {
        final List<Unit> units = fileUnit.getUnits();
        for (final Unit unit : units) {
            unit.accept(this);
        }
    }

    public void visit(final ScopeUnit scopeUnit) {
        for (final Unit unit : scopeUnit.getUnits()) {
            unit.accept(this);
        }
    }

    public void visit(final Line line) {
        final boolean actual = line.isScoped();
        if(!actual && !line.isWithinScopeUnit()){
            try {
                this.handleError(line,  "Line is unscoped but no enclosing scope has been found.", null);
            } catch (final ParseException e) {
                throw new VisitException(e);
            }
        }
        boolean expected;
        try {
            expected = line.isActive(this.scopeManager);
            if(actual ^ expected){
                try {
                    this.handleError(line,
                        "Line status is inconsistent, " +
                        "expected: " + (expected ? "SCOPED/VALIDATED" : "UNSCOPED") + ", " +
                        "actual: "   + (actual ? "SCOPED/VALIDATED" : "UNSCOPED"),
                        null);
                } catch (final ParseException e) {
                    throw new VisitException(e);
                }
            }
        } catch (final ScopeNotFoundException e) {
            try {
                this.handleError(
                    line,
                    e.getMessage(),
                    e);
            } catch (final ParseException e1) {
                throw new VisitException(e1);
            }
        }

    }

    public void visit(final ScopeUnitBeginLine scopeUnitBeginLine) {

    }

    public void visit(final ScopeUnitEndLine scopeUnitEndLine) {

    }

}
