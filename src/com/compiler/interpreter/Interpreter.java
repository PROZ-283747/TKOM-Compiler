package com.compiler.interpreter;

import com.compiler.interpreter.variables.*;
import com.compiler.interpreter.variables.Iterable;
import com.compiler.lexer.TokenType;
import com.compiler.parser.Expression;
import com.compiler.parser.Statement;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.util.*;

public class Interpreter implements Expression.Visitor<Variable>, Statement.Visitor<Void> {

    private final Environment globals = new Environment();
    private final Map<Expression, Integer> locals = new HashMap<>();
    private Environment environment = globals;

    public static boolean isTrue(Object object) {
        return (boolean) object;
    }

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                if (statement != null) execute(statement);
            }
        }
        catch (Exception error) {
           throw error;
        }

    }

    @Override
    public Variable visitLiteralExpr(Expression.Literal expr) {
        return new Variable(expr.value);
    }

    public void resolve(Expression expression, int depth) {
        locals.put(expression, depth);
    }

    @Override
    public Variable visitLogicalExpr(Expression.Logical expr) {
        Variable left = evaluate(expr.left);

        if (expr.operator.getType() == TokenType.OR) {
            if (isTrue(left.value)) return left;
        } else {
            if (!isTrue(left.value)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Void visitBlockStmt(Statement.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Statement.Class stmt) {
        environment.define(stmt.name.getLexeme(), new Klass(stmt.name.getLexeme(), stmt.body));
        return null;
    }

    @Override
    public Variable visitGroupingExpr(Expression.Grouping expr) {
        return evaluate(expr.expression);
    }

    public Variable
    evaluate(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Statement.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    void execute(Statement statement) {
        statement.accept(this);
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

    @Override
    public Void visitIfStmt(Statement.If stmt) {
        if (isTrue(evaluate(stmt.condition).value)) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
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

        environment.define(stmt.name.getLexeme(), value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Statement.While stmt) {
        while (isTrue(evaluate(stmt.condition).value)) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitForStmt(Statement.For stmt) {
        Variable in = evaluate(stmt.container);

        for (Variable element : ((Iterable)in).getCollection().getElements()) {
            environment = new Environment(environment);

            environment.define(stmt.iter.getLexeme(), element);
            executeBlock(((Statement.Block) stmt.body).statements, environment);
            environment = environment.enclosing;
        }

        return null;
    }

    @Override
    public Void visitContainerStmt(Statement.Container stmt) {
        Container container = new Container(stmt.name.getLexeme());

        if(!stmt.elements.isEmpty()) {
            for(Expression elem : stmt.elements) {
                container.addElement(evaluate(elem));
            }
        }
        environment.define(stmt.name.getLexeme(), container);
        return null;
    }

    @Override
    public Void visitClassObjectDefinitionStmt(Statement.ClassObject stmt) {
        environment.define(stmt.objectName.lexeme, null);
        Klass baseKlass = (Klass) evaluate(stmt.className);

        Environment env = new Environment(environment);
        executeBlock(baseKlass.body, env);

        KlassInstance klass = new KlassInstance(stmt.objectName.getLexeme(), env);

        environment.assign(stmt.objectName.getLexeme(), klass);
        return null;
    }

    @Override
    public Variable visitAssignExpr(Expression.Assign expr) {
        Variable value = evaluate(expr.value);
        environment.assign(expr.name.getLexeme(), value);

        return value;
    }

    @Override
    public Variable visitBinaryExpr(Expression.Binary expr) {
        throw new NotImplementedException();
    }

    @Override
    public Variable visitCallExpr(Expression.Call expr) {
        Callable callee = (Callable) evaluate(expr.callee);

        List<Variable> arguments = new ArrayList<>();
        for (Expression argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        return callee.call(this, arguments);
    }

    @Override
    public Void visitFunctionStmt(Statement.Function stmt) {
        Function function = new Function(stmt, environment);
        environment.define(stmt.name.getLexeme(), function);
        return null;
    }

    @Override
    public Variable visitUnaryExpr(Expression.Unary expr) {
        throw new NotImplementedException();
    }

    @Override
    public Variable visitBangUnaryExpr(Expression.BangUnary expr) {
        Variable operand = evaluate(expr.right);

        return new Variable(!isTrue(operand.value));
    }

    @Override
    public Variable visitMinusUnaryExpr(Expression.MinusUnary expr) {
        Variable operand = evaluate(expr.right);
        return new Variable(((Fraction)operand.value).changeSign());
    }

    @Override
    public Variable visitVariableExpr(Expression.Variable expr) {
        return lookUpVariable(expr.name.getLexeme(), expr);
    }

    @Override
    public Variable visitGetExpr(Expression.Get expr) {
        Gettable gettable = (Gettable) evaluate(expr.object);
        return gettable.get(expr.name.getLexeme());
    }

    @Override
    public Variable visitSetExpr(Expression.Set expr) {
        Variable value = evaluate(expr.value);
        Gettable object = (Gettable) evaluate(expr.object);

        object.set(expr.name.getLexeme(), value);
        return value;
    }

    @Override
    public Variable visitAddExpr(Expression.Add expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(((Fraction)left.value).add((Fraction)right.value));
        }
        if (left.varType.equals(Variable.VarType.String)) {
            return new Variable((String)left.value + right.value);
        }

        throw new NotImplementedException();
    }

    @Override
    public Variable visitSubtractExpr(Expression.Subtract expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(((Fraction)left.value).substract((Fraction)right.value));
        }

        throw new NotImplementedException();
    }

    @Override
    public Variable visitMultiplyExpr(Expression.Multiply expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(((Fraction)left.value).multiply((Fraction)right.value));
        }

        throw new NotImplementedException();
    }

    @Override
    public Variable visitDivideExpr(Expression.Divide expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(((Fraction)left.value).divide((Fraction)right.value));
        }

        throw new NotImplementedException();
    }

    @Override
    public Variable visitGreaterExpr(Expression.Greater expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(((Fraction)left.value).isGreater((Fraction)right.value));
        }

        throw new NotImplementedException();
    }

    @Override
    public Variable visitGreaterEqualExpr(Expression.GreaterEqual expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(((Fraction)left.value).isGreaterEqual((Fraction)right.value));
        }

        throw new NotImplementedException();
    }

    @Override
    public Variable visitLessExpr(Expression.Less expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(((Fraction)left.value).isLess((Fraction)right.value));
        }

        throw new NotImplementedException();
    }

    @Override
    public Variable visitLessEqualExpr(Expression.LessEqual expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(((Fraction)left.value).isLessEqual((Fraction)right.value));
        }

        throw new NotImplementedException();
    }

    @Override
    public Variable visitBangEqualExpr(Expression.BangEqual expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(!((Fraction)left.value).isEqual((Fraction)right.value));
        }

        throw new NotImplementedException();
    }

    @Override
    public Variable visitEqualEqualExpr(Expression.EqualEqual expr) {
        Variable left = evaluate(expr.left);
        Variable right = evaluate(expr.right);

        if (left.varType.equals(Variable.VarType.Fraction)) {
            return new Variable(((Fraction)left.value).isEqual((Fraction)right.value));
        }

        throw new NotImplementedException();
    }

    @Override
    public Void visitPrintStmt(Statement.Print stmt) {
        Variable variable = evaluate(stmt.expression);
        System.out.println(variable.value);
        return null;
    }

    private Variable lookUpVariable(String name, Expression expression) {
        Integer distance = locals.get(expression);
        if (distance != null) {
            return environment.getAt(distance, name);
        } else {
            return globals.get(name);
        }
    }

}
