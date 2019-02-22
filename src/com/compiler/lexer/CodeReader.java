package com.compiler.lexer;

import java.io.*;

public class CodeReader {

    private final Position currentPos; // Position of recently read char
   //private final StringBuilder readBuffer; // Keeps what has been read but wasn't used yet.
    private File file;
    private BufferedReader reader;
    private boolean EOF = false;

    private char nextChar = '\0';  // character read by peek() it can be sometimes empty
    private char lastChar = '\0'; // last read char

    public CodeReader(Reader reader) {
        currentPos = new Position(1, 0);
        this.reader = new BufferedReader(reader);
        //this.readBuffer = new StringBuilder();
    }

    public CodeReader(String fileName){
        currentPos = new Position(1, 0);
        try {
            file = new File(fileName);
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
       // this.readBuffer = new StringBuilder();
    }


    public boolean isEOF() {
        return EOF;
    }

    public Position getPosition() {
        return currentPos;
    }

    public char getChar() throws IOException {
        char c;
        if(lastChar == '\0')
            c = (char) reader.read();
        else if()

        System.out.println(c);
        return c;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return 'c';
    }

}
