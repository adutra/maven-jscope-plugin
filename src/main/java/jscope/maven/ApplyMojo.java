package jscope.maven;

import jscope.task.ApplyTask;
import jscope.task.JScopeTask;

/**
 * 
 * <p>Applique au code source les scopes définis dans la configuration du projet.</p>
 * 
 * <p><strong>Ce goal modifie les fichiers originaux du projet et doit
 * être utilisé avec précaution.</strong> En particulier,
 * il est recommandé d'exécuter les goals <code>check-syntax</code>
 * et <code>verify</code> préalablement.</p>
 * 
 * @goal apply
 * @aggregator
 */
public class ApplyMojo extends AbstractJScopeMojo {

    @Override
    protected JScopeTask createTask() {
        return new ApplyTask();
    }

}
