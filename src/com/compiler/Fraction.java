package com.compiler;

public class Fraction {

    int nominator;
    int denominator;

    public Fraction(int nomin, int denomin){
        if(denomin == 0){
            System.err.println("ERROR: It's  impossible to create fraction. Donominator cannot be 0 !");
            System.exit(-1);
        }
        nominator = nomin;
        denominator = denomin;
    };

    public Fraction(String value){
        int nomin = 0;
        int denomin = 0;
        int index = value.indexOf("%");
        if(index == -1) {
            nomin = Integer.parseInt(value);
            denomin = 1;
        }
        else{
            nomin = Integer.parseInt(value.substring(0, index));
            denomin = Integer.parseInt(value.substring(index+1, value.length()));
        }
        new Fraction(nomin, denomin);
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

    int NWW(int x, int y){
        return ((x*y)/NWD(x,y));
    };

    int NWD(int x, int y){
        while (x != y) {
            if (x > y)
                x -= y;
            else
                y -= x;
        }
        return x;
    };

    Fraction reduceFraction(Fraction fraction){
        int nwd = NWD(fraction.nominator, fraction.denominator);
        return new Fraction(fraction.nominator/nwd, fraction.denominator/nwd);
    }

    Fraction add (Fraction first, Fraction second) {
        int nww = NWW(first.denominator, second.denominator);

        int newNominator = first.nominator*(nww/first.denominator) + second.nominator*(nww/second.denominator);
        int newDenominator = nww;

        return reduceFraction(new Fraction(newNominator, newDenominator));
    }

    Fraction substract(Fraction first, Fraction second) {
        int nww = NWW(first.denominator, second.denominator);
        int newNominator = first.nominator*(nww/first.denominator) - second.nominator*(nww/second.denominator);
        int newDenominator = nww;

        return reduceFraction(new Fraction(newNominator, newDenominator));
    }

    Fraction multiply(Fraction first, Fraction second) {
        return reduceFraction(new Fraction(first.getNominator() * second.getNominator(), first.getDenominator() * second.getDenominator()));
    }

    Fraction divide(Fraction first, Fraction second) {
        return reduceFraction(new Fraction(first.getNominator() * second.getDenominator(), first.getDenominator() * second.getNominator()));
    }
}
