package jscope.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jscope.configuration.ScopeManager;
import jscope.exception.ScopeManagerException;
import jscope.parser.ParseResult;
import jscope.parser.java.JavaParser;
import jscope.parser.properties.PropertiesParser;
import jscope.parser.sql.SqlParser;
import jscope.configuration.Scope;
import jscope.configuration.Status;
import jscope.exception.ParseException;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import jscope.exception.ScopeNotFoundException;
import jscope.parser.Parser;
import jscope.parser.java.JavaParserTest;
import jscope.parser.xml.XmlParser;

@RunWith(Parameterized.class)
public class FileUnitTest {

    private File original;

    private File transformed1;

    private File transformed2;

    private File transformed3;

    private final String extension;

    private final Parser parser;

    private final String encoding;

    public FileUnitTest(final String extension, final Parser parser, final String encoding) {
        super();
        this.extension = extension;
        this.parser = parser;
        this.encoding = encoding;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"java",       new JavaParser(),       "UTF-8"},
            {"xml",        new XmlParser(),        "UTF-8"},
            {"properties", new PropertiesParser(), "ISO-8859-1"},
            {"sql",        new SqlParser(),        "UTF-8"}
        });
    }

    @Before
    public void setUp() throws IOException, URISyntaxException {
        this.original = new File(JavaParserTest.class.getResource("/original." + this.extension).toURI());
        this.transformed1 = new File(JavaParserTest.class.getResource("/transformed1." + this.extension).toURI());
        this.transformed2 = new File(JavaParserTest.class.getResource("/transformed2." + this.extension).toURI());
        this.transformed3 = new File(JavaParserTest.class.getResource("/transformed3." + this.extension).toURI());
    }

    @Test
    public void testFormat() throws IOException, ParseException, ScopeManagerException, ScopeNotFoundException {

        final ParseResult result = this.parser.parse(this.original, this.encoding);
        FileUnit fileUnit = result.getFileUnit();
        this.compare(this.original, fileUnit.getReader());

        ScopeManager sm = new ScopeManager(this.createScopes("OP-4567", Status.SCOPED, "OP-3456", Status.UNSCOPED));
        fileUnit = fileUnit.applyScopes(sm);
        this.compare(this.transformed1, fileUnit.getReader());

        sm = new ScopeManager(this.createScopes("OP-4567", Status.UNSCOPED, "OP-3456", Status.SCOPED));
        fileUnit = fileUnit.applyScopes(sm);
        this.compare(this.transformed2, fileUnit.getReader());

        sm = new ScopeManager(this.createScopes("OP-4567", Status.UNSCOPED, "OP-3456", Status.UNSCOPED));
        fileUnit = fileUnit.applyScopes(sm);
        this.compare(this.transformed3, fileUnit.getReader());

    }

    private void compare(final File expected, final Reader actual) throws IOException {
        final LineNumberReader br1 = new LineNumberReader(new InputStreamReader(new FileInputStream(expected), this.encoding));
        final LineNumberReader br2 = new LineNumberReader(actual);

        String lineExpected, lineActual;
        while((lineExpected = br1.readLine()) != null) {
            lineActual = br2.readLine();
            Assert.assertNotNull("Line " + br1.getLineNumber() + " should not be null", lineActual);
            Assert.assertEquals("Line " + br1.getLineNumber() + " differ", lineExpected, lineActual);
        }

    }

    private List<Scope> createScopes(final String id1, final Status status1, final String id2, final Status status2) {
        final List<Scope> scopes = new ArrayList<Scope>(2);
        final Scope scope1 = new Scope();
        scope1.setId(id1);
        scope1.setStatus(status1);
        scopes.add(scope1);
        final Scope scope2 = new Scope();
        scope2.setId(id2);
        scope2.setStatus(status2);
        scopes.add(scope2);
        return scopes;
    }


}

