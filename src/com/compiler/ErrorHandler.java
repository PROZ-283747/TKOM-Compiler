package com.compiler;

import com.compiler.lexer.CodeReader;
import com.compiler.lexer.Token;
import java.io.IOException;

public class ErrorHandler {

    public static int lexerErrorCount = 0;
    public static int parserErrorCount = 0;
    public static int interpreterErrorCount = 0;
    public static int resolverErrorCount = 0;

    public void printToken(Token token){
        System.out.println("type: " + token.getType() + " \"" + token.getLexeme() + "\"" + " line: " + token.getLine() +" column: " + token.getColumn() + " signNumber: " + token.getSignNumber() );
    }

    public static void printLexerError(String msg, Token token, String path){
        System.err.println(String.format("Error while scanning at line %d column %d: %s ", token.getLine(), token.getColumn(), msg));
        ++lexerErrorCount;
        printLineWithError(path, token.getLine(), token.getColumn(), token.getSignNumber());
    }

    public static void printParserError(String msg, Token token){
        System.err.println(String.format("Error while parsing at line %d column %d: %s ", token.getLine(), token.getColumn(), msg));
        ++parserErrorCount;
    }

    public static void printInterpreterError(String msg, Token token){
        System.err.println(String.format("Error while interpreting at line %d column %d: %s ", token.getLine(), token.getColumn(), msg));
        ++interpreterErrorCount;
    }

    public static void printResolverError(String msg, int line, int column){
        System.err.println(String.format("Error while resolving: " + msg + " Line: " + line + " Column: " + column));
        ++resolverErrorCount;
    }

    public static void printNoErrorMsg(){
        System.out.println("Perfect! There are no errors in your code. :) ");
    }

    public static void stopIfError() {
        if (lexerErrorCount > 0) {
            System.err.println(String.format("Oh no! While scanning %d error(s) have been found in your code :(. ", lexerErrorCount));
            System.exit(1);
        }
        if (parserErrorCount > 0) {
            System.err.println(String.format("Oh no! While parsing %d error(s)have been found in your code :(. ", parserErrorCount));
            System.exit(1);
        }
        if (resolverErrorCount > 0) {
            System.err.println(String.format("Oh no! While resolving %d error(s) in your code :(. ", resolverErrorCount));
            System.exit(1);
        }
        if (interpreterErrorCount > 0) {
            System.err.println(String.format("Oh no! While interpreting %d error(s) in your code :(. ", interpreterErrorCount));
            System.exit(1);
        }
    }

    public static void resetErrors(){
        lexerErrorCount = 0;
        parserErrorCount = 0;
        interpreterErrorCount = 0;
        resolverErrorCount = 0;
    }

    public static void printLineWithError(String path, int line, int column, int signNumber){
        if(path == null)
            return;
        try {
            char c = '\0';
            int currLine = 1;
            CodeReader reader = new CodeReader(path);

            while(currLine < line) {
                do{
                    c = reader.getChar();
                }while(c != '\n');
                currLine++;
            }

            c = reader.getChar();
            while(c != '\n'){
                System.out.print(c);
                c = reader.getChar();
            }

            System.out.println();
            for(int i =0; i < column-1 ; i++){
                System.out.print(' ');
            }
            System.out.println('^');

        } catch (IOException e) {
            e.printStackTrace();
        }
        // coś tam coś tam TU_BLAD coś tam dalej
        //                 ^
    }
}
