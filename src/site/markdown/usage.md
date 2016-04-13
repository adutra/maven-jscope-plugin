Usage
-----

Le plugin Maven JScope comporte les goals suivants:

  -------------- -------------------------- ---------------------------------------
  **Goal**       **Modifie les sources?**   **Doit être inclus dans le pom.xml?**
  check-syntax   non                        **oui**
  verify         non                        non
  verify-all     non                        non
  apply          **oui**                    non
  diff           non                        non
  -------------- -------------------------- ---------------------------------------

En outre, le plugin propose un goal dit "de reporting", qui génère un
rapport inclus dans le site Maven généré:

  ---------- -------------------------- ---------------------------------------
  **Goal**   **Modifie les sources?**   **Doit être inclus dans le pom.xml?**
  report     non                        **oui**
  ---------- -------------------------- ---------------------------------------

### Le Goal [check-syntax](./check-syntax-mojo.html)

Le goal [check-syntax](./check-syntax-mojo.html) permet de vérifier la
validité de la syntaxe JScope.

Il est automatiquement lié à la phase `process-sources` et est destiné à
être exécuté systématiquement par l'inclusion du plugin dans le pom.xml,
comme suit:

    <build>
        <plugins>
            <plugin>
                <groupId>jscope</groupId>
                <artifactId>jscope-maven-plugin</artifactId>
                <version>${currentVersion}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>check-syntax</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

### Le Goal [verify](./verify-mojo.html)

Le goal [verify](./verify-mojo.html) permet de tester une modification dans
les scopes définis dans la configuration du projet, avant de l'appliquer définitivement.

### Le Goal [verify-all](./verify-all-mojo.html)

Le goal [verify-all](./verify-all-mojo.html) permet de tester toutes les
variantes possibles du fichier `jscope.xml`, avant d'en appliquer une
définitivement. Il permet ainsi de s'assurer qu'une fonctionnalité peut
être déscopée ou scopée à tout moment, sans mettre en danger la
stabilité du projet.

### Le Goal [apply](./apply-mojo.html)

Le goal [apply](./apply-mojo.html) applique au code source les scopes définis dans la configuration du projet.

**Ce goal modifie les fichiers originaux du projet et doit être utilisé
avec précaution.**

### Le Goal [diff](./diff-mojo.html)

Le goal [diff](./diff-mojo.html) génère une documentation HTML contenant
le "diff" entre deux fichiers `jscope.xml`.

### Le Goal [report](./report-mojo.html)

Le goal [report](./report-mojo.html) génère une documentation HTML
contenant un résumé des scopes JScope du projet. C'est un goal de type
"reporting". Il est conçu pour être exécuté automatiquement lors de la
génération du site Maven; pour cela, il faut inclure la déclaration
suivante dans le pom.xml:

    <reporting>
        <plugins>
            <plugin>
                <groupId>jscope</groupId>
                <artifactId>jscope-maven-plugin</artifactId>
                <version>${currentVersion}</version>
            </plugin>
        </plugins>
    </reporting>
