package jscope.io;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {

    /**
     * List directory contents for a resource folder. Not recursive.
     * This is basically a brute-force implementation.
     * Works for regular files and also JARs.
     * 
     * @author Greg Briggs
     * @see "http://www.uofr.net/~greg/java/get-resource-listing.html"
     * @param path
     *            Should end with "/", but not start with one.
     * @return a Set of URLs contained in the specified path.
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Set<URL> listResources(final String path) throws URISyntaxException, IOException {

        final URL dirURL = JarUtils.class.getClassLoader().getResource(path);

        final Set<URL> results = new HashSet<URL>(); // avoid duplicates in case it is a subdirectory

        if (dirURL.getProtocol().equals("file")) {

            /* A file path: easy enough */
            final File[] files = new File(dirURL.toURI()).listFiles();
            for (final File file : files) {
                results.add(file.toURL());
            }

        } else if (dirURL.getProtocol().equals("jar")) {

            /* A JAR path */
            // strip out only the JAR file
            final String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            final JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            final Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
            while (entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                if (name.startsWith(path)) { // filter according to the path
                    final String entry = name.substring(path.length());
                    if("".equals(entry)){
                        //dir itself
                    } else if (entry.endsWith("/")) {
                        //result.addAll(this.getResourceListing(path + entry));
                    } else  {
                        final URL resource = new URL("jar", "", "file:" + jarPath + "!/" + path + entry);
                        results.add(resource);
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
        }
        return results;
    }

}
