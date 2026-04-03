# ICSS Compiler – Test Plan & Test Strategy

## 🎯 Goal of Testing

The goal of this test plan is to verify that the ICSS compiler pipeline works correctly from end to end:

```
ICSS input → Parser → AST → Checker → Evaluator → Generator → CSS output
```

We want to ensure:

* Syntax is parsed correctly
* Semantic errors are detected
* Expressions are evaluated correctly
* Variables and scopes behave correctly
* Final CSS output is valid and correct

---

## 🧱 Test Strategy

We test the system in **layers**, but validate primarily through **end-to-end tests**.

### 1. Parser (Syntax)

* Does valid ICSS parse without errors?
* Do invalid inputs produce syntax errors?

### 2. Checker (Semantic Analysis)

* Detect:

  * Undefined variables
  * Type mismatches
  * Invalid operations
  * Invalid property usage

### 3. Evaluator (Execution)

* Resolve variables correctly
* Evaluate expressions (+, -, *)
* Apply correct scoping rules
* Correctly handle `if/else`

### 4. Generator (Output)

* Output valid CSS
* Remove variables
* Keep only final declarations
* Format output correctly

---

## 🧪 Test Approach

Each test follows this structure:

1. Input ICSS string
2. Run:

   ```
   parse → check → transform → generate
   ```
3. Verify:

   * Errors (if expected)
   * Generated CSS (if no errors)

---

## ✅ Test Cases

---

### 🔹 Test 1 — Basic Declaration

#### Input:

```icss
h1 {
  width: 10px;
}
```

#### Expected:

```css
h1 {
  width: 10px;
}
```

#### Purpose:

* Basic parsing
* Basic generation

---

### 🔹 Test 2 — Variable Usage

#### Input:

```icss
$size: 10px;

h1 {
  width: $size;
}
```

#### Expected:

```css
h1 {
  width: 10px;
}
```

#### Purpose:

* Variable assignment
* Variable resolution

---

### 🔹 Test 3 — Expression Evaluation

#### Input:

```icss
h1 {
  width: 10px + 5px;
}
```

#### Expected:

```css
h1 {
  width: 15px;
}
```

#### Purpose:

* Arithmetic evaluation

---

### 🔹 Test 4 — Variable + Expression

#### Input:

```icss
$base: 10px;

h1 {
  width: $base + 5px;
}
```

#### Expected:

```css
h1 {
  width: 15px;
}
```

#### Purpose:

* Combined variable + operation

---

### 🔹 Test 5 — If TRUE

#### Input:

```icss
$flag: TRUE;

h1 {
  if [$flag] {
    color: #ff0000;
  } else {
    color: #0000ff;
  }
}
```

#### Expected:

```css
h1 {
  color: #ff0000;
}
```

#### Purpose:

* If condition (true branch)

---

### 🔹 Test 6 — If FALSE

#### Input:

```icss
$flag: FALSE;

h1 {
  if [$flag] {
    color: #ff0000;
  } else {
    color: #0000ff;
  }
}
```

#### Expected:

```css
h1 {
  color: #0000ff;
}
```

#### Purpose:

* Else branch execution

---

### 🔹 Test 7 — Scope Isolation

#### Input:

```icss
$size: 10px;

h1 {
  $size: 20px;
  width: $size;
}
```

#### Expected:

```css
h1 {
  width: 20px;
}
```

#### Purpose:

* Inner scope overrides outer scope

---

### 🔹 Test 8 — Duplicate Properties

#### Input:

```icss
h1 {
  width: 10px;
  width: 20px;
}
```

#### Expected:

```css
h1 {
  width: 20px;
}
```

#### Purpose:

* Last declaration wins
* Old declarations removed

---

### 🔹 Test 9 — Invalid Operation

#### Input:

```icss
h1 {
  width: 10px + 5%;
}
```

#### Expected:

* ❌ Error reported
* ❌ No CSS output (or invalid declaration removed)

#### Purpose:

* Type checking
* Error propagation

---

### 🔹 Test 10 — Undefined Variable

#### Input:

```icss
h1 {
  width: $unknown;
}
```

#### Expected:

* ❌ Error: undefined variable
* ❌ No valid CSS output

#### Purpose:

* Variable validation

---

## 🧪 How to Execute Tests

For each test:

```java
Pipeline pipeline = new Pipeline();

pipeline.parseString(input);
pipeline.check();
pipeline.transform();
String result = pipeline.generate();

System.out.println(result);
System.out.println(pipeline.getErrors());
```

---

## 📊 Evaluation Criteria

| Component | Must Work                 |
| --------- | ------------------------- |
| Parser    | No crashes, correct AST   |
| Checker   | Detects all invalid cases |
| Evaluator | Correct values + scoping  |
| Generator | Clean CSS output          |

---

## 🧭 Next Step

1. Run all tests
2. Record:

   * Output
   * Errors
3. Compare with expected results

---

👉 After testing, we will:

* Analyze mismatches
* Fix remaining bugs
* Update this report with results

---
