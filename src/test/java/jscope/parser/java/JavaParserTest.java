package jscope.parser.java;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import jscope.model.Line;
import jscope.model.ScopeUnit;
import jscope.model.Unit;
import jscope.parser.ParseResult;
import jscope.exception.ParseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class JavaParserTest {

    private final JavaParser parser = new JavaParser();

    private final String sep;

    public JavaParserTest(final String sep) {
        super();
        this.sep = sep;
    }

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][]{{"\n"},{"\r"},{"\r\n"}});
    }

    @Test
    public void shouldSucceed1() throws IOException, ParseException {
        final InputStream is = JavaParserTest.class.getResourceAsStream("/original.java");
        this.parser.parse(new InputStreamReader(is, "UTF-8"), "UTF-8");
    }

    @Test
    public void shouldSucceed2() throws IOException, ParseException {
        final StringReader sr = new StringReader(
            "    //@" + "SCOPE OP-3456 BEGIN " + this.sep +
            "    //@" + "SCOPE END"
        );
        final ParseResult result = this.parser.parse(sr, "UTF-8");
        Assert.assertThat(result.getFileUnit().getUnits().size(), CoreMatchers.is(1));
        final Unit unit = result.getFileUnit().getUnits().get(0);
        Assert.assertThat(unit, CoreMatchers.instanceOf(ScopeUnit.class));
        final ScopeUnit scopeUnit = (ScopeUnit) unit;
        Assert.assertThat(scopeUnit.getBegin(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().size(), CoreMatchers.is(1));
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().get(0), CoreMatchers.is("OP-3456"));
        Assert.assertThat(scopeUnit.getUnits().size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldSucceed3() throws IOException, ParseException {
        final StringReader sr = new StringReader(
            "    //@" + "SCOPE OP-3456 BEGIN " + this.sep +
            "    public class Toto {} " + this.sep +
            "    //@" + "SCOPE END"
        );
        final ParseResult result = this.parser.parse(sr, "UTF-8");
        Assert.assertThat(result.getFileUnit().getUnits().size(), CoreMatchers.is(1));
        final Unit unit = result.getFileUnit().getUnits().get(0);
        Assert.assertThat(unit, CoreMatchers.instanceOf(ScopeUnit.class));
        final ScopeUnit scopeUnit = (ScopeUnit) unit;
        Assert.assertThat(scopeUnit.getBegin(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().size(), CoreMatchers.is(1));
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().get(0), CoreMatchers.is("OP-3456"));
        Assert.assertThat(scopeUnit.getUnits().size(), CoreMatchers.is(1));
        final Unit unit2 = scopeUnit.getUnits().get(0);
        Assert.assertThat(unit2, CoreMatchers.instanceOf(Line.class));
        final Line line = (Line) unit2;
        Assert.assertThat(line.getText(), CoreMatchers.is("    public class Toto {} "));
    }

    @Test
    public void shouldSucceed4() throws IOException, ParseException {
        final StringReader sr = new StringReader(
            "    public class Toto { " + this.sep +
            "    //@" + "SCOPE OP-3456 BEGIN " + this.sep +
            "    //@" + "SCOPE END  " + this.sep +
            "    } " + this.sep
        );

        final ParseResult result = this.parser.parse(sr, "UTF-8");
        Assert.assertThat(result.getFileUnit().getUnits().size(), CoreMatchers.is(3));

        Unit unit = result.getFileUnit().getUnits().get(0);
        Assert.assertThat(unit, CoreMatchers.instanceOf(Line.class));
        final Line line1 = (Line) unit;
        Assert.assertThat(line1.getText(), CoreMatchers.is("    public class Toto { "));

        unit = result.getFileUnit().getUnits().get(1);
        Assert.assertThat(unit, CoreMatchers.instanceOf(ScopeUnit.class));
        final ScopeUnit scopeUnit = (ScopeUnit) unit;
        Assert.assertThat(scopeUnit.getBegin(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().size(), CoreMatchers.is(1));
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().get(0), CoreMatchers.is("OP-3456"));
        Assert.assertThat(scopeUnit.getUnits().size(), CoreMatchers.is(0));

        unit = result.getFileUnit().getUnits().get(2);
        Assert.assertThat(unit, CoreMatchers.instanceOf(Line.class));
        final Line line2 = (Line) unit;
        Assert.assertThat(line2.getText(), CoreMatchers.is("    } "));
    }


    @Test
    public void shouldSucceed5() throws IOException, ParseException {
        final StringReader sr = new StringReader(
            "    public class Toto {" + this.sep +
            "    //@" + "SCOPE OP-3456 BEGIN " + this.sep +
            "    //@" + "SCOPE OP-4567 BEGIN " + this.sep +
            "    public void foo(){} " + this.sep +
            "    //@" + "SCOPE END  " + this.sep +
            "    //@" + "SCOPE END  " + this.sep +
            "    } " + this.sep +
            "    " + this.sep
        );

        final ParseResult result = this.parser.parse(sr, "UTF-8");
        Assert.assertThat(result.getFileUnit().getUnits().size(), CoreMatchers.is(4));

        Unit unit = result.getFileUnit().getUnits().get(0);
        Assert.assertThat(unit, CoreMatchers.instanceOf(Line.class));
        final Line line1 = (Line) unit;
        Assert.assertThat(line1.getText(), CoreMatchers.is("    public class Toto {"));

        unit = result.getFileUnit().getUnits().get(1);
        Assert.assertThat(unit, CoreMatchers.instanceOf(ScopeUnit.class));
        final ScopeUnit scopeUnit = (ScopeUnit) unit;
        Assert.assertThat(scopeUnit.getBegin(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().size(), CoreMatchers.is(1));
        Assert.assertThat(scopeUnit.getBegin().getScopeIds().get(0), CoreMatchers.is("OP-3456"));
        Assert.assertThat(scopeUnit.getUnits().size(), CoreMatchers.is(1));

        unit = scopeUnit.getUnits().get(0);
        Assert.assertThat(unit, CoreMatchers.instanceOf(ScopeUnit.class));
        final ScopeUnit scopeBlock2 = (ScopeUnit) unit;
        Assert.assertThat(scopeBlock2.getBegin(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeBlock2.getBegin().getScopeIds(), CoreMatchers.notNullValue());
        Assert.assertThat(scopeBlock2.getBegin().getScopeIds().size(), CoreMatchers.is(1));
        Assert.assertThat(scopeBlock2.getBegin().getScopeIds().get(0), CoreMatchers.is("OP-4567"));
        Assert.assertThat(scopeBlock2.getUnits().size(), CoreMatchers.is(1));

        unit = scopeBlock2.getUnits().get(0);
        Assert.assertThat(unit, CoreMatchers.instanceOf(Line.class));
        final Line line2 = (Line) unit;
        Assert.assertThat(line2.getText(), CoreMatchers.is("    public void foo(){} "));

        unit = result.getFileUnit().getUnits().get(2);
        Assert.assertThat(unit, CoreMatchers.instanceOf(Line.class));
        final Line line3 = (Line) unit;
        Assert.assertThat(line3.getText(), CoreMatchers.is("    } "));

        unit = result.getFileUnit().getUnits().get(3);
        Assert.assertThat(unit, CoreMatchers.instanceOf(Line.class));
        final Line line4 = (Line) unit;
        Assert.assertThat(line4.getText(), CoreMatchers.is("    "));

    }

}
