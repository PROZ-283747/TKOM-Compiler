//package com.compiler.interpreter;
//
//import com.compiler.ErrorHandler;
//import com.compiler.interpreter.variables.Callable;
//import com.compiler.interpreter.variables.Function;
//import com.compiler.interpreter.variables.Variable;
//import com.compiler.interpreter.variables.Variable.*;
//import com.compiler.lexer.Token;
//import com.compiler.lexer.TokenType;
//import com.compiler.parser.Expression;
//import com.compiler.parser.Statement;
//import java.util.*;
//import java.util.function.BiFunction;
//
//public class Resolver implements Expression.Visitor<Variable>, Statement.Visitor<Void> {
//    private static final Map<TokenType, TriFunction<Token, Variable, Variable, Variable>> binaryExprTypeChecker = new HashMap<>();
//    private static final Map<TokenType, BiFunction<Token, Variable, Variable>> unaryExprTypeChecker = new HashMap<>();
//
//    static {
//        binaryExprTypeChecker.put(TokenType.GREATER, (o, l, r) -> {
//            checkNumericOperands(o, l, r);
//            return new Variable(VarType.Bool, null);
//        });
//        binaryExprTypeChecker.put(TokenType.GREATER_EQUAL, (o, l, r) -> {
//            checkNumericOperands(o, l, r);
//            return new Variable(VarType.Bool, null);
//        });
//        binaryExprTypeChecker.put(TokenType.LESS, (o, l, r) -> {
//            checkNumericOperands(o, l, r);
//            return new Variable(VarType.Bool, null);
//        });
//        binaryExprTypeChecker.put(TokenType.LESS_EQUAL, (o, l, r) -> {
//            checkNumericOperands(o, l, r);
//            return new Variable(VarType.Bool, null);
//        });
//        binaryExprTypeChecker.put(TokenType.MINUS, (o, l, r) -> {
//            checkNumericOperands(o, l, r);
//            return new Variable(VarType.Bool, null);
//        });
//        binaryExprTypeChecker.put(TokenType.PLUS, (o, l, r) -> {
//            if (!(l.varType == VarType.Fraction && r.varType == VarType.Fraction) &&
//                    !(l.varType == VarType.String && r.varType == VarType.String)) {
//                ErrorHandler.printResolverError("Operands must be two numbers or two strings.", o.getLine(), o.getColumn());
//            }
//            return l;
//        });
//        binaryExprTypeChecker.put(TokenType.SLASH, (o, l, r) -> {
//            checkNumericOperands(o, l, r);
//            return new Variable(VarType.Bool, null);
//        });
//        binaryExprTypeChecker.put(TokenType.STAR, (o, l, r) -> {
//            checkNumericOperands(o, l, r);
//            return new Variable(VarType.Bool, null);
//        });
//        binaryExprTypeChecker.put(TokenType.BANG_EQUAL, (o, l, r) -> {
//            if (l.varType != r.varType) {
//                ErrorHandler.printResolverError("Comparable operands must be of the same type.", o.getLine(), o.getColumn());
//            }
//            return new Variable(VarType.Bool, null);
//        });
//        binaryExprTypeChecker.put(TokenType.EQUAL_EQUAL, (o, l, r) -> {
//            if (l.varType != r.varType) {
//                ErrorHandler.printResolverError("Comparable operands must be of the same type.", o.getLine(), o.getColumn());
//            }
//            return new Variable(VarType.Bool, null);
//        });
//    }
//
//    static {
//        unaryExprTypeChecker.put(TokenType.BANG, (o, t) -> {
//            if (t.varType != VarType.Bool) {
//                ErrorHandler.printResolverError("Operand must be a Bool.",o.getLine(), o.getColumn());
//            }
//            return new Variable(VarType.Bool, null);
//        });
//        unaryExprTypeChecker.put(TokenType.MINUS, (o, t) -> {
//            checkNumericOperand(o, t);
//            return new Variable(VarType.Bool, null);
//        });
//    }
//
//    private final Interpreter interpreter;
//    private final Stack<Map<String, Variable>> scopes = new Stack<>();
//    private Function currentFunction = null;
//
//    public Resolver(Interpreter interpreter) {
//        beginScope();
//        this.interpreter = interpreter;
//    }
//
//    private static void checkNumericOperand(Token operator, Variable operand) {
//        if (operand.varType != VarType.Fraction) {
//            ErrorHandler.printResolverError("Operand must be a number.",operator.getLine(), operator.getColumn());
//        }
//    }
//
//    private static void checkNumericOperands(Token operator, Variable left, Variable right) {
//        if (left.varType != VarType.Fraction || right.varType != VarType.Fraction) {
//            ErrorHandler.printResolverError("Operands must be numbers.",operator.getLine(), operator.getColumn());
//        }
//    }
//
//    public void resolve(List<Statement> statements) {
//        for (Statement statement : statements) {
//            if (statement != null) resolve(statement);
//        }
//    }
//
//    @Override
//    public Void visitBlockStmt(Statement.Block stmt) {
//        beginScope();
//        resolve(stmt.statements);
//        endScope();
//        return null;
//    }
//
//    @Override
//    public Void visitClassStmt(Statement.Class stmt) {
//        return null;
//    }
//
//
//    @Override
//    public Void visitExpressionStmt(Statement.Expression stmt) {
//        resolve(stmt.expression);
//        return null;
//    }
//
//
//    @Override
//    public Variable visitVariableExpr(Expression.Variable expr) {
//        if (scopes.peek().containsKey(expr.name.getLexeme()) &&
//                !scopes.peek().get(expr.name.getLexeme()).defined) {
//            ErrorHandler.printResolverError("Cannot read local variable in its own initializer.",expr.name.getLine(), expr.name.getColumn());
//        }
//
//        return resolveVariable(expr, expr.name);
//    }
//
//    @Override
//    public Variable visitAssignExpr(Expression.Assign expr) {
//        Variable right = resolve(expr.value);
//        Variable left = resolveVariable(expr, expr.name);
//
//        if (left.varType != right.varType) {
//            ErrorHandler.printResolverError("Incompatible type of value.",expr.name.getLine(), expr.name.getColumn());
//        }
//
//        return right;
//    }
//
//    @Override
//    public Variable visitBinaryExpr(Expression.Binary expr) {
//        Variable left = resolve(expr.left);
//        Variable right = resolve(expr.right);
//
//        return binaryExprTypeChecker.get(expr.operator.getType()).apply(expr.operator, left, right);
//    }
//
//    @Override
//    public Variable visitCallExpr(Expression.Call expr) {
//        Variable callee = resolve(expr.callee);
//
//        if (!(callee instanceof Callable)) {
//            ErrorHandler.printResolverError("This is not callable.", expr.paren.getLine(), expr.paren.getColumn());
//            return new Variable();
//        }
//
//        Callable callable = (Callable) callee;
//
//        if (expr.arguments.size() != callable.arity()) {
//            ErrorHandler.printResolverError("Expected " + callable.arity() + " arguments but got " + expr.arguments.size() + ".", expr.paren.getLine(), expr.paren.getColumn());
//        }
//
//        for (int i = 0; i < expr.arguments.size(); i++) {
//            Variable argType = resolve(expr.arguments.get(i));
//            if (i < callable.arity()) {
//                Variable paramType = callable.getParams().get(i);
//                if (argType.varType != paramType.varType) {
//                    ErrorHandler.printResolverError("Incorrect type of argument number " + (i + 1) + ".",expr.paren.getLine(), expr.paren.getColumn());
//                }
//            }
//        }
//
//        return callee;
//    }
//
//    @Override
//    public Variable visitSuperExpr(Expression.Super expr) {
//        return null;
//    }
//
//    @Override
//    public Variable visitGroupingExpr(Expression.Grouping expr) {
//        return resolve(expr.expression);
//    }
//
//    @Override
//    public Variable visitLiteralExpr(Expression.Literal expr) {
//        return new Variable(VarType.fromValue(expr.value), expr.value);
//    }
//
//    @Override
//    public Variable visitLogicalExpr(Expression.Logical expr) {
//        Variable left = resolve(expr.left);
//        Variable right = resolve(expr.right);
//
//        if (left.varType != VarType.Bool || right.varType != VarType.Bool) {
//            ErrorHandler.printResolverError("Logical expression must take two booleans.",expr.operator.getLine(), expr.operator.getColumn());
//        }
//
//        return left;
//    }
//
//    @Override
//    public Variable visitUnaryExpr(Expression.Unary expr) {
//        Variable operand = resolve(expr.right);
//        return unaryExprTypeChecker.get(expr.operator.getType()).apply(expr.operator, operand);
//    }
//
//    @Override
//    public Void visitFunctionStmt(Statement.Function stmt) {
//        Function function = new Function(stmt);
//        declare(stmt.name, function);
//
//        Function enclosingFunction = currentFunction;
//        currentFunction = function;
//
//        beginScope();
//        for (Statement param : stmt.parameters) {
//            resolve(param);
//        }
//        for (int i = 0; i < stmt.parameters.size(); i++) {
//            function.addParam(((Variable) scopes.peek().values().toArray()[i]));
//        }
//        scopes.peek().put(stmt.name.getLexeme(), function);
//
//        define(stmt.name);
//        resolve(stmt.body);
//
//        if (stmt.body.isEmpty()) {
//            ErrorHandler.printResolverError("Expect statement in a for body.",stmt.name.getLine(), stmt.name.getColumn());
//        } else {
//            Statement lastStatement = stmt.body.get(stmt.body.size() - 1);
//            if (!(lastStatement instanceof Statement.Return) && stmt.returnType != null) {
//                ErrorHandler.printResolverError("Last statement in function must be a return statement.",stmt.returnType.getLine(), stmt.returnType.getColumn());
//            }
//        }
//
//        endScope();
//        currentFunction = enclosingFunction;
//
//        return null;
//    }
//
//    @Override
//    public Void visitWhileStmt(Statement.While stmt) {
//
//        Variable conditionType = resolve(stmt.condition);
//
//        if (conditionType.varType != VarType.Bool) {
//            ErrorHandler.printResolverError("When condition must be of Bool type.",stmt.whenToken.getLine(), stmt.whenToken.getColumn());
//        }
//        if (((Statement.Block) stmt.body).statements.isEmpty()) {
//            ErrorHandler.printResolverError("Expect statement in a if body.",stmt.colon.getLine(), stmt.colon.getColumn());
//        }
//
//        beginScope();
//        resolve(stmt.body);
//        endScope();
//
//        return null;
//    }
//
//    @Override
//    public Void visitIfStmt(Statement.If stmt) {
//        Variable conditionType = resolve(stmt.condition);
//        if (conditionType.varType != VarType.Bool) {
//            ErrorHandler.printResolverError("Condition must be of Bool type.",stmt.colon.getLine(), stmt.colon.getColumn());
//        }
//
//        if (((Statement.Block) stmt.thenBranch).statements.isEmpty()) {
//            ErrorHandler.printResolverError("Expect statement in a if body.",stmt.colon.getLine(), stmt.colon.getColumn());
//        }
//
//        beginScope();
//        resolve(stmt.thenBranch);
//        endScope();
//
//        if (stmt.elseBranch != null) {
//            if (((Statement.Block) stmt.elseBranch).statements.isEmpty()) {
//                ErrorHandler.printResolverError("Expect statement in a else body.",stmt.colon.getLine(), stmt.colon.getColumn());
//            }
//
//            beginScope();
//            resolve(stmt.elseBranch);
//            endScope();
//        }
//
//        return null;
//    }
//
//    @Override
//    public Void visitReturnStmt(Statement.Return stmt) {
//        if (currentFunction == null) {
//            ErrorHandler.printResolverError("You can only return from a function.",stmt.keyword.getLine(), stmt.keyword.getColumn());
//        }
//
//        Variable returnValue = null;
//        for (int i = scopes.size() - 1; i >= 0; i--) {
//            if (scopes.get(i).containsKey(currentFunction.getName())) {
//                returnValue = scopes.get(i).get(currentFunction.getName());
//                break;
//            }
//        }
//
//        if (stmt.value == null && ((Callable) returnValue).getReturnType() != VarType.NonType) {
//            ErrorHandler.printResolverError("Missing return value.",stmt.keyword.getLine(), stmt.keyword.getColumn());
//        } else if (stmt.value != null && ((Callable) returnValue).getReturnType() == VarType.NonType) {
//            ErrorHandler.printResolverError("Cannot return a value form a function with no return type declared.",stmt.keyword.getLine(), stmt.keyword.getColumn());
//        } else if (stmt.value != null) {
//            Variable returnedType = resolve(stmt.value);
//
//            if (returnedType.varType != ((Callable) returnValue).getReturnType()) {
//                ErrorHandler.printResolverError("Incorrect return type.",stmt.keyword.getLine(), stmt.keyword.getColumn());
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public Void visitVarStmt(Statement.Var stmt) {
//        return null;
//    }
//
//    private void resolve(Statement statement) {
//        statement.accept(this);
//    }
//
//    private Variable resolve(Expression expression) {
//        return expression.accept(this);
//    }
//
//    private void beginScope() {
//        scopes.push(new LinkedHashMap<>());
//    }
//
//    private void endScope() {
//        scopes.pop();
//    }
//
//    private Variable declare(Token name, Variable variable) {
//        Map<String, Variable> scope = scopes.peek();
//        if (scope.containsKey(name.getLexeme())) {
//            ErrorHandler.printResolverError("Item with this name already declared in this scope.",name.getLine(), name.getColumn());
//        }
//
//        scope.put(name.getLexeme(), variable);
//        return variable;
//    }
//
//    private void define(Token name) {
//        Variable variable = scopes.peek().get(name.getLexeme());
//        variable.defined = true;
//        scopes.peek().put(name.getLexeme(), variable);
//    }
//
//    private Variable resolveVariable(Expression expression, Token name) {
//        for (int i = scopes.size() - 1; i >= 0; i--) {
//            if (scopes.get(i).containsKey(name.getLexeme())) {
//                interpreter.resolve(expression, scopes.size() - i - 1);
//                return scopes.get(i).get(name.getLexeme());
//            }
//        }
//
//        ErrorHandler.printResolverError("Cannot resolve symbol '" + name.getLexeme() + "'.",name.getLine(), name.getColumn());
//
//        return new Variable();
//    }
//
//    @Override
//    public Void visitPrintStmt(Statement.Print stmt) {
//        Variable value = resolve(stmt.expression);
//        if (value.varType == VarType.NonType) {
//            ErrorHandler.printResolverError("No value to print",stmt.print.getLine(), stmt.print.getColumn());
//        }
//
//        return null;
//    }
//
////     this.type = type;
////     this.iter = iter;
////     this.container = container;
////     this.body = body;
////     for(fraction i : contener)
//    @Override
//    public Void visitForStmt(Statement.For stmt) {
//        Variable in = resolve(stmt.container);
//
//        if (!(in instanceof Iterable)) {
//            ErrorHandler.printResolverError("Item is not iterable.",stmt.iter.getLine(), stmt.iter.getColumn());
//        }
//
//        beginScope();
//        declare(stmt.each, in);
//        define(stmt.each);
//
//        if (((Statement.Block) stmt.body).statements.isEmpty()) {
//            ErrorHandler.printResolverError("Expect statement in a for body.",stmt.iter.getLine(), stmt.iter.getColumn());
//        }
//
//        resolve(((Statement.Block) stmt.body).statements);
//        endScope();
//
//        return null;
//    }
//
//    @Override
//    public Void visitContainerStmt(Statement.Container stmt) {
//        // todo !!!
//        for( Expression elem : stmt.elements){
//            resolve(elem);
//        }
//
//        return null;
//    }
//
//    @FunctionalInterface
//    interface TriFunction<T1, T2, T3, T4> {
//        T4 apply(T1 one, T2 two, T3 three);
//    }
//}
