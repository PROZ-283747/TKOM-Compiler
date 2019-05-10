package com.compiler.test;

import com.compiler.lexer.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class TestLexer {

    @Test
    public void testCas0() {
        System.out.println("Zero test case. ");
        String code = "fraction v;";

        List<Token> tokens = testLexer(code);
        assertTrue(tokens.get(0).type == TokenType.FRACTION_T);
        assertTrue(tokens.get(1).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(2).type == TokenType.SEMICOLON);
    }

    @Test
    public void testCas1() {
        System.out.println("First test case. ");
        String code = "fraction var = 3%4;";

        List<Token> tokens = testLexer(code);
        assertTrue(tokens.get(0).type == TokenType.FRACTION_T);
        assertTrue(tokens.get(1).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(2).type == TokenType.EQUAL);
        assertTrue(tokens.get(3).type == TokenType.FRACTION);
        assertTrue(tokens.get(4).type == TokenType.SEMICOLON);
    }

    @Test
    public void testCase2() {
        System.out.println("Second test case. ");
        String code = "if( a > 5%3){" +
                    "       print(a);" +
                    "       a = a - 1;" +
                    "   }";

        List<Token> tokens = testLexer(code);
        assertTrue(tokens.get(0).type == TokenType.IF);
        assertTrue(tokens.get(1).type == TokenType.LEFT_PAREN);
        assertTrue(tokens.get(2).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(3).type == TokenType.GREATER);
        assertTrue(tokens.get(4).type == TokenType.FRACTION);
        assertTrue(tokens.get(5).type == TokenType.RIGHT_PAREN);
        assertTrue(tokens.get(6).type == TokenType.LEFT_BRACKET);
        assertTrue(tokens.get(7).type == TokenType.PRINT);
        assertTrue(tokens.get(8).type == TokenType.LEFT_PAREN);
        assertTrue(tokens.get(9).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(10).type == TokenType.RIGHT_PAREN);
        assertTrue(tokens.get(11).type == TokenType.SEMICOLON);
        assertTrue(tokens.get(12).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(13).type == TokenType.EQUAL);
        assertTrue(tokens.get(14).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(15).type == TokenType.MINUS);
        assertTrue(tokens.get(16).type == TokenType.FRACTION);
        assertTrue(tokens.get(17).type == TokenType.SEMICOLON);
        assertTrue(tokens.get(18).type == TokenType.RIGHT_BRACKET);
    }

    @Test
    public void testCase3() {
        System.out.println("Third test case. ");
        String code = "string s = \"String\";";

        List<Token> tokens = testLexer(code);
        assertTrue(tokens.get(0).type == TokenType.STRING_T);
        assertTrue(tokens.get(1).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(2).type == TokenType.EQUAL);
        assertTrue(tokens.get(3).type == TokenType.STRING);
        assertTrue(tokens.get(4).type == TokenType.SEMICOLON);
    }

    @Test
    public void testCase4() {
        System.out.println("Fourth test case. ");
        String code = "class myClass{" +
                "           string s = \"elo\";" +
                "           string  fun(){" +
                "               return \"so much fun!\";" +
                "           }};";

        List<Token> tokens = testLexer(code);
        assertTrue(tokens.get(0).type == TokenType.CLASS);
        assertTrue(tokens.get(1).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(2).type == TokenType.LEFT_BRACKET);
        assertTrue(tokens.get(3).type == TokenType.STRING_T);
        assertTrue(tokens.get(4).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(5).type == TokenType.EQUAL);
        assertTrue(tokens.get(6).type == TokenType.STRING);
        assertTrue(tokens.get(7).type == TokenType.SEMICOLON);
        assertTrue(tokens.get(8).type == TokenType.STRING_T);
        assertTrue(tokens.get(9).type == TokenType.IDENTIFIER);
        assertTrue(tokens.get(10).type == TokenType.LEFT_PAREN);
        assertTrue(tokens.get(11).type == TokenType.RIGHT_PAREN);
        assertTrue(tokens.get(12).type == TokenType.LEFT_BRACKET);
        assertTrue(tokens.get(13).type == TokenType.RETURN);
        assertTrue(tokens.get(14).type == TokenType.STRING);
        assertTrue(tokens.get(15).type == TokenType.SEMICOLON);
        assertTrue(tokens.get(16).type == TokenType.RIGHT_BRACKET);
        assertTrue(tokens.get(17).type == TokenType.RIGHT_BRACKET);
    }



    List<Token> testLexer(String code){
        List<Token> tokens = new ArrayList<>();
        StringReader sr = new StringReader(code);
        Lexer lexer = new Lexer(sr);
       do{
           tokens.add(lexer.getCurrentToken());
           lexer.advanceToken();
        } while(!sr.isEOF());
        tokens.add(lexer.getCurrentToken());

       return tokens;
    }
}

