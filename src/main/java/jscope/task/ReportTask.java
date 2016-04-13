package jscope.task;

import jscope.configuration.Scope;
import jscope.exception.JScopeVelocityException;
import jscope.exception.TaskErrorException;
import jscope.exception.TaskFailedException;
import jscope.html.HtmlReport;
import jscope.model.FileUnit;
import jscope.parser.ParseResult;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

/**
 * <p>Génère une documentation HTML contenant un résumé
 * des scopes JScope du projet.</p>
 * <p>Chaque scope est détaillé dans une page HTML dédiée,
 * et chaque fichier affecté par un scope est affiché
 * avec les fragment de code sous scope mis en évidence.</p>
 */
public class ReportTask extends AbstractHtmlJScopeTask {

    private static final class ScopeComparator implements Comparator<Scope> {
        public int compare(final Scope o1, final Scope o2) {
            return o1.getId().compareTo(o2.getId());
        }
    }

    private static final class FileUnitComparator implements Comparator<FileUnit> {
        public int compare(final FileUnit o1, final FileUnit o2) {
            return o1.getFile().compareTo(o2.getFile());
        }
    }

    private final Map<Scope, Set<FileUnit>> fileUnitsByScopeMap =
            new TreeMap<Scope, Set<FileUnit>>(new ScopeComparator());


    @Override
    protected void preProcess() throws TaskErrorException, TaskFailedException {
        super.preProcess();
        //should be done by a mvn clean
        //this.cleanJScopeDirectory();
        this.initMap();
    }

    @Override
    protected void doWithFile(final File file) throws TaskErrorException {
        final ParseResult result = this.parseFile(file, false);
        final FileUnit fileUnit = result.getFileUnit();
        for (final Scope scope : this.scopeManager.getScopes()) {
            if (fileUnit.containsScope(scope)) {
                final Set<FileUnit> set = this.fileUnitsByScopeMap.get(scope);
                set.add(fileUnit);
            }
        }
    }

    @Override
    protected void postProcess() throws TaskErrorException {
        this.copyResources();
        this.createIndexFile();
        this.createHtml();
    }

    private void initMap() {
        for (final Scope scope : this.scopeManager.getScopes()) {
            this.fileUnitsByScopeMap.put(scope, new TreeSet<FileUnit>(new FileUnitComparator()));
        }
    }

    private void createIndexFile() throws TaskErrorException {
        final File index = new File(this.outputDirectory, "index.html");
        try {
            this.vu.applyTemplate(
                    "vm/report/index.vm",
                    index, this.projectName,
                    "JScope Report",
                    "Scopes Overview ", "",
                    "scopesMap", this.getScopesMap());
        } catch (final JScopeVelocityException e) {
            throw new TaskErrorException("Can't apply Velocity template to file: " + index, e);
        }
    }

    private void createHtml() throws TaskErrorException {

        final Map<Scope, Set<String>> scopesMap = this.getScopesMap();

        for (final Entry<Scope, Set<FileUnit>> entry : this.fileUnitsByScopeMap.entrySet()) {

            final Scope scope = entry.getKey();

            final File scopeDir = new File(this.outputDirectory, scope.getId());
            scopeDir.mkdirs();

            this.createScopeIndexFile(scope, scopeDir);

            final Set<FileUnit> units = entry.getValue();
            final HtmlReport htmlReport = new HtmlReport(Collections.singleton(scope));

            for (final FileUnit fileUnit : units) {

                htmlReport.report(fileUnit);

                final String currentPath = this.relativizePath(this.projectDirectory, fileUnit.getFile());
                final File htmlFile = new File(scopeDir, currentPath + ".html");
                final File parent = htmlFile.getParentFile();
                parent.mkdirs();

                LOGGER.info(JSCOPE_PREFIX + "Generating file: " + htmlFile);

                final int repeat = StringUtils.countMatches(currentPath, "/") + 1;
                final String prefix = StringUtils.repeat("../", repeat);
                final String brushName = fileUnit.getFileType().getExtensions()[0];

                try {
                    this.vu.applyTemplate(
                            "vm/report/unit.vm",
                            htmlFile, this.projectName,
                            "JScope Report",
                            "Scope " + scope.getId() + " - File " + currentPath,
                            prefix,
                            "currentScope", scope,
                            "scopesMap", scopesMap,
                            "currentPath", currentPath,
                            "brushName", brushName,
                            "content", htmlReport.getStringBuffer(),
                            "highlightedLines", htmlReport.getHighlightedLines()
                    );
                } catch (final JScopeVelocityException e) {
                    throw new TaskErrorException("Can't apply Velocity template to file: " + htmlFile, e);
                }
            }
        }
    }

    private Map<Scope, Set<String>> getScopesMap() {
        final Map<Scope, Set<String>> scopesMap = new TreeMap<Scope, Set<String>>(new ScopeComparator());
        for (final Entry<Scope, Set<FileUnit>> entry : this.fileUnitsByScopeMap.entrySet()) {
            final Scope scope = entry.getKey();
            final Set<FileUnit> fileUnits = entry.getValue();
            final Set<String> paths = new TreeSet<String>();
            for (final FileUnit fileUnit : fileUnits) {
                final String displayPath = this.relativizePath(this.projectDirectory, fileUnit.getFile());
                paths.add(displayPath);
            }
            scopesMap.put(scope, paths);
        }
        return scopesMap;
    }

    private void createScopeIndexFile(final Scope scope, final File scopeDir) throws TaskErrorException {
        final File scopeIndex = new File(scopeDir, "index.html");
        final Set<FileUnit> units = this.fileUnitsByScopeMap.get(scope);
        final List<String> paths = new ArrayList<String>();
        for (final FileUnit fileUnit : units) {
            final String displayPath = this.relativizePath(this.projectDirectory, fileUnit.getFile());
            paths.add(displayPath);
        }
        try {
            this.vu.applyTemplate(
                    "vm/report/scope.vm",
                    scopeIndex, this.projectName,
                    "JScope Report",
                    "Scope " + scope.getId() + " Overview", "../",
                    "currentScope", scope,
                    "scopesMap", this.getScopesMap());
        } catch (final JScopeVelocityException e) {
            throw new TaskErrorException("Can't apply Velocity template to file: " + scopeIndex, e);
        }
    }

}
