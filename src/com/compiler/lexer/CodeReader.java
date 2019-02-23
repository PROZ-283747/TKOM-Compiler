package com.compiler.lexer;

import java.io.*;

public class CodeReader{

    private final Position currentPos; // Position of recently read char
    private File file;
    private BufferedReader reader;
    private final boolean EOF = false;
    //private char nextChar = '\0';  // character read by peek() it can be sometimes empty
    private char currChar = '\0'; // sign to read first

    public CodeReader(Reader reader) {
        currentPos = new Position(1, 1);
        this.reader = new BufferedReader(reader);
    }

    public CodeReader(String fileName) throws IOException {
        currentPos = new Position(1, 1);
        try {
            file = new File(fileName);
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        currChar = (char)reader.read();   // TODO: is this correct ?
    }


    public boolean isEOF() {
        return EOF;
    }

    public Position getPosition() {
        return currentPos;
    }

    public char getChar(){
        //System.out.println("currChar: " + currChar);
        //System.out.println("nextChar: " + nextChar);

        if(currChar == '\n'){
            currentPos.incrementLine();
            currentPos.setColumn(0);
        }
        currentPos.incrementColumn();

        char c = currChar;
        try {
            currChar = (char) reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return c;
    }

    public char peek(){
        return currChar;
    }
}
