package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;

import java.util.HashMap;

public class Checker {

    // Stack of scopes (each scope = variable name → type)
    private IHANLinkedList<HashMap<String, Object>> variableScopes;

    public void check(AST ast) {
        variableScopes = new HANLinkedList<>();

        // Global scope
        pushScope();

        // Walk root
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
            enterStylerule((Stylerule) node);
        }
        else if (node instanceof IfClause) {
            enterIfClause((IfClause) node);
        }
        else if (node instanceof ElseClause) {
            enterElseClause((ElseClause) node);
        }
        else if (node instanceof VariableAssignment) {
            enterVariableAssignment((VariableAssignment) node);
        }
        else if (node instanceof Declaration) {
            enterDeclaration((Declaration) node);
        }

        // Always continue walking children
        for (ASTNode child : node.getChildren()) {
            walk(child);
        }

        // Exit hooks
        if (node instanceof Stylerule) {
            exitStylerule();
        }
        else if (node instanceof IfClause) {
            exitIfClause();
        }
        else if (node instanceof ElseClause) {
            exitElseClause();
        }
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

    // -------------------------
    // ENTER METHODS (EMPTY FOR NOW)
    // -------------------------

    private void enterStylerule(Stylerule rule) {
        pushScope();
    }

    private void exitStylerule() {
        popScope();
    }

    private void enterIfClause(IfClause ifClause) {
        pushScope();
    }

    private void exitIfClause() {
        popScope();
    }

    private void enterElseClause(ElseClause elseClause) {
        pushScope();
    }

    private void exitElseClause() {
        popScope();
    }

    private void enterVariableAssignment(VariableAssignment assignment) {
        // Step 2: implement
    }

    private void enterDeclaration(Declaration declaration) {
        // Step 2: implement
    }
}