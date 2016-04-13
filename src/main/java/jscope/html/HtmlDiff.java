package jscope.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jscope.configuration.ScopeManager;
import jscope.model.FileUnit;
import jscope.model.Line;
import jscope.model.Unit;
import jscope.parser.UnitVisitor;
import jscope.exception.ScopeNotFoundException;
import org.apache.commons.lang.StringEscapeUtils;

import jscope.model.ScopeUnit;
import jscope.model.ScopeUnitBeginLine;
import jscope.model.ScopeUnitEndLine;


public class HtmlDiff implements UnitVisitor {

    private final class VisitException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private VisitException(final ScopeNotFoundException cause) {
            super(cause);
        }

    }

    private final ScopeManager sm1;

    private final ScopeManager sm2;

    private StringWriter sw;

    private PrintWriter pw;

    private Set<Integer> highlightedLines;

    private StringBuffer leftBuffer;

    private StringBuffer rightBuffer;

    public HtmlDiff(final ScopeManager sm1, final ScopeManager sm2) {
        super();
        this.sm1 = sm1;
        this.sm2 = sm2;
    }

    public void diff(final FileUnit fileUnit) throws ScopeNotFoundException {
        this.highlightedLines = new TreeSet<Integer>();
        this.leftBuffer = this.diff(fileUnit, this.sm1);
        this.rightBuffer = this.diff(fileUnit, this.sm2);
    }

    private StringBuffer diff(final FileUnit fileUnit, final ScopeManager sm) throws ScopeNotFoundException {
        this.sw = new StringWriter();
        this.pw = new PrintWriter(this.sw);
        try {
            fileUnit.applyScopes(sm).accept(this);
        }catch (final VisitException e) {
            throw (ScopeNotFoundException) e.getCause();
        } finally {
            this.pw.flush();
            this.pw.close();
        }
        return this.sw.getBuffer();
    }


    public Set<Integer> getHighlightedLines() {
        return this.highlightedLines;
    }

    public StringBuffer getLeftBuffer() {
        return this.leftBuffer;
    }

    public StringBuffer getRightBuffer() {
        return this.rightBuffer;
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
        try {
            if(scopeUnit.isActive(this.sm1) ^ scopeUnit.isActive(this.sm2)) {
                for (int i = scopeUnit.getStartLineNumber(); i <= scopeUnit.getEndLineNumber(); i++) {
                    this.highlightedLines.add(i);
                }
            }
        } catch (final ScopeNotFoundException e) {
            throw new VisitException(e);
        }
    }

    public void visit(final Line line) {
        //        if(line.isActive(this.sm1) ^ line.isActive(this.sm2)) {
        //            this.highlightedLines.add(line.getStartLineNumber());
        //        }
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
