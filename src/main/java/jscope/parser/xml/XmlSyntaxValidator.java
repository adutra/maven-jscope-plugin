package jscope.parser.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import jscope.parser.ParseError;
import jscope.exception.ParseException;
import jscope.model.FileUnit;
import jscope.parser.AbstractSyntaxValidator;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlSyntaxValidator extends AbstractSyntaxValidator {

    static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    public List<ParseError> validate(final FileUnit fileUnit) throws ParseException {
        this.validateDOM(fileUnit);
        return this.errors;
    }

    private void validateDOM(final FileUnit fileUnit) throws ParseException {
        try {

            // http://download.oracle.com/javaee/1.4/tutorial/doc/JAXPSAX9.html

            final SAXParser saxParser = new SAXParser();

            // saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

            // http://xerces.apache.org/xerces2-j/features.html
            // saxParser.setProperty("http://xml.org/sax/features/namespaces", false);
            // saxParser.setProperty("http://xml.org/sax/features/validation", false);
            saxParser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            saxParser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            saxParser.setErrorHandler(new DefaultHandler());

            /*
             * You should not use a Reader object because the UTF-8 decoder
             * that ships with Java does not know how to handle a BOM in a
             * UTF-8 stream. Instead, pass an InputStream to the parser and
             * let it detect the encoding and use its own UTF-8 Reader.
             */
            saxParser.parse(new InputSource(fileUnit.getInputStream()));

        } catch (final SAXParseException e) {
            this.handleError(fileUnit, fileUnit.getFile(), e.getLineNumber(), e.getMessage(), e);
        } catch (final SAXException e) {
            this.handleError(fileUnit, e.getMessage(), e);
        } catch (final FileNotFoundException e) {
            // DTD or schema not found
        } catch (final IOException e) {
            this.handleError(fileUnit, e.getMessage(), e);
        }
    }

}
