/**
 * 
 */
package jscope.parser.java;

import jscope.model.FileUnit;
import jscope.model.Line;
import jscope.parser.FileType;
import jscope.exception.ParseException;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Alexandre Dutra
 *
 */
public class JavaSyntaxValidatorTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testShouldValidate() throws ParseException {
        final FileUnit unit = new FileUnit(null, FileType.JAVA, "UTF-8");
        unit.addUnit(new Line(1,
            "import com.foo.*; " +
            "public class Foo { " +
            "private Foo<Bar> field = new Foo<Bar>();  " +
            "public void foo(String[] args){" +
            "System.out.println(\"\");" +
            "}" +
        "}"));
        final JavaSyntaxValidator validator = new JavaSyntaxValidator();
        validator.setLenient(false);
        validator.validate(unit);
    }

    @Test(expected=ParseException.class)
    public void testShouldNotValidate() throws ParseException {
        final FileUnit unit = new FileUnit(null, FileType.JAVA, "UTF-8");
        unit.addUnit(new Line(1,
            "import com.foo.*; " +
            "public class Foo { " +
            "private Foo<Bar> field = new Foo<Bar>();  " +
            "public void foo(String[] args {" +
            "System.out.println(\"\");" +
            "}" +
        "}"));
        final JavaSyntaxValidator validator = new JavaSyntaxValidator();
        validator.setLenient(false);
        validator.validate(unit);
    }

}
