package com.compiler.lexer;

import com.compiler.ErrorHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import static sun.nio.ch.IOStatus.EOF;


public class Lexer{

    private Source source;
    private StringBuilder lexeme;
    private ErrorHandler errorHandler = new ErrorHandler();
    public Token currentToken; // token to process next
    public Token previousToken; // previous token
    int tokenColumn;

    private static final Map<String, TokenType> keywords;
    private final Map<Character, Supplier<Token>> functions;

    static{
        keywords = new HashMap<>();

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
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("null", TokenType.NULL);
        keywords.put("new", TokenType.NEW);
        keywords.put("object", TokenType.OBJECT);
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
        functions.put('.', () -> setToken(TokenType.DOT));
        functions.put('+', () -> plusSign());
        functions.put('-', () -> minusSign());

        functions.put('=', () -> setToken(expect('=') ? TokenType.EQUAL_EQUAL: TokenType.EQUAL));
        functions.put('>', () -> setToken(expect('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER));
        functions.put('<', () -> setToken(expect('=') ? TokenType.LESS_EQUAL : TokenType.LESS));
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

    private Token plusSign(){
        if(expect('='))
            return setToken(TokenType.PLUS_EQUAL);
        else if(expect('+'))
            return setToken(TokenType.PLUS_PLUS);
        else
            return setToken(TokenType.PLUS);
    }

    private Token minusSign(){
        if(expect('='))
            return setToken(TokenType.MINUS_EQUAL);
        else if(expect('-'))
            return setToken(TokenType.MINUS_MINUS);
        else
            return setToken(TokenType.MINUS);
    }

    public void advanceToken() {
        skipUnrelevant();
        char c = advance();

        tokenColumn = source.getPosition().column();
        previousToken = currentToken;

        if(c == (char) -1 || c == (char) 0x04){
            currentToken = new Token(TokenType.EOF, "", getLine(), getColumn(), getSignNumber());
            return;
        }
        Token token = functions.getOrDefault(c, () -> unexpCharError(c)).get();
        lexeme.delete(0,lexeme.length());
        currentToken = token;

        if(token.type == TokenType.ERROR) {
            ErrorHandler.printLexerError("Invalid token.", token, null);
            ErrorHandler.stopIfError();
        }
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public Token getPreviousToken() {
        return previousToken;
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
        char c = source.getChar();
        lexeme.append(c);

        return c;
    }

    private boolean expect(int expected){
        if (source.peek() == expected) {
            advance();
            return true;
        }
        return false;
    }

    private void skipUnrelevant(){
        char c = source.peek();
        while (skipWhitespace(c) || skipComment(c)) {
            getLexeme();
            c = source.peek();
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
            while (source.peek() != '\n' && source.peek() != EOF) {
                advance();
            }
            return true;
        }
        return false;
    }

    private Token number() {
        char c = '\0';
        
        while(isDigit(source.peek())){
            advance();
        }
        if(source.peek() == '%')
            c = advance();       
        if (c == '%' && isDigit(source.peek())) {
            advance();
            while (isDigit(source.peek())) {
                advance();
            }
        }
        isFractionCorrect(String.valueOf(lexeme));
        return setToken(TokenType.FRACTION);
    }

    private boolean isFractionCorrect(String value){
        int nomin = 0;
        int denomin = 0;
        int index = value.indexOf("%");

        if(index == -1) {
            nomin = Integer.parseInt(value);
            denomin = 1;
        }
        else{
            nomin = Integer.parseInt(value.substring(0, index));
            denomin = Integer.parseInt(value.substring(index+1, value.length()));
        }
        if(denomin == 0){
            Token token = unexpCharError(source.peek());
            errorHandler.printLexerError("ERROR: It's  impossible to create fraction. Denominator cannot be 0 !", token,null);
            System.exit(-1);
        }
        return true;
    }

    private Token identifier(){
        while (isAlphaNumeric(source.peek())) {
            advance();
        }

        String text = lexemeToString();

        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;

        return setToken(type);

    }

    private Token string(){
        while (source.peek() != '"') {
            if (advance() == '\n' || source.isEOF()) {
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
        return source.getPosition().column();
    }

    private int getLine() {
        return source.getPosition().line();
    }

    private int getSignNumber(){
        return source.getPosition().signNumber();
    }

    public Token setToken(TokenType type) {
        String text = getLexeme();
        int line = getLine();
        int signNumber = getSignNumber();

        return new Token(type, text, line, tokenColumn, signNumber);
    }

    public Lexer(Source source) {
        this.source = source;
        this.lexeme = new StringBuilder();
        advanceToken();
    }

    private Token unexpCharError(char c) {
        return setToken(TokenType.ERROR);
    }

}
