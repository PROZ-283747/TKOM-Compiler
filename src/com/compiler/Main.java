package com.compiler;

import com.compiler.lexer.*;
import com.compiler.parser.*;
import com.compiler.interpreter.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    //static ErrorHandler errorHandler = new ErrorHandler();

    private static void run(String path) throws IOException {
        ErrorHandler errorHandler = new ErrorHandler(); // todo: Can I pass path to error handler here ?
        CodeReader reader = new CodeReader(path);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);

        List<Statement> statements = parser.parse();

        errorHandler.stopIfError();

        Interpreter interpreter = new Interpreter();
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
//        ErrorHandler.stopIfError();
//        interpreter.interpret(statements);
//        ErrorHandler.stopIfError();

        errorHandler.printNoErrorMsg();


    }

    public static void main (String[]args) throws IOException {
            if (args.length == 1) {
                run(args[0]);
            } else {
                System.out.println("Usage: smh fpath");
            }
        }
}

