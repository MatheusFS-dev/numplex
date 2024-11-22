package com.app.numplex.resistor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ResistorLogic {

    //Return value given the colors:
    public static Double[] colorToValue(int bands, List<String> list) {
        StringBuilder number = new StringBuilder();
        double tolerance = 0, coeficiente = 0;
        double multiplicador = 0;
        String cor;
        switch (bands) {
            case 4:
                for (int i = 0; i < 6; i++) {
                    cor = list.get(i);
                    switch (i) {
                        case 0:
                        case 1:
                            number.append(digit(cor));
                            break;
                        case 3:
                            multiplicador = multiplier(cor);
                            break;
                        case 4:
                            tolerance = tolerance(cor);
                            break;
                    }
                }
                break;
            case 5:
                for (int i = 0; i < 6; i++) {
                    cor = list.get(i);
                    switch (i) {
                        case 3:
                            multiplicador = multiplier(cor);
                            break;
                        case 4:
                            tolerance = tolerance(cor);
                            break;
                        case 5:
                            break;
                        default:
                            number.append(digit(cor));
                            break;
                    }
                }
                break;
            case 6:
                for (int i = 0; i < 6; i++) {
                    cor = list.get(i);
                    switch (i) {
                        case 3:
                            multiplicador = multiplier(cor);
                            break;
                        case 4:
                            tolerance = tolerance(cor);
                            break;
                        case 5:
                            coeficiente = coefficient(cor);
                            break;
                        default:
                            number.append(digit(cor));
                            break;
                    }
                }
                break;
        }
        double v = Double.parseDouble(number.toString()) * multiplicador;
        if (bands == 6)
            return new Double[]{v, tolerance, coeficiente};
        else
            return new Double[]{v, tolerance};
    }

    //Return color list given value:
    public static String[] valueToColor(int bands, double value) {
        String[] colors = new String[4];
        char[] valueChars;
        double multiplier;

        if(value == 0.0)
            throw new RuntimeException();

        String valueStr = String.valueOf(value);
        if(valueStr.contains("E")){
            String[] aux = valueStr.split("E");
            valueStr = aux[0];
        }

        if (bands == 4) {
            if(value > 99000000000.0 || value < 0.1 )
                throw new RuntimeException();

            multiplier = Double.parseDouble(String.format(Locale.ENGLISH,"%6.1e", value).split("e")[1]);
            valueStr = valueStr.replace(".0", "");
            valueStr = valueStr.replace("0.", "");
            valueStr = valueStr.replace(".", "");
            valueChars = valueStr.toCharArray();
            for (int i = 0; i < valueChars.length; i++)
                if (i > 1 && valueChars[i] != '0')
                    throw new RuntimeException();
            if (valueChars.length == 1)
                colors[1] = "black";
            else
                colors[1] = digitToColor(valueChars[1]);
            colors[0] = digitToColor(valueChars[0]);
            colors[2] = null;
            colors[3] = multiplierToColor4(multiplier);
        } else {
            if (value < 1 || value > 999000000000.0)
                throw new RuntimeException();
            multiplier = Double.parseDouble(String.format(Locale.ENGLISH,"%6.1e", value).split("e")[1]);
            valueStr = valueStr.replace(".0", "");
            valueStr = valueStr.replace(".", "");
            valueChars = valueStr.toCharArray();
            for (int i = 0; i < valueChars.length; i++)
                if (i > 2 && valueChars[i] != '0')
                    throw new RuntimeException();
            switch (valueChars.length) {
                case 1:
                    colors[1] = "black";
                    colors[2] = "black";
                    break;
                case 2:
                    colors[1] = digitToColor(valueChars[1]);
                    colors[2] = "black";
                    break;
                default:
                    colors[1] = digitToColor(valueChars[1]);
                    colors[2] = digitToColor(valueChars[2]);
                    break;
            }
            colors[0] = digitToColor(valueChars[0]);
            colors[3] = multiplierToColor5(multiplier);
        }

        //If cant find value do: valueToColor(bands, closestValue(value));

        return colors;
    }

    /*------------------------------------------------------------------------*/
    //Converting methods from color:
    private static double tolerance(String cor) {
        double tolerance = 0;
        switch (cor.toLowerCase()) {
            case "brown":
                tolerance = 1;
                break;
            case "red":
                tolerance = 2;
                break;
            case "orange":
                tolerance = 0.05;
                break;
            case "yellow":
                tolerance = 0.02;
                break;
            case "green":
                tolerance = 0.5;
                break;
            case "blue":
                tolerance = 0.25;
                break;
            case "violet":
                tolerance = 0.1;
                break;
            case "grey":
                tolerance = 0.01;
                break;
            case "gold":
                tolerance = 5;
                break;
            case "silver":
                tolerance = 10;
                break;
            case "none":
                tolerance = 20;
                break;
        }
        return tolerance;
    }

    private static double coefficient(String cor) {
        double coeficiente = 0;
        switch (cor.toLowerCase()) {
            case "black":
                coeficiente = 250;
                break;
            case "brown":
                coeficiente = 100;
                break;
            case "red":
                coeficiente = 50;
                break;
            case "orange":
                coeficiente = 15;
                break;
            case "yellow":
                coeficiente = 25;
                break;
            case "green":
                coeficiente = 20;
                break;
            case "blue":
                coeficiente = 10;
                break;
            case "violet":
                coeficiente = 5;
                break;
            case "grey":
                coeficiente = 1;
                break;
        }
        return coeficiente;
    }

    private static char digit(String cor) {
        char number = ' ';
        switch (cor.toLowerCase()) {
            case "black":
                number = '0';
                break;
            case "brown":
                number = '1';
                break;
            case "red":
                number = '2';
                break;
            case "orange":
                number = '3';
                break;
            case "yellow":
                number = '4';
                break;
            case "green":
                number = '5';
                break;
            case "blue":
                number = '6';
                break;
            case "violet":
                number = '7';
                break;
            case "grey":
                number = '8';
                break;
            case "white":
                number = '9';
                break;
        }
        return number;
    }

    private static double multiplier(String cor) {
        double multiplier = 0;
        switch (cor.toLowerCase()) {
            case "black":
                multiplier = 1;
                break;
            case "brown":
                multiplier = 10;
                break;
            case "red":
                multiplier = 100;
                break;
            case "orange":
                multiplier = 1000;
                break;
            case "yellow":
                multiplier = 10000;
                break;
            case "green":
                multiplier = 100000;
                break;
            case "blue":
                multiplier = 1000000;
                break;
            case "violet":
                multiplier = 10000000;
                break;
            case "grey":
                multiplier = 100000000;
                break;
            case "white":
                multiplier = 1000000000;
                break;
            case "gold":
                multiplier = 0.1;
                break;
            case "silver":
                multiplier = 0.01;
                break;
        }
        return multiplier;
    }

    private static String digitToColor(char value) {
        String color = "";
        switch (value) {
            case '0':
                color = "black";
                break;
            case '1':
                color = "brown";
                break;
            case '2':
                color = "red";
                break;
            case '3':
                color = "orange";
                break;
            case '4':
                color = "yellow";
                break;
            case '5':
                color = "green";
                break;
            case '6':
                color = "blue";
                break;
            case '7':
                color = "violet";
                break;
            case '8':
                color = "grey";
                break;
            case '9':
                color = "white";
                break;
        }
        return color;
    }

    private static String multiplierToColor4(double value) {
        String color = "";
        if (value == 0.0)
            color = "gold";
        else if (value == -1.0)
            color = "silver";
        else {
            value = Math.pow(10, value - 1);
            switch ((int) value) {
                case 1:
                    color = "black";
                    break;
                case 10:
                    color = "brown";
                    break;
                case 100:
                    color = "red";
                    break;
                case 1000:
                    color = "orange";
                    break;
                case 10000:
                    color = "yellow";
                    break;
                case 100000:
                    color = "green";
                    break;
                case 1000000:
                    color = "blue";
                    break;
                case 10000000:
                    color = "violet";
                    break;
                case 100000000:
                    color = "grey";
                    break;
                case 1000000000:
                    color = "white";
                    break;
            }
        }
        return color;
    }

    private static String multiplierToColor5(double value) {
        String color;
        if (value == 1.0)
            color = "gold";
        else if (value == 0.0)
            color = "silver";
        else {
            value = Math.pow(10, value - 2);
            switch ((int) value) {
                case 1:
                    color = "black";
                    break;
                case 10:
                    color = "brown";
                    break;
                case 100:
                    color = "red";
                    break;
                case 1000:
                    color = "orange";
                    break;
                case 10000:
                    color = "yellow";
                    break;
                case 100000:
                    color = "green";
                    break;
                case 1000000:
                    color = "blue";
                    break;
                case 10000000:
                    color = "violet";
                    break;
                case 100000000:
                    color = "grey";
                    break;
                default:
                    color = "white";
                    break;
            }
        }
        return color;
    }

    // Method to find closest commercial resistor:
    public static double closestValue(double value) {
        final List<Double> commercialResistors = Arrays.asList(1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2, 9.1, 10.0, 12.0, 15.0, 18.0, 22.0, 27.0, 33.0, 39.0, 47.0, 56.0, 68.0, 82.0, 91.0, 100.0, 120.0, 150.0, 180.0, 220.0, 270.0, 330.0, 390.0, 470.0, 560.0, 680.0, 820.0, 910.0, 1000.0, 1200.0, 1500.0, 1800.0, 2200.0, 2700.0, 3300.0, 3900.0, 4700.0, 5600.0, 6800.0, 8200.0, 9100.0, 10000.0, 12000.0, 15000.0, 18000.0, 22000.0, 27000.0, 33000.0, 39000.0, 47000.0, 56000.0, 68000.0, 82000.0, 91000.0, 91000.0, 100000.0, 120000.0, 150000.0, 180000.0, 220000.0, 270000.0, 330000.0, 390000.0, 470000.0, 560000.0, 680000.0, 820000.0, 910000.0, 1000000.0, 1200000.0, 1500000.0, 1800000.0, 2200000.0, 2700000.0, 3300000.0, 3900000.0, 4700000.0, 5600000.0, 6800000.0, 8200000.0, 9100000.0, 10000000.0, 12000000.0, 15000000.0, 18000000.0, 22000000.0);

        double distance = Math.abs(commercialResistors.get(0) - value);
        int idx = 0;
        for (int i = 1; i < commercialResistors.size(); i++) {
            double cdistance = Math.abs(commercialResistors.get(i) - value);
            if (cdistance < distance) {
                idx = i;
                distance = cdistance;
            }
        }
        return commercialResistors.get(idx);
    }
}
