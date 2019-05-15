package com.compiler.parser;

import com.compiler.ErrorHandler;
import com.compiler.lexer.Lexer;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

import static com.compiler.lexer.TokenType.*;


public class Parser {
    private static class ParseError extends RuntimeException {}

    private Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(declaration());
        }
            return statements;
    }

    private Statement declaration() {
        try {
            if (match(CLASS)) return classDeclaration();
            if (match(VOID_T, FRACTION_T, STRING_T)) return funcOrVarDeclaration();
            if (match(OBJECT)) return classObject();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement classDeclaration() {
        Token name = consume(IDENTIFIER, "Expect class name.");

        consume(LEFT_BRACKET, "Expect '{' before class body.");

        List<Statement> body = block();
        return new Statement.Class(name, body);
    }

    private Statement funcOrVarDeclaration(){
        Token type = previous();
        Token name = consume(IDENTIFIER, "Expect function's or variable's name.");

        if(match(LEFT_PAREN)){
            return function("function", type, name);
        }
        else if(match(LEFT_SQUARE_BRACKET)){
            return containerDefinition(type, name);
        }
        else{
            return varDeclarationAndDefinition(type, name);
        }
    }

    private Statement classObject(){
        Token objectName = consume(IDENTIFIER, "Expect class object name.");
        consume(EQUAL,"Expect assignment of class name while creating of class object.");
        consume(NEW, "Expect 'new' while creating class object.");

        Token classNameToken = consume(IDENTIFIER, "Expect class name after new while creating class object.");
        Expression.Variable className = new Expression.Variable(classNameToken);

        consume(SEMICOLON, "Expect semicolon at the end of class object creation.");

        return new Statement.ClassObject(objectName, className);
    }

    private Statement statement() {

        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACKET)) return new Statement.Block(block());
        if (match(PRINT)) return printStatement();

        return expressionStatement();
    }

    private Statement forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        Token type = null;
        Token iterName = null;
        Expression  container = null;

        if(match(FRACTION_T, STRING_T)){
            //advance
            type = previous();
            iterName = consume(IDENTIFIER, "Expect name of iterator through for loop");
            consume(COLON, "Expect ':' in a for loop condition.");
            container = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        Statement body = statement();

        return new Statement.For(type, iterName, (Expression.Variable) container, body);
    }

    private Statement.Function function(String kind, Token type, Token name) {
        List<Statement.Var> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 20) {
                    error(peek(), "Cannot have more than 20 parameters.");
                }

                if(match(STRING_T, VOID_T, FRACTION_T)){
                    Token argType = previous();
                    Token argName = consume(IDENTIFIER, "Expect parameter name.");
                    parameters.add(varDeclaration(argType, argName));
                }
                else{
                    error(previous(), "Expect argument's type.");
                }
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(LEFT_BRACKET, "Expect '{' before " + kind + " body.");
        List<Statement> body = block();

        return new Statement.Function(name, type, parameters, body);
    }

    private Statement ifStatement() {
        Token ifToken = previous();
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        consume(LEFT_BRACKET, "Expect '{' after if condition.");
        Statement bodyBranch = new Statement.Block(block());

        Statement elseBranch = null;
        if (match(ELSE)) {
            consume(LEFT_BRACKET, "Expect '{' after if condition.");
            elseBranch = new Statement.Block(block());
        }

        return new Statement.If(ifToken, condition, bodyBranch, elseBranch);
    }

    private Statement printStatement() {
        Token print = previous();
        consume(LEFT_PAREN, "Expect '(' after 'if'.");

        Expression value = null;
        if(check(RIGHT_PAREN)) {
            ErrorHandler.printParserError("Print statement cannot be empty.", print);
        } else {
            value = expression();
        }

        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        consume(SEMICOLON, "Expect ';' after value.");
        return new Statement.Print(print, value);
    }

    private Statement returnStatement() {
        Token keyword = previous();
        Expression value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }

        consume(SEMICOLON, "Expect ';' after return value.");
        return new Statement.Return(keyword, value);
    }

    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();

        while (!check(RIGHT_BRACKET) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(RIGHT_BRACKET, "Expect '}' after block.");

        return statements;
    }

    private Statement.Var varDeclaration(Token type, Token name) {

        return new Statement.Var(type, name, null);
    }

    private Statement varDeclarationAndDefinition(Token type, Token name) {
        Expression initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");

        return new Statement.Var(type, name, initializer);
    }

    private Statement containerDefinition(Token type, Token name) {

            consume(RIGHT_SQUARE_BRACKET, "Expect right square bracket.");
            consume(EQUAL, "Expect right square bracket.");
            consume(LEFT_BRACKET, "Expect left bracket in container initialization list.");

            List<Expression> containerElements = new ArrayList<>();
            if (!check(RIGHT_BRACKET)) {
                do {
                    if (containerElements.size() >= 20) {
                        error(peek(), "Container cannot have more than 20 elements");
                    }
                    containerElements.add(expression());
                } while (match(COMMA));
            }
            consume(RIGHT_BRACKET, "Expect right bracket in container initialization list.");
            consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.Container(type, name, containerElements);
    }

    private Statement whileStatement() {
        Token whileToken = previous();
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Statement body = statement();

        return new Statement.While(whileToken, condition, body);
    }

    private Statement expressionStatement() {
        Expression expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Statement.Expression(expr);
    }

    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        Expression expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expression value = assignment();

            if (expr instanceof Expression.Variable) {
                Token name = ((Expression.Variable)expr).name;
                return new Expression.Assign(name, value);
            } else if (expr instanceof Expression.Get) {
                Expression.Get get = (Expression.Get)expr;
                return new Expression.Set(get.object, get.name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expression or() {
        Expression expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expression right = and();
            expr = new Expression.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expression and() {
        Expression expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expression right = equality();
            expr = new Expression.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expression equality() {
        Expression expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression comparison() {
        Expression expr = addition();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expression right = addition();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression addition() {
        Expression expr = multiplication();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expression right = multiplication();

            if(operator.type == MINUS) {
                throw new NotImplementedException();
//                expr = new Expression.Add(expr, operator, right);
            } else if(operator.type == PLUS) {
                expr = new Expression.Add(expr, operator, right);
            }
        }

        return expr;
    }

    private Expression multiplication() {
        Expression expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expression right = unary();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }
        // wyłuskanie lub call
        return call();
    }

    // when '(' and it is not grouping
    private Expression call() {
        Expression expr = primary();

        while (match(LEFT_PAREN, DOT)) {
            if ((previous().type).equals(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if ((previous().type).equals(DOT)) {
                Token name = consume(IDENTIFIER, "Expect property name after '.'.");
                expr = new Expression.Get(expr, name);
            }
        }
        return expr;
    }

    // function call arguments
    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 20) {
                    error(peek(), "Cannot have more than 20 arguments.");
                }
                Expression expr = expression();
                if(expr != null)
                    arguments.add(expr);
                //arguments.add(expression()); // alternative way
            } while (match(COMMA));
        }

        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

        return new Expression.Call(callee, paren, arguments);
    }

    private Expression primary() {
        if (match(FALSE)) return new Expression.Literal(false);
        if (match(TRUE)) return new Expression.Literal(true);

        if (match(FRACTION, STRING, NULL)) {
            return new Expression.Literal(Token.tokenConverter(previous())); // returns Fraction or String
        }

        if (match(IDENTIFIER)) {
            return new Expression.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expression expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.Grouping(expr);
        }

        throw error(peek(), "(in primary())Expect expression.");
    }


    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) return false;
        return peek().type == tokenType;
    }

    private Token advance() {
        if (!isAtEnd()) lexer.advanceToken();
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return lexer.getCurrentToken();
    }

    private Token previous() {
        return lexer.getPreviousToken();
    }

    private ParseError error(Token token, String message) {
        ErrorHandler.printParserError(message, token);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FOR:
                case IF:
                case WHILE:
                case RETURN:
                    return;
            }
            advance();
        }
    }
}