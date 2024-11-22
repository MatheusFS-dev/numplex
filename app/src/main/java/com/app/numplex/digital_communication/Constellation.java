package com.app.numplex.digital_communication;

import org.apache.commons.math3.special.Erf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

class Constellation {
    // Class parameter for the number of decimal places for rounding
    private static int decimalPlaces = 5;

    // ArrayList to store the points (symbols) of the constellation
    private static final ArrayList<Point> points = new ArrayList<>();

    // Static method to add a symbol to the constellation
    public static void addSymbol(Point symbol) {
        points.add(symbol);
    }

    // Static method to clear all symbols from the constellation
    public static void clearSymbols() {
        points.clear();
    }

    // Static method to calculate the energy of a symbol in the constellation
    public static double calculateEnergy(Point symbol) {
        double energy = symbol.x * symbol.x + symbol.y * symbol.y;
        return round(energy, decimalPlaces);
    }

    // Static method to calculate the distance between two symbols
    public static double calculateDistance(Point symbol1, Point symbol2) {
        double distance = Math.sqrt(calculateDistanceSquared(symbol1, symbol2));
        return round(distance, decimalPlaces);
    }

    // Static method to calculate the distance between a symbol and the origin (0,0)
    public static double calculateDistanceToOrigin(Point symbol) {
        double distance = Math.sqrt(symbol.x * symbol.x + symbol.y * symbol.y);
        return round(distance, decimalPlaces);
    }

    // Function to calculate the union bound for the entire constellation of points
    public static double calculateUnionBounding(Point[] symbols, double[] symbolProbabilities, double noisePower) {
        int M = symbols.length;
        double maxUnionBounding = 0;

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                if (i != j) {
                    double innerSum = 0;

                    for (int k = 0; k < M; k++) {
                        if (k != i) {
                            double Dik = calculateDistance(symbols[i], symbols[k]);
                            innerSum += symbolProbabilities[k] * Erf.erfc(Dik / (2 * Math.sqrt(noisePower)));
                        }
                    }

                    double symbolErrorProbability = symbolProbabilities[i] * innerSum;

                    if (symbolErrorProbability > maxUnionBounding) {
                        maxUnionBounding = symbolErrorProbability;
                    }
                }
            }
        }

        maxUnionBounding *= 0.5;
        return round(maxUnionBounding, decimalPlaces);
    }

    // Private static method to calculate the squared distance between two points
    private static double calculateDistanceSquared(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return dx * dx + dy * dy;
    }

    // Method to round a double value to a specified number of decimal places
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Main method for testing
    public static void main(String[] args) {
        // Example of creating points (symbols) and calculating metrics
        Point symbol1 = new Point(2, 1);
        Point symbol2 = new Point(3, 4);
        Point symbol3 = new Point(1, 1);

        addSymbol(symbol1);
        addSymbol(symbol2);
        addSymbol(symbol3);

        // Display the results of the calculations
        System.out.println("Energy of symbol 1: " + calculateEnergy(symbol1));
        System.out.println("Distance between symbol 1 and symbol 2: " + calculateDistance(symbol1, symbol2));
        System.out.println("Distance between symbol 3 and the origin: " + calculateDistanceToOrigin(symbol3));

        // Calculate the union bound for a specific symbol (symbol1)
        double[] symbolProbabilities = {0.5, 0.3, 0.2};
        double noisePower = 0.1;
        Point[] symbols = {symbol1, symbol2, symbol3};

        // Calculate the union bound for the entire constellation
        double unionBounding = calculateUnionBounding(symbols, symbolProbabilities, noisePower);

        // Display the result
        System.out.println("Union bound for the entire constellation: " + unionBounding);
    }
}

