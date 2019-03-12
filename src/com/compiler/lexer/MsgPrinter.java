package com.compiler.lexer;

import java.io.IOException;

public class MsgPrinter {

    public void printToken(Token token){
        System.out.println("type: " + token.getType() + " \"" + token.getLexeme() + "\"" + " line: " + token.getLine() +" column: " + token.getColumn() + " signNumber: " + token.getSignNumber() );
    }

    public static void printFinalErrorInfo(int errorCount){
        System.err.println("You made " + errorCount + "error(s).");
    }

    public static void error(String msg, int line, int column, int signNumber, String path) {
        System.err.println(String.format("There is something wrong at line %d column %d: %s", line, column, msg));
        printLineWithError(path, line, column, signNumber);
    }

    public static void printLineWithError(String path, int line, int column, int signNumber){
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
