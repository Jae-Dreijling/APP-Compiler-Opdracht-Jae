package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableScopes;

    public void check(AST ast) {
        variableScopes = new HANLinkedList<>();

        pushScope();

        for (ASTNode node : ast.root.getChildren()) {
            walk(node);
        }

        popScope();
    }

    // -------------------------
    // TREE WALKER
    // -------------------------

    private void walk(ASTNode node) {

        if (node instanceof Stylerule) {
            pushScope();
        }
        else if (node instanceof IfClause) {
            handleIfClause((IfClause) node);
            pushScope();
        }
        else if (node instanceof ElseClause) {
            pushScope();
        }
        else if (node instanceof VariableAssignment) {
            handleVariableAssignment((VariableAssignment) node);
        }
        else if (node instanceof Declaration) {
            handleDeclaration((Declaration) node);
        }

        for (ASTNode child : node.getChildren()) {
            walk(child);
        }

        if (node instanceof Stylerule) {
            popScope();
        }
        else if (node instanceof IfClause) {
            popScope();
        }
        else if (node instanceof ElseClause) {
            popScope();
        }
    }

    // -------------------------
    // VARIABLE ASSIGNMENT
    // -------------------------

    private void handleVariableAssignment(VariableAssignment assignment) {
        ExpressionType type = resolveType(assignment.expression);

        variableScopes.getFirst().put(assignment.name.name, type);
    }

    // -------------------------
    // DECLARATION (FINAL CHECK)
    // -------------------------

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

    // -------------------------
    // IF CLAUSE CHECK
    // -------------------------

    private void handleIfClause(IfClause ifClause) {
        ExpressionType condType = resolveType(ifClause.getConditionalExpression());

        if (condType != ExpressionType.UNDEFINED && condType != ExpressionType.BOOL) {
            ifClause.setError("Condition must be boolean");
        }
    }

    // -------------------------
    // TYPE RESOLUTION
    // -------------------------

    private ExpressionType resolveType(Expression expr) {

        if (expr instanceof BoolLiteral) return ExpressionType.BOOL;
        if (expr instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expr instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expr instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expr instanceof ScalarLiteral) return ExpressionType.SCALAR;

        if (expr instanceof VariableReference) {
            return resolveVariable((VariableReference) expr);
        }

        if (expr instanceof Operation) {
            return resolveOperation((Operation) expr);
        }

        return ExpressionType.UNDEFINED;
    }

    // -------------------------
    // VARIABLE LOOKUP
    // -------------------------

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

    // -------------------------
    // OPERATIONS
    // -------------------------

    private ExpressionType resolveOperation(Operation op) {
        ExpressionType left = resolveType(op.lhs);
        ExpressionType right = resolveType(op.rhs);

        if (left == ExpressionType.UNDEFINED || right == ExpressionType.UNDEFINED) {
            return ExpressionType.UNDEFINED;
        }

        if (left == ExpressionType.COLOR || right == ExpressionType.COLOR) {
            op.setError("Cannot use color in operations");
            return ExpressionType.UNDEFINED;
        }

        if (left == ExpressionType.BOOL || right == ExpressionType.BOOL) {
            op.setError("Cannot use boolean in operations");
            return ExpressionType.UNDEFINED;
        }

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

        return ExpressionType.UNDEFINED;
    }

    // -------------------------
    // SCOPES
    // -------------------------

    private void pushScope() {
        variableScopes.addFirst(new HashMap<>());
    }

    private void popScope() {
        variableScopes.removeFirst();
    }
}