package com.compiler.interpreter.variables;

import com.compiler.parser.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Klass extends Variable implements Gettable, Cloneable {
    final String name;
    public Map<String, Variable> properties;
    public List<Statement> body;

    public Klass(String name, List<Statement> body) {
        super.varType = VarType.Class;
        super.value = this;
        this.name = name;
        this.properties = new HashMap<>();
        this.body = body;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
