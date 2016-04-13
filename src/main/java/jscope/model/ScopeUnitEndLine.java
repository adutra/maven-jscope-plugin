package jscope.model;

import jscope.configuration.ScopeManager;
import jscope.parser.UnitVisitor;


public class ScopeUnitEndLine extends Line {

    public ScopeUnitEndLine(final int number, final String line) {
        super(number, line);
    }

    @Override
    public ScopeUnitEndLine applyScopes(final ScopeManager scopeManager) {
        return this;
    }

    @Override
    public void accept(final UnitVisitor visitor) {
        visitor.visit(this);
    }
}
