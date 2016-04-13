package jscope.maven.reporting;

import com.pyx4j.log4j.MavenLogAppender;
import jscope.configuration.Scope;
import jscope.configuration.ScopeManager;
import jscope.configuration.Status;
import jscope.exception.JScopeException;
import jscope.maven.util.JScopeScanDirsCollector;
import jscope.task.JScopeTask;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.File;
import java.util.*;

/**
 * @author Alexandre Dutra
 *
 */
public abstract class AbstractJScopeReportMojo extends AbstractMavenReport {

    /**
     * <p>Liste des périmètres fonctionnels du projet.</p>
     * <p>Voici un exemple de configuration:</p>
     * <pre>
     * &lt;configuration>
     *     &lt;scopes>
     *         &lt;scope implementation="Scope">
     *             &lt;id>JIRA-1&lt;/id>
     *             &lt;status>SCOPED&lt;/status>
     *             &lt;description>JIRA 1&lt;/description>
     *         &lt;/scope>
     *         &lt;scope implementation="Scope">
     *             &lt;id>JIRA-2&lt;/id>
     *             &lt;status>UNSCOPED&lt;/status>
     *             &lt;description>JIRA 2&lt;/description>
     *         &lt;/scope>
     *     &lt;/scopes>
     * &lt;/configuration>
     * </pre>
     * <p>Si ce paramètre n'est pas fourni, on essaiera de lire la liste des périmètres
     * depuis la déclaration du plugin JScope dans la section "build" du projet.
     * Si une telle déclaration n'existe pas, ou si sa configuration ne peut être lue,
     * une erreur est générée à l'exécution du plugin.</p>
     * @parameter
     */
    protected List<Scope> scopes;

    /**
     * Projet Maven en cours de construction.
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @component
     * @required
     * @readonly
     */
    protected Renderer siteRenderer;

    /**
     * Composant Plexus encapsulant un {@link VelocityEngine}.
     * @component role-hint="jscope-velocity"
     * @required
     * @readonly
     */
    protected VelocityComponent velocityComponent;

    /**
     * The projects in the reactor for aggregation report.
     * @parameter property="reactorProjects"
     * @readonly
     */
    protected List<MavenProject> reactorProjects;

    /**
     * L'encodage par défaut des fichiers du projet.
     * @parameter property="encoding" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;

    /**
     * Encodages par défaut et par type de fichier. Les encodages
     * spécifiés ici priment sur l'encodage par défaut spécifié
     * par le paramètre <code>encoding</code>.
     * La clé de la Map doit représenter une extension de fichier,
     * et la valeur un encodage reconnu.
     * Par défaut, la Map contient l'entrée suivante:
     * <code>properties = ISO-8859-1</code>.
     * @parameter
     */
    protected Map<String,String> encodings = new HashMap<String, String>();
    {
        this.encodings.put("properties", "ISO-8859-1");
    }

    /**
     * Répertoires de base où chercher des fichiers JScope.
     * Si non spécifié les répertoires de sources et de ressources
     * de compilation et de test seront utilisés, à savoir:
     * <ol>
     * <li>src/main/java</li>
     * <li>src/main/resources</li>
     * <li>src/test/java</li>
     * <li>src/test/resources</li>
     * </ol>
     * @parameter
     */
    protected Set<File> sourcePaths;


    /**
     * Spécifie si le plugin JScope doit être désactivé.
     * @parameter property="jscope.skip" default-value="false"
     */
    protected boolean skip;


    @Override
    protected void executeReport(final Locale locale) throws MavenReportException {

        if (this.skip) {
            this.getLog().info(JScopeTask.JSCOPE_PREFIX + "Skipping JScope Report generation");
            return;
        }

        if(!this.project.isExecutionRoot()) {
            //this.getLog().info(JScopeTask.JSCOPE_PREFIX + "Mojo is an aggregator and current project is not the execution root: " + this.project.getName());
            return;
        }

        MavenLogAppender.startPluginLog(this);

        try {

            this.initScopes();

            final JScopeTask task = this.createTask();
            task.setProjectDirectory(this.project.getBasedir());
            task.setProjectName(this.project.getName());
            task.setJScopeScanDirs(this.getJScopeScanDirs());
            task.setEncoding(this.encoding);
            task.setEncodings(this.encodings);
            task.setScopeManager(new ScopeManager(this.scopes));
            try {
                task.call();
            } catch (final JScopeException e) {
                throw new MavenReportException("Mojo execution error: " + e.getMessage(), e);
            }
        } finally {
            MavenLogAppender.endPluginLog(this);
        }

    }

    protected void initScopes() throws MavenReportException {
        if(this.scopes == null) {
            final Plugin jscopePlugin = this.findJScopeBuildPlugin();
            if(jscopePlugin != null) {
                this.getLog().info(JScopeTask.JSCOPE_PREFIX + "Creating scopes from build plugin configuration");
                this.scopes = this.createScopesFromBuildPlugin(jscopePlugin);
            }
            if(this.scopes == null) {
                throw new MavenReportException("No scopes defined");
            }
        }
    }

    protected List<Scope> createScopesFromBuildPlugin(final Plugin jscopePlugin) {
        final List<Scope> scopes = new ArrayList<Scope>();
        final Xpp3Dom config = (Xpp3Dom) jscopePlugin.getConfiguration();
        final Xpp3Dom scopesElement = config.getChild("scopes");
        if (scopesElement != null) {
            final Xpp3Dom[] children = scopesElement.getChildren();
            for (final Xpp3Dom scopeElement : children) {
                final Scope scope = this.createScope(scopeElement);
                scopes.add(scope);
            }
        }
        return scopes;
    }

    protected Scope createScope(final Xpp3Dom scopeElement) {
        final Scope scope = new Scope();
        scope.setId(scopeElement.getChild("id").getValue());
        scope.setDescription(scopeElement.getChild("description").getValue());
        scope.setStatus(Status.valueOf(scopeElement.getChild("status").getValue()));
        return scope;
    }

    protected Plugin findJScopeBuildPlugin() {
        @SuppressWarnings("unchecked")
        final List<Plugin> plugins = this.getProject().getBuildPlugins();
        for (final Plugin plugin : plugins) {
            if(plugin.getArtifactId().equals("jscope-maven-plugin") &&
                plugin.getGroupId().equals("jscope")) {
                return plugin;
            }
        }
        return null;
    }

    @Override
    public boolean canGenerateReport() {
        return this.project.isExecutionRoot();
    }

    protected abstract JScopeTask createTask();

    @Override
    protected MavenProject getProject() {
        return this.project;
    }

    @Override
    protected Renderer getSiteRenderer() {
        return this.siteRenderer;
    }

    protected ResourceBundle getBundle( final Locale locale ) {
        return ResourceBundle.getBundle("jscope", locale, this.getClass().getClassLoader() );
    }

    protected Set<File> getJScopeScanDirs() throws MavenReportException {
        if (this.sourcePaths != null) {
            return this.sourcePaths;
        }
        return new JScopeScanDirsCollector().collectJScopeScanDirs(this.project, true, this.reactorProjects);
    }


}
