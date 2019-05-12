package com.compiler.interpreter.variables;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Klass extends Variable implements Gettable {
    final String name;
    public Map<String, Variable> properties;

    public Klass(String name) {
        super.varType = VarType.Class;
        super.value = this;
        this.name = name;
        this.properties = new HashMap<>();
    }

    @Override
    public String toString() {
        return name;
    }

    public Variable get(String name) {
        if (properties.containsKey(name)) {
            return properties.get(name);
        }

        return null;
    }

    public void set(String name, Variable value) {
        properties.put(name, value);
    }

    public String getName() {
        return name;
    }
}
