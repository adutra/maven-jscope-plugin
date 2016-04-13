package jscope.model;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import jscope.configuration.Scope;
import jscope.configuration.ScopeManager;
import jscope.parser.FileType;
import jscope.parser.UnitVisitor;
import jscope.exception.ScopeNotFoundException;


public class ScopeUnit implements Unit, UnitContainer {

    private ScopeUnitBeginLine begin;

    private final List<Unit> units = new ArrayList<Unit>();

    private ScopeUnitEndLine end;

    private Unit parent;

    private final int startLineNumber;

    public ScopeUnit(final int startLineNumber) {
        super();
        this.startLineNumber = startLineNumber;
    }

    public int getStartLineNumber() {
        return this.startLineNumber;
    }

    public int getEndLineNumber() {
        return this.end.getEndLineNumber();
    }

    public File getFile() {
        return this.parent.getFile();
    }

    public FileType getFileType() {
        return this.parent.getFileType();
    }

    public ScopeUnitBeginLine getBegin() {
        return this.begin;
    }

    public void setBegin(final ScopeUnitBeginLine begin) {
        this.begin = begin;
        begin.setParent(this);
    }

    public ScopeUnitEndLine getEnd() {
        return this.end;
    }

    public void setEnd(final ScopeUnitEndLine end) {
        this.end = end;
        end.setParent(this);
    }

    public List<Unit> getUnits() {
        return this.units;
    }

    public void addUnit(final Unit unit) {
        this.units.add(unit);
        unit.setParent(this);
    }

    public Unit getParent() {
        return this.parent;
    }

    public void setParent(final Unit unit) {
        this.parent = unit;
    }

    public void accept(final UnitVisitor visitor) {
        visitor.visit(this);
    }

    public ScopeUnit applyScopes(final ScopeManager scopeManager) throws ScopeNotFoundException {
        final ScopeUnit clone = new ScopeUnit(this.startLineNumber);
        clone.setBegin(this.getBegin().applyScopes(scopeManager));
        clone.setEnd(this.getEnd().applyScopes(scopeManager));
        for (final Unit unit : this.units) {
            clone.addUnit(unit.applyScopes(scopeManager));
        }
        return clone;
    }

    public boolean isActive(final ScopeManager scopeManager) throws ScopeNotFoundException {
        return this.getParent().isActive(scopeManager) && this.getBegin().isActive(scopeManager);
    }

    public boolean hasScopeUnits() {
        return true;
    }

    public boolean containsScope(final Scope scope) {
        return this.getBegin().containsScope(scope);
    }

    public boolean isWithinScope(final Scope scope) {
        return this.containsScope(scope) || this.getParent().isWithinScope(scope);
    }

    public List<Unit> getChildUnits() {
        return this.units;
    }

    public void write(final PrintWriter pw) {
        //avoid NPE in case of malformed file
        if(this.begin != null) {
            this.begin.write(pw);
        }
        for (final Unit unit : this.units) {
            unit.write(pw);
        }
        //avoid NPE in case of malformed file
        if(this.end != null) {
            this.end.write(pw);
        }
    }

    @Override
    public String toString() {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        try {
            this.write(pw);
        } finally {
            pw.flush();
            pw.close();
        }
        return sw.toString();
    }

}
