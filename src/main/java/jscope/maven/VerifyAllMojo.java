package jscope.maven;

import org.apache.maven.shared.release.env.DefaultReleaseEnvironment;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.exec.MavenExecutor;

import jscope.task.JScopeTask;
import jscope.task.VerifyAllTask;

/**
 * <p>Le goal <code>jscope:verify-all</code> permet de tester toutes les variantes possibles des statuts
 * des périmètres fonctionnels du projet, avant d'en appliquer une définitivement. Il permet ainsi
 * de s'assurer qu'une fonctionnalité peut être déscopée ou scopée à tout moment,
 * sans mettre en danger la stabilité du projet.</p>
 * <p><strong>Ce goal est primordial avant d'effectuer tout autre goal pouvant altérer le code
 * car il permet d'attester que le projet compile et que les tests unitaires et d'intégration
 * résussissent après modification des périmètres.</strong></p>
 * <p>Vérifie l'intégrité du projet en exécutant un goal "<code>mvn verify</code>" sur une copie
 * du projet sur laquelle une combinaison de statuts est préalablement appliquée.</p>
 * <p>Contrairement au goal <code>jscope:verify</code>, ce goal jouera la commande "<code>mvn verify</code>"
 * sur <em>toutes les combinaisons de statuts de périmètres possibles</em>, et pas seulement sur la combinaison
 * couramment indiquée dans le fichier <code>pom.xml</code>.</p>
 * <p>Exemple: soit les périmètres suivants: </p>
 * <pre>
 * &lt;scope implementation="Scope">
 *     &lt;id>JIRA-1&lt;/id>
 *     &lt;status>SCOPED&lt;/status>
 * &lt;/scope>
 * &lt;scope implementation="Scope">
 *     &lt;id>JIRA-2&lt;/id>
 *     &lt;status>UNSCOPED&lt;/status>
 * &lt;/scope>
 * &lt;scope implementation="Scope">
 *     &lt;id>JIRA-3&lt;/id>
 *     &lt;status>VALIDATED&lt;/status>
 * &lt;/scope>
 * </pre>
 * <p>Le goal "<code>mvn verify</code>" sera exécuté 4 fois avec les combinatoires suivantes:</p>
 * <table>
 * <tr><th>Scope ID</th><th>Combinatoire 1</th><th>Combinatoire 2</th><th>Combinatoire 3</th><th>Combinatoire 4</th></tr>
 * <tr><td>JIRA-1</td><td>SCOPED</td><td>UNSCOPED</td><td>SCOPED</td><td>UNSCOPED</td></tr>
 * <tr><td>JIRA-2</td><td>SCOPED</td><td>SCOPED</td><td>UNSCOPED</td><td>UNSCOPED</td></tr>
 * <tr><td>JIRA-3</td><td>VALIDATED</td><td>VALIDATED</td><td>VALIDATED</td><td>VALIDATED</td></tr>
 * </table>
 * <p>(Les scopes en statuts VALIDATED ne participent pas à la combinatoire.)</p>
 * <p>Ce goal effectue les actions suivantes pour chacune des combinatoires des scopes définis dans la configuration du projet:</p>
 * <ol>
 * <li>Clean du répertoire temporaire JScope;</li>
 * <li>Copie de tous les fichiers du projet dans un répertoire temporaire;</li>
 * <li>Application de la combinatoire sur la copie du projet;
 * tous les fichiers ayant des scopes JScopes sont modifiés (mais leurs originaux demeurent inchangés),
 * ainsi que le pom.xml du projet;</li>
 * <li>Exécution, sur la copie du projet, du goal "<code>mvn verify</code>" (ce goal comprend entre
 * autres: la compilation des sources, des sources de test, les tests unitaires et
 * les tests d'intégration).</li>
 * </ol>
 * <p>L'exécution de ce goal est sans danger pour les sources du projet.</p>
 * @goal verify-all
 * @execute phase="generate-sources"
 * @aggregator
 */
public class VerifyAllMojo extends VerifyMojo {

    @Override
    protected JScopeTask createTask() {
        final VerifyAllTask task = new VerifyAllTask();
        task.setArguments(this.arguments);
        final ReleaseEnvironment releaseEnvironment = new DefaultReleaseEnvironment();
        final MavenExecutor mavenExecutor = this.mavenExecutors.get(releaseEnvironment.getMavenExecutorId());
        task.setMavenExecutor(mavenExecutor);
        return task;
    }

}
