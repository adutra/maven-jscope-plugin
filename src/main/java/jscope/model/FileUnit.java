package jscope.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jscope.configuration.ScopeManager;
import jscope.parser.FileType;
import jscope.configuration.Scope;
import jscope.exception.ScopeNotFoundException;
import jscope.parser.UnitVisitor;


public class FileUnit implements UnitContainer {

    private final List<Unit> units = new ArrayList<Unit>();

    private final File file;

    private final FileType fileType;

    private final String encoding;

    public FileUnit(final File file, final FileType fileType, final String encoding) {
        super();
        this.file = file;
        this.fileType = fileType;
        this.encoding = encoding;
    }

    public int getStartLineNumber() {
        return 1;
    }

    public int getEndLineNumber() {
        return this.units.get(this.units.size() - 1).getEndLineNumber();
    }

    public File getFile() {
        return this.file;
    }

    public FileType getFileType() {
        return this.fileType;
    }

    public void setParent(final Unit unit) {
    }

    public void addUnit(final Unit unit) {
        this.units.add(unit);
        unit.setParent(this);
    }

    public UnitContainer getParent() {
        return null;
    }

    public List<Unit> getUnits() {
        return this.units;
    }

    public void accept(final UnitVisitor visitor) {
        visitor.visit(this);
    }

    public FileUnit applyScopes(final ScopeManager scopeManager) throws ScopeNotFoundException {
        final FileUnit clone = new FileUnit(this.file, this.fileType, this.encoding);
        for (final Unit unit : this.units) {
            clone.addUnit(unit.applyScopes(scopeManager));
        }
        return clone;
    }

    public boolean isActive(final ScopeManager scopeManager) {
        return true;
    }

    public boolean hasScopeUnits() {
        for (final Unit unit : this.units) {
            if(unit.hasScopeUnits()){
                return true;
            }
        }
        return false;
    }

    public boolean containsScope(final Scope scope) {
        for (final Unit unit : this.units) {
            if(unit.containsScope(scope)){
                return true;
            }
        }
        return false;
    }

    public boolean isWithinScope(final Scope scope) {
        return false;
    }

    public List<Unit> getChildUnits() {
        return this.units;
    }

    public void write() throws IOException {
        this.write(this.file, this.encoding);
    }

    public void write(final String encoding) throws IOException {
        this.write(this.file, encoding);
    }

    public void write(final File file, final String encoding) throws IOException {
        final PrintWriter pw = new PrintWriter(file, encoding);
        try {
            this.write(pw);
        } finally {
            pw.flush();
            pw.close();
        }
    }

    public void write(final PrintWriter pw) {
        for (final Unit unit : this.units) {
            unit.write(pw);
        }
    }

    public Reader getReader() {
        return new StringReader(this.toString());
    }

    public InputStream getInputStream() throws UnsupportedEncodingException {
        return new ByteArrayInputStream(this.toString().getBytes(this.encoding));
    }

    public InputStream getInputStream(final String encoding) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(this.toString().getBytes(encoding));
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
