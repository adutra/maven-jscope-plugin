package jscope.model;

import java.util.Arrays;
import java.util.List;

import jscope.configuration.Scope;
import jscope.configuration.ScopeManager;
import jscope.parser.UnitVisitor;
import jscope.exception.ScopeNotFoundException;
import org.apache.commons.lang.StringUtils;


public class ScopeUnitBeginLine extends Line {

    private final List<String> scopeIds;

    public ScopeUnitBeginLine(final int number, final String line, final String expression) {
        super(number, line);
        this.scopeIds = Arrays.asList(StringUtils.split(expression));
    }

    public List<String> getScopeIds() {
        return this.scopeIds;
    }

    @Override
    public boolean isActive(final ScopeManager scopeManager) throws ScopeNotFoundException {
        for (final String scope : this.scopeIds) {
            if(scopeManager.isScopeActive(scope)){
                return true;
            }
        }
        return false;
    }

    @Override
    public ScopeUnitBeginLine applyScopes(final ScopeManager scopeManager) {
        return this;
    }

    @Override
    public boolean containsScope(final Scope scope) {
        return this.scopeIds.contains(scope.getId());
    }

    @Override
    public void accept(final UnitVisitor visitor) {
        visitor.visit(this);
    }

}
