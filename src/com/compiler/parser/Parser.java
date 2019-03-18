package com.compiler.parser;

import com.compiler.Main;
import com.compiler.lexer.MsgPrinter;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenType;
import javafx.util.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.compiler.lexer.TokenType.*;

// TODO list:
// obsluzyć kontener: nazwa[size]
// chyba źle wychodzi z bloku, bo nie ogarnia main() jako funkcje
// sum = sum + i; zamiast plusa chce już średnik
//
//

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
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
            if (match(VOID_T, FRACTION_T, STRING_T)) return funcOrVar();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement classDeclaration() {
        System.out.println("CLASS");
        Token name = consume(IDENTIFIER, "Expect class name.");

        Expression superclass = null;
        if (match(COLON)) {
            consume(IDENTIFIER, "Expect superclass name.");
            superclass = new Expression.Variable(previous());
        }

        consume(LEFT_BRACKET, "Expect '{' before class body.");

        List<Statement> body = new ArrayList<>();
        while (!check(RIGHT_BRACKET) && !isAtEnd()) {
            body = block();
        }

        consume(RIGHT_BRACKET, "Expect '}' after class body.");

        return new Statement.Class(name, superclass, body);
    }

    private Statement funcOrVar(){
        Token type = previous();
        System.out.println("Func or var here.");
        Token name = consume(IDENTIFIER, "Expect function's or variable's name.");
        System.out.println(name.getLexeme());
        if(match(LEFT_PAREN)){
            return function("function", type, name);
        }
        else if(match(LEFT_SQUARE_BRACKET)){
            return containerDefinition(type, name);
        }
        else{
            return varDeclaration(type, name);
        }
    }

    private Statement statement() {
        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACKET)) return new Statement.Block(block());

        return expressionStatement();
    }

    // todo: zmienić
    private Statement forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        Token type = null;
        Token iterName = null;
        Token container = null;

        if(match(FRACTION_T, STRING_T)){
            type = previous();
            iterName = consume(IDENTIFIER, "Expect name of iterator through for loop");
            consume(COLON, "Expect ':' in a for loop condition.");
            container = consume(IDENTIFIER, "Expect name of container to iterate on.");
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        Statement body = statement();

        return new Statement.For(type, iterName, container, body);
    }

    private Statement.Function function(String kind, Token type, Token name) {
        System.out.println("FUNCTION: " + name);
        List<Pair<Token, Token>> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 20) {
                    error(peek(), "Cannot have more than 20 parameters.");
                }

                if(match(STRING_T, VOID_T, FRACTION_T)){
                    Token argType = previous();
                    Token argName = consume(IDENTIFIER, "Expect parameter name.");
                    parameters.add(new Pair<>(argType, argName));
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
        System.out.println("IF");
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        consume(LEFT_BRACKET, "Expect '{' after if condition.");
        List<Statement> bodyBranch = block();

        List<Statement> elseBranch = null;
        if (match(ELSE)) {
            consume(LEFT_BRACKET, "Expect '{' after if condition.");
            elseBranch = block();
        }

        return new Statement.If(condition, bodyBranch, elseBranch);
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

        System.out.println("BLOCK: " + consume(RIGHT_BRACKET, "Expect '}' after block.")); //TODO: usun printa zostaw zawartość
        return statements;
    }

    private Statement varDeclaration(Token type, Token name) {

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
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Statement body = statement();

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
            }
// else if (expr instanceof Expression.Get) { // todo: usunac ??
//                Expression.Get get = (Expression.Get)expr;
//                return new Expression.Set(get.object, get.name, value);
//            }

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

    // przejadam argumenty
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
        MsgPrinter.errorToken(token, message);
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