package com.compiler.parser;


import com.compiler.lexer.Token;

import java.util.List;

public abstract class Expression {
    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitAssignExpr(Assign expr);

        R visitSetExpr(Set expr);

        R visitBinaryExpr(Binary expr);

        R visitCallExpr(Call expr);

        R visitGetExpr(Get expr);

        R visitSubscriptionExpr(Subscription expr);

        R visitPropertyRefExpr(PropertyRef expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitLogicalExpr(Logical expr);

        R visitUnaryExpr(Unary expr);

        R visitVariableExpr(Variable expr);
    }

    public static class Assign extends Expression {
        public final Token name;
        public final Expression value;

        Assign(Token name, Expression value) {
            this.name = name;
            this.value = value;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    public static class Binary extends Expression {
        public final Expression left;
        public final Token operator;
        public final Expression right;

        Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    public static class Call extends Expression {
        public final Expression callee;
        public final Token paren;
        public final List<Expression> arguments;

        Call(Expression callee, Token paren, List<Expression> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    public static class Get extends Expression {
        public final Expression object;
        public final Token name;

        Get(Expression object, Token name) {
            this.object = object;
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }
    }

    public static class PropertyRef extends Expression {
        public final Expression object;
        public final Token name;

        PropertyRef(Token name, Expression object) {
            this.object = object;
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPropertyRefExpr(this);
        }
    }

    public static class Subscription extends Expression {
        public final Expression object;
        public final Expression inside;
        public final Token rightBracket;

        Subscription(Expression object, Expression inside, Token rightBracket) {
            this.object = object;
            this.inside = inside;
            this.rightBracket = rightBracket;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSubscriptionExpr(this);
        }
    }

    public static class Grouping extends Expression {
        public final Expression expression;

        Grouping(Expression expression) {
            this.expression = expression;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    public static class Literal extends Expression {
        public final Object value;

        Literal(Object value) {
            this.value = value;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    public static class Logical extends Expression {
        public final Expression left;
        public final Token operator;
        public final Expression right;

        Logical(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    public static class Unary extends Expression {
        public final Token operator;
        public final Expression right;

        Unary(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    public static class Variable extends Expression {
        public final Token name;

        Variable(Token name) {
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    public static class Set extends Expression {
        public final Expression object;
        public final Token name;
        public final Expression value;

        Set(Expression object, Token name, Expression value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }
    }
}
