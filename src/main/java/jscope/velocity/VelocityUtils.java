package jscope.velocity;

import jscope.exception.JScopeVelocityException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class VelocityUtils {

    private final VelocityEngine velocityEngine;

    public VelocityUtils(final VelocityEngine velocityEngine) {
        super();
        this.velocityEngine = velocityEngine;
    }

    public VelocityContext createContext(final Object... keyValuePairs) {
        final VelocityContext context = new VelocityContext();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            final String key = (String) keyValuePairs[i];
            final Object value = keyValuePairs[i+1];
            context.put(key, value);
        }
        return context;
    }

    public void applyTemplate(final String templateFileName, final File outputFile, final VelocityContext context) throws JScopeVelocityException {
        try {
            final Template t = this.velocityEngine.getTemplate(templateFileName);
            final FileOutputStream fos = new FileOutputStream(outputFile);
            final PrintWriter p = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
            try {
                t.merge(context, p);
            } finally {
                p.close();
                fos.close();
            }
        } catch (final Exception e) {
            throw new JScopeVelocityException("Can't apply template: " + templateFileName, e);
        }
    }

    public void applyTemplate(
        final String templateFileName,
        final File outputFile,
        final String projectName,
        final String heading,
        final String title,
        final String prefix,
        final Object... keyValuePairs)
    throws JScopeVelocityException {
        final VelocityContext ctx = this.createContext(keyValuePairs);
        ctx.put("projectName", projectName);
        ctx.put("title", title);
        ctx.put("heading", heading);
        ctx.put("prefix", prefix);
        this.applyTemplate(templateFileName, outputFile, ctx);
    }

}
