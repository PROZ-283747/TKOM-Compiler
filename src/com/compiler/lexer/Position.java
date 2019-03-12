package com.compiler.lexer;


public class Position {

    private int line;
    private int column;
    private int signNumber;

    public Position(int line, int column, int num) {
        this.line = line;
        this.column = column;
        this.signNumber = num;
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

    public int signNumber(){
        return getSignNumber();
    }

    public int getSignNumber() {
        return signNumber;
    }

    public void setSignNumber(int num) {
        this.signNumber = num;
    }

    public void incrementLine() {
        this.line++;
    }

    public void incrementSignNumber(){
        this.signNumber++;
    }

    public void decrementSignNumber(){
        this.signNumber--;
    }

}
