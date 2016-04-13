FAQ
---

### Comment m'assurer que je n'ai pas oublié une balise JScope?

Exécutez le goal [check-syntax](./check-syntax-mojo.html).

Exemple:

    mvn jscope:check-syntax


### Comment placer du code sous un bloc JScope afin qu'il soit exécuté uniquement si aucune fonctionnalité n'est scopée?

Utilisez le mot-clé `BASELINE`.

Exemple:

    System.out.println("Ce message n'appartient à aucun périmètre fonctionnel");

    //@SCOPE BASELINE BEGIN
    System.out.println("Ce message ne s'affiche que si aucun périmètre fonctionnel n'est actif");
    //@SCOPE END

    //@SCOPE AB-1234 BEGIN
    System.out.println("Ce message appartient à la fonctionnalité AB-1234");
    //@SCOPE END
