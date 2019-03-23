package com.compiler.interpreter.variables;


import com.compiler.interpreter.Environment;
import com.compiler.interpreter.Interpreter;
import com.compiler.parser.Statement;

import java.util.List;

public class Function implements Callable {
    private final Statement.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    public Function(Statement.Function declaration, Environment closure, boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
    }

    Function bind(Instance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new Function(declaration, environment, isInitializer);
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public int arity() {
        return declaration.parameters.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            environment.define(declaration.parameters.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }

        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }
}