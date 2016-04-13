/**
 * 
 */
package jscope.parser;

import java.util.HashSet;
import java.util.Set;

import jscope.parser.java.JavaParser;
import jscope.parser.properties.PropertiesParser;
import jscope.parser.sql.SqlFormatter;
import jscope.parser.sql.SqlParser;
import jscope.parser.xml.XmlSyntaxValidator;
import jscope.parser.java.JavaSyntaxValidator;
import jscope.parser.properties.PropertiesFormatter;
import org.apache.commons.lang.ArrayUtils;

import jscope.parser.java.JavaFormatter;
import jscope.parser.xml.XmlFormatter;
import jscope.parser.xml.XmlParser;


/**
 * @author Alexandre Dutra
 *
 */
public enum FileType {

    JAVA ("java") {

        @Override
        public Parser newParser() {
            return new JavaParser();
        }

        @Override
        public Formatter newFormatter() {
            return new JavaFormatter();
        }

        @Override
        public SyntaxValidator newSyntaxValidator() {
            return new JavaSyntaxValidator();
        }

    },

    XML("xml", "xsd") {

        @Override
        public Parser newParser() {
            return new XmlParser();
        }

        @Override
        public Formatter newFormatter() {
            return new XmlFormatter();
        }

        @Override
        public SyntaxValidator newSyntaxValidator() {
            return new XmlSyntaxValidator();
        }
    },

    PROPERTIES("properties") {

        @Override
        public Parser newParser() {
            return new PropertiesParser();
        }

        @Override
        public Formatter newFormatter() {
            return new PropertiesFormatter();
        }
    },

    SQL("sql") {

        @Override
        public Parser newParser() {
            return new SqlParser();
        }

        @Override
        public Formatter newFormatter() {
            return new SqlFormatter();
        }
    }

    ;

    private final String[] extensions;

    private FileType(final String... extensions) {
        this.extensions = extensions;
    }

    public abstract Parser newParser();

    public abstract Formatter newFormatter();

    public String[] getExtensions() {
        return this.extensions;
    }

    public static FileType byExtension(final String extension) {
        for (final FileType type : FileType.values()) {
            if(ArrayUtils.contains(type.extensions, extension)) {
                return type;
            }
        }
        return null;
    }

    public static String[] getKnownExtensions() {
        final Set<String> extensionsSet = new HashSet<String>();
        for (final FileType type : FileType.values()) {
            for (final String extension : type.extensions) {
                extensionsSet.add(extension);
            }
        }
        return extensionsSet.toArray(new String[extensionsSet.size()]);
    }

    public SyntaxValidator newSyntaxValidator() {
        return null;
    }
}
