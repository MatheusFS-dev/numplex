package com.app.numplex.matrix_complex;

import com.app.numplex.customExceptions.DeterminantNotPossible;
import com.app.numplex.customExceptions.InverseNotPossible;
import com.app.numplex.customExceptions.MatrixDifferentDimensions;
import com.app.numplex.utils.MathFunctions;

import org.apache.commons.math3.complex.Complex;

public class ComplexMatrixLogic {

    // método para realizar a soma de duas matrizes complexas
    public static Complex[][] add(Complex[][] a, Complex[][] b) throws MatrixDifferentDimensions {// verifica se as matrizes têm as mesmas dimensões
        if (a.length != b.length || a[0].length != b[0].length) {
            throw new MatrixDifferentDimensions();
        }
        // cria uma nova matriz para armazenar o resultado
        int rows = a.length;
        int cols = a[0].length;
        Complex[][] result = new Complex[rows][cols];

    // realiza a soma dos elementos de cada posição e armazena na nova matriz
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j].add(b[i][j]);
            }
        }

        return result;
    }

    // método para realizar a subtração de duas matrizes complexas
    public static Complex[][] subtract(Complex[][] a, Complex[][] b) throws MatrixDifferentDimensions {
    // verifica se as matrizes têm as mesmas dimensões
        if (a.length != b.length || a[0].length != b[0].length) {
            throw new MatrixDifferentDimensions();
        }
        // cria uma nova matriz para armazenar o resultado
        int rows = a.length;
        int cols = a[0].length;
        Complex[][] result = new Complex[rows][cols];

// realiza a subtração dos elementos de cada posição e armazena na nova matriz
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j].subtract(b[i][j]);
            }
        }

        return result;
    }

    public static Complex[][] multiply(Complex[][] a, Complex[][] b) throws MatrixDifferentDimensions {
        // Verificar se as matrizes podem ser multiplicadas
        if (a[0].length != b.length) {
            throw new MatrixDifferentDimensions();
        }

        int rowsA = a.length;
        int colsA = a[0].length;
        int rowsB = b.length;
        int colsB = b[0].length;

        // Inicializar a matriz resultante
        Complex[][] c = new Complex[rowsA][colsB];

        // Calcular o produto matricial
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                c[i][j] = Complex.ZERO;
                for (int k = 0; k < colsA; k++) {
                    c[i][j] = c[i][j].add(a[i][k].multiply(b[k][j]));
                    c[i][j] = MathFunctions.roundComplex(c[i][j]);
                }
            }
        }

        return c;
    }

    public static Complex[][] divide(Complex[][] matrix1, Complex[][] matrix2) throws MatrixDifferentDimensions, InverseNotPossible, DeterminantNotPossible {
        // Obtém o número de linhas e colunas de cada matriz
        int rows1 = matrix1.length;
        int cols1 = matrix1[0].length;
        int rows2 = matrix2.length;
        int cols2 = matrix2[0].length;

        // Verifica se as matrizes têm as mesmas dimensões
        if (rows1 != rows2 || cols1 != cols2) {
            throw new MatrixDifferentDimensions();
        }

        // Verifica se a matriz divisor é invertível
        if (!isInvertible(matrix2)) {
            throw new InverseNotPossible();
        }

        // Cria uma matriz para armazenar o resultado
        Complex[][] result = new Complex[rows1][cols1];

        // Itera sobre cada elemento das matrizes e realiza a divisão
        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols1; j++) {
                result[i][j] = matrix1[i][j].divide(matrix2[i][j]);
            }
        }

        // Retorna a matriz resultante
        return result;
    }

    public static Complex determinant(Complex[][] matrix) throws DeterminantNotPossible {
        // Obtém o tamanho da matriz
        int n = matrix.length;
        // Inicia a variável de determinante com 1
        Complex det = Complex.ONE;

        if(matrix.length != matrix[0].length)
            throw new DeterminantNotPossible();

        // Percorre as colunas da matriz até a última coluna
        for (int i = 0; i < n; i++) {
            // Encontra o índice da linha com o maior valor absoluto na coluna atual
            int maxIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (matrix[j][i].abs() > matrix[maxIndex][i].abs()) {
                    maxIndex = j;
                }
            }

            // Se o índice encontrado não for o mesmo da linha atual, troca as linhas
            if (i != maxIndex) {
                Complex[] temp = matrix[i];
                matrix[i] = matrix[maxIndex];
                matrix[maxIndex] = temp;
                // Inverte o sinal do determinante
                det = det.negate();
            }

            // Percorre as linhas abaixo da atual
            for (int j = i + 1; j < n; j++) {
                // Calcula o fator de divisão para zerar os elementos abaixo da diagonal
                Complex factor = matrix[j][i].divide(matrix[i][i]);
                // Percorre as colunas da linha atual até a última coluna
                for (int k = i; k < n; k++) {
                    // Subtrai o valor da coluna atual multiplicado pelo fator de divisão
                    matrix[j][k] = matrix[j][k].subtract(factor.multiply(matrix[i][k]));
                }
            }

            // Multiplica o valor da diagonal atual ao determinante
            det = det.multiply(matrix[i][i]);
        }

        // Retorna o determinante da matriz
        return det;
    }

    public static Complex[][] inverse(Complex[][] matrix) throws DeterminantNotPossible, InverseNotPossible {
        int n = matrix.length;
        Complex[][] extendedMatrix = new Complex[n][2 * n]; // matriz aumentada com a matriz identidade

        if(matrix.length != matrix[0].length || determinant(matrix).getReal() == 0 && determinant(matrix).getImaginary() == 0)
            throw new InverseNotPossible();

        // copia a matriz original para a esquerda da matriz aumentada e a matriz identidade para a direita
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, extendedMatrix[i], 0, n);
            for (int j = n; j < 2 * n; j++) {
                if (i == j - n) {
                    extendedMatrix[i][j] = Complex.ONE;
                } else {
                    extendedMatrix[i][j] = Complex.ZERO;
                }
            }
        }

        // aplica a eliminação de Gauss-Jordan
        for (int i = 0; i < n; i++) {
            Complex pivot = extendedMatrix[i][i];
            for (int j = i + 1; j < 2 * n; j++) {
                extendedMatrix[i][j] = extendedMatrix[i][j].divide(pivot);
            }
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    Complex factor = extendedMatrix[j][i];
                    for (int k = i; k < 2 * n; k++) {
                        extendedMatrix[j][k] = extendedMatrix[j][k].subtract(factor.multiply(extendedMatrix[i][k]));
                    }
                }
            }
        }

        // extrai a matriz inversa da matriz aumentada
        Complex[][] inverse = new Complex[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(extendedMatrix[i], n, inverse[i], 0, n);
        }
        return inverse;
    }

    public static Complex[][] transpose(Complex[][] matrix) {
        int numRows = matrix.length; // Obtém o número de linhas da matriz original
        int numCols = matrix[0].length; // Obtém o número de colunas da matriz original
        Complex[][] transposedMatrix = new Complex[numCols][numRows]; // Cria a matriz transposta com o número de linhas e colunas invertidos

        // Percorre a matriz original e atribui o valor correspondente na matriz transposta
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                transposedMatrix[j][i] = matrix[i][j];
            }
        }

        return transposedMatrix; // Retorna a matriz transposta
    }

    public static Complex[][] conjugate(Complex[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        Complex[][] conjugatedMatrix = new Complex[rows][cols];

        // itera sobre cada elemento da matriz e aplica a conjugação
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                conjugatedMatrix[i][j] = matrix[i][j].conjugate();
            }
        }

        return conjugatedMatrix; // Retorna a matriz conjugada
    }

    public static Complex cofactor(Complex[][] matrix, int row, int col) throws DeterminantNotPossible { // utilizado na adjunta, ignorar se quiser
        int n = matrix.length;
        Complex[][] submatrix = new Complex[n - 1][n - 1];
        int subRow = 0;
        int subCol = 0;

        // Copia os elementos da matriz original para a submatriz, ignorando a linha e a coluna do elemento selecionado
        for (int i = 0; i < n; i++) {
            if (i == row) {
                continue;
            }
            subCol = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) {
                    continue;
                }
                submatrix[subRow][subCol] = matrix[i][j];
                subCol++;
            }
            subRow++;
        }

        // Calcula o determinante da submatriz
        Complex cofactor = determinant(submatrix);

        // Aplica o sinal do cofator (negativo se a soma de i e j for ímpar)
        if ((row + col) % 2 == 1) {
            cofactor = cofactor.negate();
        }

        return cofactor;
    }

    public static Complex[][] adjoint(Complex[][] matrix) throws DeterminantNotPossible {
        int n = matrix.length;
        Complex[][] adjointMatrix = new Complex[n][n];

        // preenche cada elemento da matriz adjunta com o cofator correspondente
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adjointMatrix[i][j] = cofactor(matrix, i, j).multiply((i + j) % 2 == 0 ? Complex.ONE : Complex.ONE.negate());
            }
        }

        // retorna a matriz transposta da matriz adjunta
        return transpose(adjointMatrix);
    }

    public static Complex[][] tensorProduct(Complex[][] matrix1, Complex[][] matrix2) {
        /*
        O método tensorProduct retorna o produto tensorial de duas matrizes, que é uma operação que cria uma nova matriz
        com dimensão igual ao produto das dimensões das matrizes originais.
        Essa nova matriz é preenchida com todos os possíveis produtos de cada elemento das duas matrizes originais.
        Por exemplo, se a primeira matriz é 2x2 e a segunda é 3x3, a matriz resultante será 6x6, onde
        cada elemento [i,j] é o produto do elemento [i/2,j/2] da primeira matriz com o elemento [i%2,j%2] da segunda matriz.
         */

        int numRows1 = matrix1.length;
        int numCols1 = matrix1[0].length;
        int numRows2 = matrix2.length;
        int numCols2 = matrix2[0].length;
        int numRowsResult = numRows1 * numRows2;
        int numColsResult = numCols1 * numCols2;
        Complex[][] tensorProductMatrix = new Complex[numRowsResult][numColsResult];

        // Preenche a matriz resultado com o produto tensorial das duas matrizes.
        for (int i = 0; i < numRows1; i++) {
            for (int j = 0; j < numCols1; j++) {
                for (int k = 0; k < numRows2; k++) {
                    for (int l = 0; l < numCols2; l++) {
                        int rowResult = i * numRows2 + k;
                        int colResult = j * numCols2 + l;
                        tensorProductMatrix[rowResult][colResult] = matrix1[i][j].multiply(matrix2[k][l]);
                    }
                }
            }
        }

        return tensorProductMatrix;
    }

    public static Complex[][] identity(int n) {
        Complex[][] identityMatrix = new Complex[n][n];

        // Preenche a diagonal principal com o número complexo 1 + 0i e o restante com 0 + 0i.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    identityMatrix[i][j] = Complex.ONE;
                } else {
                    identityMatrix[i][j] = Complex.ZERO;
                }
            }
        }

        return identityMatrix; // retorna matriz identidade
    }

    // método para imprimir a matriz
    public void print(Complex[][] matrix) {
        for (Complex[] complexes : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(complexes[j] + " ");
            }
            System.out.println();
        }
    }

    public static Complex[][] matrixPower(Complex[][] matrix, int n) throws MatrixDifferentDimensions {
        // Verifica se a matriz é quadrada
        int rows = matrix.length;
        int cols = matrix[0].length;
        if (rows != cols) {
            throw new IllegalArgumentException("A matriz deve ser quadrada para calcular a potência.");
        }

        // Inicializa a matriz resultante como a matriz de entrada
        Complex[][] result = matrix.clone();

        // Calcula a potência da matriz
        for (int k = 1; k < n; k++) {
            result = multiply(result, matrix);
        }

        return result;
    }

    public static Complex[][] scalarProduct(Complex[][] matrix, Complex scalar) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Complex[][] result = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j].multiply(scalar);
            }
        }
        return result;
    }

    public static Complex[][] scalarDivision(Complex[][] matrix, Complex scalar) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Complex[][] result = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j].divide(scalar);
            }
        }
        return result;
    }

   public static String complexToString(Complex complex) {
        double real = complex.getReal();
        double imag = complex.getImaginary();

        return real + "i" + imag;
   }

    public static boolean isInvertible(Complex[][] matrix) throws DeterminantNotPossible {
        // Verifica se a matriz é quadrada
        int rows = matrix.length;
        int cols = matrix[0].length;
        if (rows != cols) {
            return false; // A matriz não é quadrada, portanto não é invertível
        }

        // Calcula o determinante da matriz
        Complex determinant = determinant(matrix);

        // Verifica se o determinante é diferente de zero
        return !determinant.equals(Complex.ZERO);
    }

}