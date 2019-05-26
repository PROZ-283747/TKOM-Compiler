package com.compiler.interpreter.variables;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Variable {
    public VarType varType;
    public Object value;
    public boolean defined = false;


    public Variable(Object value) {
        if (value instanceof Boolean) {
            this.value = value;
            varType = VarType.Bool;
        } else if (value instanceof Fraction) {
            this.value = value;
            varType = VarType.Fraction;
        } else if (value instanceof String) {
            this.value = value;
            varType = VarType.String;
        }
    }

    public Variable(Fraction value) {
        this.value = value;
        varType = VarType.Fraction;
    }

    public Variable(String value) {
        this.value = value;
        varType = VarType.String;
    }

    public Variable(boolean value) {
        this.value = value;
        varType = VarType.Bool;
    }

    public Variable() {
        this(VarType.Void, null);
    }

    public Variable(VarType varType, Object value) {
        this.varType = varType;
        this.value = value;
    }

    public enum VarType {
        Fraction("fraction"),
        String("string"),
        Bool("bool"),
        Container("container"),
        Void("void"),
        Function("function"),
        Class("class");

        private static final Map<String, VarType> string2VarType;

        static {
            Map<String, VarType> map = new HashMap<>();
            for (VarType instance : VarType.values()) {
                map.put(instance.toString(), instance);
            }
            string2VarType = Collections.unmodifiableMap(map);
        }

        private String name;

        VarType(String name) {
            this.name = name;
        }

        public static VarType fromString(String name) {
            return string2VarType.get(name);
        }

        public static VarType fromValue(Object value) {
            if (value instanceof Boolean) return Bool;
            if (value instanceof String) return String;
            if (value instanceof Fraction) return Fraction;
            throw new NotImplementedException();
        }

        @Override
        public String toString() {
            return this.name;
        }

    }

    public boolean isDefined(){
        return defined;
    }
}

