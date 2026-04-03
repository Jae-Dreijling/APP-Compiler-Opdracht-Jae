package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        variableTypes.addFirst(new HashMap<>());

        for (ASTNode node : ast.root.getChildren()) {
            if (node instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) node);
            } else if (node instanceof Stylerule) {
                checkStylerule((Stylerule) node);
            }
        }

        variableTypes.removeFirst();
    }

    private void checkStylerule(Stylerule stylerule) {
        for (ASTNode node : stylerule.getChildren()) {
            if (node instanceof Declaration) {
                checkDeclaration((Declaration) node);
            }
        }
    }

    private void checkVariableAssignment(VariableAssignment assignment) {
        // for now: just store variable
        variableTypes.getFirst().put(assignment.name.name, ExpressionType.UNDEFINED);
    }

    private void checkDeclaration(Declaration declaration) {
        checkExpression(declaration.expression);
    }

    private void checkExpression(Expression expr) {
        if (expr instanceof VariableReference) {
            String name = ((VariableReference) expr).name;

            if (!variableTypes.getFirst().containsKey(name)) {
                expr.setError("Undefined variable: " + name);
            }
        }
    }
}