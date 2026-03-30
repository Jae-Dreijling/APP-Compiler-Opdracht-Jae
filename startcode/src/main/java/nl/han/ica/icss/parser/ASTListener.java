package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private HANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.push(ast.root);
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule rule = new Stylerule();

		currentContainer.peek().addChild(rule);
		currentContainer.push(rule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
		Selector selector;

		if (ctx.LOWER_IDENT() != null) {
			selector = new TagSelector(ctx.getText());
		} else if (ctx.ID_IDENT() != null) {
			selector = new IdSelector(ctx.getText());
		} else {
			selector = new ClassSelector(ctx.getText());
		}

		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();

		// Property
		String property = ctx.LOWER_IDENT().getText();
		declaration.property = new PropertyName(property);

		// Design choice: process literals in enterDeclaration() to maintain control over
		// parent-child relationships in the AST, instead of relying on separate listener callbacks.

		// Value (literal)
		Expression expr;

		ICSSParser.ExpressionContext exprCtx = ctx.expression();

		if (exprCtx.literal() != null) {
			if (exprCtx.literal().COLOR() != null) {
				expr = new ColorLiteral(exprCtx.literal().getText());
			} else if (exprCtx.literal().PIXELSIZE() != null) {
				expr = new PixelLiteral(exprCtx.literal().getText());
			} else if (exprCtx.literal().TRUE() != null) {
				expr = new BoolLiteral(true);
			} else if (exprCtx.literal().FALSE() != null) {
				expr = new BoolLiteral(false);
			} else {
				expr = new PercentageLiteral(exprCtx.literal().getText());
			}
		} else {
			// must be variable reference
			expr = new VariableReference(exprCtx.getText());
		}

		declaration.expression = expr;

		currentContainer.peek().addChild(declaration);
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment assignment = new VariableAssignment();

		// variable name
		VariableReference varRef = new VariableReference(ctx.CAPITAL_IDENT().getText());
		assignment.addChild(varRef);

		// expression
		Expression expr;

		ICSSParser.ExpressionContext exprCtx = ctx.expression();

		if (exprCtx.literal() != null) {
			if (exprCtx.literal().COLOR() != null) {
				expr = new ColorLiteral(exprCtx.literal().getText());
			} else if (exprCtx.literal().PIXELSIZE() != null) {
				expr = new PixelLiteral(exprCtx.literal().getText());
			} else if (exprCtx.literal().TRUE() != null) {
				expr = new BoolLiteral(true);
			} else if (exprCtx.literal().FALSE() != null) {
				expr = new BoolLiteral(false);
			} else {
				expr = new PercentageLiteral(exprCtx.literal().getText());
			}
		
		} else {
			expr = new VariableReference(exprCtx.getText());
		}

		assignment.addChild(expr);

		currentContainer.peek().addChild(assignment);
	}


}