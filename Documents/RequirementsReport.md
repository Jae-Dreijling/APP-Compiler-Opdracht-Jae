# ICSS Compiler – Requirements Coverage Report

## 1. Inleiding

Dit document beschrijft in hoeverre de ontwikkelde ICSS-compiler voldoet aan de eisen zoals gesteld in de opdracht *Beroepsproduct: parser (B_Compiler)*.

De compiler bestaat uit de volgende componenten:

* Parser (ANTLR + ASTListener)
* Checker (semantische analyse)
* Evaluator (transformaties)
* Generator (CSS output)

---

## 2. Algemene eisen

| ID   | Status | Toelichting                                    |
| ---- | ------ | ---------------------------------------------- |
| AL01 | ✅      | Packagestructuur van de startcode is behouden  |
| AL02 | ✅      | Project build succesvol met Maven              |
| AL03 | ✅      | Code is gestructureerd en leesbaar             |
| AL04 | ✅      | Compiler is zelfstandig ontwikkeld en begrepen |

---

## 3. Parser (40 punten)

| ID   | Status | Toelichting                                                              |
| ---- | ------ | ------------------------------------------------------------------------ |
| PA00 | ✅      | Eigen datastructuur (`IHANStack`) gebruikt voor stack-achtig gedrag |
| PA01 | ✅      | Basis CSS parsing werkt (level0)                                         |
| PA02 | ✅      | Variabelen en assignments worden geparsed                                |
| PA03 | ✅      | Expressies (+, -, *) correct geparsed met juiste prioriteit              |
| PA04 | ✅      | If/else structuren worden ondersteund                                    |
| PA05 | ✅      | Minimale score behaald                                                   |

Extra:

* Extra testlevels (level4, level5) toegevoegd voor uitgebreidere parsing

---

## 4. Checker (30 punten)

Minimaal 4 checks vereist — onderstaande zijn geïmplementeerd:

| ID   | Status | Toelichting                                     |
| ---- | ------ | ----------------------------------------------- |
| CH01 | ✅      | Undefined variables worden gedetecteerd         |
| CH02 | ✅      | Type-checking op operaties (+, -, *)            |
| CH03 | ✅      | Kleuren niet toegestaan in operaties            |
| CH04 | ✅      | Property-type validatie (color vs width/height) |
| CH05 | ✅      | If-conditie moet boolean zijn                   |
| CH06 | ✅      | Scope regels correct geïmplementeerd            |

---

## 5. Transformeren (20 punten)

| ID   | Status | Toelichting                                            |
| ---- | ------ | ------------------------------------------------------ |
| TR01 | ✅      | Expressies worden geëvalueerd naar literals            |
| TR02 | ✅      | If/else wordt verwijderd en vervangen door juiste body |

Extra:

* Dubbele declaraties worden opgeschoond (laatste wint)
* Variabelen worden uit AST verwijderd na evaluatie

---

## 6. Genereren (10 punten)

| ID   | Status | Toelichting                     |
| ---- | ------ | ------------------------------- |
| GE01 | ✅      | Correcte CSS-output gegenereerd |
| GE02 | ✅      | 2 spaties indentatie per niveau |

---

## 7. Testen

Naast de standaard tests zijn extra testcases toegevoegd:

* level4: variabelen + expressies
* level5: if/else structuren

## 8. Gebruik van AI-hulpmiddelen

Bij het opstellen van dit document is gebruikgemaakt van AI-ondersteuning voor het structureren en formuleren van de tekst.

De inhoud, keuzes, implementatie en technische uitwerking van de opdracht zijn zelfstandig uitgevoerd en begrepen door de auteur.

---

## 9. Conclusie

Alle *Must*-eisen zijn geïmplementeerd en werken correct.
Daarnaast zijn meerdere *Should*-eisen en extra testgevallen toegevoegd.

De compiler is functioneel compleet en robuust getest.
