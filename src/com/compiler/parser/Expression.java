package com.compiler.parser;


import com.compiler.lexer.Token;

import java.io.Serializable;
import java.util.List;

public abstract class Expression implements Serializable {
    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(Call expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
        R visitGetExpr(Get expr);
        R visitSetExpr(Set set);

        R visitAddExpr(Add add);
        R visitSubtractExpr(Subtract expr);
        R visitMultiplyExpr(Multiply expr);
        R visitDivideExpr(Divide expr);
        R visitGreaterExpr(Greater expr);
        R visitGreaterEqualExpr(GreaterEqual expr);
        R visitLessExpr(Less expr);
        R visitLessEqualExpr(LessEqual expr);
        R visitBangEqualExpr(BangEqual expr);
        R visitEqualEqualExpr(EqualEqual expr);
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

    public static class Add extends Binary {
        Add(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAddExpr(this);
        }
    }

    public static class Subtract extends Binary {
        Subtract(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSubtractExpr(this);
        }
    }

    public static class Multiply extends Binary {
        Multiply(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitMultiplyExpr(this);
        }
    }

    public static class Divide extends Binary {
        Divide(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitDivideExpr(this);
        }
    }

    public static class Greater extends Binary {
        Greater(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGreaterExpr(this);
        }
    }

    public static class GreaterEqual extends Binary {
        GreaterEqual(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGreaterEqualExpr(this);
        }
    }

    public static class Less extends Binary {
        Less(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLessExpr(this);
        }
    }

    public static class LessEqual extends Binary {
        LessEqual(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLessEqualExpr(this);
        }
    }

    public static class BangEqual extends Binary {
        BangEqual(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBangEqualExpr(this);
        }
    }

    public static class EqualEqual extends Binary {
        EqualEqual(Expression left, Token operator, Expression right) {
            super(left, operator, right);
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitEqualEqualExpr(this);
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

    public static class Get extends Expression {
        Get(Expression object, Token name) {
            this.object = object;
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }

        public final Expression object;
        public final Token name;
    }

    public static class Set extends Expression {
        Set(Expression object, Token name, Expression value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }

        public final Expression object;
        public final Token name;
        public final Expression value;
    }
}
