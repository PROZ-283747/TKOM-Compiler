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
    public void emptyProgram() {
        String code = "";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void incorrectAssignmentType() {
        String code = "fraction a = \"string\";";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Variable initializer is of different type than variable. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void missingSemicolon() {
        String code = "fraction a";
        String expectedOutput = "";
        String expectedErrors = "Error while parsing at line 0 column 0: Expect ';' after variable declaration.";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void correctVariableDeclaration() {
        String code = "fraction a;";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void twoIdenticalNamesSameScope() {
        String code = "fraction a;" +
                "fraction a = 4%6;";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Item with this name already declared in this scope. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void fractionAssignment() {
        String code = "fraction var = 5%8;" +
                "print(var);";
        String expectedOutput = "5%8";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void correctIfStmt() {
        String code = "if(true){\n" +
                "\tprint(\"Inside if statement\");\n" +
                "}";
        String expectedOutput = "Inside if statement";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void elseBranch() {
        String code = "if(1 >3){\n" +
                "\tprint(\"if\");\n" +
                "}else{\n" +
                "\tprint(\"else\");\n" +
                "}";
        String expectedOutput = "else";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void stringAsBooleanCondition() {
        String code = "if(\"string\"){" +
                "print(\"ok\");\n" +
                "}";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Condition must be of Bool type. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void numberAsBooleanCondition() {
        String code = "if(7%93){" +
                "print(\"ok\");\n" +
                "}";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Condition must be of Bool type. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void correctWhileStmt() {
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
    public void printContainerContent() {
        String code = "fraction container[] = {1%4, 5%2, -3};\n" +
                "for( fraction i : container){\n" +
                "\tprint(i);\n" +
                "}";
        String expectedOutput = "1%4\n5%2\n-3%1";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void correctFunctionDefinitionAndCall() {
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
    public void correctClassDefinition() {
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
    public void missingReturnStatementInFunction() {
        String code = "fraction double(fraction s){\n" +
                "\tprint(s*2);\n" +
                "}";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Last statement in function must be a return statement. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);

    }

    @Test
    public void incorrectContainersElementType() {
        String code = "fraction container[] = {1%4, 5%2, -3, \"string\"};";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: All elements in a container must be the same type as container. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void incorrectReturnTypeInFunction() {
        String code = "fraction double(fraction s){\n" +
                "\tprint(s*2);\n" +
                "return \"string\";" +
                "}";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Incorrect return type. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void inNotEqualAsBooleanType() {
        String code = "if(1 != 4){" +
                "           print(\"Correct\");" +
                "       }";
        String expectedOutput = "Correct";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void correctClassObjectCreation() {
        String code = "class klasa{}" +
                "object ob = new klasa;";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void missingFunctionBody() {
        String code = "void printSth(string s){}";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: Expect statement in a function body. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void substraction() {
        String code = "print(5%6 - 1%6);";
        String expectedOutput = "2%3";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void addition() {
        String code = "print(5%6 + 1%5);";
        String expectedOutput = "31%30";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void multiplication() {
        String code = "print(5%6 * 1%6);";
        String expectedOutput = "5%36";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void division() {
        String code = "print(5%6 / 1%2);";
        String expectedOutput = "5%3";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void classObjectCreation() {
        String code = "class klasa{}" +
                "      object ob = new klasa;";
        String expectedOutput = "";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void changeValueOFClassProperty() {
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
    public void assignClassPropertyToAnother() {
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
    public void unexistingClassElement() {
        String code = "class A{\n" +
                "\tstring sA = \"classA\";\n" +
                "}\n" +
                "object a = new A;" +
                "a.cos;";
        String expectedOutput = "";
        String expectedErrors = "Error while resolving: 'A' doesn't have 'cos' property. Line: 0 Column: 0";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void udefinedClassElement() {
        String code = "class A{\n" +
                "\tstring sA;\n" +
                "\tvoid printClass(){\n" +
                "\t\tprint(sA);\n" +
                "\t}\n" +
                "}\n" +
                "object a = new A;" +
                "print(a.sA);";
        String expectedOutput = "null";
        String expectedErrors = "";
        testInterpreter(code, expectedOutput, expectedErrors);
    }

    @Test
    public void diffFunNamesInDiffScopes() {
        String code = "class C{\n" +
                "string fun(){\n" +
                "return \"Funkcja z klasy\";\n" +
                "}\n" +
                "}\n" +
                "string fun(){\n" +
                "return \"Funkcja\";\n" +
                "}\n" +
                "print(fun());\n" +
                "object c = new C;\n" +
                "print(c.fun());";
        String expectedOutput = "Funkcja\nFunkcja z klasy";
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

        ErrorHandler errorHandler =  new ErrorHandler();
        StringReader reader = new StringReader(program);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);

        List<Statement> statements = parser.parse();

        Interpreter interpreter = new Interpreter();
        Resolver resolver = new Resolver(interpreter);

        if(!errorHandler.isError())
            resolver.resolve(statements);
        if(!errorHandler.isError())
            interpreter.interpret(statements);

        assertEquals(removeNewLine(expectedOutput.trim()), removeNewLine(outContent.toString().trim()));
        assertEquals(removeNewLine(expectedErrors.trim()), removeNewLine(errContent.toString().trim()));

    }

}

