package jscope.task;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jscope.configuration.ScopeManager;
import jscope.parser.ParseError;
import jscope.exception.ParseException;
import jscope.exception.TaskFailedException;
import jscope.parser.FileType;
import jscope.parser.ParseResult;
import jscope.parser.ParserFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jscope.exception.TaskErrorException;
import jscope.parser.Parser;

/**
 * @author Alexandre Dutra
 *
 */
public abstract class AbstractJScopeTask implements JScopeTask {

    protected static final Log LOGGER = LogFactory.getLog(AbstractJScopeTask.class);

    /**
     * Le fichier descripteur de périmètres fonctionnels du projet.
     */
    protected File jscopeFile;

    /**
     * Répertoire de travail et de génération des fichiers JScope.
     */
    protected File outputDirectory;

    /**
     * Nom du projet.
     */
    protected String projectName;

    /**
     * Répertoire racine du projet.
     */
    protected File projectDirectory;


    /**
     * L'encodage par défaut des fichiers du projet.
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
     */
    protected Map<String,String> encodings = new HashMap<String, String>();
    {
        this.encodings.put("properties", "ISO-8859-1");
    }

    /**
     * <code>{@link ScopeManager}</code> associé au projet.
     */
    protected ScopeManager scopeManager;

    /**
     * Répertoires de base où chercher des fichiers JScope.
     */
    protected Set<File> jscopeScanDirs;


    public File getJscopeFile() {
        return this.jscopeFile;
    }


    public void setJscopeFile(final File jscopeFile) {
        this.jscopeFile = jscopeFile;
    }


    public File getOutputDirectory() {
        return this.outputDirectory;
    }


    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }


    public String getProjectName() {
        return this.projectName;
    }


    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }


    public File getProjectDirectory() {
        return this.projectDirectory;
    }


    public void setProjectDirectory(final File projectDirectory) {
        this.projectDirectory = projectDirectory;
    }


    public String getEncoding() {
        return this.encoding;
    }


    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }


    public Map<String, String> getEncodings() {
        return this.encodings;
    }


    public void setEncodings(final Map<String, String> encodings) {
        this.encodings = encodings;
    }


    public ScopeManager getScopeManager() {
        return this.scopeManager;
    }


    public void setScopeManager(final ScopeManager scopeManager) {
        this.scopeManager = scopeManager;
    }


    public Set<File> getJScopeScanDirs() {
        return this.jscopeScanDirs;
    }


    public void setJScopeScanDirs(final Set<File> jscopeScanDirs) {
        this.jscopeScanDirs = jscopeScanDirs;
    }

    /**
     * @inheritdoc
     */
    public Void call() throws TaskErrorException, TaskFailedException {
        this.preProcess();
        for (final File base : this.getJScopeScanDirs()) {
            final Collection<File> files = this.listFiles(base);
            for (final File file : files) {
                this.doWithFile(file);
            }
        }
        this.postProcess();
        return null;
    }

    protected void preProcess() throws TaskErrorException, TaskFailedException {
    }

    protected abstract void doWithFile(final File file) throws TaskErrorException, TaskFailedException;

    protected void postProcess() throws TaskErrorException, TaskFailedException {
    }

    protected Collection<File> listFiles(final File base) {
        @SuppressWarnings("unchecked")
        final Collection<File> files = FileUtils.listFiles(
            base,
            FileType.getKnownExtensions(),
            true);
        return files;
    }

    protected ParseResult parseFile(final File file, final boolean lenient) throws TaskErrorException {
        final Parser parser = ParserFactory.newParser(file);
        parser.setLenient(lenient);
        final String encoding = this.getEncoding(file);
        try {
            return parser.parse(file, encoding);
        } catch (final ParseException e) {
            throw new TaskErrorException("Can't parse JScope file: " + file, e);
        }
    }

    protected String getEncoding(final File file) {
        return this.getEncoding(FilenameUtils.getExtension(file.getName()));
    }

    protected String getEncoding(final String extension) {
        String encoding = this.encodings.get(extension);
        if(encoding == null) {
            encoding = this.encoding;
            if(encoding == null) {
                LOGGER.warn(JSCOPE_PREFIX + "No default charset specified, using UTF-8 instead. Please set the property project.build.sourceEncoding in your pom.xml.");
                encoding = "UTF-8";
            }
        }
        return encoding;
    }

    protected void cleanJScopeDirectory() throws TaskErrorException {
        LOGGER.info(JSCOPE_PREFIX + "Cleaning JScope output directory: " + this.outputDirectory);
        try {
            FileUtils.deleteDirectory(this.outputDirectory);
        } catch (final IOException e) {
            throw new TaskErrorException("Can't delete directory: " + this.outputDirectory);
        }
        this.outputDirectory.mkdirs();
    }

    protected void copyToJScopeDirectory() throws TaskErrorException {
        LOGGER.info(JSCOPE_PREFIX + "Copying project to JScope output directory: " + this.outputDirectory);
        try {
            FileUtils.copyDirectory(this.projectDirectory, this.outputDirectory, new FileFilter() {
                public boolean accept(final File file) {
                    final String relativePath = AbstractJScopeTask.this.relativizePath(
                        AbstractJScopeTask.this.projectDirectory, file);
                    return ! "target/".equals(relativePath);
                }
            });
        } catch (final IOException e) {
            throw new TaskErrorException("Can't copy directory: " + this.projectDirectory + " to: "+ this.outputDirectory);
        }
    }

    protected String relativizePath(final File baseDir, final File file) {
        final String relativePath = baseDir.toURI().relativize(file.toURI()).getPath();
        return relativePath;
    }

    protected void displayParseErrors(final List<ParseError> errors) {
        boolean fileDone = false;
        for (final ParseError error : errors) {
            if(!fileDone){
                LOGGER.error(JSCOPE_PREFIX + "File KO: " + error.getFile());
                fileDone = true;
            }
            LOGGER.error(JSCOPE_PREFIX + "    Line " + error.getLine() + ": " + error.getMessage());
        }
        LOGGER.error(JSCOPE_PREFIX);
    }

}
