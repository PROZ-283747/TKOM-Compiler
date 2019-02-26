package com.compiler.lexer;

//  API to read code from different sources: files, streams etc
public interface Source {
    char getChar();

    char peek();

    boolean isEOF();

    Position getPosition();
}
