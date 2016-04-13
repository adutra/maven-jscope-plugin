package jscope.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import jscope.configuration.Scope;
import jscope.exception.TaskFailedException;
import jscope.configuration.Status;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import jscope.exception.TaskErrorException;

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
 * <p>Ce goal effectue les actions suivantes pour chacune des combinatoires de scopes
 * définis dans le fichier <code>jscope.xml</code>:</p>
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
 */
public class VerifyAllTask extends VerifyTask {


    @Override
    public Void call() throws TaskErrorException, TaskFailedException {
        final List<Scope> scopes = this.scopeManager.getScopesWithStatus(Status.SCOPED, Status.UNSCOPED);
        for (int bitMask = 0; bitMask < 1 << scopes.size(); bitMask++) {

            for (int bitIndex = 0; bitIndex < scopes.size(); bitIndex++) {
                scopes.get(bitIndex).setStatus( (1 << bitIndex & bitMask) != 0 ? Status.SCOPED : Status.UNSCOPED );
            }

            LOGGER.info(JSCOPE_PREFIX);
            LOGGER.info(JSCOPE_PREFIX + "Verifying combination: " + this.scopeManager.getScopes());
            LOGGER.info(JSCOPE_PREFIX);

            super.call();

        }
        return null;
    }

    @Override
    protected void preProcess() throws TaskErrorException {
        super.preProcess();
        final List<Scope> scopes = this.scopeManager.getScopesWithStatus(Status.SCOPED, Status.UNSCOPED);
        this.modifyPom(scopes);
    }

    private void modifyPom(final List<Scope> scopes) throws TaskErrorException {
        final File copiedPom = new File(this.getOutputDirectory(), "pom.xml");
        final String pomEncoding = this.getEncoding(".xml");
        try {
            final Model model = new MavenXpp3Reader().read(
                new InputStreamReader(
                    new FileInputStream(copiedPom), pomEncoding));
            this.modifyScopes(model, scopes);
            new MavenXpp3Writer().write(
                new FileWriterWithEncoding(copiedPom, pomEncoding), model);
        } catch (final UnsupportedEncodingException e) {
            throw new TaskErrorException(e);
        } catch (final FileNotFoundException e) {
            throw new TaskErrorException(e);
        } catch (final IOException e) {
            throw new TaskErrorException(e);
        } catch (final XmlPullParserException e) {
            throw new TaskErrorException(e);
        }
    }

    private void modifyScopes(final Model model, final List<Scope> scopes) throws TaskErrorException {
        final Plugin jscopePlugin = this.findJScopePlugin(model);
        final Xpp3Dom config = (Xpp3Dom) jscopePlugin.getConfiguration();
        final Xpp3Dom scopesElement = config.getChild("scopes");
        if (scopesElement != null) {
            final Xpp3Dom[] children = scopesElement.getChildren();
            for (final Scope scope : scopes) {
                boolean found = false;
                for (final Xpp3Dom scopeElement : children) {
                    final String id = scopeElement.getChild("id").getValue();
                    if(scope.getId().equals(id)) {
                        scopeElement.getChild("status").setValue(scope.getStatus().name());
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    throw new TaskErrorException("Can't find scope: " + scope.getId());
                }
            }
        }
    }

    private Plugin findJScopePlugin(final Model model) throws TaskErrorException {
        @SuppressWarnings("unchecked")
        final List<Plugin> plugins = model.getBuild().getPlugins();
        for (final Plugin plugin : plugins) {
            if(plugin.getArtifactId().equals("jscope-maven-plugin") &&
                plugin.getGroupId().equals("jscope")) {
                return plugin;
            }
        }
        throw new TaskErrorException("Can't find JScope plugin");
    }

}
