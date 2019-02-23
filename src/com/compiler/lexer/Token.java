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
        this.line = line;
        this.column = column;
        if(type==TokenType.NUMBER){
            //TODO set fraction using lexeme
            Fraction f = new Fraction(lexeme);
            //fraction = f;
        }
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
        return "type: " + type + " \"" + lexeme + "\"" + " line: " + line +" column: " + column;
    }
}
