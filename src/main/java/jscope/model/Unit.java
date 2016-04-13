package jscope.model;

import java.io.File;
import java.io.PrintWriter;

import jscope.configuration.ScopeManager;
import jscope.parser.FileType;
import jscope.configuration.Scope;
import jscope.exception.ScopeNotFoundException;
import jscope.parser.UnitVisitor;


public interface Unit {

    FileType getFileType();

    int getStartLineNumber();

    int getEndLineNumber();

    File getFile();

    Unit getParent();

    void setParent(Unit unit);

    void accept(UnitVisitor visitor);

    Unit applyScopes(ScopeManager scopeManager) throws ScopeNotFoundException;

    boolean isActive(ScopeManager scopeManager) throws ScopeNotFoundException;

    boolean hasScopeUnits();

    boolean containsScope(Scope scope);

    boolean isWithinScope(Scope scope);

    void write(PrintWriter pw);

}
