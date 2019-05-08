package com.compiler.lexer;

import com.compiler.ErrorHandler;

public class StringReader  implements Source {

    String code;
    Position currentPos;
    private char currChar = '\0'; // next sign to read and process

    public StringReader(String code) {
        currentPos = new Position(1, 0, 0);
        this.code = code;
        this.currChar = code.charAt(0);
        this.code = this.code + -1;
    }

    @Override
    public char getChar() {
        currChar++;
        return code.charAt(currChar-1);
    }

    @Override
    public char peek() {
        return currChar;
    }

    @Override
    public boolean isEOF() {
        return currChar == -1;
    }

    @Override
    public Position getPosition() {
        return currentPos;
    }
}
