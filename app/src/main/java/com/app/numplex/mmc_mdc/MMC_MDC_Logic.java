package com.app.numplex.mmc_mdc;

public class MMC_MDC_Logic {
    public static double findGCF(double[] numbers) {
        double result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = findGCF(result, numbers[i]);
        }
        return result;
    }

    public static double findLCM(double[] numbers) {
        double result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = (result * numbers[i]) / findGCF(result, numbers[i]);
        }
        return result;
    }

    public static double findGCF(double a, double b) {
        if (b == 0) {
            return a;
        }
        return findGCF(b, a % b);
    }
}
