package com.app.numplex.queue;

public class MMm0mSFIFOQueue {
    public static double calculateFactorial(int n) {
        double result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }


    public static double calculateUtilizationFactor(double lambda, double mu, int m) {
        return lambda / mu;
    }

    public static double getEmptyQueueProbability(double lambda, double mu, int m, int S) { // correto
        double utilizationFactor = calculateUtilizationFactor(lambda, mu, m);
        double sum = 0;
        for (int n = 0; n < m; n++) {
            sum += 1 / (Math.pow(utilizationFactor, n) * (calculateFactorial(S) / (calculateFactorial(S - n) * calculateFactorial(n))));
        }
        return sum;
    }

    public static double getProbabilityKCustomers(int K, double lambda, double mu, int m, int S) { // correto
        double utilizationFactor = calculateUtilizationFactor(lambda, mu, m);
        double emptyQueueProb = getEmptyQueueProbability(lambda, mu, m, S);
        // Verifica se K está dentro dos limites da capacidade da fila
        if (K >= 0 && K <= S) {
            return Math.pow(utilizationFactor, K) * emptyQueueProb * (calculateFactorial(S) / (calculateFactorial(S - K) * calculateFactorial(K)));
        } else {
            return 0.0; // Probabilidade de K fora dos limites da capacidade da fila é zero
        }
    }

    public static double getAverageNumberInQueue(double lambda, double mu, int m, int S) {
        double utilizationFactor = calculateUtilizationFactor(lambda, mu, m);
        double emptyQueueProb = getEmptyQueueProbability(lambda, mu, m, S);
        return utilizationFactor * emptyQueueProb / (1 - Math.pow(S / (m + 1), m - m * utilizationFactor));
    }

    public static double getAverageNumberInSystem(double lambda, double mu, int m, int S) { // correto
        return calculateLambdaE(lambda, mu, m, S) / mu;
    }

    public static double getAverageNumberInWait(double lambda, double mu, int m, int S) {
        return getAverageNumberInQueue(lambda, mu, m, S) - lambda / mu;
    }

    public static double getAverageTimeInQueue(double lambda, double mu, int m, int S) {
        return getAverageNumberInQueue(lambda, mu, m, S) / lambda;
    }

    public static double getAverageTimeInSystem(double mu) { // correto
        return 1 / mu;
    }

    public static double calculateLambdaE(double lambda, double mu, int m, int S) { // correto
        double sum = 0;
        for (int n = 0; n < m; n++) {
            sum += (S - n) * lambda * getProbabilityKCustomers(n, lambda, mu, m, S);
        }
        return sum;
    }

    public static double getAverageTimeInWait(double lambda, double mu, int m, int S) {
        return getAverageNumberInWait(lambda, mu, m, S) / lambda;
    }

    public static void main(String[] args) {
        double lambda = 4.0;
        double mu = 2.0;
        int m = 3;
        int S = 5;
        int K = 3;

        System.out.println("Empty Queue Probability: " + getEmptyQueueProbability(lambda, mu, m, S));
        System.out.println("K Queue Probability: " + getProbabilityKCustomers(K, lambda, mu, m, S));
        System.out.println("Average Number in Queue: " + getAverageNumberInQueue(lambda, mu, m, S));
        System.out.println("Average Number in System: " + getAverageNumberInSystem(lambda, mu, m, S));
        System.out.println("Average Number in Wait: " + getAverageNumberInWait(lambda, mu, m, S));
        System.out.println("Average Time in Queue: " + getAverageTimeInQueue(lambda, mu, m, S));
        System.out.println("Average Time in System: " + getAverageTimeInSystem(mu));
        System.out.println("Average Time in Wait: " + getAverageTimeInWait(lambda, mu, m, S));
    }
}