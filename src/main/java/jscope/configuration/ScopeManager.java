package jscope.configuration;

import jscope.exception.ScopeNotFoundException;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScopeManager {

    public static final String BASELINE = "BASELINE";

    private final List<Scope> scopes;

    public ScopeManager(final List<Scope> scopes){
        new ScopeValidator().validateScopes(scopes);
        this.scopes = scopes;
    }

    public List<Scope> getScopes() {
        return Collections.unmodifiableList(this.scopes);
    }

    public List<Scope> getScopesWithStatus(final Status... statuses) {
        final List<Scope> filteredScopes = new ArrayList<Scope>();
        for (final Scope scope : this.scopes) {
            if(ArrayUtils.contains(statuses, scope.getStatus())){
                filteredScopes.add(scope);
            }
        }
        return Collections.unmodifiableList(filteredScopes);
    }

    public boolean isScopeActive(final String scopeId) throws ScopeNotFoundException {
        if(ScopeManager.BASELINE.equals(scopeId)) {
            if(this.scopes == null || this.scopes.isEmpty()){
                return true;
            }
            return ! this.hasActiveScopes();
        } else {
            return this.findScopeById(scopeId).getStatus() != Status.UNSCOPED;
        }
    }

    private boolean hasActiveScopes() {
        if(this.scopes != null) {
            for (final Scope scope : this.scopes) {
                if(scope.getStatus() != Status.UNSCOPED){
                    return true;
                }
            }
        }
        return false;
    }

    public Scope findScopeById(final String scopeId) throws ScopeNotFoundException {
        if(this.scopes != null) {
            for (final Scope scope : this.scopes) {
                if(scope.getId().equals(scopeId)) {
                    return scope;
                }
            }
        }
        throw new ScopeNotFoundException("Unknown scope ID: " + scopeId);
    }

    @Override
    public String toString() {
        return "ScopeManager [scopes=" + this.scopes + "]";
    }

}

