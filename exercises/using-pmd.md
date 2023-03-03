# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset. Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer
**Vrai positif**
AvoidMutableStaticFields -	Avoid non-final or mutable static fields. Make final immutable or access thread-safely AND use @GuardedBy. -	19
```
    static Random random = new Random();
```

On peut amélioreren rendant cette variable final, surtout dans une application qui fait du parralèlisme. On certifie que l'on aura pas d'erreur de concurrence. 
```
    static final Random random = new Random();
```

**Faux positif**
LimitStatementsInLambdas -	Avoid many statements in lambda expressions. -	21–31
```
observeurs.stream().forEach((observeurDeCapteurAsync) -> {
    if(!observeurAsyncMap.containsKey(observeurDeCapteurAsync)) {
        if(isLocked) {
            // if waiting for all the observer to getValue
            observeurAsyncMap.put(observeurDeCapteurAsync, true);
        } else {
            // if waiting for a tick
            observeurAsyncMap.put(observeurDeCapteurAsync, false);
        }
    }
});
```
Ici pour résoudre ça, on pourrait avoir deux solutions : 
 - Extraire cette lambda dans une classe entière, mais ce bout de code est intrinsèquement lié au code de la classe le contenant.
 - Transformer ce **forEach** en for, mais ici on utilise l'interface Stream qui permet le parralèlisme, ce qui est important pour les performances.   
