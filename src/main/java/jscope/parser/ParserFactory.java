/**
 * 
 */
package jscope.parser;

import java.io.File;

import org.codehaus.plexus.util.FileUtils;



/**
 * @author Alexandre Dutra
 *
 */
public class ParserFactory {

    public static Parser newParser(final File file) {
        final String extension = FileUtils.extension(file.getName());
        return FileType.byExtension(extension).newParser();
    }
}
