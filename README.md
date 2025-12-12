# Voting CI Demo (MP2)

Application de vote refactorée appliquant **Factory Method**, **Strategy** et **Observer**. Projet Maven Java 17 avec tests JUnit 5, JaCoCo, SonarQube et pipeline Jenkins.

## Prérequis
- Java 17
- Maven 3.9+

## Construire, tester, vérifier la couverture
```bash
mvn clean verify
```
Produits :
- Tests : `target/surefire-reports`
- Couverture JaCoCo : `target/site/jacoco/index.html` et `target/site/jacoco/jacoco.xml`
- JAR : `target/voting-ci-demo-1.0-SNAPSHOT.jar`

## Exécuter la CLI
```bash
mvn -q -DskipTests exec:java -Dexec.mainClass=org.example.vote.App
```
Commandes : `help`, `list`, `add <id> <name>`, `vote <voterId> <candidateId>`, `count [plurality|majority]`, `reset`, `exit`.  
Observer : `LoggingVoteListener` est enregistré au démarrage (logs `[LOG] Vote received: ...`), ainsi qu'un listener d'audit en mémoire.

## Architecture finale
- `src/main/java/org/example/vote/App.java` : CLI, enregistre les listeners, choisit les stratégies.
- `model/` : `Candidate`, `Vote`.
- `repo/` : `VoteRepository` (contrat), `InMemoryVoteRepository`.
- `factory/` : `RepositoryFactory` (Factory Method) pour instancier les repositories.
- `strategy/` : `CountingStrategy`, `PluralityCountingStrategy`, `MajorityCountingStrategy` (Strategy).
- `observer/` : `VoteListener`, `LoggingVoteListener`, `AuditTrailVoteListener` (Observer).
- `service/` : `VoteService`, `CountingResult` (orchestration métier).
- `docs/patterns.md` : rapport patterns (avant/après, UML ASCII).
- `src/test/java/...` : tests JUnit 5 couvrant service, stratégies, factory, listeners et CLI.

## Design patterns (rôle)
- **Factory Method** (`factory/RepositoryFactory`) : création découpée des `VoteRepository` pour pouvoir changer d’implémentation sans modifier l’app.
- **Strategy** (`strategy/*CountingStrategy`) : choix dynamique de l’algorithme de dépouillement (pluralité ou majorité absolue).
- **Observer** (`observer/*VoteListener`) : notification des événements de vote (log/audit) sans couplage direct.

## Pipeline Jenkins
Fichier `Jenkinsfile` (declarative) :
1. Checkout
2. Build (`mvn -B -DskipTests clean package`)
3. Tests (`mvn -B test` + publication JUnit)
4. Couverture (`mvn -B jacoco:report` + publication HTML)
5. SonarQube (`mvn -B sonar:sonar` sous `withSonarQubeEnv('SonarQube')`, variables credentials `SONAR_HOST` et `SONAR_TOKEN`)
6. Quality Gate (`waitForQualityGate`)

Plugins requis : Pipeline, JUnit, JaCoCo, SonarQube Scanner, HTML Publisher. Configurer outils JDK/Maven globalement. Credentials Jenkins : `SONAR_HOST` (string), `SONAR_TOKEN` (secret text) et serveur Sonar nommé `SonarQube`.

## SonarQube
`sonar-project.properties` configure sources/tests/binaires/rapports JaCoCo. Le pipeline passe les paramètres host/token. Quality Gate bloquante.

## Livrables complémentaires
- Rapport patterns : `docs/patterns.md`
- Couverture minimale imposée (jacoco:check) : 60%
- `.gitignore` Maven pour nettoyer `target/`, bundles et fichiers OS.
