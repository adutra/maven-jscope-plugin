package jscope.maven;

import com.pyx4j.log4j.MavenLogAppender;
import jscope.configuration.Scope;
import jscope.configuration.ScopeManager;
import jscope.exception.JScopeException;
import jscope.exception.TaskFailedException;
import jscope.maven.util.JScopeScanDirsCollector;
import jscope.task.JScopeTask;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Alexandre Dutra
 */
public abstract class AbstractJScopeMojo extends AbstractMojo {

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
     * @parameter
     * @required
     */
    protected List<Scope> scopes;

    /**
     * Répertoire de travail et de génération des fichiers JScope.
     * 
     * @parameter property="outputDirectory" default-value="${project.build.directory}/jscope"
     * @required
     */
    protected File outputDirectory;

    /**
     * L'encodage par défaut des fichiers du projet.
     * 
     * @parameter property="encoding" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;

    /**
     * Encodages par défaut et par type de fichier. Les encodages
     * spécifiés ici priment sur l'encodage par défaut spécifié
     * par le paramètre <code>encoding</code>.
     * La clé de la Map doit représenter une extension de fichier,
     * et la valeur un encodage reconnu.
     * Par défaut, la Map contient l'entrée suivante: <code>properties = ISO-8859-1</code>.
     * 
     * @parameter
     */
    protected Map<String, String> encodings = new HashMap<String, String>();
    {
        this.encodings.put("properties", "ISO-8859-1");
    }

    /**
     * Projet Maven en cours de construction.
     * 
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The projects in the reactor for aggregation report.
     * @parameter property="reactorProjects"
     * @readonly
     */
    protected List<MavenProject> reactorProjects;

    /**
     * Composant Plexus encapsulant un {@link VelocityEngine}.
     * @component role-hint="jscope-velocity"
     * @required
     * @readonly
     */
    protected VelocityComponent velocityComponent;

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

    public void execute() throws MojoExecutionException, MojoFailureException {

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
            final JScopeTask task = this.createTask();
            task.setProjectDirectory(this.project.getBasedir());
            task.setProjectName(this.project.getName());
            task.setJScopeScanDirs(this.getJScopeScanDirs());
            task.setEncoding(this.encoding);
            task.setEncodings(this.encodings);
            task.setScopeManager(new ScopeManager(this.scopes));
            task.setOutputDirectory(this.outputDirectory);
            try {
                task.call();
            } catch (final TaskFailedException e) {
                throw new MojoFailureException("Mojo execution failed: " + e.getMessage());
            } catch (final JScopeException e) {
                throw new MojoExecutionException("Mojo execution error: " + e.getMessage(), e);
            }
        } finally {
            MavenLogAppender.endPluginLog(this);
        }

    }

    protected abstract JScopeTask createTask();

    protected Set<File> getJScopeScanDirs() {
        if (this.sourcePaths != null) {
            return this.sourcePaths;
        }
        return new JScopeScanDirsCollector().collectJScopeScanDirs(this.project, true, this.reactorProjects);
    }


}
