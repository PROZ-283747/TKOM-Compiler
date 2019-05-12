package com.compiler.interpreter.variables;


public interface Gettable {
    Variable get(String name);

    void set(String name, Variable value);

    String getName();
}
