package com.app.numplex.utils;

import org.apache.commons.math3.complex.Complex;

import java.util.Locale;

public class MathFunctions {

    public static double round(double x){
        return Double.parseDouble(String.format(Locale.ENGLISH,"%.12f", x));
    }

    public static Complex roundComplex(Complex complex){
        return new Complex(Double.parseDouble(String.format(Locale.ENGLISH,"%.12f", complex.getReal())),
                Double.parseDouble(String.format(Locale.ENGLISH,"%.12f", complex.getImaginary())));
    }
}
