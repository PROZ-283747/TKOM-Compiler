package com.compiler.interpreter;

import com.compiler.interpreter.variables.*;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenType;
import com.compiler.parser.Expression;
import com.compiler.parser.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expression, Integer> locals = new HashMap<>();

    public Interpreter() {}

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                if(statement != null)
                    execute(statement);
            }
        } catch (RuntimeError error) {
            throw error;
        }
    }

    private void execute(Statement stmt) {
        stmt.accept(this);
    }

    void resolve(Expression expr, int depth) {
        locals.put(expr, depth);
    }

    public void executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private Variable evaluate(Expression expr) {
        return expr.accept(this);
//        return null;
    }

    @Override
    public Void visitBlockStmt(Statement.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Statement.Class stmt) {
//        environment.define(stmt.name.lexeme, null);
//        Variable superclass = null;
//        if (stmt.superclass != null) {
//            superclass = evaluate(stmt.superclass);
//            if (!(superclass instanceof Klass)) {
//                throw new RuntimeError(stmt.name, "Superclass must be a class.");
//            }
//            environment = new Environment(environment);
//            environment.define("super", superclass);
//        }
//
//        //Map<String, Function> methods = new HashMap<>();
//        List<Statement> methods = new ArrayList<>();
//        for (Statement method : stmt.body){
//            Function function = new Function(method, environment, method.name.lexeme.equals("init"));
//            methods.put(method.name.lexeme, function);
//        }
//
//        Klass klass = new Klass(stmt.name.lexeme, (Klass)superclass, methods);
//
//        if (superclass != null) {
//            environment = environment.enclosing;
//        }
//
//        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Statement.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Statement.Function stmt) {
        Function function = new Function(stmt, environment);
        environment.define(stmt.name.getLexeme(), function);
        return null;
    }

    @Override
    public Void visitIfStmt(Statement.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Statement.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println("PRINT: " + stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Statement.Return stmt) {
        Variable value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Statement.Var stmt) {
        Variable value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Statement.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitForStmt(Statement.For stmt) {
        return null;
    }

    @Override
    public Void visitContainerStmt(Statement.Container expr) {

        return null;
    }

    @Override
    public Object visitAssignExpr(Expression.Assign expr) {
        Variable value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }

        return value;
    }

    @Override
    public Object visitBinaryExpr(Expression.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return ((Fraction)left).substract((Fraction)right);
            case PLUS:
                if (left instanceof Fraction && right instanceof Fraction) {
                    return ((Fraction)left).add((Fraction)right);
                }

                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }

                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return ((Fraction)left).divide((Fraction)right);
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return ((Fraction)left).multiply((Fraction)right);
        }
        // Unreachable.
        return null;
    }

    @Override
    public Object visitCallExpr(Expression.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Variable> arguments = new ArrayList<>();
        for (Expression argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof Callable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and klasses.");
        }

        Callable function = (Callable)callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitGroupingExpr(Expression.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expression.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expression.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Expression.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                // todo: rzutować na Fraction czy co ?
                return -(double)right;
        }

        // Unreachable.
        return null;
    }

    @Override
    public Object visitVariableExpr(Expression.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expression expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    //todo: jeśli Fraction to metoda z fraction, jesli string to equals()
    private boolean isEqual(Object a, Object b) {
        // nil is only equal to nil.
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }
}