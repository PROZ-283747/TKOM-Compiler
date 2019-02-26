package com.compiler.lexer;

public class Position {

    private int line;
    private int column;

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int column() {
        return getColumn();
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void incrementColumn() {
        this.column++;
    }

    public void decrementColumn() {
        this.column--;
    }

    public int line() {
        return getLine();
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void incrementLine() {
        this.line++;
    }

}
