package com.compiler.lexer;

import com.compiler.Fraction;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Optional;



public class Token {

    public final TokenType type;
    final String lexeme;
    Optional<Fraction> fraction = null;
    private final int column;
    private final int line;
    private final int signNumber;


    Token(TokenType type, String lexeme, int line, int column, int signNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.signNumber = signNumber;
        if(type==TokenType.FRACTION){
            Fraction f = new Fraction(lexeme);
            this.fraction = Optional.of(f);
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

    public int getSignNumber(){
        return signNumber;
    }

    // osobna klasa prezenter do ywswietlania
    public String toString() {
        return "type: " + type + " \"" + lexeme + "\"" + " line: " + line +" column: " + column + " signNumber: " + signNumber ;
    }

    public static Object tokenConverter(Token token) {
        if(token.type == TokenType.FRACTION) return new Fraction(token.lexeme);
        if(token.type == TokenType.STRING) return token.lexeme;

        throw new NotImplementedException();
    }
}

