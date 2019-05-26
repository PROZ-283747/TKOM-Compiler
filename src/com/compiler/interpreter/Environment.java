package com.compiler.interpreter;

import com.compiler.interpreter.variables.Variable;
import com.compiler.lexer.Token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Environment implements Serializable {
    final Environment enclosing;
    final Map<String, Variable> values = new HashMap<>(); // nazwa, typ, wartość

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Variable get(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError("Undefined variable '" + name + "'.");
    }

    public void assign(String name, Variable value) {
        if (values.containsKey(name)) {
            values.put(name, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError("Undefined variable '" + name + "'.");
    }


    public void define(String name, Variable value) {
        values.put(name, value);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    public Variable getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, Variable value) {
        ancestor(distance).values.put(name.getLexeme(), value);
    }
}