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
//    Container() {
//        this.varType = VarType.Container;
//        elements = new LinkedList<>();
//    }
//
//    Container(List<Variable> elements) {
//        this.varType = VarType.Container;
//        this.elements = elements;
//    }
//
//    public Container(List<Variable> elements, VarType type) {
//        this.elements = elements;
//        this.varType = type;
//    }

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
