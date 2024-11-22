package com.app.numplex.queue;

public class MM1InfiniteFIFOQueue {
    // Método para calcular o fator de utilização a partir das taxas de entrada (lambda) e saída (mu)
    public static double calculateUtilizationFactor(double lambda, double mu) {
        return lambda / mu;
    }

    // Método para calcular a probabilidade de fila vazia a partir das taxas de entrada (lambda) e saída (mu)
    public static double getEmptyQueueProbability(double lambda, double mu) {
        return 1 - calculateUtilizationFactor(lambda, mu);
    }

    // Método para calcular a probabilidade de K clientes na fila a partir das taxas de entrada (lambda) e saída (mu)
    public static double getProbabilityKCustomers(int K, double lambda, double mu) {
        return Math.pow(calculateUtilizationFactor(lambda, mu), K) * getEmptyQueueProbability(lambda, mu);
    }

    // Método para calcular o número médio na fila (E(q)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageNumberInQueue(double lambda, double mu) {
        return calculateUtilizationFactor(lambda, mu) / (1 - calculateUtilizationFactor(lambda, mu));
    }

    // Método para calcular o número médio no sistema (E(s)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageNumberInSystem(double lambda, double mu) {
        return calculateUtilizationFactor(lambda, mu);
    }

    // Método para calcular o número médio de espera (E(w)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageNumberInWait(double lambda, double mu) {
        return Math.pow(calculateUtilizationFactor(lambda, mu), 2) / (1 - calculateUtilizationFactor(lambda, mu));
    }

    // Método para calcular o tempo médio de espera na fila (E(tq)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageTimeInQueue(double lambda, double mu) {
        return getAverageNumberInQueue(lambda, mu) / lambda;
    }

    // Método para calcular o tempo médio no sistema (E(ts)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageTimeInSystem(double lambda, double mu) {
        return getAverageNumberInSystem(lambda, mu) / lambda;
    }

    // Método para calcular o tempo médio de espera (E(tw)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageTimeInWait(double lambda, double mu) {
        return getAverageNumberInWait(lambda, mu) / lambda;
    }

    public static void main(String[] args) {
        double lambda = 2.0;  // Example arrival rate
        double mu = 3.0;      // Example service rate

        System.out.println("Empty Queue Probability: " + MM1InfiniteFIFOQueue.getEmptyQueueProbability(lambda, mu));
        System.out.println("Probability of 2 Customers: " + MM1InfiniteFIFOQueue.getProbabilityKCustomers(2, lambda, mu));
        System.out.println("Average Number in Queue: " + MM1InfiniteFIFOQueue.getAverageNumberInQueue(lambda, mu));
        System.out.println("Average Number in System: " + MM1InfiniteFIFOQueue.getAverageNumberInSystem(lambda, mu));
        System.out.println("Average Number in Wait: " + MM1InfiniteFIFOQueue.getAverageNumberInWait(lambda, mu));
        System.out.println("Average Time in Queue: " + MM1InfiniteFIFOQueue.getAverageTimeInQueue(lambda, mu));
        System.out.println("Average Time in System: " + MM1InfiniteFIFOQueue.getAverageTimeInSystem(lambda, mu));
        System.out.println("Average Time in Wait: " + MM1InfiniteFIFOQueue.getAverageTimeInWait(lambda, mu));
    }
}