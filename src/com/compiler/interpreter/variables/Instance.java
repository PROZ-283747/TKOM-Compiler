//package com.compiler.interpreter.variables;
//
//import com.compiler.interpreter.RuntimeError;
//import com.compiler.lexer.Token;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Instance {
//    private Klass klass;
//    private final Map<String, Object> fields = new HashMap<>();
//
//    Instance(Klass klass) {
//        this.klass = klass;
//    }
//
//    public Object get(Token name) {
//        if (fields.containsKey(name.lexeme)) {
//            return fields.get(name.lexeme);
//        }
//
//        Function method = klass.findMethod(this, name.lexeme);
//        if (method != null) return method;
//
//        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
//    }
//
//    public void set(Token name, Object value) {
//        fields.put(name.lexeme, value);
//    }
//
//    @Override
//    public String toString() {
//        return klass.name + " instance";
//    }
//}
