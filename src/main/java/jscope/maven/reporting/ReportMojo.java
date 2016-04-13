package jscope.maven.reporting;

import jscope.task.JScopeTask;
import jscope.task.ReportTask;

import java.io.File;
import java.util.Locale;

/**
 * <p>Génère une documentation HTML contenant un résumé
 * des scopes JScope du projet.</p>
 * <p>Chaque scope est détaillé dans une page HTML dédiée,
 * et chaque fichier affecté par un scope est affiché
 * avec les fragment de code sous scope mis en évidence.</p>
 * <p>Ce goal se déclenche automatiquement à la phase <code>site</code>;
 * le rapport généré est automatiquement inclus au site généré,
 * sous la rubrique "Documentation sur le projet - Rapports Projet - Rapport JScope".</p>
 *
 * @goal report
 * @aggregator
 */
public class ReportMojo extends AbstractJScopeReportMojo {

    private static final String JSCOPE_REPORT_DESCRIPTION = "jscope.report.description";

    private static final String JSCOPE_REPORT_NAME = "jscope.report.name";

    private static final String JSCOPE_REPORTS_OUTPUT_NAME = "jscope-reports/index";

    /**
     * Répertoire de génération du rapport JScope.
     *
     * @parameter property="jscope.reports.outputDirectory" default-value="${project.reporting.outputDirectory}/jscope-reports"
     * @required
     * @readonly
     */
    private File outputDirectory;

    @Override
    protected JScopeTask createTask() {
        final ReportTask reportTask = new ReportTask();
        reportTask.setVelocityEngine(this.velocityComponent.getEngine());
        reportTask.setOutputDirectory(this.outputDirectory);
        return reportTask;
    }

    public String getName(final Locale locale) {
        return this.getBundle(locale).getString(JSCOPE_REPORT_NAME);
    }

    public String getDescription(final Locale locale) {
        return this.getBundle(locale).getString(JSCOPE_REPORT_DESCRIPTION);
    }

    public String getOutputName() {
        return JSCOPE_REPORTS_OUTPUT_NAME;
    }

    @Override
    public String getCategoryName() {
        return CATEGORY_PROJECT_REPORTS;
    }

    @Override
    public boolean isExternalReport() {
        return true;
    }

    @Override
    protected String getOutputDirectory() {
        return this.outputDirectory.getAbsolutePath();
    }

}
