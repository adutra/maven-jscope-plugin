package jscope.maven;

import jscope.task.CheckSyntaxTask;
import jscope.task.JScopeTask;

/**
 * <p>Vérifie l'intégrité de la syntaxe des fichiers contenant des scopes JScope.</p>
 * <p><strong>Ce goal est primordial avant d'effectuer tout autre goal pouvant altérer le code
 * car il permet d'attester que les fichiers contenant des scopes JScope
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
 * @goal check-syntax
 * @phase process-sources
 * @aggregator
 */
public class CheckSyntaxMojo extends AbstractJScopeMojo {

    @Override
    protected JScopeTask createTask() {
        return new CheckSyntaxTask();
    }

}
