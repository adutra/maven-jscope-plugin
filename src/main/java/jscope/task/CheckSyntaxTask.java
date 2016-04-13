package jscope.task;

import jscope.exception.ParseException;
import jscope.exception.TaskErrorException;
import jscope.exception.TaskFailedException;
import jscope.model.FileUnit;
import jscope.parser.ParseError;
import jscope.parser.ParseResult;
import jscope.parser.SyntaxValidationHelper;

import java.io.File;
import java.util.List;

/**
 * <p>Vérifie l'intégrité de la syntaxe des fichiers contenant des scopes JScope.</p>
 * <p><strong>Cette tâche est primordiale avant d'effectuer toute autre tâche pouvant altérer le code
 * car elle permet d'attester que les fichiers contenant des scopes JScope
 * sont intègres du point de vu de leur syntaxe.</strong></p>
 * <p>Plus précisément, ce plugin vérifie les points suivants:</p>
 * <ol>
 * <li>Validité de la syntaxe JScope (balises de début et de fin de scope, etc.);</li>
 * <li>Validité de la syntaxe sous-jacente (pour certains types de fichier seulement);</li>
 * <li>Cohérence entre les scopes définis dans la configuration du projet et le code.</li>
 * </ol>
 * <p>Concernant ce dernier point, il s'agit de vérifier que:</p>
 * <ol>
 * <li>les scopes recontrés dans le code sont bien présents dans le fichier <code>jscope.xml</code>;</li>
 * <li>le statut des blocs de scope rencontrés dans le code correspond bien au statut du scope
 * tel qu'indiqué dans la configuration du projet.</li>
 * </li>
 * </ol>
 * <p>Il est préférable d'inclure ce plugin dans le pom.xml du projet:</p>
 * <pre>
 * &lt;plugin>
 *     &lt;groupId>jscope&lt;/groupId>
 *     &lt;artifactId>jscope-maven-plugin&lt;/artifactId>
 *     &lt;executions>
 *         &lt;execution>
 *             &lt;goals>
 *                 &lt;goal>check-syntax&lt;/goal>
 *             &lt;/goals>
 *         &lt;/execution>
 *     &lt;/executions>
 * &lt;/plugin>
 * </pre>
 * 
 */
public class CheckSyntaxTask extends AbstractJScopeTask {

    private boolean syntaxErrors = false;

    @Override
    protected void doWithFile(final File file) throws TaskErrorException {
        final ParseResult result = this.parseFile(file, true);
        if (result.isSuccessful()) {
            final FileUnit fileUnit = result.getFileUnit();
            if(fileUnit.hasScopeUnits()) {
                List<ParseError> errors;
                try {
                    errors = SyntaxValidationHelper.checkSyntax(fileUnit, this.scopeManager, true);
                } catch (final ParseException e) {
                    throw new TaskErrorException(e);
                }
                if(errors.isEmpty()) {
                    LOGGER.info(JSCOPE_PREFIX + "File OK: " + fileUnit.getFile());
                } else {
                    this.syntaxErrors = true;
                    this.displayParseErrors(errors);
                }
            }
        } else {
            this.syntaxErrors = true;
            this.displayParseErrors(result.getErrors());
        }
    }

    @Override
    protected void postProcess() throws TaskFailedException {
        if(this.syntaxErrors) {
            throw new TaskFailedException(JSCOPE_PREFIX + "Some files have syntax errors. See details above.");
        }
    }

}
