package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {

    public String generate(AST ast) {
        StringBuilder css = new StringBuilder();
        boolean firstRule = true;

        for (ASTNode node : ast.root.body) {
            if (!(node instanceof Stylerule)) {
                continue;
            }

            if (!firstRule) {
                css.append("\n");
            }

            writeStylerule(css, (Stylerule) node);
            firstRule = false;
        }

        return css.toString();
    }

    private void writeStylerule(StringBuilder css, Stylerule rule) {
        if (rule.selectors.isEmpty()) {
            return;
        }

        // Multiple selectors support
        for (int i = 0; i < rule.selectors.size(); i++) {
            if (i > 0) {
                css.append(", ");
            }
            css.append(rule.selectors.get(i).toString());
        }

        css.append(" {\n");

        for (ASTNode node : rule.body) {
            if (node instanceof Declaration) {
                Declaration declaration = (Declaration) node;

                if (declaration.hasError() || declaration.expression == null || declaration.property == null) {
                    continue;
                }

                writeDeclaration(css, declaration);
            }
        }

        css.append("}\n");
    }

    private void writeDeclaration(StringBuilder css, Declaration declaration) {
        css.append("  ");
        css.append(declaration.property.name);
        css.append(": ");
        css.append(expressionToCss(declaration.expression));
        css.append(";\n");
    }

    private String expressionToCss(Expression expression) {
        if (expression instanceof PixelLiteral) {
            return ((PixelLiteral) expression).value + "px";
        }
        if (expression instanceof PercentageLiteral) {
            return ((PercentageLiteral) expression).value + "%";
        }
        if (expression instanceof ColorLiteral) {
            return ((ColorLiteral) expression).value;
        }
        if (expression instanceof ScalarLiteral) {
            return Integer.toString(((ScalarLiteral) expression).value);
        }
        if (expression instanceof BoolLiteral) {
            return ((BoolLiteral) expression).value ? "TRUE" : "FALSE";
        }
        return "";
    }
}