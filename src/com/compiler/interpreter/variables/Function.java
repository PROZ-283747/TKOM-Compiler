package com.compiler.interpreter.variables;

import com.compiler.interpreter.Environment;
import com.compiler.interpreter.Interpreter;
import com.compiler.parser.Statement;
import java.util.LinkedList;
import java.util.List;

public class Function extends Variable implements Callable {
    private Statement.Function declaration;
    private Environment closure;
    private final List<Variable> params;
    private VarType returnType;

    public Function() {
        params = new LinkedList<>();
    }

    public Function(Statement.Function declaration) {
        this(declaration, null);
    }

    public Function(Statement.Function declaration, Environment closure) {
        if (declaration.returnType != null)
            this.returnType = VarType.fromString(declaration.returnType.getLexeme());
        else
            this.returnType = VarType.NonType;

        super.varType = VarType.Function;
        this.closure = closure;
        this.declaration = declaration;
        params = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.getLexeme() + ">";
    }

    @Override
    public int arity() {
        return params.size();
    }

    @Override
    public Variable call(Interpreter interpreter, List<Variable> arguments) {
        if (declaration == null) return null; // This is a hack to allow Thing to be gettable

        Environment environment = new Environment(closure);

        interpreter.executeBlock((List) declaration.parameters, environment);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            environment.assign(declaration.parameters.get(i).name, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return (Variable) returnValue.value;
        }

        return null;
    }

    @Override
    public String getName() {
        return declaration.name.getLexeme();
    }

    @Override
    public List<Variable> getParams() {
        return params;
    }

    @Override
    public VarType getReturnType() {
        return returnType;
    }

    @Override
    public void addParam(Variable param) {
        params.add(param);
    }
}
