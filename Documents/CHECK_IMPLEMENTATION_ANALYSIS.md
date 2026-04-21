# Semantic Checks Implementation Analysis

## Summary
✅ **ALL 6 CHECKS ARE IMPLEMENTED**

The project successfully implements all semantic checks required by the assignment (CH01 through CH06). Since CH00 requires a minimum of 4 checks, this project **exceeds the requirement**.

---

## Detailed Implementation Status

### CH01: Undefined Variables Check ✅ IMPLEMENTED
**Requirement**: Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn.

**Implementation**: [Checker.java](../startcode/src/main/java/nl/han/ica/icss/checker/Checker.java#L140-L151)
- Method: `resolveVariable(VariableReference var)`
- Logic: Searches through all variable scopes (from innermost to outermost)
- Error message: `"Undefined variable: " + name`
- Test: `testUndefinedVariable()` in [CheckerTest.java](../startcode/src/test/java/nl/han/ica/icss/Checker/CheckerTest.java#L10)

```java
private ExpressionType resolveVariable(VariableReference var) {
    String name = var.name;
    for (int i = 0; i < variableScopes.getSize(); i++) {
        HashMap<String, ExpressionType> scope = variableScopes.get(i);
        if (scope.containsKey(name)) {
            return scope.get(name);
        }
    }
    var.setError("Undefined variable: " + name);
    return ExpressionType.UNDEFINED;
}
```

---

### CH02: Operation Operand Type Checking ✅ IMPLEMENTED
**Requirement**: Controleer of de operanden van de operaties plus en min van gelijk type zijn. Controleer dat bij vermenigvuldigen minimaal een operand een scalaire waarde is.

**Implementation**: [Checker.java](../startcode/src/main/java/nl/han/ica/icss/checker/Checker.java#L165-L205)
- Method: `resolveOperation(Operation op)`
- **Addition/Subtraction** (lines 181-185): Both operands must be the same type
  - Error: `"Operands must be same type"`
  - Example: `10px + 5%` is invalid, but `10px + 5px` is valid
- **Multiplication** (lines 186-192): At least one operand must be scalar
  - Error: `"Multiply requires one scalar"`
  - Valid: `20% * 3`, `4 * 5`
  - Invalid: `2px * 3px`
- Test: `testInvalidOperation()` in [CheckerTest.java](../startcode/src/test/java/nl/han/ica/icss/Checker/CheckerTest.java#L25)

```java
// ADD / SUBTRACT
if (op instanceof AddOperation || op instanceof SubtractOperation) {
    if (left != right) {
        op.setError("Operands must be same type");
        return ExpressionType.UNDEFINED;
    }
    return left;
}

// MULTIPLY
if (op instanceof MultiplyOperation) {
    if (left == ExpressionType.SCALAR) return right;
    if (right == ExpressionType.SCALAR) return left;
    op.setError("Multiply requires one scalar");
    return ExpressionType.UNDEFINED;
}
```

---

### CH03: No Colors in Operations ✅ IMPLEMENTED
**Requirement**: Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer).

**Implementation**: [Checker.java](../startcode/src/main/java/nl/han/ica/icss/checker/Checker.java#L165-L177)
- Method: `resolveOperation(Operation op)` - early check
- Logic: Before any operation type checking, verifies neither operand is a color
- Error message: `"Cannot use color in operations"`
- Also checks: Colors cannot be used with boolean values in operations

```java
if (left == ExpressionType.COLOR || right == ExpressionType.COLOR) {
    op.setError("Cannot use color in operations");
    return ExpressionType.UNDEFINED;
}
```

---

### CH04: Declaration Type Matching ✅ IMPLEMENTED
**Requirement**: Controleer of bij declaraties het type van de value klopt met de property (e.g., width: #ff0000 is invalid).

**Implementation**: [Checker.java](../startcode/src/main/java/nl/han/ica/icss/checker/Checker.java#L93-L111)
- Method: `handleDeclaration(Declaration declaration)`
- **Color properties** (`color`, `background-color`): Must be `ExpressionType.COLOR`
  - Error: `"Color expected"`
- **Size properties** (`width`, `height`): Must be `ExpressionType.PIXEL` or `ExpressionType.PERCENTAGE`
  - Error: `"Size must be pixel or percentage"`

```java
private void handleDeclaration(Declaration declaration) {
    ExpressionType type = resolveType(declaration.expression);
    String property = declaration.property.name;

    if (type == ExpressionType.UNDEFINED) return;

    if (property.equals("color") || property.equals("background-color")) {
        if (type != ExpressionType.COLOR) {
            declaration.setError("Color expected");
        }
    }

    if (property.equals("width") || property.equals("height")) {
        if (type != ExpressionType.PIXEL && type != ExpressionType.PERCENTAGE) {
            declaration.setError("Size must be pixel or percentage");
        }
    }
}
```

---

### CH05: If Condition Type Checking ✅ IMPLEMENTED
**Requirement**: Controleer of de conditie bij een if-statement van het type boolean is (zowel bij een variabele-referentie als een boolean literal).

**Implementation**: [Checker.java](../startcode/src/main/java/nl/han/ica/icss/checker/Checker.java#L113-L119)
- Method: `handleIfClause(IfClause ifClause)`
- Logic: Resolves the type of the conditional expression
- Error message: `"Condition must be boolean"`
- Handles both variable references (CH01 undefined check also applies) and literal values
- Test: `testInvalidIfCondition()` in [CheckerTest.java](../startcode/src/test/java/nl/han/ica/icss/Checker/CheckerTest.java#L40)

```java
private void handleIfClause(IfClause ifClause) {
    ExpressionType condType = resolveType(ifClause.getConditionalExpression());
    if (condType != ExpressionType.UNDEFINED && condType != ExpressionType.BOOL) {
        ifClause.setError("Condition must be boolean");
    }
}
```

---

### CH06: Variable Scope Validation ✅ IMPLEMENTED (MUST requirement)
**Requirement**: Controleer of variabelen enkel binnen hun scope gebruikt worden (Must = 5 points)

**Implementation**: [Checker.java](../startcode/src/main/java/nl/han/ica/icss/checker/Checker.java) - Entire scope management system
- **Scope tracking mechanism** (lines 1-66):
  - Stack of HashMaps: `variableScopes` tracks nested scopes
  - `pushScope()`: Creates new scope for stylerules, if-clauses, else-clauses
  - `popScope()`: Removes scope when exiting block
  
- **Variable lookup** (lines 140-151):
  - `resolveVariable()` searches from innermost to outermost scope
  - Returns variable type if found, sets error if not
  
- **Scope hierarchy**:
  1. Global scope (outermost)
  2. Stylerule scope
  3. If/Else clause scopes (nested)

**Example - Scope Rules in Action**:
```icss
GlobalVar := 100px;      // Global scope - available everywhere

h1 {                      // Push stylerule scope
  LocalVar := 50%;        // Local to this rule
  if [TRUE] {             // Push if scope
    IfVar := 10px;        // Local to this if block
    width: LocalVar;      // ✅ Valid - LocalVar in scope
  }
  width: IfVar;           // ❌ Error - IfVar out of scope
  width: GlobalVar;       // ✅ Valid - GlobalVar in global scope
}
```

---

## CH00: Minimum Requirement
**Requirement**: Minimaal vier van onderstaande checks moeten zijn geïmplementeerd (Must = 0 points)

**Status**: ✅ **EXCEEDED** - All 6 checks implemented
- CH01: ✅
- CH02: ✅
- CH03: ✅
- CH04: ✅
- CH05: ✅
- CH06: ✅ (Must requirement)

**Minimum:** 4 checks required  
**Actual:** 6 checks implemented  
**Result:** Master satisfaction of CH00 requirement

---

## Type System
The checker uses `ExpressionType` enum to track types throughout the AST:
- `BOOL` - Boolean values (TRUE/FALSE)
- `COLOR` - Hexadecimal colors (#rrggbb)
- `PIXEL` - Pixel measurements (100px)
- `PERCENTAGE` - Percentage values (50%)
- `SCALAR` - Dimensionless numbers (3, 5)
- `UNDEFINED` - Unknown or error types

All type resolution flows through `resolveType()` method which handles:
- Literals (BoolLiteral, ColorLiteral, PixelLiteral, PercentageLiteral, ScalarLiteral)
- Variable references (with undefined check)
- Operations (with type compatibility checks)

---

## Test Coverage
Basic tests in [CheckerTest.java](../startcode/src/test/java/nl/han/ica/icss/Checker/CheckerTest.java):
- ✅ `testUndefinedVariable()` - CH01
- ✅ `testInvalidOperation()` - CH02
- ✅ `testInvalidIfCondition()` - CH05

Note: Tests are minimal. Additional testing scenarios would strengthen validation:
- CH03: Color operations (e.g., `#ff0000 + 10px`)
- CH04: More declaration type mismatches
- CH06: Complex nested scope scenarios
- CH02: More operation combinations

---

## Architecture Notes
The checker implements a **single-pass tree walker** that:
1. Maintains a scope stack as it traverses the AST
2. Collects semantic errors directly on AST nodes
3. Resolves types recursively through expressions
4. Follows the AST visitor pattern for different node types

This design ensures:
- ✅ Efficient O(n) complexity
- ✅ Scope rules properly enforced
- ✅ Type system consistently applied
- ✅ Errors preserved in AST for later reporting
