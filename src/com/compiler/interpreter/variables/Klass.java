package com.compiler.interpreter.variables;

import com.compiler.parser.Statement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Klass extends Variable implements Gettable, Cloneable, Serializable {
    final String name;
    public Map<String, Variable> properties;
    public List<Statement> body;
//
//    Klass(Klass klass) {
//        this.name = klass.name;
//        super.varType = klass.varType;
//        super.value = klass.value;
//        super.defined = klass.defined;
//
//        properties = new HashMap<>();
//        for(Map.Entry<String, Variable> entry : klass.properties.entrySet()) {
//            properties.put(entry.getKey(), new Variable(entry.getValue()));
//        }
//    }

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
