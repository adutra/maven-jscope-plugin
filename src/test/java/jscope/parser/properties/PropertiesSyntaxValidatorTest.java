/**
 * 
 */
package jscope.parser.properties;

import jscope.model.FileUnit;
import jscope.model.Line;
import jscope.parser.FileType;
import org.junit.Before;
import org.junit.Test;

import jscope.exception.ParseException;


/**
 * @author Alexandre Dutra
 *
 */
public class PropertiesSyntaxValidatorTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testShouldValidate() throws ParseException {
        final FileUnit unit = new FileUnit(null, FileType.JAVA, "UTF-8");
        unit.addUnit(new Line(1, "key1=value1"));
        unit.addUnit(new Line(2, "#comment"));
        unit.addUnit(new Line(3, "key2=value2 \\"));
        unit.addUnit(new Line(4, " value2 continued \\"));
        final PropertiesSyntaxValidator validator = new PropertiesSyntaxValidator();
        validator.setLenient(false);
        validator.validate(unit);
    }

}
