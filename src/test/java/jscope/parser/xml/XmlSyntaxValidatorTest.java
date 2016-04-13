/**
 * 
 */
package jscope.parser.xml;

import jscope.exception.ParseException;
import jscope.model.FileUnit;
import jscope.model.Line;
import jscope.parser.FileType;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Alexandre Dutra
 *
 */
public class XmlSyntaxValidatorTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testShouldValidate() throws ParseException {
        final FileUnit unit = new FileUnit(null, FileType.XML, "UTF-8");
        //on ne valide pas la DTD
        unit.addUnit(new Line(1,
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
            "<root>"+
            "<child>"+
            "<!-- comment -->"+
            "</child>" +
            "<child><![CDATA["+
            "<foo>"+ // malformé
            "]]></child></root>"
        ));
        final XmlSyntaxValidator validator = new XmlSyntaxValidator();
        validator.setLenient(false);
        validator.validate(unit);
    }

    @Test(expected=ParseException.class)
    public void testShouldNotValidate() throws ParseException {
        final FileUnit unit = new FileUnit(null, FileType.JAVA, "UTF-8");
        unit.addUnit(new Line(1,
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
            "<root>"+
            "<child>"+
            "<!-- comment -->"+
            "</child>" +
            "<child><![CDATA["+
            "<foo></foo>"+
            "]]></root>"
        ));
        final XmlSyntaxValidator validator = new XmlSyntaxValidator();
        validator.setLenient(false);
        validator.validate(unit);
    }

}
