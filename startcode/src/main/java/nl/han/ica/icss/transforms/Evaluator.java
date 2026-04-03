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

        ArrayList<ASTNode> rootChildren = new ArrayList<>(ast.root.getChildren());

        for (ASTNode node : rootChildren) {
            evaluateNode(ast.root, node);
        }

        // 🔥 REMOVE GLOBAL VARIABLE ASSIGNMENTS
        cleanVariables(ast.root);

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
            return;
        }

        ArrayList<ASTNode> children = new ArrayList<>(node.getChildren());

        for (ASTNode child : children) {
            evaluateNode(node, child);
        }

        if (node instanceof Stylerule) {
            cleanDeclarations((Stylerule) node); // 🔥 NEW
            popScope();
        }
    }

    // -------------------------
    // IF CLAUSE
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

        int index = parent.getChildren().indexOf(ifClause);
        parent.removeChild(ifClause);

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

        if (value != null) {
            declaration.expression = value;
        }
    }

    // -------------------------
    // EXPRESSION
    // -------------------------

    private Literal evaluateExpression(Expression expr) {

        if (expr instanceof Literal)
            return (Literal) expr;

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

        if (op instanceof AddOperation)
            return add(left, right);
        if (op instanceof SubtractOperation)
            return subtract(left, right);
        if (op instanceof MultiplyOperation)
            return multiply(left, right);

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
    // CLEANUP (🔥 IMPORTANT)
    // -------------------------

    private void cleanVariables(ASTNode node) {
        ArrayList<ASTNode> toRemove = new ArrayList<>();

        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                toRemove.add(child);
            } else {
                cleanVariables(child);
            }
        }

        for (ASTNode n : toRemove) {
            node.removeChild(n);
        }
    }

    private void cleanDeclarations(Stylerule rule) {
        HashMap<String, Declaration> seen = new HashMap<>();
        ArrayList<ASTNode> toRemove = new ArrayList<>();

        for (ASTNode node : rule.getChildren()) {
            if (node instanceof Declaration) {
                Declaration decl = (Declaration) node;
                String prop = decl.property.name;

                if (seen.containsKey(prop)) {
                    toRemove.add(seen.get(prop));
                }

                if (decl.hasError()) {
                    toRemove.add(decl);
                }

                seen.put(prop, decl);
            }
        }

        

        for (ASTNode n : toRemove) {
            rule.removeChild(n);
        }
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