package com.compiler.lexer;

// for tests
public class StringReader  implements Source {

    String code;
    private int currPos; // position of char which is next to be readen and proccessed

    public StringReader(String code) {
        this.code = code;
        this.currPos = 0;
    }

    @Override
    public char getChar() {
        if(isEOF())
            return (char) -1;
        else {
            currPos++;
            return code.charAt(currPos - 1);
        }
    }

    @Override
    public char peek() {
        return code.charAt(currPos);
    }

    @Override
    public boolean isEOF() {
        return currPos >= code.length();
    }

    @Override
    public Position getPosition() {
        return new Position(0,0,0);
    }
}
