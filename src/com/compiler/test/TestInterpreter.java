package com.compiler.test;

import com.compiler.ErrorHandler;
import com.compiler.interpreter.Interpreter;
import com.compiler.interpreter.Resolver;
import com.compiler.lexer.Lexer;
import com.compiler.lexer.StringReader;
import com.compiler.parser.Parser;
import com.compiler.parser.Statement;
import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestInterpreter {

    @Test
    public void testCase1() {
        String code = "";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase2() {
        String code = "if(true){\n" +
                "\tprint(\"Inside if statement\");\n" +
                "}";
        String expectedOutput = "Inside if statement";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase3() {
        String code = "fraction var = 5%8;" +
                "print(var);";
        String expectedOutput = "5%8";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase4() {
        String code = "fraction i = 1;\n" +
                "\n" +
                "\twhile(i < 5){\n" +
                "\t\tprint(i);\n" +
                "\t\ti = i+1;\n" +
                "\t}";
        String expectedOutput = "1%1\n2%1\n3%1\n4%1";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }


    @Test
    public void testCase5() {
        String code = "fraction container[] = {1%4, 5%2, -3};\n" +
                "for( fraction i : container){\n" +
                "\tprint(i);\n" +
                "}";
        String expectedOutput = "1%4\n5%2\n-3%1";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase6() {
        String code = "fraction silniaFun(fraction f){\n" +
                "\tfraction silnia = 1;\n" +
                "\tfraction i = 1;\n" +
                "\n" +
                "\twhile(i <= f){\n" +
                "\t\tsilnia = silnia * i;\n" +
                "\t\ti = i+1;\n" +
                "\t}\n" +
                "\n" +
                "\tprint(silnia);\n" +
                "\treturn silnia;\n" +
                "}\n" +
                "\n" +
                "silniaFun(5);";
        String expectedOutput = "120%1";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase7() {
        String code = "class klasa{\n" +
                "\tstring s = \"classString\";\n" +
                "\tvoid printClass(){\n" +
                "\t\tprint(s);\n" +
                "\t}\n" +
                "}";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase8() {
        String code = "fraction a;";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase9() {
        String code = "fraction a;";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase10() {
        String code = "fraction a";
        String expectedOutput = "";
        String expectedErrors = "Error while parsing at line 0 column 0: Expect ';' after variable declaration.";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase11() {
        String code = "fraction double(fraction s){\n" +
                "\tprint(s*2);\n" +
                "}";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Last statement in function must be a return statement. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase12() {
        String code = "fraction container[] = {1%4, 5%2, -3, \"string\"};";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: All elements in a container must be the same type as container. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase13() {
        String code = "fraction double(fraction s){\n" +
                "\tprint(s*2);\n" +
                "return \"string\";" +
                "}";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Incorrect return type. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase14() {
        String code = "if(1 != 4){" +
                "           print(\"Correct\");" +
                "       }";
        String expectedOutput = "Correct";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase15() {
        String code = "class klasa{}" +
                "object ob = new klasa;";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase16() {
        String code = "void printSth(string s){}";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Expect statement in a function body. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase17() {
        String code = "print(5%6 - 1%6);";
        String expectedOutput = "2%3";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase18() {
        String code = "class klasa{}" +
                "      object ob = new klasa;";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase19() {
        String code = "class klasa{\n" +
                "\tstring s = \"classString\";\n" +
                "\tvoid printClass(){\n" +
                "\t\tprint(s);\n" +
                "\t}\n" +
                "}" +
                "object ob = new klasa;" +
                "ob.printClass();" +
                "ob.s = \"nowa wartosc\";" +
                "ob.printClass();";
        String expectedOutput = "classString\nnowa wartosc";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase20() {
        String code = "class A{\n" +
                "\tstring sA = \"classA\";\n" +
                "\tvoid printClass(){\n" +
                "\t\tprint(sA);\n" +
                "\t}\n" +
                "}\n" +
                "\n" +
                "class B{\n" +
                "\tstring sB = \"classB\";\n" +
                "\tvoid printClass(){\n" +
                "\t\tprint(sB);\n" +
                "\t}\n" +
                "}" +
                "object a = new A;" +
                "object b = new B;" +
                "a.printClass();" +
                "a.sA = b.sB;" +
                "a.printClass();";
        String expectedOutput = "classA\nclassB";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase21() {
        String code = "print(35%4 + 3*9)";
        String expectedOutput = "";
        String expectedErrors = "Error while parsing at line 0 column 0: Expect ';' after value.";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase22() {
        String code = "print(35%4 + 3*9);";
        String expectedOutput = "143%4";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void testCase23() {
        String code = "if(1 >3){\n" +
                "\tprint(\"if\");\n" +
                "}else{\n" +
                "\tprint(\"else\");\n" +
                "}";
        String expectedOutput = "else";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }


    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private static final PrintStream originalErr = System.err;


    @BeforeAll
    static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    static void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @BeforeEach
    void resetStreams() {
        outContent.reset();
        errContent.reset();
        ErrorHandler.resetErrors();
    }

    static String removeNewLine(String text) {
        return text.replaceAll("\\r?\\n", "\n");
    }


    void testInterpreter(String code, String expectedOutput, String expectedErrors){
        String program = code + (char) 0x04;
        ErrorHandler errorHandler = new ErrorHandler();
        StringReader reader = new StringReader(program);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);

        List<Statement> statements = parser.parse();

        Interpreter interpreter = new Interpreter();
        Resolver resolver = new Resolver(interpreter);

        resolver.resolve(statements);
        interpreter.interpret(statements);

        assertEquals(removeNewLine(expectedOutput.trim()), removeNewLine(outContent.toString().trim()));
        assertEquals(removeNewLine(expectedErrors.trim()), removeNewLine(errContent.toString().trim()));

    }

}

