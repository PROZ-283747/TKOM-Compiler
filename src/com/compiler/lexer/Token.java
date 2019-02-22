package com.compiler.lexer;

import com.compiler.Fraction;

import java.util.Optional;

public class Token {

    final TokenType type;
    final String lexeme;
    final Optional<Fraction> fraction = null;
    private final int column;
    private final int line;


    Token(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        //this.literal = literal;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Optional<Fraction> getFraction() {
        return fraction;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }
    public String toString() {
        return type + " " + lexeme;
    }
}
