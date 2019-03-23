package com.compiler.interpreter.variables;

import com.compiler.interpreter.Interpreter;

import java.util.List;

public interface Callable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
