package com.compiler.test;

import com.compiler.lexer.Token;
import com.compiler.lexer.TokenType;
import com.compiler.parser.Parser;
import com.compiler.parser.Statement;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static com.compiler.lexer.TokenType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestParser {

    @Test
    public void testCase1(){
        System.out.println("First test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(FRACTION_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(EQUAL));
        tokens.add(new Token(FRACTION, "7%3", 0, 0, 0));
        tokens.add(setToken(SEMICOLON));

        List<Statement> statements = testParser(tokens);

        assertEquals(statements.size(), 1);
        assertTrue(statements.get(0) instanceof Statement.Var);

//        Expression fraction = ((Statement.Var)statements.get(0)).initializer;
//        System.out.println();
//        assertEquals((((Expression.Literal)(fraction)).value.), 7%3);
    }

    @Test
    public void testCase2(){
        System.out.println("Second test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(CLASS));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(COLON));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(LEFT_BRACKET));
        tokens.add(setToken(FRACTION_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(EQUAL));
        tokens.add(new Token(FRACTION, "7%3", 0, 0, 0));
        tokens.add(setToken(SEMICOLON));
        tokens.add(setToken(RIGHT_BRACKET));

        List<Statement> statements = testParser(tokens);

        assertEquals(statements.size(), 1);
        assertTrue(statements.get(0) instanceof Statement.Class);
    }

    @Test
    public void testCas3(){
        System.out.println("Third test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(FRACTION_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(LEFT_PAREN));
        tokens.add(setToken(RIGHT_PAREN));
        tokens.add(setToken(LEFT_BRACKET));
        tokens.add(setToken(RIGHT_BRACKET));
        tokens.add(setToken(VOID_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(LEFT_PAREN));
        tokens.add(setToken(FRACTION_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(RIGHT_PAREN));
        tokens.add(setToken(LEFT_BRACKET));
        tokens.add(setToken(RIGHT_BRACKET));
        tokens.add(setToken(FRACTION_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(EQUAL));
        tokens.add(new Token(FRACTION, "7%3", 0, 0, 0));
        tokens.add(setToken(SEMICOLON));
        tokens.add(setToken(STRING_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(EQUAL));
        tokens.add(new Token(STRING, "This is string", 0, 0, 0));
        tokens.add(setToken(SEMICOLON));

        List<Statement> statements = testParser(tokens);

        assertEquals(statements.size(), 4);
        assertTrue(statements.get(0) instanceof Statement.Function);
        assertTrue(statements.get(1) instanceof Statement.Function);
        assertTrue(statements.get(2) instanceof Statement.Var);
    }

    @Test
    public void testCas4() {
        System.out.println("Fourth test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(LEFT_BRACKET));
        tokens.add(setToken(RIGHT_BRACKET));

        List<Statement> statements = testParser(tokens);
        assertTrue(statements.get(0) instanceof Statement.Block);
    }

    @Test
    public void testCas5() {
        System.out.println("Fifth test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(EQUAL));
        tokens.add(setToken(FRACTION));
        tokens.add(setToken(SEMICOLON));

        List<Statement> statements = testParser(tokens);
        assertTrue(statements.get(0) instanceof Statement.Expression);
    }

    @Test
    public void testCas6() {
        System.out.println("Sixth test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(IF));
        tokens.add(setToken(LEFT_PAREN));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(EQUAL_EQUAL));
        tokens.add(setToken(TRUE));
        tokens.add(setToken(RIGHT_PAREN));
        tokens.add(setToken(LEFT_BRACKET));
        tokens.add(setToken(RIGHT_BRACKET));

        List<Statement> statements = testParser(tokens);
        assertTrue(statements.get(0) instanceof Statement.If);
    }

    @Test
    public void testCas7() {
        System.out.println("Seventh test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(PRINT));
        tokens.add(setToken(LEFT_PAREN));
        tokens.add(setToken(STRING));
        tokens.add(setToken(RIGHT_PAREN));
        tokens.add(setToken(SEMICOLON));

        List<Statement> statements = testParser(tokens);
        assertTrue(statements.get(0) instanceof Statement.Print);
    }

    @Test
    public void testCas8() {
        System.out.println("Eight test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(RETURN));
        tokens.add(setToken(FRACTION));
        tokens.add(setToken(SEMICOLON));

        List<Statement> statements = testParser(tokens);
        assertTrue(statements.get(0) instanceof Statement.Return);
    }

    @Test
    public void testCas9() {
        System.out.println("Ninth test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(STRING_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(EQUAL));
        tokens.add(setToken(STRING));
        tokens.add(setToken(SEMICOLON));

        List<Statement> statements = testParser(tokens);
        assertTrue(statements.get(0) instanceof Statement.Var);
    }

    @Test
    public void testCas10() {
        System.out.println("Tenth test case. ");
        ArrayList<Token> tokens = new ArrayList<>();

        tokens.add(setToken(WHILE));
        tokens.add(setToken(LEFT_PAREN));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(GREATER_EQUAL));
        tokens.add(setToken(FRACTION));
        tokens.add(setToken(RIGHT_PAREN));
        tokens.add(setToken(LEFT_BRACKET));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(EQUAL));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(PLUS));
        tokens.add(setToken(FRACTION));
        tokens.add(setToken(SEMICOLON));
        tokens.add(setToken(RIGHT_BRACKET));

        List<Statement> statements = testParser(tokens);
        assertTrue(statements.get(0) instanceof Statement.While);
    }

    @Test
    public void testCas11() {
        System.out.println("Eleventh test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(FOR));
        tokens.add(setToken(LEFT_PAREN));
        tokens.add(setToken(FRACTION_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(COLON));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(RIGHT_PAREN));
        tokens.add(setToken(LEFT_BRACKET));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(EQUAL));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(PLUS));
        tokens.add(setToken(FRACTION));
        tokens.add(setToken(SEMICOLON));
        tokens.add(setToken(RIGHT_BRACKET));


        List<Statement> statements = testParser(tokens);
        assertTrue(statements.get(0) instanceof Statement.For);
    }

    @Test
    public void testCas12() {
        System.out.println("Ninth test case. ");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(setToken(FRACTION_T));
        tokens.add(setToken(IDENTIFIER));
        tokens.add(setToken(LEFT_SQUARE_BRACKET));
        tokens.add(setToken(RIGHT_SQUARE_BRACKET));
        tokens.add(setToken(EQUAL));
        tokens.add(setToken(LEFT_BRACKET));
        tokens.add(setToken(FRACTION));
        tokens.add(setToken(COMMA));
        tokens.add(setToken(FRACTION));
        tokens.add(setToken(RIGHT_BRACKET));
        tokens.add(setToken(SEMICOLON));

        List<Statement> statements = testParser(tokens);
        assertTrue(statements.get(0) instanceof Statement.Container);
    }


    private Token setToken(TokenType type){
        return new Token(type, "", 0, 0, 0);
    }

    List<Statement> testParser(List<Token> tokens){
        tokens.add(setToken(EOF));
        Parser parser = new Parser(tokens);
        return parser.parse();
    }
}
