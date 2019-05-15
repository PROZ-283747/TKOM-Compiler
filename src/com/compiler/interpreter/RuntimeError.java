package com.compiler.interpreter;


import com.compiler.lexer.Token;

public class RuntimeError extends RuntimeException {
     public RuntimeError(String message) {
        super(message);
    }
}