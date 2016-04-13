package jscope.maven.util;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;

/**
 * @author Alexandre Dutra
 *
 */
public class JScopeScanDirsCollector {

    /**
     * @param project
     * @param reactorProjects
     * @return a Set of Files representing directories to scan
     */
    public Set<File> collectJScopeScanDirs(final MavenProject project, final boolean aggregator, final List<MavenProject> reactorProjects) {
        final Set<File> sourcePaths = new LinkedHashSet<File>(this.getJScopeScanDirs(project));
        if (project.getExecutionProject() != null) {
            sourcePaths.addAll(this.getJScopeScanDirs(project.getExecutionProject()));
        }
        if (aggregator && project.isExecutionRoot()) {
            for (final MavenProject subProject : reactorProjects) {
                if (subProject != project) {
                    final Set<File> sourceRoots = this.getJScopeScanDirs(subProject);
                    if (subProject.getExecutionProject() != null) {
                        sourceRoots.addAll(this.getJScopeScanDirs(subProject.getExecutionProject()));
                    }
                    final ArtifactHandler artifactHandler = subProject.getArtifact().getArtifactHandler();
                    if ("java".equals(artifactHandler.getLanguage())) {
                        sourcePaths.addAll(sourceRoots);
                    }
                }
            }
        }
        return sourcePaths;
    }

    /**
     * @param p not null maven project
     * @return a Set of Files representing directories to scan
     */
    private Set<File> getJScopeScanDirs(final MavenProject p) {
        if ("pom".equals( p.getPackaging().toLowerCase())) {
            return Collections.emptySet();
        }
        final Set<File> fileSet = new LinkedHashSet<File>();
        if(p.getCompileSourceRoots() != null) {
            for (final Object path : p.getCompileSourceRoots()) {
                this.addFileIfExists(p, (String) path, fileSet);
            }
        }
        if(p.getTestCompileSourceRoots() != null) {
            for (final Object path : p.getTestCompileSourceRoots()) {
                this.addFileIfExists(p, (String) path, fileSet);
            }
        }
        @SuppressWarnings("unchecked")
        final List<Resource> resources = p.getResources();
        if(resources != null) {
            for (final Resource resource : resources) {
                this.addFileIfExists(p, resource.getDirectory(), fileSet);
            }
        }
        @SuppressWarnings("unchecked")
        final List<Resource> testResources = p.getTestResources();
        if(testResources != null) {
            for (final Resource resource : testResources) {
                this.addFileIfExists(p, resource.getDirectory(), fileSet);
            }
        }
        return fileSet;
    }

    /**
     * Method that removes the invalid directories in the specified directories.
     * <b>Note</b>: All elements in <code>dirs</code> could be an absolute or relative against the project's base
     * directory <code>String</code> path.
     *
     * @param project the current Maven project not null
     * @param dirs the list of <code>String</code> directories path that will be validated.
     */
    private void addFileIfExists(final MavenProject project, final String path, final Set<File> files) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(project.getBasedir(), file.getPath());
        }
        if(file.exists()) {
            files.add(file);
        }
    }

}
