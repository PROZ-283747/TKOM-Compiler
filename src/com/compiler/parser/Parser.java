package com.compiler.parser;

import com.compiler.Main;
import com.compiler.lexer.MsgPrinter;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.compiler.lexer.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Statement declaration() {
        try {
            if (match(CLASS)) return classDeclaration();
            if (match(VOID_T, FRACTION_T, STRING_T)) return funcOrVar();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement classDeclaration() {
        Token name = consume(IDENTIFIER, "Expect class name.");

        Expression superclass = null;
        if (match(LESS)) {
            consume(IDENTIFIER, "Expect superclass name.");
            superclass = new Expression.Variable(previous());
        }

        consume(LEFT_BRACKET, "Expect '{' before class body.");

        List<Statement.Function> methods = new ArrayList<>();
        while (!check(RIGHT_BRACKET) && !isAtEnd()) {
            methods.add(function("method"));
        }

        consume(RIGHT_BRACKET, "Expect '}' after class body.");

        return new Statement.Class(name, superclass, methods);
    }

    private Statement funcOrVar(){
        throw new NotImplementedException();
    }

    private Statement statement() {
        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACKET)) return new Statement.Block(block());

        return expressionStatement();
    }

    private Statement forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        Statement initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expression condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Expression increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        Statement body = statement();

        if (increment != null) {
            body = new Statement.Block(Arrays.asList(
                    body,
                    new Statement.Expression(increment)));
        }

        if (condition == null) condition = new Expression.Literal(true);
        body = new Statement.While(condition, body);

        if (initializer != null) {
            body = new Statement.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    // todo: nowa funkcja wspolna dla funkcji i zmiennych. zjada typ i identifier a potem sprawdza czy jest '(' czy nie
    private Statement.Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");

        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 20) {
                    error(peek(), "Cannot have more than 20 parameters.");
                }
                // todo: dodac typ
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(LEFT_BRACKET, "Expect '{' before " + kind + " body.");
        List<Statement> body = block();

        return new Statement.Function(name, parameters, body);
    }

    private Statement ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");
        // todo: dodać blok ifa z klamrami !!!
        Statement thenBranch = statement();
        Statement elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Statement.If(condition, thenBranch, elseBranch);
    }

    private Statement printStatement() {

        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expression value = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        consume(SEMICOLON, "Expect ';' after value.");
        return new Statement.Print(value);
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

    private Statement varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expression initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.Var(name, initializer);
    }

    private Statement whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Statement body = statement();

        //return new Statement.While(condition, body);
        return new Statement.While(condition, body);
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
            } else if (expr instanceof Expression.Get) { // todo: usunac ??
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

        while (match(BANG_EQUAL, EQUAL)) {
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
            expr = new Expression.Binary(expr, operator, right);
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

        return call();
    }

    // when '(' and it is not grouping
    private Expression call() {
        Expression expr = primary();

        // todo: is while necessary ??
        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }

        return expr;
    }

    // prejadam argumenty
    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 20) {
                    error(peek(), "Cannot have more than 20 arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }

        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

        return new Expression.Call(callee, paren, arguments);
    }

    private Expression primary() {
        if (match(FALSE)) return new Expression.Literal(false);
        if (match(TRUE)) return new Expression.Literal(true);

        if (match(FRACTION, STRING)) {
            return new Expression.Literal(Token.tokenConverter(previous()));
        }

        if (match(IDENTIFIER)) {
            return new Expression.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expression expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
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
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) return false;
        return peek().type == tokenType;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        MsgPrinter.error(token, message);
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