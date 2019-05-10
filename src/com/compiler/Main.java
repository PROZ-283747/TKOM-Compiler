package com.compiler;

import com.compiler.interpreter.variables.Fraction;
import com.compiler.lexer.*;
import com.compiler.parser.*;
import com.compiler.interpreter.*;
import java.io.IOException;
import java.util.List;

public class Main {

    private static void run(String path) throws IOException {

//        System.out.println("test");
//        Fraction r = new Fraction(4,5);
//        Fraction l = new Fraction(12,13);
//        Fraction result = l.add(r);
//        System.out.println(result.getNominator() + " " + result.getDenominator());
//        System.out.println(l.isEqual(r));
//        System.out.println(l.isGreater(r));
//        System.out.println(l.isGreaterEqual(r));
//        System.out.println(l.isLess(r));
//        System.out.println(l.isLessEqual(r));
//        System.out.println("koniec");
        ErrorHandler errorHandler = new ErrorHandler(); // todo: Can I pass path to error handler here ?
        CodeReader reader = new CodeReader(path);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);

        List<Statement> statements = parser.parse();

        errorHandler.stopIfError();

        Interpreter interpreter = new Interpreter();
        Resolver resolver = new Resolver(interpreter);

        resolver.resolve(statements);
        ErrorHandler.stopIfError();

        interpreter.interpret(statements);
        ErrorHandler.stopIfError();

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

