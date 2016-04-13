package jscope.task;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import jscope.configuration.ScopeManager;
import jscope.exception.TaskFailedException;
import jscope.exception.TaskErrorException;

public interface JScopeTask extends Callable<Void> {

    String JSCOPE_PREFIX = "[JSCOPE] ";

    Void call() throws TaskErrorException, TaskFailedException;

    public File getOutputDirectory();

    public void setOutputDirectory(final File outputDirectory);

    public String getProjectName();

    public void setProjectName(final String projectName);

    public File getProjectDirectory();

    public void setProjectDirectory(final File projectDirectory);

    public String getEncoding();

    public void setEncoding(final String encoding);

    public Map<String, String> getEncodings();

    public void setEncodings(final Map<String, String> encodings);

    public ScopeManager getScopeManager();

    public void setScopeManager(final ScopeManager scopeManager);

    public Set<File> getJScopeScanDirs();

    public void setJScopeScanDirs(final Set<File> sourcePaths);

}
