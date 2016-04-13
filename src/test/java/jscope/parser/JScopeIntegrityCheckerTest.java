package jscope.parser;


import java.util.ArrayList;
import java.util.List;

import jscope.configuration.ScopeManager;
import jscope.model.Line;
import jscope.configuration.Scope;
import jscope.model.FileUnit;
import jscope.model.ScopeUnit;
import org.junit.Before;
import org.junit.Test;

import jscope.configuration.Status;
import jscope.exception.ParseException;
import jscope.model.ScopeUnitBeginLine;
import jscope.model.ScopeUnitEndLine;


public class JScopeIntegrityCheckerTest {

    private ScopeManager sm;

    @Before
    public void setUp() throws Exception {
        final List<Scope> scopes = new ArrayList<Scope>();
        final Scope scope = new Scope();
        scope.setId("AB-1234");
        scope.setStatus(Status.UNSCOPED);
        scopes.add(scope);
        final Scope scope2 = new Scope();
        scope2.setId("BC-3456");
        scope2.setStatus(Status.SCOPED);
        scopes.add(scope2);
        this.sm = new ScopeManager(scopes);
    }

    @Test(expected=ParseException.class)
    public void shouldNotValidate() throws ParseException {
        final FileUnit unit = new FileUnit(null, FileType.JAVA, "UTF-8");
        unit.addUnit(new Line(1, "//@" + "UNSCOPED@//import com.foo.*;"));
        final JScopeIntegrityChecker validator = new JScopeIntegrityChecker(this.sm);
        validator.setLenient(false);
        validator.validate(unit);
    }

    @Test(expected=ParseException.class)
    public void shouldNotValidate2()throws ParseException {
        final FileUnit unit = new FileUnit(null, FileType.JAVA, "UTF-8");
        final ScopeUnit scopeUnit = new ScopeUnit(1);
        unit.addUnit(scopeUnit);
        final ScopeUnitBeginLine begin = new ScopeUnitBeginLine(1, "//@" + "SCOPE AB-1234 BEGIN", "AB-1234");
        scopeUnit.setBegin(begin);
        begin.setParent(scopeUnit);
        final Line line = new Line(2, "import com.foo.*;");
        scopeUnit.addUnit(line);
        line.setParent(scopeUnit);
        final ScopeUnitEndLine end = new ScopeUnitEndLine(3, "//@" + "SCOPE END");
        scopeUnit.setEnd(end);
        end.setParent(scopeUnit);
        final JScopeIntegrityChecker validator = new JScopeIntegrityChecker(this.sm);
        validator.setLenient(false);
        validator.validate(unit);
    }

    @Test(expected=ParseException.class)
    public void shouldNotValidate3()throws ParseException {
        final FileUnit unit = new FileUnit(null, FileType.JAVA, "UTF-8");
        final ScopeUnit scopeUnit = new ScopeUnit(1);
        unit.addUnit(scopeUnit);
        final ScopeUnitBeginLine begin = new ScopeUnitBeginLine(1, "//@" + "SCOPE BC-3456 BEGIN", "BC-3456");
        scopeUnit.setBegin(begin);
        begin.setParent(scopeUnit);
        final Line line = new Line(2, "//@" + "UNSCOPED@//import com.foo.*;");
        scopeUnit.addUnit(line);
        line.setParent(scopeUnit);
        final ScopeUnitEndLine end = new ScopeUnitEndLine(3, "//@" + "SCOPE END");
        scopeUnit.setEnd(end);
        end.setParent(scopeUnit);
        final JScopeIntegrityChecker validator = new JScopeIntegrityChecker(this.sm);
        validator.setLenient(false);
        validator.validate(unit);
    }

}
