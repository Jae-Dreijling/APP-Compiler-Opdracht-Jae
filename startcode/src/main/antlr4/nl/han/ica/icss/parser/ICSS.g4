grammar ICSS;

//--- LEXER: ---

IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';

TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

WS: [ \t\r\n]+ -> skip;

OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---

stylesheet: (assignment | stylerule)* EOF;

assignment: variable ASSIGNMENT_OPERATOR expression SEMICOLON;

stylerule: selector OPEN_BRACE body CLOSE_BRACE;

body: (declaration | ifClause)*;

selector: ID_IDENT | CLASS_IDENT | LOWER_IDENT;

declaration: prop=LOWER_IDENT COLON expression SEMICOLON;

expression: expression op=MUL expression              #OperationExpression
          | expression op=(PLUS | MIN) expression     #OperationExpression
          | literal                                   #LiteralExpression
          | variable                                  #VariableExpression
          ;

ifClause: IF BOX_BRACKET_OPEN variable BOX_BRACKET_CLOSE
          OPEN_BRACE body CLOSE_BRACE
          elseClause?;

elseClause: ELSE OPEN_BRACE body CLOSE_BRACE;

variable: CAPITAL_IDENT;

literal: COLOR | PIXELSIZE | PERCENTAGE | SCALAR | TRUE | FALSE;