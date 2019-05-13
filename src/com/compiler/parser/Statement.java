package com.compiler.parser;

import com.compiler.lexer.Token;
import java.util.List;

public abstract class Statement {
    public interface Visitor<R> {
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
        R visitContainerStmt(Container expr);
    }
    public static class Block extends Statement {
        Block(List<Statement> statements) {
            this.statements = statements;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        public final List<Statement> statements;
    }
    public static class Class extends Statement {
        Class(Token name, List<Statement> body) {
            this.name = name;
            this.body = body;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }

        public final Token name;
        public final List<Statement> body;
    }

    public static class Expression extends Statement {
        Expression(com.compiler.parser.Expression expression) {
            this.expression = expression;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        public final com.compiler.parser.Expression expression;
    }

    public static class Function extends Statement {
        Function(Token name, Token type, List<Statement.Var> parameters, List<Statement> body) {
            this.name = name;
            this.returnType = type;
            this.parameters = parameters;
            this.body = body;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        public final Token name;
        public final Token returnType;
        public final List<Statement.Var> parameters;
        public final List<Statement> body;
    }

    public static class If extends Statement {
        If(Token ifToken, com.compiler.parser.Expression condition, Statement thenBranch, Statement elseBranch) {
            this.ifToken = ifToken;
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        public final Token ifToken; // it is needed just to localize errors and report them
        public final com.compiler.parser.Expression condition;
        public final Statement thenBranch;
        public final Statement elseBranch;
    }

    public static class Print extends Statement {
        Print(Token print, com.compiler.parser.Expression expression) {
            this.print = print;
            this.expression = expression;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        public final  Token print;
        public final com.compiler.parser.Expression expression;
    }
    public static class Return extends Statement {
        Return(Token keyword, com.compiler.parser.Expression value) {
            this.keyword = keyword;
            this.value = value;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }

        public final Token keyword;
        public final com.compiler.parser.Expression value;
    }

    public static class Var extends Statement {
        Var(Token type, Token name, com.compiler.parser.Expression initializer) {
            this.type = type;
            this.name = name;
            this.initializer = initializer;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        public final Token type;
        public final Token name;
        public final com.compiler.parser.Expression initializer;
    }

    public static class While extends Statement {
        While(Token whileToken, com.compiler.parser.Expression condition, Statement body) {
            this.whileToken = whileToken;
            this.condition = condition;
            this.body = body;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
        public final Token whileToken;
        public final com.compiler.parser.Expression condition;
        public final Statement body;
    }

    public static class For extends Statement {
        For(Token type, Token iter, com.compiler.parser.Expression container, Statement body) {
            this.type = type;
            this.iter = iter;
            this.container = container;
            this.body = body;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }

        public final Token type;
        public final Token iter;
        public com.compiler.parser.Expression container;
        // lista statementów
        public final Statement body;
    }

    public static class Container extends Statement {
        Container(Token type, Token name, List<com.compiler.parser.Expression> elements) {
            this.type = type;
            this.name = name;
            this.elements = elements;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitContainerStmt(this);
        }

        public final Token type;
        public final Token name;
        public final List<com.compiler.parser.Expression> elements;
    }
    public abstract <R> R accept(Visitor<R> visitor);
}