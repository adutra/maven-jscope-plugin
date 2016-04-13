package jscope.task;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.velocity.app.VelocityEngine;

import jscope.exception.TaskErrorException;
import jscope.io.FileUtils;
import jscope.velocity.VelocityUtils;

/**
 * 
 */
public abstract class AbstractHtmlJScopeTask extends AbstractJScopeTask {

    private static final String STATIC_RESOURCES_ROOT = "html/";

    protected VelocityUtils vu;

    public AbstractHtmlJScopeTask() {
        super();
    }

    public void setVelocityEngine(final VelocityEngine velocityEngine){
        this.vu = new VelocityUtils(velocityEngine);
    }

    public void setVelocityUtils(final VelocityUtils vu){
        this.vu = vu;
    }

    protected void copyResources() throws TaskErrorException {
        try {
            FileUtils.copyResources(STATIC_RESOURCES_ROOT, this.outputDirectory);
        } catch (final URISyntaxException e) {
            throw new TaskErrorException(e.getMessage(), e);
        } catch (final IOException e) {
            throw new TaskErrorException(e.getMessage(), e);
        }
    }

}
