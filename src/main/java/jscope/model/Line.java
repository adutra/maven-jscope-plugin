package jscope.model;

import java.io.File;
import java.io.PrintWriter;

import jscope.configuration.ScopeManager;
import jscope.parser.Formatter;
import jscope.configuration.Scope;
import jscope.exception.ScopeNotFoundException;
import jscope.parser.FileType;
import jscope.parser.UnitVisitor;


public class Line implements Unit {

    private final String text;

    private final int number;

    private Unit parent;

    private Formatter formatter;

    public Line(final int number, final String text) {
        super();
        this.text = text;
        this.number = number;
    }

    public Unit getParent() {
        return this.parent;
    }

    public void setParent(final Unit unit) {
        this.parent = unit;
    }

    public File getFile() {
        return this.parent.getFile();
    }

    public FileType getFileType() {
        return this.parent.getFileType();
    }

    public Formatter getLineFormatter() {
        if(this.formatter == null) {
            this.formatter = this.getFileType().newFormatter();
        }
        return this.formatter;
    }

    public int getStartLineNumber() {
        return this.number;
    }

    public int getEndLineNumber() {
        return this.number;
    }

    public void accept(final UnitVisitor visitor) {
        visitor.visit(this);
    }

    public Line applyScopes(final ScopeManager scopeManager) throws ScopeNotFoundException {
        if(this.isActive(scopeManager)) {
            return this.scope();
        } else {
            return this.unscope();
        }
    }

    public boolean isScoped() {
        return this.getLineFormatter().isScoped(this);
    }

    public Line unscope() {
        return this.getLineFormatter().unscope(this);
    }

    public Line scope() {
        return this.getLineFormatter().scope(this);
    }

    public boolean isActive(final ScopeManager scopeManager) throws ScopeNotFoundException {
        return this.getParent().isActive(scopeManager);
    }

    public boolean hasScopeUnits() {
        return false;
    }

    public boolean containsScope(final Scope scope) {
        return false;
    }

    public boolean isWithinScope(final Scope scope) {
        return this.getParent().isWithinScope(scope);
    }

    public boolean isWithinScopeUnit() {
        return this.getParent().hasScopeUnits();
    }

    public String getText() {
        return this.text;
    }

    public void write(final PrintWriter pw) {
        pw.println(this.text);
    }

    @Override
    public String toString() {
        return this.text;
    }

}
