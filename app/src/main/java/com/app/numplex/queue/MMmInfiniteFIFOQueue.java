package com.app.numplex.queue;

public class MMmInfiniteFIFOQueue {

    // Método para calcular o fator de utilização a partir das taxas de entrada (lambda) e saída (mu)
    public static double calculateUtilizationFactor(double lambda, double mu) {
        return lambda / mu;
    }

    // Método para calcular a probabilidade de fila vazia a partir das taxas de entrada (lambda) e saída (mu)
    public static double getEmptyQueueProbability(double lambda, double mu, int m) {
        double utilization = calculateUtilizationFactor(lambda, mu);
        double sum = 0;
        for (int i = 0; i < m; i++) {
            sum += Math.pow(utilization, i) / factorial(i);
        }
        return 1 / (sum + (Math.pow(utilization, m) / (factorial(m) * (1 - (utilization / m)))));
    }

    // Método para calcular a probabilidade de K clientes na fila a partir das taxas de entrada (lambda) e saída (mu)
    public static double getProbabilityKCustomers(int K, double lambda, double mu, int m) {
        double utilization = calculateUtilizationFactor(lambda, mu);
        double emptyQueueProb = getEmptyQueueProbability(lambda, mu, m);
        if(K <= m){
            return (Math.pow(utilization, K) * emptyQueueProb) / factorial(K);
        }
        else{
            return (Math.pow(utilization, K) * emptyQueueProb) / (Math.pow(m, (K - m)) * factorial(m));
        }
    }

    // Método para calcular o tempo médio de espera na fila (E(w)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageNumberInWait(double lambda, double mu, int m) {
        double utilization = calculateUtilizationFactor(lambda, mu);
        double emptyQueueProb = getEmptyQueueProbability(lambda, mu, m);

        return (Math.pow(utilization, m) * emptyQueueProb) * ((utilization / m) / (Math.pow((1 - utilization / m), 2)) / (factorial(m)));
    }

    // Método para calcular o tempo médio no sistema (E(ts)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageTimeInSystem(double mu) {
        return 1 / mu;
    }

    // Método para calcular o número médio na fila (E(q)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageNumberInQueue(double lambda, double mu, int m) {
        double utilization = calculateUtilizationFactor(lambda, mu);
        return (Math.pow(utilization, m) * utilization) / (factorial(m) * Math.pow(1 - (utilization / m), 2));
    }

    // Método para calcular o tempo médio de espera na fila (E(tq)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageTimeInQueue(double lambda, double mu, int m) {
        return getAverageNumberInQueue(lambda, mu, m) / lambda;
    }

    // Método para calcular o tempo médio de espera (E(tw)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageTimeInWait(double lambda, double mu, int m) {
        return getAverageNumberInWait(lambda, mu, m) / lambda;
    }

    // Método para calcular o fatorial de um número
    private static int factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    // Método para calcular o número médio no sistema (E(s)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageNumberInSystem(double lambda, double mu) {

        return getAverageTimeInSystem(mu) * lambda;
    }

    public static void main(String[] args) {
        double lambda = 2.0;  // Taxa de chegada (arrival rate)
        double mu = 3.0;      // Taxa de serviço (service rate)
        int m = 2;            // Número de servidores
        int K = 2;            // Número de elementos na fila

        System.out.println("Empty Queue Probability: " + MMmInfiniteFIFOQueue.getEmptyQueueProbability(lambda, mu, m));
        System.out.println("K Queue Probability: " + MMmInfiniteFIFOQueue.getProbabilityKCustomers(K, lambda, mu, m));
        System.out.println("Average Number in Queue: " + MMmInfiniteFIFOQueue.getAverageNumberInQueue(lambda, mu, m));
        System.out.println("Average Number in System: " + MMmInfiniteFIFOQueue.getAverageNumberInSystem(lambda, mu));
        System.out.println("Average Number in Wait: " + MMmInfiniteFIFOQueue.getAverageNumberInWait(lambda, mu, m));
        System.out.println("Average Time in Queue: " + MMmInfiniteFIFOQueue.getAverageTimeInQueue(lambda, mu, m));
        System.out.println("Average Time in Wait: " + MMmInfiniteFIFOQueue.getAverageTimeInWait(lambda, mu, m));
        System.out.println("Average Time in System: " + MMmInfiniteFIFOQueue.getAverageTimeInSystem(mu));
    }
}
