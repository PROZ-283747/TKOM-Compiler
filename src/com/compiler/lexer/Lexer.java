package com.compiler.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jdk.nashorn.internal.parser.TokenType.EOF;

public class Lexer {

    private CodeReader reader;
    public Token lastToken; // last created token
    private static final Map<String, TokenType> keywords;
    private static final Map<String, TokenType> singleSigns;

    static{
        keywords = new HashMap<>();
        singleSigns = new HashMap<>();

        keywords.put("fraction", TokenType.FRACTION_T);
        keywords.put("string", TokenType.STRING_T);
        keywords.put("void", TokenType.VOID_T);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("if", TokenType.IF);
        keywords.put("while", TokenType.WHILE);
        keywords.put("return", TokenType.RETURN);

        singleSigns.put("{", TokenType.LEFT_PARENTHESIS);
        singleSigns.put("}", TokenType.RIGHT_PARENTHESIS);
        keywords.put("(", TokenType.LEFT_BRACKET);
        keywords.put(")", TokenType.RIGHT_BRACKET);
        keywords.put("[", TokenType.LEFT_SQUARE_BRACKET);
        keywords.put("]", TokenType.RIGHT_SQUARE_BRACKET);
        keywords.put(",", TokenType.COMMA);
        keywords.put(":", TokenType.COLON);
        keywords.put(";", TokenType.SEMICOLON);
        keywords.put("\'", TokenType.APOSTROPHE);
        keywords.put("/", TokenType.SLASH);
        keywords.put("*", TokenType.STAR);
        keywords.put("#", TokenType.HASH);

    }

    public Token getToken(){
        System.out.println("getToken");
        return new Token(TokenType.APOSTROPHE,"cs", 2,1);
    }


    public Lexer(CodeReader reader) {
        this.reader = reader;
    }
}
