package com.app.numplex.queue;

public class MMm0mInfiniteFIFOQueue {
    // Método para calcular fatoriais recursivamente
    public static double calculateFactorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * calculateFactorial(n - 1);
    }

    // Método para calcular o fator de utilização a partir das taxas de entrada (lambda), saída (mu) e número de servidores (m)
    public static double calculateUtilizationFactor(double lambda, double mu) {
        return lambda / mu;
    }

    // Método para calcular a probabilidade de fila vazia a partir das taxas de entrada (lambda), saída (mu) e número de servidores (m)
    public static double getEmptyQueueProbability(double lambda, double mu, int m) {
        double utilizationFactor = calculateUtilizationFactor(lambda, mu);
        double sum = 0;
        for (int n = 0; n < m; n++) {
            sum += (Math.pow(utilizationFactor, n) / calculateFactorial(n));
        }
        return 1 / sum;
    }

    // Método para calcular a probabilidade de bloqueio a partir das taxas de entrada (lambda), saída (mu) e número de servidores (m)
    public static double getBlockingProbability(double lambda, double mu, int m) {
        double utilizationFactor = calculateUtilizationFactor(lambda, mu);
        return Math.pow(utilizationFactor, m) / calculateFactorial(m) * getEmptyQueueProbability(lambda, mu, m);
    }

    // Método para calcular a probabilidade de K clientes na fila a partir das taxas de entrada (lambda) e saída (mu)
    public static double getProbabilityKCustomers(int K, double lambda, double mu, int m) {
        return Math.pow(calculateUtilizationFactor(lambda, mu), K) * getEmptyQueueProbability(lambda, mu, m) / calculateFactorial(K);
    }

    // Método para calcular o número médio na fila (E(q)) a partir das taxas de entrada (lambda), saída (mu) e número de servidores (m)
    public static double getAverageNumberInQueue(double lambda, double mu, int m) {
        double utilizationFactor = calculateUtilizationFactor(lambda, mu);
        return utilizationFactor * (1 - getEmptyQueueProbability(lambda, mu, m));
    }

    // Método para calcular o número médio no sistema (E(s)) a partir das taxas de entrada (lambda), saída (mu) e número de servidores (m)
    public static double getAverageNumberInSystem(double lambda, double mu, int m) {
        return getAverageNumberInQueue(lambda, mu, m);
    }

    // Método para calcular o número médio de espera na fila (E(w)) a partir das taxas de entrada (lambda), saída (mu) e tamanho do buffer (J)
    public static double getAverageNumberInWait(double lambda, double mu, int m) {
        return getAverageNumberInQueue(lambda, mu, m) - getAverageNumberInSystem(lambda, mu, m);
    }

    // Método para calcular o tempo médio de espera na fila (E(tq))  a partir da saída (mu)
    public static double getAverageTimeInQueue(double mu) {
        return 1 / mu;
    }

    // Método para calcular o tempo médio no sistema (E(ts))  a partir da saída (mu)
    public static double getAverageTimeInSystem(double mu) {
        return getAverageTimeInQueue(mu);
    }

    // Método para calcular o tempo médio de espera (E(tw)) a partir da saída (mu)
    public static double getAverageTimeInWait(double mu) {
        return getAverageTimeInQueue(mu) - getAverageTimeInSystem(mu);
    }

    public static void main(String[] args) {
        double lambda = 4.0;  // Example arrival rate
        double mu = 2.0;      // Example service rate
        int m = 3;            // Number of servers

        System.out.println("Empty Queue Probability: " + getEmptyQueueProbability(lambda, mu, m));
        System.out.println("Blocking Probability: " + getBlockingProbability(lambda, mu, m));
        System.out.println("Average Number in Queue: " + getAverageNumberInQueue(lambda, mu, m));
        System.out.println("Average Number in System: " + getAverageNumberInSystem(lambda, mu, m));
        System.out.println("Average Number in Wait: " + getAverageNumberInWait(lambda, mu, m));
        System.out.println("Average Time in Queue: " + getAverageTimeInQueue(mu));
        System.out.println("Average Time in System: " + getAverageTimeInSystem(mu));
        System.out.println("Average Time in Wait: " + getAverageTimeInWait(mu));
    }
}
