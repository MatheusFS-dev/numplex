package com.app.numplex.distributions;

import java.util.Random;

public class Distributions {
    public static double[] gaussianDistribution(int count, double mean, double stdDev) {
    // Cria um array para armazenar os números aleatórios
    double[] result = new double[count];

    Random rng = new Random();
    for (int i = 0; i < count; i++) {
        // Gera um número aleatório com distribuição gaussiana (normal)
        double randomValue = rng.nextGaussian() * stdDev + mean;

        // Arredonda o valor para 3 casas decimais
        randomValue = Math.round(randomValue * 1000.0) / 1000.0;

        result[i] = randomValue;
    }

    return result;
}

    public static double[] rayleighDistribution(int count, double scale) {
    // Cria um array para armazenar os números aleatórios
    double[] result = new double[count];

    Random rng = new Random();
    for (int i = 0; i < count; i++) {
        // Gera um número aleatório com distribuição Rayleigh
        double randomValue = Math.sqrt(-2.0 * Math.log(rng.nextDouble())) * scale;

        // Arredonda o valor para 3 casas decimais
        randomValue = Math.round(randomValue * 1000.0) / 1000.0;

        result[i] = randomValue;
    }

    return result;
}

    public static double[] riceDistribution(int count, double scale) {
    // Cria um array para armazenar os números aleatórios
    double[] result = new double[count];

    Random rng = new Random();
    for (int i = 0; i < count; i++) {
        // Gera um número aleatório com distribuição normal (gaussiana)
        double gaussianValue = rng.nextGaussian();

        // Calcula o valor da distribuição Rice usando a fórmula:
        // sqrt(scale^2 + (gaussianValue * scale)^2) + offset
        double randomValue = Math.sqrt(Math.pow(scale, 2) + Math.pow(gaussianValue * scale, 2));

        // Arredonda o valor para 3 casas decimais
        randomValue = Math.round(randomValue * 1000.0) / 1000.0;

        // Atribui o valor gerado ao elemento correspondente no array
        result[i] = randomValue;
    }

    return result;
}
}