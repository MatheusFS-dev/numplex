package com.app.numplex.complex_calculator;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public class NumplexComplex {
    private final double real; //Real part
    private final double imaginary; //Imaginary part
    private static double polarunit = 57.296;
    private static String unit = "º";

    //Constructor
    public NumplexComplex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    //Return a string representation
    @NonNull
    public String toString() {
        String real_part = String.valueOf(real);
        String img_part = String.valueOf(imaginary);

        real_part = real_part.replace(".0E", "E");
        img_part = img_part.replace(".0E", "E");

        if(real_part.endsWith(".0"))
            real_part = real_part.replace(".0", "");
        if(img_part.endsWith(".0"))
            img_part = img_part.replace(".0", "");

        if (imaginary == 0) return real_part + "";
        if (real == 0) return img_part + "i";
        if (imaginary <  0) {
            img_part = img_part.replaceFirst("-", "");
            return real_part + " - " + img_part + "i";
        }
        return real_part + " + " + img_part + "i";
    } //3 + 3i

    //Return modulus
    public double abs() {
        return Math.hypot(real, imaginary);
    }

    //Return angle
    public double phase() {
        return Math.atan2(imaginary, real);
    }

    //Return (a + b)
    public static NumplexComplex plus(NumplexComplex a, NumplexComplex b) {
        BigDecimal real = BigDecimal.valueOf(a.real).add(BigDecimal.valueOf(b.real));
        BigDecimal imag = BigDecimal.valueOf(a.imaginary).add(BigDecimal.valueOf(b.imaginary));

        return new NumplexComplex(real.doubleValue(), imag.doubleValue());
    }

    //Return (a - b)
    public static NumplexComplex minus(NumplexComplex a, NumplexComplex b) {
        BigDecimal real = BigDecimal.valueOf(a.real).subtract(BigDecimal.valueOf(b.real));
        BigDecimal imag = BigDecimal.valueOf(a.imaginary).subtract(BigDecimal.valueOf(b.imaginary));

        return new NumplexComplex(real.doubleValue(), imag.doubleValue());
    }

    //Return (a * b)
    public static NumplexComplex times(NumplexComplex a, NumplexComplex b) {
        BigDecimal real = BigDecimal.valueOf(a.real).multiply(BigDecimal.valueOf(b.real))
                .subtract(BigDecimal.valueOf(a.imaginary).multiply(BigDecimal.valueOf(b.imaginary)));
        BigDecimal imag = BigDecimal.valueOf(a.real).multiply(BigDecimal.valueOf(b.imaginary))
                .add(BigDecimal.valueOf(a.imaginary).multiply(BigDecimal.valueOf(b.real)));
        double re = real.doubleValue();
        double img = imag.doubleValue();

        if (Math.abs(re) < 1e-12) {
            re = 0.0;
        }
        if (Math.abs(img) < 1e-12) {
            img = 0.0;
        }

        return new NumplexComplex(re, img);
    }

    //Return a new Complex object whose value is the reciprocal of this
    public NumplexComplex reciprocal() {
        double scale = real * real + imaginary * imaginary;
        return new NumplexComplex(real / scale, -imaginary / scale);
    }

    //Return (a / b)
    public static NumplexComplex divides(NumplexComplex a, NumplexComplex b) {
        return NumplexComplex.times(a, b.reciprocal());
    }

    //Polar form
    public static String polar(NumplexComplex a) {
        BigDecimal value = BigDecimal.valueOf(a.abs());
        String module = String.valueOf(a.abs());
        String angle = String.valueOf(Math.round((a.phase()*polarunit)*1000.0)/1000.0);
        if(angle.endsWith(".0"))
            angle = angle.replace(".0", "");

        if(module.contains("E"))
            module = scientificFormat(String.format(Locale.ENGLISH,"%.4E", value));
        else {
            module = String.valueOf(Math.round(a.abs() * 1000.0) / 1000.0);
            if(module.endsWith(".0"))
                module = module.replace(".0", "");
        }

        return module + "∠" + angle + unit;
    }

    //Trigonometric form
    public static String trigonometric(NumplexComplex a) {
        BigDecimal value = BigDecimal.valueOf(a.abs());
        double phase = Math.round((a.phase()*polarunit)*1000.0)/1000.0;
        String module = String.valueOf(a.abs());
        String angle;
        String local_unit = unit;

        if(a.phase() < 0)
            phase = phase*(-1.0);
        angle = String.valueOf(phase);

        if(angle.endsWith(".0"))
            angle = angle.replace(".0", "");

        if(Objects.equals(unit, "rad"))
            local_unit = "";

        if(module.contains("E"))
            module = scientificFormat(String.format(Locale.ENGLISH,"%.4E", value));
        else {
            module = String.valueOf(Math.round(a.abs() * 1000.0) / 1000.0);
            if(module.endsWith(".0"))
                module = module.replace(".0", "");
        }

        if (a.phase() < 0)
            return module + "[cos(" + angle + local_unit + ") - isin(" + angle + local_unit + ")]";
        else
            return module + "[cos(" + angle + local_unit + ") + isin(" + angle + local_unit + ")]";
    }

    //Exponential form
    public static String exponential(NumplexComplex a) {
        BigDecimal value = BigDecimal.valueOf(a.abs());
        double phase = Math.round((a.phase()*polarunit)*1000.0)/1000.0;
        String module = String.valueOf(a.abs());
        String angle;
        String local_unit = unit;

        if(a.phase() < 0)
            phase = phase*(-1.0);
        angle = String.valueOf(phase);

        if(angle.endsWith(".0"))
            angle = angle.replace(".0", "");

        if(Objects.equals(unit, "rad"))
            local_unit = "";

        if(module.contains("E"))
            module = scientificFormat(String.format(Locale.ENGLISH,"%.4E", value));
        else {
            module = String.valueOf(Math.round(a.abs() * 1000.0) / 1000.0);
            if(module.endsWith(".0"))
                module = module.replace(".0", "");
        }

        if(a.phase() < 0)
            return module + "e<sup><small>-i" + angle + local_unit + "</small></sup>";
        else
            return module + "e<sup><small>i" + angle + local_unit + "</small></sup>";
    }

    //Getters
    public double getReal() {
        return real;
    }
    public double getImaginary() {
        return imaginary;
    }
    public static double getPolarunit() {return polarunit;}

    //Setters
    public static void setPolarunit(double polarunit) {
        NumplexComplex.polarunit = polarunit;
        if (polarunit == 1)
            unit = "rad";
        else
            unit = "º";
    }

    //Converting to scientific format:
    public static String scientificFormat(String value){
        char[] number = value.toCharArray();

        String numbers;
        String exp;

        StringBuilder data_numbers = new StringBuilder();
        StringBuilder data_exp = new StringBuilder();
        StringBuilder output = new StringBuilder();

        int expo = 0;

        int j = 0;
        while (j < number.length) {
            if (number[j] == 'E') {
                numbers =  value.substring(0, j-1);
                exp = value.substring(j + 1, number.length);
                data_numbers = new StringBuilder(numbers);
                data_numbers.append("x10");

                data_exp.append(exp);
                expo = Integer.parseInt(data_exp.toString());
                break;
            }
            j++;
        }
        output.append(data_numbers);
        output.append(ComplexCalculatorActivity.exponent(expo));

        return output.toString();
    }
}