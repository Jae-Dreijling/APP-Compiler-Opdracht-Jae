# ICSS Compiler Project Plan

## 1. Project Overview

I am building a compiler for the ICSS language (a CSS-like language with extensions such as variables, expressions, and conditional logic).

The compiler translates ICSS into valid CSS through multiple stages:

1. Parsing (syntax → AST)
2. Semantic checking (valid meaning)
3. Transformation (simplifying AST)
4. Code generation (AST → CSS)

---

## 2. Compiler Architecture

The system follows a pipeline architecture:

```
Input (ICSS text)
    ↓
ANTLR Lexer & Parser
    ↓
Parse Tree
    ↓
ASTListener → Abstract Syntax Tree (AST)
    ↓
Checker (semantic analysis)
    ↓
Evaluator (transformations)
    ↓
Generator (CSS output)
```

### Key Components

| Component   | Responsibility                              |
| ----------- | ------------------------------------------- |
| ICSS.g4     | Defines syntax (grammar)                    |
| ASTListener | Converts parse tree → AST                   |
| AST         | Internal representation of program          |
| Checker     | Validates semantic correctness              |
| Evaluator   | Simplifies AST (compute values, remove ifs) |
| Generator   | Produces final CSS                          |

---

## 3. Development Strategy

The compiler will be built incrementally in phases, following the assignment structure.

Each phase:

* Adds new language features
* Extends grammar
* Extends ASTListener
* Keeps previous functionality working

---

## 4. Phases

---

### Phase 1 — Basic Parsing (PA01)

#### Goal

Parse simple CSS-like rules and build a correct AST.

#### Features

* Stylesheet
* Stylerules
* Selectors:

  * Tag (`a`)
  * Class (`.menu`)
  * ID (`#header`)
* Declarations:

  * `property: value;`
* Literals:

  * Color (`#ff0000`)
  * Pixel (`100px`)
  * Percentage (`50%`)

#### Components to implement

* Grammar (basic rules)
* ASTListener:

  * Stylesheet handling
  * Stylerule creation
  * Selector mapping
  * Declaration creation
  * Literal creation

#### Result

A correct AST for level0.icss

---

### Phase 2 — Variables (PA02)

#### Goal

Support variable assignment and usage.

#### Features

* Variable assignment:

  ```
  MyVar := 100px;
  ```
* Variable reference:

  ```
  width: MyVar;
  ```

#### Components

* Grammar:

  * Assignment rule
  * Variable references
* AST:

  * VariableAssignment
  * VariableReference
* ASTListener updates

#### Result

AST correctly represents variables

---

### Phase 3 — Expressions (PA03)

#### Goal

Support arithmetic expressions.

#### Features

* Addition (+)
* Subtraction (-)
* Multiplication (*)
* Operator precedence

#### Components

* Grammar:

  * Expression rules with precedence
* AST:

  * AddOperation
  * SubtractOperation
  * MultiplyOperation
* ASTListener:

  * Build operation nodes

#### Result

Expressions correctly represented in AST

---

### Phase 4 — If/Else (PA04)

#### Goal

Support conditional logic.

#### Features

```
if [condition] {
    ...
} else {
    ...
}
```

#### Components

* Grammar:

  * IfClause
  * ElseClause
* AST:

  * IfClause
  * ElseClause
* ASTListener:

  * Nested structure handling

#### Result

Conditional logic correctly parsed

---

### Phase 5 — Semantic Checking (Checker)

#### Goal

Ensure ICSS code is logically valid.

#### Checks (examples)

* Undefined variables
* Type mismatches (px vs %)
* Invalid operations
* Scope correctness

#### Components

* Symbol table (stack of scopes)
* Tree traversal

#### Result

Errors attached to AST nodes

---

### Phase 6 — Transformation (Evaluator)

#### Goal

Simplify AST for easier generation.

#### Transformations

* Evaluate expressions:

  ```
  50px + 10px → 60px
  ```
* Resolve variables
* Remove if-statements:

  * Replace with correct branch

#### Components

* Variable value stack
* AST rewriting

#### Result

Simplified AST (no expressions, no ifs)

---

### Phase 7 — Code Generation (Generator)

#### Goal

Convert AST to valid CSS.

#### Requirements

* Correct syntax
* Proper indentation (2 spaces)
* Only CSS-compatible output

#### Components

* Tree traversal
* String builder

#### Result

Final CSS output

---

## 5. Development Order (Practical)

1. Phase 1 — Basic parsing
2. Phase 2 — Variables
3. Phase 3 — Expressions
4. Phase 4 — If/Else
5. Phase 5 — Checker
6. Phase 6 — Evaluator
7. Phase 7 — Generator

---

## 6. Key Design Decisions

### 1. Incremental Grammar Design

The grammar will be built step by step instead of all at once to avoid complexity and errors.

### 2. Stack-Based AST Construction

A stack is used to track the current node during parsing.

### 3. Separation of Concerns

Each compiler phase has a single responsibility:

* Parsing = structure
* Checking = correctness
* Transform = simplification
* Generation = output

---

## 7. Risks

| Risk                          | Mitigation                    |
| ----------------------------- | ----------------------------- |
| Grammar becomes too complex   | Build incrementally           |
| ASTListener becomes confusing | Map grammar → AST clearly     |
| Bugs hard to trace            | Test per phase                |
| Scope handling errors         | Use structured stack approach |

---

