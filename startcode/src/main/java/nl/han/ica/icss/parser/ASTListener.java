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

public class ASTListener extends ICSSBaseListener {

	// ----- Core AST state -----
	private AST ast;
	private IHANStack<ASTNode> currentContainer;

	// ----- Construction & accessor -----
	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

	public AST getAST() {
		return ast;
	}

	// ----- Stylesheet root handling -----
	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		ast.setRoot(stylesheet);
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.pop();
	}

	// ----- Variable assignment handling -----
	@Override
	public void enterAssignment(ICSSParser.AssignmentContext ctx) {
		VariableAssignment assignment = new VariableAssignment();
		currentContainer.peek().addChild(assignment);
		currentContainer.push(assignment);
	}

	@Override
	public void exitAssignment(ICSSParser.AssignmentContext ctx) {
		currentContainer.pop();
	}

	// ----- Variable references in expressions -----
	@Override
	public void enterVariable(ICSSParser.VariableContext ctx) {
		VariableReference var = new VariableReference(ctx.getText());

		if (currentContainer.peek() instanceof VariableAssignment) {
			currentContainer.peek().addChild(var);
		} else if (currentContainer.peek() instanceof IfClause) {
			currentContainer.peek().addChild(var); // condition
		}
	}

	@Override
	public void enterVariableExpression(ICSSParser.VariableExpressionContext ctx) {
		VariableReference variableReference = new VariableReference(ctx.getText());
		currentContainer.peek().addChild(variableReference);
	}

	// ----- Operations (add, subtract, multiply) -----
	@Override
	public void enterOperationExpression(ICSSParser.OperationExpressionContext ctx) {
		Operation operation;
		switch (ctx.op.getType()) {
			case ICSSParser.PLUS:
				operation = new AddOperation();
				break;
			case ICSSParser.MIN:
				operation = new SubtractOperation();
				break;
			case ICSSParser.MUL:
				operation = new MultiplyOperation();
				break;
			default:
				throw new RuntimeException("Unknown operator: " + ctx.op.getText());
		}
		currentContainer.peek().addChild(operation);
		currentContainer.push(operation);
	}

	@Override
	public void exitOperationExpression(ICSSParser.OperationExpressionContext ctx) {
		currentContainer.pop();
	}

	// ----- Literal node creation -----
	@Override
	public void enterLiteral(ICSSParser.LiteralContext ctx) {
		Literal literal;
		switch (ctx.start.getType()) {
			case ICSSParser.COLOR:
				literal = new ColorLiteral(ctx.getText().toLowerCase());
				break;
			case ICSSParser.PIXELSIZE:
				literal = new PixelLiteral(ctx.getText());
				break;
			case ICSSParser.PERCENTAGE:
				literal = new PercentageLiteral(ctx.getText());
				break;
			case ICSSParser.SCALAR:
				literal = new ScalarLiteral(ctx.getText());
				break;
			case ICSSParser.TRUE:
				literal = new BoolLiteral(true);
				break;
			case ICSSParser.FALSE:
				literal = new BoolLiteral(false);
				break;
			default:
				throw new RuntimeException("Unknown literal: " + ctx.getText());
		}
		currentContainer.peek().addChild(literal);
	}

	// ----- Style rule handling -----
	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.peek().addChild(stylerule);
		currentContainer.push(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.pop();
	}

	// ----- Selector dispatch (ID, class, tag) -----
	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
		Selector selector;
		if (ctx.ID_IDENT() != null) {
			selector = new IdSelector(ctx.ID_IDENT().getText());
		} else if (ctx.CLASS_IDENT() != null) {
			selector = new ClassSelector(ctx.CLASS_IDENT().getText());
		} else if (ctx.LOWER_IDENT() != null) {
			selector = new TagSelector(ctx.LOWER_IDENT().getText());
		} else {
			throw new RuntimeException("Unknown selector: " + ctx.getText());
		}
		currentContainer.peek().addChild(selector);
	}

	// ----- Property declaration handling -----
	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		declaration.property = new PropertyName(ctx.prop.getText());
		currentContainer.peek().addChild(declaration);
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.pop();
	}

	// ----- Conditional blocks (if/else) -----
	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifClause = new IfClause();
		currentContainer.peek().addChild(ifClause);
		currentContainer.push(ifClause);
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		ElseClause elseClause = new ElseClause();
		currentContainer.peek().addChild(elseClause);
		currentContainer.push(elseClause);
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		currentContainer.pop();
	}
}