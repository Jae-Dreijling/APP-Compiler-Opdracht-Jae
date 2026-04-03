# ICSS Compiler – Eigen Taaluitbreiding

## 1. Inleiding

Als uitbreiding op de standaard ICSS-functionaliteit is een extra semantische regel geïmplementeerd:

> **Variabelen mogen niet van type veranderen na eerste declaratie**

Deze uitbreiding verhoogt de betrouwbaarheid en consistentie van de taal.

---

## 2. Beschrijving van de uitbreiding

### Probleem

In de standaard ICSS-specificatie is het mogelijk om variabelen meerdere keren te overschrijven met verschillende types:

```icss
A := 10px;
A := 20%;
```

Dit kan leiden tot:

* onvoorspelbaar gedrag
* moeilijk te debuggen code
* inconsistentie in typegebruik

---

### Oplossing

De compiler controleert nu:

> Zodra een variabele een type heeft gekregen, mag dit type niet meer veranderen.

---

## 3. Implementatie

De uitbreiding is geïmplementeerd in de `Checker`.

Bij een nieuwe assignment:

1. Het type van de variabele wordt bepaald
2. Er wordt gecontroleerd of de variabele al bestaat in de huidige scope
3. Indien het type verschilt → foutmelding

Voorbeeld:

```java
ExpressionType existing = variableScopes.getFirst().get(name);

if (existing != null && existing != newType) {
    assignment.setError("Variable type cannot change");
}
```

---

## 4. Voorbeelden

### ❌ Ongeldig

```icss
A := 10px;
A := 20%;
```

→ Error: *Variable type cannot change*

---

### ✅ Geldig

```icss
A := 10px;
A := 20px;
```

---

## 5. Toegevoegde testcases

Nieuwe testbestanden:

* level10.icss → test type mismatch
* Extra checker tests voor variabelen

---

## 6. Meerwaarde

Deze uitbreiding:

* voorkomt typefouten vroegtijdig
* maakt code voorspelbaarder
* sluit beter aan bij typed languages

---

## 7. Verwachte punten

Deze uitbreiding valt onder:

> “Iedere variabele mag alleen een vast type hebben”

Geschatte waarde: **10–20 punten**

---

## 8. Conclusie

De uitbreiding voegt duidelijke semantische strengheid toe aan ICSS en verhoogt de kwaliteit van gegenereerde CSS.

Hiermee wordt de compiler niet alleen correct, maar ook robuuster en realistischer in gebruik.
