package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {

    public String generate(AST ast) {
        StringBuilder css = new StringBuilder();

        for (ASTNode node : ast.root.body) {
            if (node instanceof Stylerule) {
                css.append(generateStylerule((Stylerule) node));
            }
        }

        return css.toString();
    }

    private String generateStylerule(Stylerule rule) {
        StringBuilder css = new StringBuilder();

        css.append(rule.selectors.get(0).toString()).append(" {\n");

        for (ASTNode node : rule.body) {
            if (node instanceof Declaration) {
                Declaration decl = (Declaration) node;

                css.append("  ")
                   .append(decl.property.name)
                   .append(": ")
                   .append(expressionToCss(decl.expression))
                   .append(";\n");
            }
        }

        css.append("}\n");

        return css.toString();
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
            return Boolean.toString(((BoolLiteral) expression).value);
        }
        return "";
    }
}