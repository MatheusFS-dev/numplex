package com.app.numplex.queue;

public class MM1J1InfiniteFIFOQueue {
    // Método para calcular fatoriais recursivamente
    public static double calculateFactorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * calculateFactorial(n - 1);
    }

    // Método para calcular o fator de utilização a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double calculateUtilizationFactor(double lambda, double mu, int J) {
        return lambda / (mu * J);
    }

    // Método para calcular a probabilidade de fila vazia a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getEmptyQueueProbability(double lambda, double mu, int J) {
        double utilizationFactor = calculateUtilizationFactor(lambda, mu, J);
        double sum = 0;
        for (int n = 0; n <= J; n++) {
            sum += (Math.pow(utilizationFactor, n) / calculateFactorial(n));
        }
        return 1 / (sum + ((Math.pow(utilizationFactor, J + 1) / calculateFactorial(J)) * (1 - utilizationFactor)));
    }

    // Método para calcular a probabilidade de bloqueio a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getBlockingProbability(double lambda, double mu, int J) {
        double utilizationFactor = calculateUtilizationFactor(lambda, mu, J);
        return (Math.pow(utilizationFactor, J + 1) / (calculateFactorial(J) * (1 - utilizationFactor))) * getEmptyQueueProbability(lambda, mu, J);
    }

    // Método para calcular o número médio na fila (E(q)) a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getAverageNumberInQueue(double lambda, double mu, int J) {
        return (calculateUtilizationFactor(lambda, mu, J) * getEmptyQueueProbability(lambda, mu, J)) / (1 - calculateUtilizationFactor(lambda, mu, J));
    }

    // Método para calcular o número médio no sistema (E(s)) a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getAverageNumberInSystem(double lambda, double mu, int J) {
        return calculateUtilizationFactor(lambda, mu, J) * getEmptyQueueProbability(lambda, mu, J);
    }

    // Método para calcular o tempo médio de espera na fila (E(tq)) a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getAverageTimeInQueue(double lambda, double mu, int J) {
        return getAverageNumberInQueue(lambda, mu, J) / lambda;
    }

    // Método para calcular o tempo médio no sistema (E(ts)) a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getAverageTimeInSystem(double lambda, double mu, int J) {
        return getAverageNumberInSystem(lambda, mu, J) / lambda;
    }

    // Método para calcular o número médio de espera na fila (E(w)) a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getAverageNumberInWait(double lambda, double mu, int J) {
        double averageNumberInQueue = getAverageNumberInQueue(lambda, mu, J);
        double averageNumberInSystem = getAverageNumberInSystem(lambda, mu, J);
        return averageNumberInQueue - averageNumberInSystem;
    }

    // Método para calcular o tempo médio de espera na fila (E(tw)) a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getAverageTimeInWait(double lambda, double mu, int J) {
        double averageTimeInQueue = getAverageTimeInQueue(lambda, mu, J);
        double averageTimeInSystem = getAverageTimeInSystem(lambda, mu, J);
        return averageTimeInQueue - averageTimeInSystem;
    }

    // Método para calcular a probabilidade de K clientes na fila a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getProbabilityKCustomers(int K, double lambda, double mu, int J) {
        double utilizationFactor = calculateUtilizationFactor(lambda, mu, J);
        double emptyQueueProbability = getEmptyQueueProbability(lambda, mu, J);

        return Math.pow(utilizationFactor, K) * emptyQueueProbability;
    }

    public static void main(String[] args) {
        double lambda = 2.0;  // Example arrival rate
        double mu = 3.0;      // Example service rate
        int J = 2;            // Buffer size
        int K = 3;  // Number of customers

        System.out.println("Empty Queue Probability: " + getEmptyQueueProbability(lambda, mu, J));
        System.out.println("Blocking Probability: " + getBlockingProbability(lambda, mu, J));
        System.out.println("Average Number in Queue: " + getAverageNumberInQueue(lambda, mu, J));
        System.out.println("Average Number in System: " + getAverageNumberInSystem(lambda, mu, J));
        System.out.println("Average Time in Queue: " + getAverageTimeInQueue(lambda, mu, J));
        System.out.println("Average Time in System: " + getAverageTimeInSystem(lambda, mu, J));
        System.out.println("Average Number in Wait: " + getAverageNumberInWait(lambda, mu, J));
        System.out.println("Average Time in Wait: " + getAverageTimeInWait(lambda, mu, J));
        System.out.println("Probability of " + K + " Customers: " + getProbabilityKCustomers(K, lambda, mu, J));
    }
}
