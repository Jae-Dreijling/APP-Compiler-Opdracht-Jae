# Setting Up on a New Computer

## Prerequisites

- **Java 21 JDK** — the only thing you need to install manually
  - Ubuntu/Debian: `sudo apt install openjdk-21-jdk`
  - Windows: download and run the installer from [adoptium.net](https://adoptium.net) (tick "Set JAVA_HOME" during install)
  - Or download from [adoptium.net](https://adoptium.net) for any OS
- **Git** — to clone the repo

Maven is **not** required — the project includes a Maven Wrapper that downloads it automatically on first use.

---

## Steps

### Linux / macOS

```bash
# 1. Clone the repo
git clone <your-repo-url>
cd APP-Compiler-Opdracht-Jae/startcode

# 2. Make the wrapper executable
chmod +x mvnw

# 3. Build the project
./mvnw compile
```

### Windows (Command Prompt or PowerShell)

```bat
REM 1. Clone the repo
git clone <your-repo-url>
cd APP-Compiler-Opdracht-Jae\startcode

REM 2. Build the project
mvnw.cmd compile
```

On first run, the wrapper will download Maven 3.9.14 to `~/.m2/wrapper/` (or `%USERPROFILE%\.m2\wrapper\` on Windows). This only happens once.

---

## Common Commands

Replace `./mvnw` with `mvnw.cmd` on Windows.

| Command | What it does |
|---|---|
| `./mvnw compile` | Compile the project (also generates the ANTLR parser) |
| `./mvnw exec:java` | Compile and launch the GUI |
| `./mvnw javafx:run` | Compile and launch the GUI (cleaner JavaFX module setup) |
| `./mvnw clean compile exec:java` | Clean build then launch |
| `./mvnw test` | Run all tests |
| `./mvnw -DskipTests package` | Build a JAR without running tests |

---

## Notes

- The project targets **Java 21**. Using an older JDK will cause compile errors.
- `ParserTest` tests are expected to fail — they are part of the assignment to implement.
- All commands run from the `startcode/` directory.
