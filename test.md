Dit is voor het testen van specifieke requirements.
---

# PARSER (PA)

## PA01 — basic styling

```icss
a {
  color: #ff0000;
}
#menu {
  width: 100%;
}
.active {
  height: 50px;
}
```

**Expect**

* parses without errors
* 3 stylerules

---

## PA02 — variables

```icss
MainColor := #ff0000;

p {
  color: MainColor;
}
```

**Expect**

* parses
* VariableAssignment + VariableReference present

---

## PA03 — operations + precedence

```icss
p {
  width: 50px + 2 * 10px - 2px;
}
```

**Expect (after transform)**

```css
p {
  width: 68px;
}
```

---

## PA04 — if/else

```icss
Use := TRUE;

p {
  if [Use] {
    width: 100px;
  } else {
    width: 50px;
  }
}
```

**Expect (after transform)**

```css
p {
  width: 100px;
}
```

---

# CHECKER (CH)

## CH01 — undefined variable

```icss
p {
  width: A;
}
```

**Expect**

```
ERROR: Undefined variable: A
```

---

## CH02 — type mismatch (add)

```icss
p {
  width: 10px + 5%;
}
```

**Expect**

```
ERROR: Operands must be same type
```

---

## CH02 — multiply rule

```icss
p {
  width: 2px * 3px;
}
```

**Expect**

```
ERROR: Multiply requires one scalar
```

---

## CH03 — color in operation

```icss
p {
  color: #ff0000 + #00ff00;
}
```

**Expect**

```
ERROR: Cannot use color in operations
```

---

## CH04 — property type mismatch

```icss
p {
  width: #ff0000;
  color: 10px;
}
```

**Expect**

```
ERROR: Size must be pixel or percentage
ERROR: Color expected
```

---

## CH05 — condition must be boolean

### valid

```icss
Cond := TRUE;

p {
  if [Cond] {
    width: 100px;
  }
}
```

**Expect**

* no errors

---

### invalid

```icss
Cond := 10px;

p {
  if [Cond] {
    width: 100px;
  }
}
```

**Expect**

```
ERROR: Condition must be boolean
```

---

## CH06 — scope (MUST)

### 1. use before declaration

```icss
p {
  width: A;
  A := 10px;
}
```

**Expect**

```
ERROR: Undefined variable: A
```

---

### 2. outside rule

```icss
p {
  A := 10px;
}

div {
  width: A;
}
```

**Expect**

```
ERROR: Undefined variable: A
```

---

### 3. if scope

```icss
p {
  if [TRUE] {
    A := 10px;
  }
  width: A;
}
```

**Expect**

```
ERROR: Undefined variable: A
```

---

### 4. valid global usage

```icss
A := 10px;

p {
  width: A;
}
```

**Expect**

```css
p {
  width: 10px;
}
```

---

### 5. shadowing

```icss
A := 10px;

p {
  A := 20px;
  width: A;
}
```

**Expect**

```css
p {
  width: 20px;
}
```

---

# TRANSFORM (TR)

## TR01 — expression evaluation

```icss
p {
  width: 50px + 2 * 10px;
}
```

**Expect**

```css
p {
  width: 70px;
}
```

---

## TR02 — if evaluation

```icss
Use := FALSE;

p {
  if [Use] {
    width: 100px;
  } else {
    width: 50px;
  }
}
```

**Expect**

```css
p {
  width: 50px;
}
```

---

## MUST combined test

```icss
AdjustWidth := TRUE;
WidthVar := 0px;

p {
  if [AdjustWidth] {
    WidthVar := 200px;
  } else {
    WidthVar := 250px;
  }

  width: WidthVar;
}
```

**Expect**

```css
p {
  width: 200px;
}
```

---

# GENERATOR (GE)

## GE01 — basic output

```icss
p {
  width: 100px;
}
```

**Expect**

```css
p {
  width: 100px;
}
```

---

## GE02 — indentation

```icss
p {
  width: 100px;
  height: 50px;
}
```

**Expect (exact spacing)**

```css
p {
  width: 100px;
  height: 50px;
}
```

---

