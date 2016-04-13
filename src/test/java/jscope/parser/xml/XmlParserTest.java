package jscope.parser.xml;


import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import jscope.exception.ParseException;
import jscope.model.Line;
import jscope.model.ScopeUnit;
import jscope.parser.ParseResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import jscope.model.Unit;

@RunWith(Parameterized.class)
public class XmlParserTest {

    private XmlParser parser;

    private final String sep;

    public XmlParserTest(final String sep) {
        super();
        this.sep = sep;
    }

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][]{{"\n"},{"\r"},{"\r\n"}});
    }

    @Before
    public void setUp() {
        this.parser = new XmlParser();
    }

    @Test
    public void shouldSucceed() throws IOException, ParseException {
        final StringReader sr = new StringReader(
            "        \t<root>  " + this.sep +
            "            <!-- @SCOPE OP-3456 BEGIN -->" + this.sep +
            "            <child>foo</child>" + this.sep +
            "            <!--<child>bar</child>-->" + this.sep +
            "            <!-- @SCOPE END -->"+ this.sep +
            "        </root>  "
        );
        final ParseResult result = this.parser.parse(sr, "UTF-8");
        Assert.assertThat(result.getFileUnit().getUnits().size(), CoreMatchers.is(3));
        Unit unit = result.getFileUnit().getUnits().get(0);
        Assert.assertThat(unit, CoreMatchers.instanceOf(Line.class));
        Line line = (Line) unit;
        Assert.assertThat(line.getText(), CoreMatchers.is("        \t<root>  "));

        unit = result.getFileUnit().getUnits().get(1);
        Assert.assertThat(unit, CoreMatchers.instanceOf(ScopeUnit.class));
        final ScopeUnit scopeUnit = (ScopeUnit) unit;
        Assert.assertThat(scopeUnit.getBegin(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().size(), CoreMatchers.is(1));
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().get(0), CoreMatchers.is("OP-3456"));

        Assert.assertThat(scopeUnit.getUnits().size(), CoreMatchers.is(2));
        unit = scopeUnit.getUnits().get(0);
        Assert.assertThat(unit, CoreMatchers.instanceOf(Line.class));
        line = (Line) unit;
        Assert.assertThat(line.getText(), CoreMatchers.is("            <child>foo</child>"));
        unit = scopeUnit.getUnits().get(1);
        Assert.assertThat(unit, CoreMatchers.instanceOf(Line.class));
        line = (Line) unit;
        Assert.assertThat(line.getText(), CoreMatchers.is("            <!--<child>bar</child>-->"));
    }

}
