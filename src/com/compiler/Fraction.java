package com.compiler;

public class Fraction {

    int nominator;
    int denominator;

    public Fraction(int nomin, int denomin){
        nominator = nomin;
        denominator = denomin;
    };

    public Fraction(String value){

    };

    void setNominator(int nomin){
        nominator = nomin;
    };

    void setDenominator(int denomin){
        denominator = denomin;
    };

    int getNominator(){
        return nominator;
    };

    int getDenominator(){
        return denominator;
    }

    int NWW(){
        return 1;
    };

    int NWD(){
        return 1;
    };

    // TODO
//    Fraction add (Fraction first, Fraction second) {
//        int newNominator;
//        int newDenominator;
//        return new Fraction();
//    }
//
//    Fraction substract(Fraction first, Fraction second) {
//        int newNominator;
//        int newDenominator;
//        return new Fraction();
//    }
//
//    Fraction multiply(Fraction first, Fraction second) {
//        int newNominator;
//        int newDenominator;
//        return new Fraction(first.getNominator()* second.getNominator(), first.getDenominator() * second.getDenominator());
//    }
//
//    Fraction divide(Fraction first, Fraction second) {
//        int newNominator;
//        int newDenominator;
//        return new Fraction();
//    }
}
