package com.compiler;

import com.compiler.lexer.*;
import com.compiler.parser.*;
import com.compiler.interpreter.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    static ErrorHandler errorHandler = new ErrorHandler();

    private static void run(String path) throws IOException {
        CodeReader reader = new CodeReader(path);
        Lexer lexer = new Lexer(reader);

        List<Token> tokens = new LinkedList<>();
        Token token = lexer.getToken();
        do {
            if (token.getType() == TokenType.ERROR) {
                ErrorHandler.printLexerError("ERROR token. ", token, path);
            } else {
                tokens.add(token);
            }
            errorHandler.printToken(token);
            token = lexer.getToken();

        } while (token.getType() != TokenType.EOF);

        errorHandler.printToken(token);
        tokens.add(token);  // Add EOF token

        ErrorHandler.stopIfError();

        // todo:
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parse();

        ErrorHandler.stopIfError();

//        Interpreter interpreter = new Interpreter();
//        Resolver resolver = new Resolver(interpreter);
//        resolver.resolve(statements);
//
//        ErrorHandler.stopIfError();
//
//        interpreter.interpret(statements);

        ErrorHandler.stopIfError();
        ErrorHandler.printNoErrorMsg();


    }

    public static void main (String[]args) throws IOException {
            if (args.length == 1) {
                run(args[0]);
            } else {
                System.out.println("Usage: smh fpath");
            }
        }

    // for testing purposes
    private static void printTokens(List<Token> tokens){
        for(int i = 0; i <tokens.size(); ++i) {
            System.out.println(tokens.get(i).getType());
        }
    }
}

