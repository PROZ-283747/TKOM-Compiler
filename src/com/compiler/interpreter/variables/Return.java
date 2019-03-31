package com.compiler.interpreter.variables;

public class Return extends RuntimeException {
    final Variable value;

    public Return(Variable value) {
        super(null, null, false, false);
        this.value = value;
    }
}
