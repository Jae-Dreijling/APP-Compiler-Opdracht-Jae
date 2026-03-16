# Readme for ICSSTool
This file contains notes and issues for the ICSSTool.
For assignment instructions, see [ASSIGNMENT.md](ASSIGNMENT.md).

## Requirements

- Java 21 (OpenJDK)
- Maven Wrapper in `startcode/` or a local Maven installation

## Running ICSSTool

ICSSTool is a Maven-based application located in `startcode/`.

From the `startcode/` directory, use one of these commands:

```bash
./mvnw compile
./mvnw exec:java
./mvnw javafx:run
```

If you prefer a system Maven installation, the equivalent commands are:

```bash
mvn compile
mvn exec:java
mvn javafx:run
```

Maven automatically generates or updates the parser from the supplied `.g4` file.

You can also run the application from an IDE by importing `startcode/` as a Maven project. When you change the `.g4` file, run `./mvnw generate-sources` before compiling because IDEs do not always regenerate the ANTLR parser automatically.

Since JavaFX is modular and not bundled with the JDK in older setups, running through Maven is the simplest option because the Maven configuration supplies the required JavaFX dependencies.

## Known issues

- Running the built JAR standalone can still be troublesome because of the JavaFX and ANTLR runtime dependencies.
- ICSSTool includes parser verification tests based on sample input files; they are helpful checks, but not full unit-test coverage.
