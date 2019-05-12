package com.compiler.interpreter;

import com.compiler.ErrorHandler;
import com.compiler.interpreter.variables.*;
import com.compiler.interpreter.variables.Iterable;
import com.compiler.interpreter.variables.Variable.*;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenType;
import com.compiler.parser.Expression;
import com.compiler.parser.Statement;
import java.util.*;
import java.util.function.BiFunction;

public class Resolver implements Expression.Visitor<Variable>, Statement.Visitor<Void> {
    private static final Map<TokenType, TriFunction<Token, Variable, Variable, Variable>> binaryExprTypeChecker = new HashMap<>();
    private static final Map<TokenType, BiFunction<Token, Variable, Variable>> unaryExprTypeChecker = new HashMap<>();

    static {
        binaryExprTypeChecker.put(TokenType.GREATER, (o, l, r) -> {
            checkNumericOperands(o, l, r);
            return new Variable(VarType.Bool, null);
        });
        binaryExprTypeChecker.put(TokenType.GREATER_EQUAL, (o, l, r) -> {
            checkNumericOperands(o, l, r);
            return new Variable(VarType.Bool, null);
        });
        binaryExprTypeChecker.put(TokenType.LESS, (o, l, r) -> {
            checkNumericOperands(o, l, r);
            return new Variable(VarType.Bool, null);
        });
        binaryExprTypeChecker.put(TokenType.LESS_EQUAL, (o, l, r) -> {
            checkNumericOperands(o, l, r);
            return new Variable(VarType.Bool, null);
        });
        binaryExprTypeChecker.put(TokenType.MINUS, (o, l, r) -> {
            checkNumericOperands(o, l, r);
            return l;
        });
        binaryExprTypeChecker.put(TokenType.PLUS, (o, l, r) -> {
            checkNumericOperands(o, l, r);
            return l;
        });
        binaryExprTypeChecker.put(TokenType.SLASH, (o, l, r) -> {
            checkNumericOperands(o, l, r);
            return l;
        });
        binaryExprTypeChecker.put(TokenType.STAR, (o, l, r) -> {
            checkNumericOperands(o, l, r);
            return l;
        });
        binaryExprTypeChecker.put(TokenType.BANG_EQUAL, (o, l, r) -> {
            if (l.varType != r.varType) {
                ErrorHandler.printResolverError("Comparable operands must be of the same type.", o.getLine(), o.getColumn());
            }
            return new Variable(VarType.Bool, null);
        });
        binaryExprTypeChecker.put(TokenType.EQUAL_EQUAL, (o, l, r) -> {
            if (l.varType != r.varType) {
                ErrorHandler.printResolverError("Comparable operands must be of the same type.", o.getLine(), o.getColumn());
            }
            return new Variable(VarType.Bool, null);
        });
    }

    static {
        unaryExprTypeChecker.put(TokenType.BANG, (o, t) -> {
            if (t.varType != VarType.Bool) {
                ErrorHandler.printResolverError("Operand must be a Bool.",o.getLine(), o.getColumn());
            }
            return new Variable(VarType.Bool, null);
        });
        unaryExprTypeChecker.put(TokenType.MINUS, (o, t) -> {
            checkNumericOperand(o, t);
            //return new Variable(VarType.Bool, null);
            return t;
        });
    }

    private final Interpreter interpreter;
    private final Stack<Map<String, Variable>> scopes = new Stack<>();
    private Function currentFunction = null;
    private Klass currentClass = null;

    public Resolver(Interpreter interpreter) {
        beginScope();
        this.interpreter = interpreter;
    }

    private static void checkNumericOperand(Token operator, Variable operand) {
        if (operand.varType != VarType.Fraction) {
            ErrorHandler.printResolverError("Operand must be a number.",operator.getLine(), operator.getColumn());
        }
    }

    private static void checkNumericOperands(Token operator, Variable left, Variable right) {
        if (left.varType != VarType.Fraction || right.varType != VarType.Fraction) {
            ErrorHandler.printResolverError("Operands must be numbers.",operator.getLine(), operator.getColumn());
        }
    }

    public void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            if (statement != null) resolve(statement);
        }
    }

    @Override
    public Void visitBlockStmt(Statement.Block stmt) {
        //beginScope();
        resolve(stmt.statements);
        //endScope();
        return null;
    }

    @Override
    public Void visitClassStmt(Statement.Class stmt) {
        Klass enclosingClass = currentClass;
        currentClass = new Klass(stmt.name.getLexeme());

        declare(stmt.name, currentClass);
        define(stmt.name);

        beginScope();

        resolve(stmt.body);
        for (Map.Entry<String, Variable> entry : scopes.peek().entrySet()) {
            currentClass.set(entry.getKey(), entry.getValue());
        }

        endScope();

        currentClass = enclosingClass;

        return null;
    }

    @Override
    public Void visitExpressionStmt(Statement.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Variable visitVariableExpr(Expression.Variable expr) {
        if (scopes.peek().containsKey(expr.name.getLexeme()) &&
                !scopes.peek().get(expr.name.getLexeme()).defined) {
            ErrorHandler.printResolverError("Cannot read local variable in its own initializer.", expr.name.getLine(), expr.name.getColumn());
        }
        return resolveVariable(expr, expr.name);
    }

    @Override
    public Variable visitGetExpr(Expression.Get expr){
        Variable gettable = resolve(expr.object);

        if (gettable instanceof Gettable){
            Variable var = ((Gettable) gettable).get(expr.name.getLexeme());

            if (var == null) {
                //Main.error("'" + ((Gettable) gettable).getName() + "' doesn't have '" + expr.name.getLexeme() + "' property.", expr.name.getLine(), expr.name.getColumn());
                return new Variable();
            }
            return var;
        } else {
            //Main.error("This item is not gettable.", expr.name.getLine(), expr.name.getColumn());
        }

        return new Variable();
    }

    @Override
    public Variable visitAssignExpr(Expression.Assign expr) {
        Variable right = resolve(expr.value);
        Variable left = resolveVariable(expr, expr.name);

        if (left.varType != right.varType) {
            // todo: always fraction i bool types !!! WHY
            System.out.println(left.varType + " " +  right.varType);
            ErrorHandler.printResolverError("Incompatible type of value.",expr.name.getLine(), expr.name.getColumn());
        }
        return right;
    }

    @Override
    public Variable visitBinaryExpr(Expression.Binary expr) {
        Variable left = resolve(expr.left);
        Variable right = resolve(expr.right);

        return binaryExprTypeChecker.get(expr.operator.getType()).apply(expr.operator, left, right);
    }

    @Override
    public Variable visitCallExpr(Expression.Call expr) {
        Variable callee = resolve(expr.callee);

        if (!(callee instanceof Callable)) {
            ErrorHandler.printResolverError("This is not callable.", expr.paren.getLine(), expr.paren.getColumn());
            return new Variable();
        }

        Callable callable = (Callable) callee;

        if (expr.arguments.size() != callable.arity()) {
            ErrorHandler.printResolverError("Expected " + callable.arity() + " arguments but got " + expr.arguments.size() + ".", expr.paren.getLine(), expr.paren.getColumn());
        }

        for (int i = 0; i < expr.arguments.size(); i++) {
            Variable argType = resolve(expr.arguments.get(i));
            if (i < callable.arity()) {
                Variable paramType = callable.getParams().get(i);
                if (argType.varType != paramType.varType) {
                    ErrorHandler.printResolverError("Incorrect type of argument number " + (i + 1) + ".",expr.paren.getLine(), expr.paren.getColumn());
                }
            }
        }
        return callee;
    }

    @Override
    public Variable visitGroupingExpr(Expression.Grouping expr) {
        return resolve(expr.expression);
    }

    @Override
    public Variable visitLiteralExpr(Expression.Literal expr) {
        return new Variable(VarType.fromValue(expr.value), expr.value);
    }

    @Override
    public Variable visitLogicalExpr(Expression.Logical expr) {
        Variable left = resolve(expr.left);
        Variable right = resolve(expr.right);

        if (left.varType != VarType.Bool || right.varType != VarType.Bool) {
            ErrorHandler.printResolverError("Logical expression must take two booleans.",expr.operator.getLine(), expr.operator.getColumn());
        }

        return left;
    }

    @Override
    public Variable visitUnaryExpr(Expression.Unary expr) {
        Variable operand = resolve(expr.right);
        return unaryExprTypeChecker.get(expr.operator.getType()).apply(expr.operator, operand);
    }

    @Override
    public Void visitFunctionStmt(Statement.Function stmt) {
        Function function = new Function(stmt);
        declare(stmt.name, function);

        Function enclosingFunction = currentFunction;
        currentFunction = function;

        beginScope();
        for (Statement param : stmt.parameters) {
            resolve(param);
        }
        for (int i = 0; i < stmt.parameters.size(); i++) {
            function.addParam(((Variable) scopes.peek().values().toArray()[i]));
        }
        scopes.peek().put(stmt.name.getLexeme(), function);

        define(stmt.name);
        resolve(stmt.body);

        if (stmt.body.isEmpty()) {
            ErrorHandler.printResolverError("Expect statement in a function body.",stmt.name.getLine(), stmt.name.getColumn());
        } else {
            if(!stmt.returnType.getLexeme().equals("void")) {
                Statement lastStatement = stmt.body.get(stmt.body.size() - 1);
                if (!(lastStatement instanceof Statement.Return) && stmt.returnType != null) {
                    ErrorHandler.printResolverError("Last statement in function must be a return statement.", stmt.returnType.getLine(), stmt.returnType.getColumn());
                }
            }
        }

        endScope();
        currentFunction = enclosingFunction;

        return null;
    }

    @Override
    public Void visitWhileStmt(Statement.While stmt) {

        Variable conditionType = resolve(stmt.condition);

        if (conditionType.varType != VarType.Bool) {
            ErrorHandler.printResolverError("While condition must be of Bool type.",stmt.whileToken.getLine(), stmt.whileToken.getColumn());
        }
        if (((Statement.Block) stmt.body).statements.isEmpty()) {
            ErrorHandler.printResolverError("Expect statement in a while body.",stmt.whileToken.getLine(), stmt.whileToken.getColumn());
        }

        beginScope();
        resolve(stmt.body);
        endScope();

        return null;
    }

    @Override
    public Void visitIfStmt(Statement.If stmt) {
        Variable conditionType = resolve(stmt.condition);
        if (conditionType.varType != VarType.Bool) {
            ErrorHandler.printResolverError("Condition must be of Bool type.",stmt.ifToken.getLine(), stmt.ifToken.getColumn());
        }

        if (((Statement.Block) stmt.thenBranch).statements.isEmpty()) {
            ErrorHandler.printResolverError("Expect statement in a if body.",stmt.ifToken.getLine(), stmt.ifToken.getColumn());
        }

        beginScope();
        resolve(stmt.thenBranch);
        endScope();

        if (stmt.elseBranch != null) {
            if (((Statement.Block) stmt.elseBranch).statements.isEmpty()) {
                ErrorHandler.printResolverError("Expect statement in a else body.",stmt.ifToken.getLine(), stmt.ifToken.getColumn());
            }

            beginScope();
            resolve(stmt.elseBranch);
            endScope();
        }

        return null;
    }

    @Override
    public Void visitReturnStmt(Statement.Return stmt) {
        if (currentFunction == null) {
            ErrorHandler.printResolverError("You can only return from a function.",stmt.keyword.getLine(), stmt.keyword.getColumn());
        }

        Variable returnValue = null;
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(currentFunction.getName())) {
                returnValue = scopes.get(i).get(currentFunction.getName());
                break;
            }
        }

        if (stmt.value == null && ((Callable) returnValue).getReturnType() != VarType.Void) {
            ErrorHandler.printResolverError("Missing return value.",stmt.keyword.getLine(), stmt.keyword.getColumn());
        } else if (stmt.value != null && ((Callable) returnValue).getReturnType() == VarType.Void) {
            ErrorHandler.printResolverError("Cannot return a value form a function with void type declared.",stmt.keyword.getLine(), stmt.keyword.getColumn());
        } else if (stmt.value != null) {
            Variable returnedType = resolve(stmt.value);

            if (returnedType.varType != ((Callable) returnValue).getReturnType()) {
                ErrorHandler.printResolverError("Incorrect return type.",stmt.keyword.getLine(), stmt.keyword.getColumn());
            }
        }

        return null;
    }

    @Override
    public Void visitVarStmt(Statement.Var stmt) {
        Variable var = declare(stmt.name, new Variable(VarType.fromString(stmt.type.getLexeme()), null));
        if (stmt.initializer != null) {
            Variable value = resolve(stmt.initializer);
            if (value.varType != var.varType) {
                ErrorHandler.printResolverError("Variable initializer is of different type than variable.", stmt.name.getLine(), stmt.name.getColumn());
            }
        }

        define(stmt.name);
        return null;
    }

    private void resolve(Statement statement) {
        statement.accept(this);
    }

    private Variable resolve(Expression expression) {
        return expression.accept(this);
    }

    private void beginScope() {
        scopes.push(new LinkedHashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }

    private Variable declare(Token name, Variable variable) {
        Map<String, Variable> scope = scopes.peek();
        if (scope.containsKey(name.getLexeme())) {
            ErrorHandler.printResolverError("Item with this name already declared in this scope.",name.getLine(), name.getColumn());
        }

        scope.put(name.getLexeme(), variable);
        return variable;
    }

    private void define(Token name) {
        Variable variable = scopes.peek().get(name.getLexeme());
        variable.defined = true;
        scopes.peek().put(name.getLexeme(), variable);
    }

    private Variable resolveVariable(Expression expression, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.getLexeme())) {
                interpreter.resolve(expression, scopes.size() - i - 1);
                return scopes.get(i).get(name.getLexeme());
            }
        }

        ErrorHandler.printResolverError("Cannot resolve symbol '" + name.getLexeme() + "'.",name.getLine(), name.getColumn());

        return new Variable();
    }

    @Override
    public Void visitPrintStmt(Statement.Print stmt) {
        Variable value = resolve(stmt.expression);
        if (value.varType == VarType.Void) {
            ErrorHandler.printResolverError("No value to print",stmt.print.getLine(), stmt.print.getColumn());
        }
        return null;
    }

    @Override
    public Void visitForStmt(Statement.For stmt) {
        Variable in = resolve(stmt.container);

        if (!(in instanceof Iterable)) {
            ErrorHandler.printResolverError("Item in for is not iterable. ",stmt.iter.getLine(), stmt.iter.getColumn());
        }
        beginScope();
        declare(stmt.iter, new Variable(VarType.fromString(stmt.type.getLexeme()), null));
        define(stmt.iter);

        if (((Statement.Block) stmt.body).statements.isEmpty()) {
            ErrorHandler.printResolverError("Expect statement in a for body.",stmt.iter.getLine(), stmt.iter.getColumn());
        }

        resolve(((Statement.Block) stmt.body).statements);
        endScope();

        return null;
    }

    @Override
    public Void visitContainerStmt(Statement.Container stmt) {
        declare(stmt.name, new Container(stmt.name.getLexeme()));
        if(!stmt.elements.isEmpty()) {
            // checks if all elements are appropriate type
            for (Expression elem : stmt.elements) {
                Variable element = resolve(elem);
                if(element.varType != VarType.fromString(stmt.type.getLexeme())){
                    if(elem instanceof Expression.Unary)
                    {
                        Variable var = resolve(((Expression.Unary) elem).right);
                        if(var.varType == VarType.fromString(stmt.type.getLexeme()))
                             continue;
                    }
                    ErrorHandler.printResolverError("All elements in a container must be the same type as container.", stmt.type.getLine(), stmt.type.getColumn());
                }
            }
        }
        define(stmt.name);
        return null;
    }

    @FunctionalInterface
    interface TriFunction<T1, T2, T3, T4> {
        T4 apply(T1 one, T2 two, T3 three);
    }
}
