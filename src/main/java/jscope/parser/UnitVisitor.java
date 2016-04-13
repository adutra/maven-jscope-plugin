package jscope.parser;

import jscope.model.FileUnit;
import jscope.model.Line;
import jscope.model.ScopeUnit;
import jscope.model.ScopeUnitBeginLine;
import jscope.model.ScopeUnitEndLine;



public interface UnitVisitor {

    void visit(FileUnit fileUnit);

    void visit(ScopeUnit scopeUnit);

    void visit(Line line);

    void visit(ScopeUnitBeginLine scopeUnitBeginLine);

    void visit(ScopeUnitEndLine scopeUnitEndLine);

}
