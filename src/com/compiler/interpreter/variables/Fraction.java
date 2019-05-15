package com.compiler.interpreter.variables;

import com.sun.javaws.exceptions.ErrorCodeResponseException;

public class Fraction extends Variable {

    int nominator;
    int denominator;

    public Fraction(int nomin, int denomin){
        if(denomin == 0){
            System.err.println("ERROR: It's  impossible to create fraction. Denominator cannot be 0 !");
            System.exit(-1);
        }
        nominator = nomin;
        denominator = denomin;

    };

    public Fraction(String value){
        int nomin;
        int denomin;
        int index = value.indexOf("%");
        if(value.isEmpty()){
            nominator = 0;
            denominator = 1;
            return;
        }
        if(index == -1) {
            nomin = Integer.parseInt(value);
            denomin = 1;
        }
        else{
            nomin = Integer.parseInt(value.substring(0, index));
            denomin = Integer.parseInt(value.substring(index+1, value.length()));
        }
        if(denomin == 0){
            System.err.println("ERROR: It's impossible to create fraction. Denominator cannot be 0 !");
            System.exit(-1);
        }
        nominator = nomin;
        denominator = denomin;
    };

    public void setNominator(int nomin){
        nominator = nomin;
    };

    public void setDenominator(int denomin){
        denominator = denomin;
    };

    public int getNominator(){
        return nominator;
    };

    public int getDenominator(){
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

    Fraction toGivenDenominator(Fraction fraction, int newDenominator){
        if(fraction.denominator < newDenominator) {
            if (newDenominator % fraction.denominator != 0)
                throw new Error("When getting fraction to given denominator, it is need to be divisible completely!");
            int num = newDenominator / fraction.denominator;
            return new Fraction(fraction.nominator * num, newDenominator);
        } else{
            if (fraction.denominator % newDenominator!= 0)
                throw new Error("When getting fraction to given denominator, it is need to be divisible completely!");
            int num = fraction.denominator /  newDenominator;
            return new Fraction(fraction.nominator / num, newDenominator);
        }
    }

    public Fraction add(Fraction right) {
        int nww = NWW(this.denominator, right.denominator);

        int newNominator = this.nominator*(nww/this.denominator) + right.nominator*(nww/right.denominator);
        int newDenominator = nww;

        return reduceFraction(new Fraction(newNominator, newDenominator));
    }

    public Fraction substract(Fraction right) {
        int nww = NWW(this.denominator, right.denominator);
        int newNominator = this.nominator*(nww/this.denominator) - right.nominator*(nww/right.denominator);
        int newDenominator = nww;

        return reduceFraction(new Fraction(newNominator, newDenominator));
    }

    public Fraction multiply(Fraction right) {
        return reduceFraction(new Fraction(this.getNominator() * right.getNominator(), this.getDenominator() * right.getDenominator()));
    }

    public Fraction divide(Fraction right) {
        return reduceFraction(new Fraction(this.getNominator() * right.getDenominator(), this.getDenominator() * right.getNominator()));
    }

    public boolean isEqual(Fraction right){
        if(reduceFraction(this).getNominator() == reduceFraction(right).getNominator() &&
                reduceFraction(this).getDenominator() == reduceFraction(right).getDenominator())
            return true;

        return false;
    }

    public boolean isGreater(Fraction right){
        Fraction reducedLeft = reduceFraction(this);
        Fraction reducedRight = reduceFraction(right);
        int nww = NWW(reducedLeft.denominator, reducedRight.denominator);

        if(toGivenDenominator(this,nww).nominator > toGivenDenominator(right, nww).nominator)
            return true;

        return false;
    }

    public boolean isGreaterEqual(Fraction right){
        Fraction reducedLeft = reduceFraction(this);
        Fraction reducedRight = reduceFraction(right);
        int nww = NWW(reducedLeft.denominator, reducedRight.denominator);

        if(toGivenDenominator(this,nww).nominator >= toGivenDenominator(right, nww).nominator)
            return true;

        return false;
    }

    public boolean isLess(Fraction right){
        Fraction reducedLeft = reduceFraction(this);
        Fraction reducedRight = reduceFraction(right);
        int nww = NWW(reducedLeft.denominator, reducedRight.denominator);

        if(toGivenDenominator(this,nww).nominator < toGivenDenominator(right, nww).nominator)
            return true;

        return false;
    }

    public boolean isLessEqual(Fraction right){
        Fraction reducedLeft = reduceFraction(this);
        Fraction reducedRight = reduceFraction(right);
        int nww = NWW(reducedLeft.denominator, reducedRight.denominator);

        if(toGivenDenominator(this,nww).nominator <= toGivenDenominator(right, nww).nominator)
            return true;

        return false;
    }
    public Fraction changeSign(){
        return new Fraction(-this.getNominator(), this.getDenominator());
    }

    @Override
    public String toString() {
        return nominator + "%" + denominator;
    }
}
