Introduction
------------

JScope est un langage permettant la gestion du
scopage / déscopage de périmètres fonctionnels dans un projet
(en anglais: _feature toggling_).

Le langage JScope est interprété et exécuté par un plugin Maven.

L'objectif de JScope et de son plugin Maven associé est de fournir des
outils capables de:

-   Scoper / déscoper des périmètres fonctionnels rapidement;
-   Assurer qu'à un instant T, le scopage/déscopage d'une fonctionnalité
    ne met pas en danger la stabilité du projet;
-   Fournir un reporting détaillé des fonctionnalités du projet
    indiquant notamment les parties du projet affectées par les
    différentes fonctionnalités.

JScope s'appuye d'une part sur une syntaxe particulière insérée dans les
fichiers sources du projet, et d'autre part, sur une configuration XML
décrivant les périmètres fonctionnels du projet; cette dernière est
insérée directement dans le POM du projet.

### Syntaxe JScope

JScope est un langage très simple.

Les instructions JScope doivent être incluses dans le fichier cible sous
forme de commentaires. Les détails du langage sont décrits dans la
section [Syntaxe JScope](./syntax.html).

### Configuration de JScope (fichier `pom.xml`)

JScope s'appuie sur une configuration incluse dans le fichier `pom.xml`
du projet.

Cette configuration décrit les périmètres fonctionnels du projet et
permet de connaître l'état des fonctionnalités du projet: scopée,
déscopée ou validée.

Pour plus d'informations sur la configuration de JScope, cf. section
[Configuration](./configuration.html).

### Usage avec Maven

Des instructions générales quant à l'usage de ce plugin peuvent être
trouvées dans la section [Usage](./usage.html).

La description des différents goals du plugin JScope peuvent être
trouvées dans la section [Documentation du plugin](./plugin-info.html).
