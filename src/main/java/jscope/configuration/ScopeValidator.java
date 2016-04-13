package jscope.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * @author Alexandre Dutra
 *
 */
public class ScopeValidator {

    public void validateScopes(final Collection<Scope> scopes) {
        if(scopes == null) {
            throw new IllegalArgumentException("No scopes supplied");
        }
        final List<String> ids = new ArrayList<String>(scopes.size());
        for (final Scope scope : scopes) {
            if(scope.getId() == null) {
                throw new IllegalArgumentException("Scope id cannot be null");
            }
            if(scope.getStatus() == null) {
                throw new IllegalArgumentException("Scope status cannot be null");
            }
            if(ids.contains(scope.getId())) {
                throw new IllegalArgumentException("Scope id must be unique: " + scope.getId());
            }
            if("BASELINE".equals(scope.getId())) {
                throw new IllegalArgumentException("Scope id cannot be BASELINE");
            }
            ids.add(scope.getId());
        }
    }
}
