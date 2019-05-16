package com.compiler.interpreter;

public class RuntimeError extends RuntimeException {
     public RuntimeError(String message) {
        super(message);
    }
}