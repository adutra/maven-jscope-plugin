package jscope.parser;

import java.util.List;

import jscope.exception.ParseException;
import jscope.model.FileUnit;
import jscope.model.Line;
import jscope.model.ScopeUnit;
import jscope.model.ScopeUnitBeginLine;
import jscope.model.ScopeUnitEndLine;
import jscope.model.Unit;


public class JScopeSyntaxValidator extends AbstractSyntaxValidator implements UnitVisitor {

    private final class VisitException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private VisitException(final ParseException cause) {
            super(cause);
        }

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
        final ScopeUnitBeginLine begin = scopeUnit.getBegin();
        if(begin == null){
            try {
                this.handleError(scopeUnit, "Scope Block is missing begin line: " + scopeUnit, null);
            } catch (final ParseException e) {
                throw new VisitException(e);
            }
        } else {
            begin.accept(this);
        }
        for (final Unit unit : scopeUnit.getUnits()) {
            unit.accept(this);
        }
        final ScopeUnitEndLine end = scopeUnit.getEnd();
        if(end == null){
            try {
                this.handleError(scopeUnit, "Scope Block is missing end line: " + begin, null);
            } catch (final ParseException e) {
                throw new VisitException(e);
            }
        } else {
            end.accept(this);
        }
    }

    public void visit(final Line line) {

    }

    public void visit(final ScopeUnitBeginLine scopeUnitBeginLine) {
        final List<String> scopes = scopeUnitBeginLine.getScopeIds();
        if(scopes == null || scopes.isEmpty()){
            try {
                this.handleError(scopeUnitBeginLine, "Scope Block begin line is missing scopes: " + scopeUnitBeginLine, null);
            } catch (final ParseException e) {
                throw new VisitException(e);
            }
        }
    }

    public void visit(final ScopeUnitEndLine scopeUnitEndLine) {

    }

}
