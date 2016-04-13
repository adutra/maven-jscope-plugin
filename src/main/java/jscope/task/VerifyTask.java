package jscope.task;

import java.io.File;
import java.io.IOException;

import jscope.parser.ParseResult;
import jscope.model.FileUnit;
import org.apache.maven.shared.release.ReleaseResult;
import org.apache.maven.shared.release.env.DefaultReleaseEnvironment;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.exec.MavenExecutor;
import org.apache.maven.shared.release.exec.MavenExecutorException;

import jscope.exception.ScopeNotFoundException;
import jscope.exception.TaskErrorException;

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
 */
public class VerifyTask extends AbstractJScopeTask {

    /**
     * Arguments additionnels passés à l'exécution de Maven, séparés par des espaces.
     */
    private String arguments;

    /**
     * 
     */
    private MavenExecutor mavenExecutor;


    public String getArguments() {
        return this.arguments;
    }


    public void setArguments(final String arguments) {
        this.arguments = arguments;
    }


    public MavenExecutor getMavenExecutor() {
        return this.mavenExecutor;
    }


    public void setMavenExecutor(final MavenExecutor mavenExecutor) {
        this.mavenExecutor = mavenExecutor;
    }

    @Override
    protected void preProcess() throws TaskErrorException {
        this.cleanJScopeDirectory();
        this.copyToJScopeDirectory();
    }

    @Override
    protected void doWithFile(final File file) throws TaskErrorException{

        final ParseResult result = this.parseFile(file, false);

        FileUnit fileUnit = result.getFileUnit();

        if(fileUnit.hasScopeUnits()){

            LOGGER.info(JSCOPE_PREFIX + "Transforming file: " + fileUnit.getFile());
            try {
                fileUnit = fileUnit.applyScopes(this.scopeManager);
            } catch (final ScopeNotFoundException e) {
                throw new TaskErrorException(e);
            }

            final String relativePath = this.relativizePath(this.projectDirectory, fileUnit.getFile());
            final File backupFile = new File(this.outputDirectory, relativePath);

            try {
                fileUnit.write(backupFile, this.encoding);
            } catch (final IOException e) {
                throw new TaskErrorException("Can't write file unit: " + fileUnit.getFile().getAbsolutePath(), e);
            }
        }
    }

    @Override
    protected void postProcess() throws TaskErrorException {
        this.compile();
    }

    private void compile() throws TaskErrorException {
        LOGGER.info(JSCOPE_PREFIX + "Running tests for project at: " + this.outputDirectory);
        final ReleaseEnvironment releaseEnvironment = new DefaultReleaseEnvironment();
        final ReleaseResult result = new ReleaseResult();
        try {
            this.mavenExecutor.executeGoals(
                this.outputDirectory,
                //"jscope:jscope-maven-plugin::check-syntax " +
                "verify",
                releaseEnvironment,
                false,
                this.prepareArguments(),
                result);
        } catch (final MavenExecutorException e) {
            LOGGER.error(JSCOPE_PREFIX + "Compilation failure: " + e.getMessage(), e);
            throw new TaskErrorException( e.getMessage(), e );
        }
        LOGGER.info(JSCOPE_PREFIX + "Tests OK");
    }

    private String prepareArguments() {
        return
        //StringUtils.join(new String[]{"-D" + JSCOPE_FLAG, this.arguments}, " ");
        this.arguments;
    }

}
