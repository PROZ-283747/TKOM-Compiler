package com.compiler.interpreter.variables;

import com.compiler.interpreter.Environment;


public class KlassInstance extends Variable implements Gettable {
    private final String name;
    private Environment environment;

    public KlassInstance(String name, Environment environment) {
        this.name = name;
        this.environment = environment;
    }

    public Variable get(String name) {
        return environment.get(name);
    }

    public void set(String name, Variable value) {
        environment.assign(name, value);
    }

    public String getName() {
        return name;
    }
}
