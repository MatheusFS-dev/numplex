package com.app.numplex.queue;

public class MMmJKInfiniteFIFOQueue {

    // Método para calcular o fator de utilização a partir das taxas de entrada (lambda) e saída (mu)
    public static double calculateUtilizationFactor(double lambda, double mu) {
        return lambda / mu;
    }

    // Método para calcular a probabilidade de fila vazia a partir das taxas de entrada (lambda) e saída (mu)
    public static double getEmptyQueueProbability(double lambda, double mu, int m, int J) {
        double utilization = calculateUtilizationFactor(lambda, mu);
        double sum = 0;
        double sum2 = 0;
        for (int i = 0; i <= m; i++) {
            sum += (Math.pow(utilization, i) / factorial(i));
        }
        for (int i = (m + 1); i <= (m + J); i++) {
            sum2 += (Math.pow(utilization, i) / (factorial(m) * Math.pow(i, (i - m))));
        }

        return 1 / (sum + sum2);
    }

    // Método para calcular o tempo médio de espera na fila (E(w)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageTimeInWait(double lambda, double mu, int m, int J, int K) {
        double utilization = calculateUtilizationFactor(lambda, mu);
        double emptyQueueProb = getEmptyQueueProbability(lambda, mu, m, J);

        double firstTerm = (Math.pow(utilization, J + 1) * Math.pow(K - J, m) / (factorial(J) * factorial(m) * Math.pow(1 - (K - J) / m, 2)));
        double secondTerm = (Math.pow(utilization, J + 1) * (K - J) / (factorial(J) * m * (1 - (K - J) / m)));

        return emptyQueueProb * (firstTerm + secondTerm);
    }

    // Método para calcular o tempo médio no sistema (E(ts)) a partir da taxa de serviço (mu)
    public static double getAverageTimeInSystem(double mu) {
        return 1 / mu;
    }

    // Método para calcular o número médio na fila (E(q)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageNumberInQueue(double lambda, double mu, int m, int J, int K) {
        double utilization = calculateUtilizationFactor(lambda, mu);
        double emptyQueueProb = getEmptyQueueProbability(lambda, mu, m, J);

        double numerator = (Math.pow(utilization, J + 1) * Math.pow(K - J, m));
        double denominator = (factorial(J) * factorial(m) * Math.pow(1 - (K - J) / m, 2));

        return emptyQueueProb * (numerator / denominator);
    }

    public static double getAverageNumberInSystem(double lambda, double mu, int m, int J, int K) {
        double utilization = calculateUtilizationFactor(lambda, mu);
        double emptyQueueProb = getEmptyQueueProbability(lambda, mu, m, J);

        return utilization * (1 - emptyQueueProb);
    }

    public static double getAverageNumberInWait(double lambda, double mu, int m, int J, int K) {
        double emptyQueueProb = getEmptyQueueProbability(lambda, mu, m, J);

        return getAverageTimeInWait(lambda, mu, m, J, K) * lambda * (1 - emptyQueueProb);
    }

    // Método para calcular a probabilidade de K clientes na fila a partir das taxas de entrada (lambda) e saída (mu)
    public static double getProbabilityKCustomers(int K, double lambda, double mu, int m, int J) {
        double utilization = calculateUtilizationFactor(lambda, mu);
        double emptyQueueProb = getEmptyQueueProbability(lambda, mu, m, J);
        if(K <= m){
            return (Math.pow(utilization, K) * emptyQueueProb) / factorial(K);
        }
        else{
            return (Math.pow(utilization, K) * emptyQueueProb) / (Math.pow(m, (K - m)) * factorial(m));
        }
    }

    // Método para calcular o tempo médio de espera na fila (E(tq)) a partir das taxas de entrada (lambda) e saída (mu)
    public static double getAverageTimeInQueue(double lambda, double mu, int m, int J, int K) {
        return getAverageNumberInQueue(lambda, mu, m, J, K) / lambda;
    }

    // Método para calcular o fatorial de um número
    private static int factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

}

