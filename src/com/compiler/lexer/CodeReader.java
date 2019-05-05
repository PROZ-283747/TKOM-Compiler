package com.compiler.lexer;

import java.io.*;

public class CodeReader implements Source{

    private final Position currentPos; // Position of recently read token
    private File file;
    private BufferedReader reader;
    private final boolean EOF = false;
    private char currChar = '\0'; // next sign to read and process

    public CodeReader(Reader reader) {
        currentPos = new Position(1, 0, 0);
        this.reader = new BufferedReader(reader);
    }

    public CodeReader(String fileName) throws IOException {
        currentPos = new Position(1, 0, 0);
        try {
            file = new File(fileName);
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        currChar = (char)reader.read();
    }

    @Override
    public boolean isEOF() {
        return EOF;
    }

    @Override
    public Position getPosition() {
        return currentPos;
    }

    @Override
    public char getChar(){

        if(currChar == '\n'){
            currentPos.incrementLine();
            currentPos.setColumn(-1);
        }
        currentPos.incrementColumn();

        currentPos.incrementSignNumber();
        char c = currChar;

        try {
            currChar = (char) reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public char peek(){
        return currChar;
    }
}
