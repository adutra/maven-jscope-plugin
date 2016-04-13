package jscope.maven;

import jscope.task.DiffTask;
import jscope.task.JScopeTask;

import java.io.File;

/**
 * <p>Génère une documentation HTML contenant le "diff" entre deux ensembles
 * de scopes JScope.</p>
 * <p>L'ensemble "de référence" est fourni par les scopes définis dans la configuration du projet.</p>
 * <p>L'ensemble "de comparaison" peut être fourni:</p>
 * <ol>
 * <li>Soit par un deuxième fichier jscope à l'aide du paramètre <code>jscopeFile2</code>;</li>
 * <li>Soit par surcharge des scopes de l'ensemble de référence, à l'aide du paramètre <code>scopeOverrides</code>.</li>
 * </ol>
 * <p>Si l'on fournit deux fichiers JScope, ils doivent contenir exactement
 * les mêmes scopes; seuls leurs statuts peuvent
 * différer (<code>SCOPED</code>, <code>UNSCOPED</code>, <code>VALIDATED</code>).</p>
 * <p>
 * Si l'on fournit une surcharge à l'aide du paramètre <code>scopeOverrides</code>,
 * la syntaxe du paramètre est la suivante:</p>
 * <pre>
 * -DscopeOverrides={SCOPE_ID}:{SCOPE_STATUS};{SCOPE_ID}:{SCOPE_STATUS}
 * </pre>
 * <p>Exemple:</p>
 * <pre>
 * -DscopeOverrides=AB-1234:UNSCOPED;CD-5678:SCOPED
 * </pre>
 * <p>Dans ce cas, les scopes non cités sur la ligne de commande restent inchangés. De plus, il n'est pas possible
 * d'ajouter des scopes avec cette méthode.</p>
 * 
 * @goal diff
 * @aggregator
 */
public class DiffMojo extends AbstractJScopeMojo {

    /**
     * Le deuxième fichier JScope servant de comparaison
     * par rapport au fichier "de référence".
     * 
     * @parameter default-value="jscope2.xml"
     */
    private File jscopeFile2;

    /**
     * <p>Une liste de scopes et de statuts, séparés
     * par des points-virgule, représentant
     * l'ensemble de comparaison par rapport à
     * l'ensemble de référence.</p>
     * <p>La syntaxe du paramètre est la suivante:</p>
     * <pre>
     * -DscopeOverrides={SCOPE_ID}:{SCOPE_STATUS};{SCOPE_ID}:{SCOPE_STATUS}
     * </pre>
     * <p>Exemple:</p>
     * <pre>
     * -DscopeOverrides=AB-1234:UNSCOPED;CD-5678:SCOPED
     * </pre>
     * <p>Dans ce cas, les scopes non cités sur la ligne de commande restent inchangés. De plus, il n'est pas possible
     * d'ajouter des scopes avec cette méthode.</p>
     * 
     * @parameter property="scopeOverrides"
     */
    private String scopeOverrides;

    @Override
    protected JScopeTask createTask() {
        final DiffTask diffTask = new DiffTask();
        diffTask.setVelocityEngine(this.velocityComponent.getEngine());
        diffTask.setJscopeFile2(this.jscopeFile2);
        diffTask.setScopeOverrides(this.scopeOverrides);
        return diffTask;
    }

}
