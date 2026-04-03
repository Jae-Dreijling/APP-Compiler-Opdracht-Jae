package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        pushScope();

        for (ASTNode node : ast.root.getChildren()) {
            evaluateNode(null, node);
        }

        popScope();
    }

    // -------------------------
    // NODE EVALUATION
    // -------------------------

    private void evaluateNode(ASTNode parent, ASTNode node) {

        if (node instanceof Stylerule) {
            pushScope();
        }

        if (node instanceof VariableAssignment) {
            handleVariableAssignment((VariableAssignment) node);
        }

        if (node instanceof Declaration) {
            handleDeclaration((Declaration) node);
        }

        if (node instanceof IfClause) {
            evaluateIfClause(parent, (IfClause) node);
            return; // IMPORTANT: stop further traversal (node removed)
        }

        // Copy to avoid concurrent modification
        ArrayList<ASTNode> children = new ArrayList<>(node.getChildren());

        for (ASTNode child : children) {
            evaluateNode(node, child);
        }

        if (node instanceof Stylerule) {
            popScope();
        }
    }

    // -------------------------
    // IF CLAUSE (NEW PART)
    // -------------------------

    private void evaluateIfClause(ASTNode parent, IfClause ifClause) {

        Literal condition = evaluateExpression(ifClause.getConditionalExpression());

        boolean isTrue = false;
        if (condition instanceof BoolLiteral) {
            isTrue = ((BoolLiteral) condition).value;
        }

        ArrayList<ASTNode> chosenBranch;

        if (isTrue) {
            chosenBranch = new ArrayList<>(ifClause.body);
        } else if (ifClause.getElseClause() != null) {
            chosenBranch = new ArrayList<>(ifClause.getElseClause().body);
        } else {
            chosenBranch = new ArrayList<>();
        }

        // Remove the if-clause from parent
        parent.removeChild(ifClause);

        // Insert chosen branch at same position
        int index = parent.getChildren().size();

        for (int i = 0; i < parent.getChildren().size(); i++) {
            if (parent.getChildren().get(i) == ifClause) {
                index = i;
                break;
            }
        }

        pushScope();

        for (int i = 0; i < chosenBranch.size(); i++) {
            ASTNode child = chosenBranch.get(i);

            parent.addChild(child);
            evaluateNode(parent, child);
        }

        popScope();
    }

    // -------------------------
    // VARIABLE ASSIGNMENT
    // -------------------------

    private void handleVariableAssignment(VariableAssignment assignment) {
        Literal value = evaluateExpression(assignment.expression);
        variableValues.getFirst().put(assignment.name.name, value);
    }

    // -------------------------
    // DECLARATION
    // -------------------------

    private void handleDeclaration(Declaration declaration) {
        Literal value = evaluateExpression(declaration.expression);
        declaration.expression = value;
    }

    // -------------------------
    // EXPRESSION EVALUATION
    // -------------------------

    private Literal evaluateExpression(Expression expr) {

        if (expr instanceof Literal) return (Literal) expr;

        if (expr instanceof VariableReference) {
            return resolveVariable(((VariableReference) expr).name);
        }

        if (expr instanceof Operation) {
            return evaluateOperation((Operation) expr);
        }

        return null;
    }

    private Literal resolveVariable(String name) {
        for (int i = 0; i < variableValues.getSize(); i++) {
            HashMap<String, Literal> scope = variableValues.get(i);

            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    // -------------------------
    // OPERATIONS
    // -------------------------

    private Literal evaluateOperation(Operation op) {
        Literal left = evaluateExpression(op.lhs);
        Literal right = evaluateExpression(op.rhs);

        if (op instanceof AddOperation) return add(left, right);
        if (op instanceof SubtractOperation) return subtract(left, right);
        if (op instanceof MultiplyOperation) return multiply(left, right);

        return null;
    }

    private Literal add(Literal l, Literal r) {
        if (l instanceof PixelLiteral && r instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) l).value + ((PixelLiteral) r).value);
        }
        if (l instanceof PercentageLiteral && r instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) l).value + ((PercentageLiteral) r).value);
        }
        if (l instanceof ScalarLiteral && r instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) l).value + ((ScalarLiteral) r).value);
        }
        return null;
    }

    private Literal subtract(Literal l, Literal r) {
        if (l instanceof PixelLiteral && r instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) l).value - ((PixelLiteral) r).value);
        }
        if (l instanceof PercentageLiteral && r instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) l).value - ((PercentageLiteral) r).value);
        }
        if (l instanceof ScalarLiteral && r instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) l).value - ((ScalarLiteral) r).value);
        }
        return null;
    }

    private Literal multiply(Literal l, Literal r) {
        if (l instanceof ScalarLiteral && r instanceof PixelLiteral) {
            return new PixelLiteral(((ScalarLiteral) l).value * ((PixelLiteral) r).value);
        }
        if (l instanceof PixelLiteral && r instanceof ScalarLiteral) {
            return new PixelLiteral(((PixelLiteral) l).value * ((ScalarLiteral) r).value);
        }
        if (l instanceof ScalarLiteral && r instanceof PercentageLiteral) {
            return new PercentageLiteral(((ScalarLiteral) l).value * ((PercentageLiteral) r).value);
        }
        if (l instanceof PercentageLiteral && r instanceof ScalarLiteral) {
            return new PercentageLiteral(((PercentageLiteral) l).value * ((ScalarLiteral) r).value);
        }
        if (l instanceof ScalarLiteral && r instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) l).value * ((ScalarLiteral) r).value);
        }
        return null;
    }

    // -------------------------
    // SCOPES
    // -------------------------

    private void pushScope() {
        variableValues.addFirst(new HashMap<>());
    }

    private void popScope() {
        variableValues.removeFirst();
    }
}