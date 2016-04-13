package jscope.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * 
 */
public class FileUtils {

    public static void copyResources(final String srcClasspathResourceBase, final File destDir) throws IOException, URISyntaxException {

        final Set<URL> resources = JarUtils.listResources(srcClasspathResourceBase);

        final byte[] buffer = new byte[1 << 12];

        for (final URL src : resources) {

            final String relativePath = StringUtils.substringAfter(src.getPath(), "!/" + srcClasspathResourceBase);
            final File dest = new File(destDir, relativePath);
            dest.getParentFile().mkdirs();

            final URLConnection uc = src.openConnection();
            uc.connect();

            final InputStream in = uc.getInputStream();
            final OutputStream out = new FileOutputStream(dest);

            try {
                int bytesRead = -1;
                while((bytesRead = in.read(buffer)) > -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                in.close();
                out.close();
            }
        }

    }

}
