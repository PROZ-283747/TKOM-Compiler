package com.compiler.interpreter.variables;

import java.util.LinkedList;
import java.util.List;

public class Container extends Variable implements Iterable {
    private final String name;
    private final List<Variable> elements;

    public Container(String name){
        this.name = name;
        this.varType = VarType.Container;
        this.elements = new LinkedList<>();

    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public List<Variable> getElements() {
        return elements;
    }

    public void addElement(Variable element) {
        elements.add(element);
    }

    public Container getCollection() {
        return this;
    }
}
