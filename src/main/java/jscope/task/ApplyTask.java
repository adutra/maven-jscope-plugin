package jscope.task;

import jscope.exception.ScopeNotFoundException;
import jscope.exception.TaskErrorException;
import jscope.model.FileUnit;
import jscope.parser.ParseResult;

import java.io.File;
import java.io.IOException;

/**
 *
 * <p>Applique au code source les scopes définis dans la configuration du projet.</p>
 * 
 * <p><strong>Ce goal modifie les fichiers originaux du projet et doit
 * être utilisé avec précaution.</strong> En particulier,
 * il est recommandé d'exécuter les goals <code>check-syntax</code>
 * et <code>verify</code> préalablement.</p>
 * 
 */
public class ApplyTask extends AbstractJScopeTask {

    @Override
    protected void doWithFile(final File file) throws TaskErrorException {
        final ParseResult result = this.parseFile(file, false);
        if(result.getFileUnit().hasScopeUnits()){
            LOGGER.info("Overriding file: " + result.getFileUnit().getFile());
            FileUnit fileUnit;
            try {
                fileUnit = result.getFileUnit().applyScopes(this.scopeManager);
            } catch (final ScopeNotFoundException e) {
                throw new TaskErrorException(e);
            }
            try {
                fileUnit.write(this.getEncoding(file));
            } catch (final IOException e) {
                throw new TaskErrorException("Can't write file unit: " + fileUnit.getFile().getAbsolutePath(), e);
            }
        }
    }

}
