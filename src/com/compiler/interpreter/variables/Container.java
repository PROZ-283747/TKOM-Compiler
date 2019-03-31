package com.compiler.interpreter.variables;

import java.util.LinkedList;
import java.util.List;

public class Container extends Variable {
    private final List<Variable> elements;

    Container() {
        elements = new LinkedList<>();
    }

    Container(List<Variable> elements) {
        this.elements = elements;
    }

    Container(List<Variable> elements, VarType type) {
        this.elements = elements;
        this.varType = type;
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
