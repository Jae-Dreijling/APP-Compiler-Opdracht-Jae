Compacted conversationHere's what each command does in this project:

**`./mvnw compile`**
Compiles all Java source files in `src/main/java/` to `.class` files. It also runs ANTLR first to generate the parser Java classes from `ICSS.g4`. Output lands in `target/classes/`. Use this to check your code compiles without running anything.

**`./mvnw exec:java`**
Compiles (if needed) then runs the application's main class — `nl.han.ica.icss.gui.Main` — directly in the same JVM process as Maven. It starts the JavaFX GUI. Quick way to run the app during development without packaging it first.

**`./mvnw javafx:run`**
Also compiles and launches the JavaFX GUI, but uses the `javafx-maven-plugin` instead. It sets up the JavaFX module path properly, which avoids the "unnamed module" warning you saw with `exec:java`. Slightly cleaner for JavaFX specifically.

In practice for this project, all three result in the same app window opening. The main difference is that `javafx:run` handles JavaFX module wiring more cleanly, while `exec:java` is the generic Java launcher.




Maven lets you chain phases and goals in one command:

```bash
./mvnw clean compile exec:java
```

This runs them left to right: `clean` deletes `target/`, `compile` rebuilds everything fresh (including ANTLR generation), then `exec:java` launches the app. Useful when you want a guaranteed clean build before running.