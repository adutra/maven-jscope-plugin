package jscope.maven;

import java.util.Map;

import jscope.task.JScopeTask;
import jscope.task.VerifyTask;
import org.apache.maven.shared.release.env.DefaultReleaseEnvironment;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.exec.MavenExecutor;

/**
 * <p>Le goal <code>verify</code> permet de tester une modification dans les périmètres fonctionnels du projet,
 * avant de l'appliquer définitivement.</p>
 * <p><strong>Ce goal est primordial avant d'effectuer tout autre goal pouvant altérer le code
 * car il permet d'attester que le projet compile et que les tests unitaires et d'intégration
 * résussissent malgré la modification des périmètres.</strong></p>
 * <p>Concrètement, le goal vérifie l'intégrité du projet en exécutant un goal "mvn verify" sur une copie
 * du projet.</p>
 * <p>Ce goal effectue les actions suivantes:</p>
 * <ol>
 * <li>Copie de tous les fichiers du projet dans un répertoire temporaire;</li>
 * <li>Application des périmètres sur la copie du projet;
 * tous les fichiers ayant des scopes JScopes sont modifiés (mais leurs originaux demeurent inchangés);</li>
 * <li>Exécution, sur la copie du projet, du goal "mvn verify" (ce goal comprend entre
 * autres: la compilation des sources, des sources de test, les tests unitaires et
 * les tests d'intégration).</li>
 * </ol>
 * <p>L'exécution de ce goal est sans danger pour les sources du projet.</p>
 * @goal verify
 * @execute phase="generate-sources"
 * @aggregator
 */
public class VerifyMojo extends AbstractJScopeMojo {

    /**
     * Composant d'aide à l'exécution de Maven.
     * @component role="org.apache.maven.shared.release.exec.MavenExecutor"
     */
    protected Map<String, MavenExecutor> mavenExecutors;

    /**
     * Arguments additionnels passés à l'exécution de Maven, séparés par des espaces.
     * @parameter property="arguments"
     */
    protected String arguments;

    @Override
    protected JScopeTask createTask() {
        final VerifyTask task = new VerifyTask();
        task.setArguments(this.arguments);
        final ReleaseEnvironment releaseEnvironment = new DefaultReleaseEnvironment();
        final MavenExecutor mavenExecutor = this.mavenExecutors.get(releaseEnvironment.getMavenExecutorId());
        task.setMavenExecutor(mavenExecutor);
        return task;
    }

}
