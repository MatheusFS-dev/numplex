package com.app.numplex.matrix;

import com.app.numplex.customExceptions.DeterminantNotPossible;
import com.app.numplex.customExceptions.InverseNotPossible;
import com.app.numplex.customExceptions.MatrixDifferentDimensions;
import com.app.numplex.utils.MathFunctions;

public class MatrixLogic {

    /*----------------------------------------------Soma de Matrizes-------------------------------------------------*/
    public static double[][] sumMatrix(double[][] matrizA, double[][] matrizB) throws MatrixDifferentDimensions {

        double[][] matrizSum = new double[matrizA.length][matrizB.length];
        if (matrizA.length != matrizB.length || matrizA[0].length != matrizB[0].length) {
            throw new MatrixDifferentDimensions();
        } else {
            try {
                for (int i = 0; i < matrizA.length; i++) {
                    for (int j = 0; j < matrizB.length; j++) {
                        matrizSum[i][j] = matrizA[i][j] + matrizB[i][j];
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException();
            }
        }
        return matrizSum;
    }

    /*--------------------------------------------Subtração Matriz----------------------------------------------------*/
    public static double[][] subMatrix(double[][] matrizA, double[][] matrizB) throws MatrixDifferentDimensions {
        double[][] matrizSub = new double[matrizA.length][matrizB.length];
        if (matrizA.length != matrizB.length || matrizA[0].length != matrizB[0].length) {
            throw new MatrixDifferentDimensions();
        } else {
            for (int i = 0; i < matrizB.length; i++) {
                for (int j = 0; j < matrizB.length; j++) {
                    matrizSub[i][j] = matrizA[i][j] - matrizB[i][j];
                }
            }
        }
        return matrizSub;
    }

    /*--------------------------------------------multiplicação Matriz------------------------------------------------*/
    public static double[][] multMatrix(double[][] matrixA, double[][] matrixB) throws MatrixDifferentDimensions {
        int rowsA = matrixA.length;
        int columnsA = matrixA[0].length;
        int rowsB = matrixB.length;
        int columnsB = matrixB[0].length;

        if (columnsA != rowsB) {
            throw new MatrixDifferentDimensions();
        }

        double[][] result = new double[rowsA][columnsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < columnsB; j++) {
                for (int k = 0; k < columnsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                    result[i][j] = MathFunctions.round(result[i][j]);
                }
            }
        }

        return result;
    }

    /*--------------------------------------------multiplicação Escalar Matriz----------------------------------------*/
    public static double[][] scalarMultiplicationMatrix(double[][] input, double n) {
        double[][] output = new double[input.length][input[1].length];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                output[i][j] = input[i][j] * n;
            }
        }
        return output;
    }

    /*--------------------------------------------Transposição de Matriz----------------------------------------------*/
    public static double[][] TransposeMatrix(double[][] matrizA) {
        double[][] transpose = new double[matrizA[0].length][matrizA.length];

        for (int i = 0; i < matrizA[0].length; i++) {
            for (int j = 0; j < matrizA.length; j++) {
                transpose[i][j] = matrizA[j][i];
            }
        }
        return transpose;
    }

    /*--------------------------------------------Determinante de Matriz----------------------------------------------*/
    public static double determinante(double[][] matrizA) throws DeterminantNotPossible {

        if(matrizA.length != matrizA[0].length)
            throw new DeterminantNotPossible();

        double determinante = 0;
        if (matrizA.length == 1) {
            determinante = matrizA[0][0];
        } else if (matrizA.length == 2) {
            determinante = matrizA[0][0] * matrizA[1][1] - matrizA[0][1] * matrizA[1][0];
        } else if (matrizA.length == 3) {
            determinante = matrizA[0][0] * matrizA[1][1] * matrizA[2][2]
                    + matrizA[0][1] * matrizA[1][2] * matrizA[2][0]
                    + matrizA[0][2] * matrizA[1][0] * matrizA[2][1]
                    - matrizA[0][2] * matrizA[1][1] * matrizA[2][0]
                    - matrizA[0][0] * matrizA[1][2] * matrizA[2][1]
                    - matrizA[0][1] * matrizA[1][0] * matrizA[2][2];
        } else {
            double[][] aux;
            int i_aux, j_aux, line, column, i;
            for (i = 0; i < matrizA.length; i++) {
                if (matrizA[0][i] != 0) {
                    aux = new double[matrizA.length - 1][matrizA.length - 1];
                    i_aux = 0;
                    j_aux = 0;
                    for (line = 1; line < matrizA.length; line++) {
                        for (column = 0; column < matrizA.length; column++) {
                            if (column != i) {
                                aux[i_aux][j_aux] = matrizA[line][column];
                                j_aux++;
                            }
                        }
                        i_aux++;
                        j_aux = 0;
                    }
                    determinante += Math.pow(-1, i) * matrizA[0][i] * determinante(aux);
                }
            }
        }
        return determinante;
    }
    /*--------------------------------------------Potenciação de Matriz-----------------------------------------------*/
    public static double[][] powLinear(double[][] matriz, int n) throws MatrixDifferentDimensions {
        if(n < 0){
            throw new IllegalArgumentException("n < 0");
        }
        int order = matriz.length;
        double[][] p = Aux(order);
        for(int i = 0; i < n; i++){
            p = multMatrix(p, matriz);
        }
        return p;
    }
    private static double[][] Aux(int n){
        if(n <= 0){
            throw new IllegalArgumentException("n <= 0");
        }
        double[][] I = new double[n][n];
        for(int i = 0; i < n; i++){
            I[i][i] = 1;
        }
        return I;
    }
    /*-----------------------------------------------Inversão de Matriz-----------------------------------------------*/
    public static double[][] inverse(double[][] matriz) throws DeterminantNotPossible, InverseNotPossible {

        if(matriz.length != matriz[0].length || determinante(matriz) == 0)
            throw new InverseNotPossible();

        double[][] inverse = new double[matriz.length][matriz.length];
        for (int i = 0; i < matriz.length; i++)
            for (int j = 0; j < matriz[i].length; j++)
                inverse[i][j] = Math.pow(-1, i + j)
                        * determinante(submatrix(matriz, i, j));
        double det = 1.0 / determinante(matriz);
        for (int i = 0; i < inverse.length; i++) {
            for (int j = 0; j <= i; j++) {
                double temp = inverse[i][j];
                inverse[i][j] = inverse[j][i] * det;
                inverse[j][i] = temp * det;
            }
        }
        return inverse;
    }
    private static double[][] submatrix(double[][] matriz, int line, int column) {
        double[][] submatrix = new double[matriz.length - 1][matriz.length - 1];

        for (int i = 0; i < matriz.length; i++)
            for (int j = 0; i != line && j < matriz[i].length; j++)
                if (j != column)
                    submatrix[i < line ? i : i - 1][j < column ? j : j - 1] = matriz[i][j];
        return submatrix;
    }

    public static double[][] scalarDivisionMatrix(double[][] matriz, double n) {
        if (n == 0) {
            throw new IllegalArgumentException("O valor de n não pode ser zero.");
        }

        return scalarMultiplicationMatrix(matriz, 1.0/n);
    }

    public static double[][] divideMatrices(double[][] matrixA, double[][] matrixB) throws DeterminantNotPossible {
        double[][] result = new double[matrixA.length][matrixA[0].length];

        // calcular a inversa da matriz B
        double det = determinante(matrixB);
        double invDet = 1 / det;
        double[][] invB = {{matrixB[1][1] * invDet, -matrixB[0][1] * invDet},
                {-matrixB[1][0] * invDet, matrixB[0][0] * invDet}};

        // multiplicar a matriz A pela matriz B^(-1)
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < invB[0].length; j++) {
                double sum = 0;
                for (int k = 0; k < matrixB.length; k++) {
                    sum += matrixA[i][k] * invB[k][j];
                }
                result[i][j] = MathFunctions.round(sum);
            }
        }

        return result;
    }



}