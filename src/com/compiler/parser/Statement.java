package com.compiler.parser;


import com.compiler.lexer.Token;
import javafx.util.Pair;

import java.util.List;


public abstract class Statement {
    interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitClassStmt(Class stmt);
        R visitExpressionStmt(Expression stmt);
        R visitFunctionStmt(Function stmt);
        R visitIfStmt(If stmt);
        R visitPrintStmt(Print stmt);
        R visitReturnStmt(Return stmt);
        R visitVarStmt(Var stmt);
        R visitWhileStmt(While stmt);
        R visitForStmt(For stmt);
        R visitContainerStmt(Container stmt);
    }
    static class Block extends Statement {
        Block(List<Statement> statements) {
            this.statements = statements;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Statement> statements;
    }
    static class Class extends Statement {
        Class(Token name, com.compiler.parser.Expression superclass, List<Statement> body) {
            this.name = name;
            this.superclass = superclass;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }

        final Token name;
        final com.compiler.parser.Expression superclass;
        final List<Statement> body;
    }

    static class Expression extends Statement {
        Expression(com.compiler.parser.Expression expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final com.compiler.parser.Expression expression;
    }

    static class Function extends Statement {
        Function(Token name, Token type, List<Pair<Token, Token>> parameters, List<Statement> body) {
            this.name = name;
            this.type = type;
            this.parameters = parameters;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        final Token name;
        final Token type;
        final List<Pair<Token, Token>> parameters;
        final List<Statement> body;
    }
    static class If extends Statement {
        If(com.compiler.parser.Expression condition, List<Statement> thenBranch, List<Statement> elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final com.compiler.parser.Expression condition;
        final List<Statement> thenBranch;
        final List<Statement> elseBranch;
    }
    static class Print extends Statement {
        Print(com.compiler.parser.Expression expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        final com.compiler.parser.Expression expression;
    }
    static class Return extends Statement {
        Return(Token keyword, com.compiler.parser.Expression value) {
            this.keyword = keyword;
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }

        final Token keyword;
        final com.compiler.parser.Expression value;
    }

    public static class Var extends Statement {
        Var(Token type, Token name, com.compiler.parser.Expression initializer) {
            this.type = type;
            this.name = name;
            this.initializer = initializer;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        final Token type;
        final Token name;
        public final com.compiler.parser.Expression initializer;
    }

    static class Container extends Statement {
        Container(Token type, Token name, List<com.compiler.parser.Expression> elements) {
            this.type = type;
            this.name = name;
            this.elements = elements;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitContainerStmt(this);
        }

        final Token type;
        final Token name;
        final List<com.compiler.parser.Expression> elements;
    }

    static class While extends Statement {
        While(com.compiler.parser.Expression condition, Statement body) {
            this.condition = condition;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        final com.compiler.parser.Expression condition;
        final Statement body;
    }

    static class For extends Statement {
        For(Token type, Token iter, Token container, Statement body) {
            this.type = type;
            this.iter = iter;
            this.container = container;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }

        final Token type;
        final Token iter;
        final Token container;
        final Statement body;
    }

    abstract <R> R accept(Visitor<R> visitor);
}