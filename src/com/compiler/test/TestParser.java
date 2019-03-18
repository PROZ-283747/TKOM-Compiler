package com.compiler.test;

import com.compiler.lexer.Token;
import com.compiler.lexer.TokenType;
import com.compiler.parser.Expression;
import com.compiler.parser.Parser;
import com.compiler.parser.Statement;
import org.junit.Test;
import com.compiler.lexer.Token.*;

import java.util.ArrayList;
import java.util.List;

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

        Expression fraction = ()((Statement.Var)statements.get(0)).initializer;
        assertEquals(((Statement.Var)statements.get(0)).initializer, 7%3);
    }

//    @Test
//    public void testCase2){
//        System.out.println("First test case. ");
//        ArrayList<Token> tokens = new ArrayList<>();
//        tokens.add(setToken(FRACTION_T));
//        tokens.add(setToken(IDENTIFIER));
//        tokens.add(setToken(EQUAL));
//        tokens.add(setToken(FRACTION));
//        tokens.add(setToken(SEMICOLON));
//
//        List<Statement> statements = testParser(tokens);
//        assertEquals(statements.size(), 1);
//
//    }

    private Token setToken(TokenType type){

        return new Token(type, "", 0, 0, 0);
    }

    List<Statement> testParser(List<Token> tokens){
        tokens.add(setToken(EOF));
        Parser parser = new Parser(tokens);
        return parser.parse();
    }
}
