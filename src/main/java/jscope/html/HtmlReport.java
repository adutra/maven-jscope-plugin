package jscope.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jscope.model.Line;
import jscope.configuration.Scope;
import jscope.model.FileUnit;
import jscope.model.ScopeUnit;
import jscope.parser.UnitVisitor;
import org.apache.commons.lang.StringEscapeUtils;

import jscope.model.ScopeUnitBeginLine;
import jscope.model.ScopeUnitEndLine;
import jscope.model.Unit;


public class HtmlReport implements UnitVisitor {

    private final Collection<Scope> highlightedScopes;

    private StringWriter sw;

    private PrintWriter pw;

    private Set<Integer> highlightedLines;

    public HtmlReport(final Collection<Scope> highlightedScopes) {
        super();
        this.highlightedScopes = highlightedScopes;
    }

    public void report(final FileUnit fileUnit) {
        this.highlightedLines = new TreeSet<Integer>();
        this.sw = new StringWriter();
        this.pw = new PrintWriter(this.sw);
        try {
            fileUnit.accept(this);
        } finally {
            this.pw.flush();
            this.pw.close();
        }
    }

    public Set<Integer> getHighlightedLines() {
        return this.highlightedLines;
    }


    public StringBuffer getStringBuffer() {
        return this.sw.getBuffer();
    }

    public void visit(final FileUnit fileUnit) {
        final List<Unit> units = fileUnit.getUnits();
        for (final Unit unit : units) {
            unit.accept(this);
        }
    }

    public void visit(final ScopeUnit scopeUnit) {
        final ScopeUnitBeginLine begin = scopeUnit.getBegin();
        begin.accept(this);
        for (final Unit unit : scopeUnit.getUnits()) {
            unit.accept(this);
        }
        final ScopeUnitEndLine end = scopeUnit.getEnd();
        end.accept(this);
    }

    public void visit(final Line line) {
        for (final Scope scope : this.highlightedScopes) {
            if(line.isWithinScope(scope)) {
                this.highlightedLines.add(line.getStartLineNumber());
            }
        }
        this.pw.println(this.escapeLine(line.getText()));
    }

    public void visit(final ScopeUnitBeginLine scopeUnitBeginLine) {
        this.visit((Line) scopeUnitBeginLine);
    }

    public void visit(final ScopeUnitEndLine scopeUnitEndLine) {
        this.visit((Line) scopeUnitEndLine);
    }

    private String escapeLine(final String line) {
        return StringEscapeUtils.escapeHtml(line);
    }

}
