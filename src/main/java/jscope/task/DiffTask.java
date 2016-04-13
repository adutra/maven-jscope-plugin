package jscope.task;

import jscope.configuration.ScopeManager;
import jscope.exception.JScopeVelocityException;
import jscope.exception.ScopeNotFoundException;
import jscope.exception.TaskErrorException;
import jscope.exception.TaskFailedException;
import jscope.html.HtmlDiff;
import jscope.model.FileUnit;
import jscope.parser.ParseResult;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>
 * Génère une documentation HTML contenant le "diff" entre deux ensembles de scopes JScope.
 * </p>
 * <p>
 * L'ensemble "de référence" est fourni par les scopes définis dans la configuration du projet.
 * </p>
 * <p>
 * L'ensemble "de comparaison" peut être fourni:
 * </p>
 * <ol>
 * <li>Soit par un deuxième fichier jscope à l'aide du paramètre <code>jscopeFile2</code>;</li>
 * <li>Soit par surcharge des scopes de l'ensemble de référence, à l'aide du paramètre <code>scopeOverrides</code>.</li>
 * </ol>
 * <p>
 * Si l'on fournit deux fichiers JScope, ils doivent contenir exactement les mêmes scopes; seuls leurs statuts peuvent
 * différer (<code>SCOPED</code>, <code>UNSCOPED</code>, <code>VALIDATED</code>).
 * </p>
 * <p>
 * Si l'on fournit une surcharge à l'aide du paramètre <code>scopeOverrides</code>, la syntaxe du paramètre est la
 * suivante:
 * </p>
 * <pre>
 * -DscopeOverrides={SCOPE_ID}:{SCOPE_STATUS};{SCOPE_ID}:{SCOPE_STATUS}
 * </pre>
 * <p>
 * Exemple:
 * </p>
 * <pre>
 * -DscopeOverrides=AB-1234:UNSCOPED;CD-5678:SCOPED
 * </pre>
 * <p>
 * Dans ce cas, les scopes non cités sur la ligne de commande restent inchangés. De plus, il n'est pas possible
 * d'ajouter des scopes avec cette méthode.
 * </p>
 * 
 */
public class DiffTask extends AbstractHtmlJScopeTask {


    /**
     * Le deuxième fichier JScope servant de comparaison
     * par rapport au fichier "de référence".
     * 
     */
    private File jscopeFile2;

    /**
     * <p>Une liste de scopes et de statuts, séparés
     * par des points-virgule, représentant
     * l'ensemble de comparaison par rapport à
     * l'ensemble de référence.</p>
     * <p>
     * La syntaxe du paramètre est la
     * suivante:
     * </p>
     * <pre>
     * -DscopeOverrides={SCOPE_ID}:{SCOPE_STATUS};{SCOPE_ID}:{SCOPE_STATUS}
     * </pre>
     * <p>
     * Exemple:
     * </p>
     * <pre>
     * -DscopeOverrides=AB-1234:UNSCOPED;CD-5678:SCOPED
     * </pre>
     * <p>
     * Dans ce cas, les scopes non cités sur la ligne de commande restent inchangés. De plus, il n'est pas possible
     * d'ajouter des scopes avec cette méthode.
     * </p>
     * 
     */
    private String scopeOverrides;

    /**
     * Le <code>{@link ScopeManager}</code> de comparaison,
     * construit ou bien à l'aide du fichier
     * JScope de comparaison, ou bien
     * à l'aide du paramètre <code>scopeOverrides</code>.
     */
    private ScopeManager scopeManager2;

    private final Set<FileUnit> fileUnits = new TreeSet<FileUnit>(new Comparator<FileUnit>() {
        public int compare(final FileUnit o1, final FileUnit o2) {
            return o1.getFile().compareTo(o2.getFile());
        }
    });

    public File getJscopeFile2() {
        return this.jscopeFile2;
    }


    public void setJscopeFile2(final File jscopeFile2) {
        this.jscopeFile2 = jscopeFile2;
    }


    public String getScopeOverrides() {
        return this.scopeOverrides;
    }


    public void setScopeOverrides(final String scopeOverrides) {
        this.scopeOverrides = scopeOverrides;
    }


    public ScopeManager getScopeManager2() {
        return this.scopeManager2;
    }


    public void setScopeManager2(final ScopeManager scopeManager2) {
        this.scopeManager2 = scopeManager2;
    }

    @Override
    protected void preProcess() throws TaskErrorException, TaskFailedException {
        super.preProcess();
        this.cleanJScopeDirectory();
    }

    @Override
    protected void doWithFile(final File file) throws TaskErrorException {
        final ParseResult result = this.parseFile(file, false);
        final FileUnit fileUnit = result.getFileUnit();
        if (fileUnit.hasScopeUnits()) {
            this.fileUnits.add(fileUnit);
        }
    }

    @Override
    protected void postProcess() throws TaskErrorException {
        this.createDiffs();
        this.copyResources();
        this.createIndexFile();
    }

    private void createDiffs() throws TaskErrorException {
        final HtmlDiff htmlDiff = new HtmlDiff(this.scopeManager, this.scopeManager2);
        for (final FileUnit fileUnit : this.fileUnits) {


            final String currentPath = this.relativizePath(this.projectDirectory, fileUnit.getFile());
            final File htmlFile = new File(this.outputDirectory, currentPath + ".html");
            final File parent = htmlFile.getParentFile();
            parent.mkdirs();

            LOGGER.info(JSCOPE_PREFIX + "Generating file: " + htmlFile);
            try {
                htmlDiff.diff(fileUnit);
            } catch (final ScopeNotFoundException e) {
                throw new TaskErrorException(e);
            }

            final int repeat = StringUtils.countMatches(currentPath, "/");
            final String prefix = StringUtils.repeat("../", repeat);
            final String brushName = fileUnit.getFileType().getExtensions()[0];
            try {
                this.vu.applyTemplate(
                    "vm/diff/unit.vm",
                    htmlFile, this.projectName,
                    "JScope Diff",
                    "File " + currentPath,
                    prefix,
                    "currentPath", currentPath,
                    "brushName", brushName,
                    "contentLeft", htmlDiff.getLeftBuffer(),
                    "contentRight", htmlDiff.getRightBuffer(),
                    "highlightedLines", htmlDiff.getHighlightedLines(),
                    "paths", this.getPaths()
                );
            } catch (final JScopeVelocityException e) {
                throw new TaskErrorException("Can't apply Velocity template to file: " + htmlFile, e);
            }
        }
    }

    private void createIndexFile() throws TaskErrorException {
        final File index = new File(this.outputDirectory, "index.html");
        try {
            this.vu.applyTemplate(
                "vm/diff/index.vm",
                index, this.projectName,
                "JScope Diff",
                "Scopes Overview ", "",
                "scopeManager1", this.scopeManager,
                "scopeManager2", this.scopeManager2,
                "paths", this.getPaths());
        } catch (final JScopeVelocityException e) {
            throw new TaskErrorException("Can't apply Velocity template to file: " + index, e);
        }
    }

    private Set<String> getPaths() {
        final Set<String> paths = new TreeSet<String>();
        for (final FileUnit fileUnit : this.fileUnits) {
            final String path = fileUnit.getFile().getPath().replace("\\", "/");
            paths.add(path);
        }
        return paths;
    }

}
