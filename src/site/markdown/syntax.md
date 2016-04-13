Syntaxe JScope
--------------

### Principes de base

Les instructions JScope doivent être incluses dans le fichier cible sous
forme de commentaires.

La syntaxe précise dépendra donc de la façon de placer les commentaires
dans le code, en fonction du langage utilisé (java, XML, etc.).

Le principe général cependant reste simple: il s'agit de délimiter des
périmètres fonctionnels dans le code puis de les entourer par deux
balises JScope, dites d'ouverture et de fermeture, et d'associer ce bloc
JScope à une ou plusieurs fonctionnalités.

Les balises d'ouverture et de fermeture contiennent obligatoirement le
marqueur JScope: `@SCOPE`. Ce marqueur permet au plugin Maven JScope de
détecter les blocs correspondant à des périmètres fonctionnels.

La balise d'ouverture contient en outre la liste des fonctionnalités
associées au bloc, suivie du mot-clé `BEGIN`.

La balise de fermeture contient en outre le mot-clé `END`.

Voici un exemple en Java:

    //@SCOPE AB-1234 BEGIN
    System.out.println("Ce message appartient à la fonctionnalité AB-1234");
    //@SCOPE END

La portion de code ainsi délimitée est placée sous le périmètre de la
fonctionnalité "AB-1234" et peut alors être gérée en tant que périmètre
fonctionnel par le plugin Maven JScope.

Par exemple, si l'on décide de déscoper la fonctionnalité "AB-1234", le
plugin Maven JScope recherchera tous les blocs JScope associés à cette
fonctionnalité, puis les désactivera en les plaçant entre commentaires.

Le code précédent deviendrait alors:

    //@SCOPE AB-1234 BEGIN
    //@UNSCOPED@//    System.out.println("Ce message appartient à la fonctionnalité AB-1234");
    //@SCOPE END

On remarque que le code déscopé est mis en commentaires avec un autre
marqueur: `@UNSCOPED@`. La présence de ce marqueur permet au plugin
Maven JScope de différencier un commentaire de déscopage d'un
commentaire normal.

**IMPORTANT: Le rôle du développeur, dans un projet JScope, doit être de
délimiter les périmètres des fonctionnalités à l'aide de JScope. Il ne
doit pas cependant gérer manuellement le scopage/déscopage de la
fonctionnalité, ni modifier manuellement le code déscopé par le plugin
Maven JScope. Seul le plugin Maven JScope devrait exécuter ces
actions.**

#### Association entre un bloc JScope et plusieurs périmètres fonctionnels

Un bloc JScope peut être associé à plus d'une fonctionnalité. Voici un
exemple en Java:

    //@SCOPE AB-1234 AB-5678 BEGIN
    System.out.println("Ce message appartient à la fois aux fonctionnalités AB-1234 et AB-5678");
    //@SCOPE END

Dans ce cas, ce bloc est scopé si au moins l'une des deux
fonctionnalités est scopée. Il ne sera descopé que si *toutes les
fonctionnalités associées sont déscopées*.

#### Imbrication de blocs JScope

Il est également possible d'imbriquer les blocs JScope. Voici un exemple
en Java:

    //@SCOPE AB-1234 AB-5678 BEGIN
    System.out.println("Ce message appartient à la fois aux fonctionnalités AB-1234 et AB-5678");

        //@SCOPE AB-5678 BEGIN
        System.out.println("Mais celui-ci appartient uniquement à AB-5678");
        //@SCOPE END

    //@SCOPE END

Dans ce cas, le premier message est scopé si au moins l'une des deux
fonctionnalités est scopée. Le deuxième, cependant, ne sera scopé que si
la fonctionnalité "AB-5678" est scopée.

**IMPORTANT: Ne pas abuser des blocs JScope imbriqués!**

### Formats de fichiers

JScope est compatible avec les formats suivants:

-   Java (.java)
-   Properties (.properties)
-   XML (.xml, .xsd)
-   SQL (.sql)

La syntaxe JScope appropriée dépend du type de fichier.

#### Fichiers Java

Les blocs JScope doivent être déclarés de la manière suivante:

    //@SCOPE AB-1234 BEGIN
    /**
     * <p>Javadoc</p>
     * @see "http://foo.com/bar"
     */
    public void execute() {
    }
    //@SCOPE END

La balise d'ouverture doit commencer par le commentaire mot-clé
`//@SCOPE`, précédé éventuellement d'espaces.

La balise de fermeture doit le commentaire mot-clé `//@SCOPE END`,
précédé éventuellement d'espaces.

#### Fichiers Properties

Les blocs JScope doivent être déclarés de la manière suivante:

    # @SCOPE AB-1234 BEGIN 
    key1=value1
    # @SCOPE END 

La balise d'ouverture doit commencer par la séquence `# @SCOPE`, précédé
éventuellement d'espaces. Il peut également y avoir des espaces entre le
signe `#` et le mot-clé `@SCOPE`.

La balise de fermeture doit être la séquence `# @SCOPE END `, précédée
éventuellement d'espaces. Il peut également y avoir des espaces entre le
signe `#` et le mot-clé `@SCOPE`.

#### Fichiers XML et XSD

Les blocs JScope doivent être déclarés de la manière suivante:

    <!-- @SCOPE AB-1234 BEGIN -->
            
    <dependency>
        <groupId>com.sun</groupId>
        <artifactId>tools</artifactId>
        <version>1.5.0</version>
        <!--<scope>system</scope>-->
        <systemPath>${JAVA_HOME}/lib/tools.jar</systemPath>
    </dependency>

    <!-- @SCOPE END -->

La balise d'ouverture doit commencer par la séquence `<!-- @SCOPE`,
précédé éventuellement d'espaces. Il peut également y avoir des espaces
entre la balise de commentaires `<!--` et le mot-clé `@SCOPE`.

La balise de fermeture doit être la séquence `<!-- @SCOPE END`, précédée
éventuellement d'espaces. Il peut également y avoir des espaces entre la
balise de commentaires `<!--` et le mot-clé `@SCOPE`.

#### Fichiers SQL

Les blocs JScope doivent être déclarés de la manière suivante:

    --@SCOPE AB-1234 BEGIN
    ALTER TABLE FOO ADD CONSTRAINT FOO_REF FOREIGN KEY (FOO_BAR) REFERENCES BAR (ID);
    --@SCOPE END

La balise d'ouverture doit commencer par la séquence `-- @SCOPE`,
précédé éventuellement d'espaces. Il peut également y avoir des espaces
entre le signe `--` et le mot-clé `@SCOPE`.

La balise de fermeture doit être la séquence `-- @SCOPE END `, précédée
éventuellement d'espaces. Il peut également y avoir des espaces entre le
signe `--` et le mot-clé `@SCOPE`.

### Utilisation du scope spécial `BASELINE`

Le mot-clé spécial `BASELINE` peut être utilisé dans un bloc JScope pour
indiquer que le bloc ne doit être exécuté que si aucun périmètre
fonctionnel connu n'est scopé.

Il permet ainsi l'exécution du code tel qu'il était à l'état initial du
projet (avant la mise en place de périmètres fonctionnels.

Voici un exemple en Java:

    System.out.println("Ce message n'appartient à aucun périmètre fonctionnel");

    //@SCOPE BASELINE BEGIN
    System.out.println("Ce message ne s'affiche que si aucun périmètre fonctionnel n'est actif");
    //@SCOPE END

    //@SCOPE AB-1234 BEGIN
    System.out.println("Ce message appartient à la fonctionnalité AB-1234");
    //@SCOPE END

Supposons que le projet ne contienne qu'une fonctionnalité: "AB-1234".
Dans cet exemple, si la fonctionnalité "AB-1234" est scopée, le code
ci-dessus afficherait:

    Ce message n'appartient à aucun périmètre fonctionnel
    Ce message appartient à la fonctionnalité AB-1234

Si la fonctionnalité "AB-1234" est déscopée, le code ci-dessus
afficherait:

    Ce message n'appartient à aucun périmètre fonctionnel
    Ce message ne s'affiche que si aucun périmètre fonctionnel n'est actif
    Ce message appartient à la fonctionnalité AB-1234
