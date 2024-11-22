package com.app.numplex.digital;

public class BinaryComplements {

    // Java program to print 1's and 2's complement of
    // a binary number
    // Returns '0' for '1' and '1' for '0'
    static char flip(char c) {
        return (c == '0') ? '1' : '0';
    }

    public static String firstComplement(String binary, int bits) {
        boolean itsNegative = false;

        if (binary.contains("."))
            throw new RuntimeException();

        if (binary.startsWith("-")) {
            binary = binary.replaceFirst("-", "");
            itsNegative = true;
            bits -= 1;
        }

        if (binary.length() > bits)
            throw new RuntimeException();

        StringBuilder binaryBuilder = new StringBuilder(binary);
        while (bits > binaryBuilder.length())
            binaryBuilder.insert(0, "0");
        binary = binaryBuilder.toString();

        StringBuilder firstComplement = new StringBuilder();

        // for firstComplement complement flip every bit
        for (int i = 0; i < binary.length(); i++) {
            firstComplement.append(flip(binary.charAt(i)));
        }

        if (itsNegative)
            return ("-" + firstComplement);
        else
            return firstComplement.toString();
    }

    public static String secondComplement(String binary, int bits) {
        StringBuilder secondComplement = new StringBuilder(binary);

        if (binary.contains("."))
            throw new RuntimeException();

        if (binary.startsWith("-") && binary.length() - 1 > bits)
            throw new RuntimeException();
        else if (binary.startsWith("-") && binary.length() > bits)
            throw new RuntimeException();
        else if (!binary.startsWith("-") && binary.length() >= bits)
            throw new RuntimeException();


        if (binary.startsWith("-")) {
            secondComplement = new StringBuilder(firstComplement(secondComplement.toString(), bits));
            secondComplement = new StringBuilder(secondComplement.toString().replaceFirst("-", ""));

            double numeroDecimal = Double.parseDouble(
                    ConversionLogic.BinToDecimal(secondComplement + ".0")) +
                    1;
            secondComplement = new StringBuilder(ConversionLogic.DecimalFloatToBinary(String.valueOf(numeroDecimal)));

            while (bits - 1 > secondComplement.length())
                secondComplement.insert(0, "0");

            secondComplement.insert(0, "1");
        } else
            while (bits > secondComplement.length())
                secondComplement.insert(0, "0");

        return secondComplement.toString();
    }

    public static String secondComplementReverse(String binary, int bits) {
        boolean itsNegative = false;
        if (binary.contains("."))
            throw new RuntimeException();

        if (binary.length() > bits)
            throw new RuntimeException();

        if (binary.startsWith("1"))
            itsNegative = true;

        StringBuilder decimal = new StringBuilder(binary);

        if (itsNegative) {
            decimal = new StringBuilder(firstComplement(decimal.toString(), bits));

            double numeroDecimal = Double.parseDouble(
                    ConversionLogic.BinToDecimal(decimal + ".0")) +
                    1;
            decimal = new StringBuilder(ConversionLogic.DecimalFloatToBinary(String.valueOf(numeroDecimal)));

            while (bits - 1 > decimal.length())
                decimal.insert(0, "0");

            return ("-" + decimal);
        }else
            return decimal.toString();
    }
}

