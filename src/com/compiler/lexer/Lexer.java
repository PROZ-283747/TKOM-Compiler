package com.compiler.lexer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static sun.nio.ch.IOStatus.EOF;


public class Lexer{

    private CodeReader reader;
    private StringBuilder lexeme;
    public Token lastToken; // last created token
    int tokenColumn;

    private static final Map<String, TokenType> keywords;
    //private static final Map<String, TokenType> singleSigns;

    private final Map<Character, Supplier<Token>> functions;

    static{
        keywords = new HashMap<>();
        //singleSigns = new HashMap<>();

        keywords.put("fraction", TokenType.FRACTION_T);
        keywords.put("string", TokenType.STRING_T);
        keywords.put("void", TokenType.VOID_T);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("if", TokenType.IF);
        keywords.put("while", TokenType.WHILE);
        keywords.put("return", TokenType.RETURN);
        keywords.put("class", TokenType.CLASS);
        keywords.put("print", TokenType.PRINT);

//        singleSigns.put("(", TokenType.LEFT_PAREN);
//        singleSigns.put(")", TokenType.RIGHT_PAREN);
//        singleSigns.put("{", TokenType.LEFT_BRACKET);
//        singleSigns.put("}", TokenType.RIGHT_BRACKET);
//        singleSigns.put("[", TokenType.LEFT_SQUARE_BRACKET);
//        singleSigns.put("]", TokenType.RIGHT_SQUARE_BRACKET);
//        singleSigns.put(",", TokenType.COMMA);
//        singleSigns.put(":", TokenType.COLON);
//        singleSigns.put(";", TokenType.SEMICOLON);
//        singleSigns.put("\'", TokenType.APOSTROPHE);
//        singleSigns.put("/", TokenType.SLASH);
//        singleSigns.put("*", TokenType.STAR);
    }

    {
        functions = new HashMap<>();

        functions.put('(', () -> setToken(TokenType.LEFT_PAREN));
        functions.put(')', () -> setToken(TokenType.RIGHT_PAREN));
        functions.put('{', () -> setToken(TokenType.LEFT_BRACKET));
        functions.put('}', () -> setToken(TokenType.RIGHT_BRACKET));
        functions.put('[', () -> setToken(TokenType.LEFT_SQUARE_BRACKET));
        functions.put(']', () -> setToken(TokenType.RIGHT_SQUARE_BRACKET));
        functions.put(',', () -> setToken(TokenType.COMMA));
        functions.put(':', () -> setToken(TokenType.COLON));
        functions.put(';', () -> setToken(TokenType.SEMICOLON));
        functions.put('\'', () -> setToken(TokenType.APOSTROPHE));
        functions.put('/', () -> setToken(TokenType.SLASH));
        functions.put('*', () -> setToken(TokenType.STAR));

        // todo: funkcja do + += ++
        functions.put('+', () -> setToken(expect('=') ? TokenType.PLUS : TokenType.PLUS_EQUAL));
        functions.put('+', () -> setToken(expect('+') ? TokenType.PLUS : TokenType.INCREMENTATION));
        functions.put('-', () -> setToken(expect('=') ? TokenType.MINUS : TokenType.MINUS_EQUAL));
        functions.put('-', () -> setToken(expect('-') ? TokenType.MINUS : TokenType.DECREMENTATION));
        functions.put('=', () -> setToken(expect('=') ? TokenType.ASSIGNMENT : TokenType.EQUAL));
        functions.put('>', () -> setToken(expect('=') ? TokenType.GREATER : TokenType.GREATER_EQUAL));
        functions.put('<', () -> setToken(expect('=') ? TokenType.LESS : TokenType.LESS_EQUAL));
        functions.put('!', () -> setToken(expect('=') ? TokenType.BANG_EQUAL : TokenType.BANG));
        functions.put('&', () -> setToken(expect('&') ? TokenType.AND : TokenType.ERROR));
        functions.put('|', () -> setToken(expect('|') ? TokenType.OR : TokenType.ERROR));

        functions.put('"', this::string);

        // Set numbers
        for (char c = '0'; c <= '9'; c++) {
            functions.put(c, this::number);
        }
        // Set lowercase letters
        for (char c = 'a'; c <= 'z'; c++) {
            functions.put(c, this::identifier);
        }
        // Set uppercase letters
        for (char c = 'A'; c <= 'Z'; c++) {
            functions.put(c, this::identifier);
        }
    }

    public Token getToken() {
        skipUnrelevant();

        char c = advance();

        tokenColumn = reader.getPosition().column();
        
        if(c == EOF || c == (char) -1 || c == (char) 0x04){
            return new Token(TokenType.EOF, "", getLine(), getColumn(), getSignNumber());
        }
        Token token = functions.getOrDefault(c, () -> unexpCharError(c)).get();

        lexeme.delete(0,lexeme.length());
        return token;
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c) || c == '_';
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    private char advance(){
        char c = reader.getChar();
        lexeme.append(c);

        return c;
    }

    private boolean expect(int expected){
        if (reader.peek() == expected) {
            advance();
            return true;
        }
        return false;
    }

    private void skipUnrelevant(){
        char c = reader.peek();
        while (skipWhitespace(c) || skipComment(c)) {
            getLexeme();
            c = reader.peek();
        }
    }

    private boolean skipWhitespace(char c){
        if (isWhitespace(c)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean skipComment(char c){
        if (c == '#') {
            while (reader.peek() != '\n' && reader.peek() != EOF) advance();
            return true;
        }
        return false;
    }

    private Token number() {
        char c = '\0';
        
        while(isDigit(reader.peek())){
            advance();
        }
        if(reader.peek() == '%')
            c = advance();       
        if (c == '%' && isDigit(reader.peek())) {
            advance();
            while (isDigit(reader.peek())) {
                advance();
            }
        }
        return setToken(TokenType.FRACTION);
    }

    private Token identifier(){
        while (isAlphaNumeric(reader.peek())) {
            advance();
        }

        String text = lexemeToString();

        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;

        return setToken(type);

    }

    private Token string(){
        while (reader.peek() != '"') {
            if (advance() == '\n' || reader.isEOF()) {
            }
        }
        advance();

        String value = lexemeToString();
        lexeme.delete(0, lexeme.length());
        lexeme.append(value.substring(1, value.length() - 1)); // Remove "" marks

        return setToken(TokenType.STRING);
    }

    private String getLexeme() {
        String text = lexeme.toString();
        lexeme.delete(0, lexeme.length());

        return text;
    }

    private String lexemeToString() {
        return lexeme.toString();
    }

    private int getColumn() {
        return reader.getPosition().column();
    }

    private int getLine() {
        return reader.getPosition().line();
    }

    private int getSignNumber(){
        return reader.getPosition().signNumber();
    }

    private Token setToken(TokenType type) {
        String text = getLexeme();
        int line = getLine();
        int signNumber = getSignNumber();

        return new Token(type, text, line, tokenColumn, signNumber);
    }

    public Lexer(CodeReader reader) {
        this.reader = reader;
        this.lexeme = new StringBuilder();
    }

    private Token unexpCharError(char c) {
        return setToken(TokenType.ERROR);
    }

}
