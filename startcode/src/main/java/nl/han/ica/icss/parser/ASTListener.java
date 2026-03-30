package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class ASTListener extends ICSSBaseListener {

	private AST ast;
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

	// -------------------------
	// DECLARATION
	// -------------------------

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		declaration.property = new PropertyName(ctx.LOWER_IDENT().getText());

		currentContainer.peek().addChild(declaration);
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Expression expr = (Expression) currentContainer.pop(); // result of expression
		Declaration declaration = (Declaration) currentContainer.peek();
		declaration.expression = expr;
		currentContainer.pop(); // remove declaration
	}

	// -------------------------
	// VARIABLE ASSIGNMENT
	// -------------------------

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment assignment = new VariableAssignment();

		VariableReference varRef = new VariableReference(ctx.CAPITAL_IDENT().getText());
		assignment.addChild(varRef);

		currentContainer.peek().addChild(assignment);
		currentContainer.push(assignment);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		Expression expr = (Expression) currentContainer.pop();
		VariableAssignment assignment = (VariableAssignment) currentContainer.peek();
		assignment.addChild(expr);
		currentContainer.pop();
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
			}  else {
				expr = new BoolLiteral(false);
			}
		} else {
			expr = new VariableReference(ctx.getText());
		}

		currentContainer.push(expr);
	}

	@Override
	public void exitMulExpr(ICSSParser.MulExprContext ctx) {
		if (ctx.primary().size() == 1) return;

		Expression result = (Expression) currentContainer.pop();

		for (int i = ctx.primary().size() - 2; i >= 0; i--) {
			Expression left = (Expression) currentContainer.pop();
			MultiplyOperation op = new MultiplyOperation();
			op.addChild(left);
			op.addChild(result);
			result = op;
		}

		currentContainer.push(result);
	}

	@Override
	public void exitAddExpr(ICSSParser.AddExprContext ctx) {
		if (ctx.mulExpr().size() == 1) return;

		Expression result = (Expression) currentContainer.pop();

		for (int i = ctx.mulExpr().size() - 2; i >= 0; i--) {
			Expression left = (Expression) currentContainer.pop();

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

		currentContainer.push(result);
	}
}