package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.ArrayList;
import java.util.List;

public class ASTListener extends ICSSBaseListener {

    private AST ast;
    private IHANStack<ASTNode> containerStack;
    private IHANStack<Expression> expressionStack;

    public ASTListener() {
        ast = new AST();
        containerStack = new HANStack<>();
        expressionStack = new HANStack<>();
    }

    public AST getAST() {
        return ast;
    }

    // -------------------------
    // ROOT
    // -------------------------

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        ast.setRoot(stylesheet);
        containerStack.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        containerStack.pop();
    }

    // -------------------------
    // STYLE RULE
    // -------------------------

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule rule = new Stylerule();
        containerStack.peek().addChild(rule);
        containerStack.push(rule);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        containerStack.pop();
    }

    // -------------------------
    // SELECTORS
    // -------------------------

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
        Selector selector;

        if (ctx.ID_IDENT() != null) {
            selector = new IdSelector(ctx.ID_IDENT().getText());
        } else if (ctx.CLASS_IDENT() != null) {
            selector = new ClassSelector(ctx.CLASS_IDENT().getText());
        } else {
            selector = new TagSelector(ctx.LOWER_IDENT().getText());
        }

        containerStack.peek().addChild(selector);
    }

    // -------------------------
    // DECLARATION
    // -------------------------

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration(ctx.LOWER_IDENT().getText());
        containerStack.peek().addChild(declaration);
        containerStack.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        Expression expr = expressionStack.pop();
        Declaration declaration = (Declaration) containerStack.peek();
        declaration.expression = expr;
        containerStack.pop();
    }

    // -------------------------
    // VARIABLE ASSIGNMENT
    // -------------------------

    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment assignment = new VariableAssignment();

        VariableReference var = new VariableReference(ctx.CAPITAL_IDENT().getText());
        assignment.addChild(var);

        containerStack.peek().addChild(assignment);
        containerStack.push(assignment);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        Expression expr = expressionStack.pop();
        VariableAssignment assignment = (VariableAssignment) containerStack.peek();

        assignment.addChild(expr);
        containerStack.pop();
    }

    // -------------------------
    // EXPRESSIONS
    // -------------------------

    @Override
    public void enterPrimary(ICSSParser.PrimaryContext ctx) {
        Expression expr;

        if (ctx.literal() != null) {
            if (ctx.literal().COLOR() != null) {
                expr = new ColorLiteral(ctx.literal().getText());
            } else if (ctx.literal().PIXELSIZE() != null) {
                expr = new PixelLiteral(ctx.literal().getText());
            } else if (ctx.literal().PERCENTAGE() != null) {
                expr = new PercentageLiteral(ctx.literal().getText());
            } else if (ctx.literal().SCALAR() != null) {
                expr = new ScalarLiteral(ctx.literal().getText());
            } else if (ctx.literal().TRUE() != null) {
                expr = new BoolLiteral(true);
            } else {
                expr = new BoolLiteral(false);
            }
        } else {
            expr = new VariableReference(ctx.getText());
        }

        expressionStack.push(expr);
    }

    @Override
    public void exitMulExpr(ICSSParser.MulExprContext ctx) {
        if (ctx.primary().size() <= 1) return;

        Expression result = expressionStack.pop();

        for (int i = ctx.primary().size() - 2; i >= 0; i--) {
            Expression left = expressionStack.pop();
            MultiplyOperation op = new MultiplyOperation();
            op.addChild(left);
            op.addChild(result);
            result = op;
        }

        expressionStack.push(result);
    }

    @Override
    public void exitAddExpr(ICSSParser.AddExprContext ctx) {
        if (ctx.mulExpr().size() <= 1) return;

        Expression result = expressionStack.pop();

        for (int i = ctx.mulExpr().size() - 2; i >= 0; i--) {
            Expression left = expressionStack.pop();

            if (ctx.PLUS(i) != null) {
                AddOperation op = new AddOperation();
                op.addChild(left);
                op.addChild(result);
                result = op;
            } else {
                SubtractOperation op = new SubtractOperation();
                op.addChild(left);
                op.addChild(result);
                result = op;
            }
        }

        expressionStack.push(result);
    }

    // -------------------------
    // IF / ELSE
    // -------------------------

    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause ifClause = new IfClause();
        containerStack.peek().addChild(ifClause);
        containerStack.push(ifClause);
    }

    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause ifClause = (IfClause) containerStack.pop();

        // condition must be first child
        Expression condition = expressionStack.pop();
        ifClause.getChildren().add(0, condition);
    }

    @Override
    public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
        ElseClause elseClause = new ElseClause();
        containerStack.peek().addChild(elseClause);
        containerStack.push(elseClause);
    }

    @Override
    public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
        containerStack.pop();
    }
}