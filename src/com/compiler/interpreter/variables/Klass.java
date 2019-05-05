package com.compiler.interpreter.variables;

import com.compiler.interpreter.Interpreter;

import java.util.List;
import java.util.Map;

public class Klass extends Variable implements Callable {
    final String name;
    private Map<String, Function> methods = null;

    public Klass(String name, Map<String, Function> methods) {
        this.name = name;
        this.methods = methods;
    }

    public Function findMethod(Instance instance, String name) {
        if (methods.containsKey(name)) {
            //return methods.get(name).bind(instance);
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Variable> arguments) {
        Instance instance = new Instance(this);

        Function initializer = methods.get("init");
        if (initializer != null) {
            //initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public int arity() {
        Function initializer = methods.get("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Variable> getParams() {
        return null;
    }

    @Override
    public VarType getReturnType() {
        return null;
    }

    @Override
    public void addParam(Variable param) {

    }
}