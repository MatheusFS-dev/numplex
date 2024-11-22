package com.app.numplex.algebra;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AlgebraLogic {
    // Var. Aux
    static double[] cross_P = new double[3];
    protected static double angle, angleAux;
    protected static double Denominator, numerator, result;

    /*-------------------------------------------------SQRT ROOT------------------------------------------------------*/
    //Function that calculates the sumVetA - sqrt root
    public static double sumVetA(double[] vectorOne) {
        double sumVetA = 0;
        for (double v : vectorOne) {
            sumVetA += pow(v, 2);
        }
        return sqrt(sumVetA);
    }

    //Function that calculates the sumVetB - sqrt root
    public static double sumVetB(double[] vectorTwo) {
        double sumVetB = 0;
        for (double v : vectorTwo) {
            sumVetB += pow(v, 2);
        }
        return sqrt(sumVetB);
    }

    /*----------------------------------------------DOT PRODUCT-------------------------------------------------------*/
    // Function that will calculate the dot product
    public static double calculateDotProduct(double[] vectorOne, double[] vectorTwo) {
        double dotProduct = 0;
        for (int i = 0; i < 3; i++)
            dotProduct += vectorOne[i] * vectorTwo[i];
        return dotProduct;
    }

    /*-------------------------------------------VECTOR COORDINATES---------------------------------------------------*/
    //Function that calculates the Vector Coordinates
    public static double[] calculateVectorCoordinates(double[] vectorOne, double[] vectorTwo) {
        double[] coordinatesOfThePoints = new double[3];
        for (int i = 0; i < 3; i++)
            coordinatesOfThePoints[i] += vectorTwo[i] - vectorOne[i];
        return coordinatesOfThePoints;
    }

    /*---------------------------------------------CROSS PRODUCT------------------------------------------------------*/
    //Function that calculates the Cross Product
    public static double[] crossProduct(double[] vectorOne, double[] vectorTwo) {
        cross_P[0] = vectorOne[1] * vectorTwo[2] - vectorOne[2] * vectorTwo[1];
        cross_P[1] = vectorOne[2] * vectorTwo[0] - vectorOne[0] * vectorTwo[2];
        cross_P[2] = vectorOne[0] * vectorTwo[1] - vectorOne[1] * vectorTwo[0];
        return cross_P;
    }

    /*-----------------------------------------------SUBTRACTION------------------------------------------------------*/
    //Function that calculates the subtraction
    public static double[] calculateSubtraction(double[] vectorOne, double[] vectorTwo) {
        double[] sub = new double[3];
        for (int i = 0; i < 3; i++)
            sub[i] += vectorOne[i] - vectorTwo[i];
        return sub;
    }

    /*-------------------------------------------------ADDITION-------------------------------------------------------*/
    //Function that calculates the Addition
    public static double[] calculateAddition(double[] vectorOne, double[] vectorTwo) {
        double[] addition = new double[3];
        for (int i = 0; i < 3; i++)
            addition[i] += vectorOne[i] + vectorTwo[i];
        return addition;
    }

    /*-----------------------------------------------VECTOR ANGLE-----------------------------------------------------*/
    // Function that calculates the angle of two vectors
    // Return cosine of angle
    public static double calculateAngleVectors(double[] vectorOne, double[] vectorTwo) {
        angleAux = sumVetA(vectorOne) * sumVetB(vectorTwo);
        angle = (calculateDotProduct(vectorOne, vectorTwo) / angleAux);

        if(AlgebraActivity.deg)
            return Math.round(Math.toDegrees(Math.acos(angle)) * 1000.0)/ 1000.0;
        else
            return Math.round(Math.acos(angle) * 1000.0)/ 1000.0;
    }

    /*------------------------------------------------LINE ANGLE------------------------------------------------------*/
    // Function that calculates the line angle
    // Return cosine of angle
    public static double calculateAngleLine(double[] LineOne, double[] LineTwo) {
        angleAux = sumVetA(LineOne) * sumVetB(LineTwo);
        angle = (calculateDotProduct(LineOne, LineTwo) / angleAux);
        if(AlgebraActivity.deg)
            return Math.round(Math.toDegrees(Math.acos(angle)) * 1000.0)/ 1000.0;
        else
            return Math.round(Math.acos(angle) * 1000.0)/ 1000.0;
    }

    /*-----------------------------------------------PLANE ANGLE------------------------------------------------------*/
    // Function that calculates the plane angle
    // Return cosine of angle
    public static double calculateAnglePlane(double[] PlaneOne, double[] PlaneTwo) {
        double AnglePlaneOne, AnglePlaneTwo;
        AnglePlaneOne = sqrt(pow(PlaneOne[0], 2) + pow(PlaneOne[1], 2) + pow(PlaneOne[2], 2));
        AnglePlaneTwo = sqrt(pow(PlaneTwo[0], 2) + pow(PlaneTwo[1], 2) + pow(PlaneTwo[2], 2));

        angleAux = AnglePlaneOne * AnglePlaneTwo;
        angle = (calculateDotProduct(PlaneOne, PlaneTwo) / angleAux);

        if(AlgebraActivity.deg)
            return Math.round(Math.toDegrees(Math.acos(angle)) * 1000.0)/ 1000.0;
        else
            return Math.round(Math.acos(angle) * 1000.0)/ 1000.0;
    }

    /*---------------------------------------------ANGLE LINE AND PLANE-----------------------------------------------*/
    // Function that calculates the angle line and Plane
    // Return sine of angle
    public static double calculateAngleLineAndPlane(double[] Line, double[] Plane) {
        double AngleLine, AnglePlane;
        AngleLine = sqrt(pow(Line[0], 2) + pow(Line[1], 2) + pow(Line[2], 2));
        AnglePlane = sqrt(pow(Plane[0], 2) + pow(Plane[1], 2) + pow(Plane[2], 2));

        angleAux = AngleLine * AnglePlane;
        angle = (calculateDotProduct(Line, Plane) / angleAux);

        if(AlgebraActivity.deg)
            return Math.round(Math.toDegrees(Math.asin(angle)) * 1000.0)/ 1000.0;
        else
            return Math.round(Math.asin(angle) * 1000.0)/ 1000.0;
    }

    /*-------------------------------------------DISTANCE PLAN AND POINT----------------------------------------------*/
    // Functions that calculates distance plane and point
    // First entry is the plane, seconds entry is the point
    public static double distancePlaneAndPoint(double[] plane, double[] point) {
        numerator = plane[0] * point[0] + plane[1] * point[1] + plane[2] * point[2] + plane[3];
        Denominator = pow(plane[0], 2) + pow(plane[1], 2) + pow(plane[2], 2);
        result = abs(numerator) / sqrt(Denominator);
        return result;
    }

    /*-------------------------------------------DISTANCE POINT AND LINE----------------------------------------------*/
    // Functions that calculates distance Line and Point
    // First entry is the line, seconds entry is the point
    public static double distanceLineAndPoint(double[] line, double[] point) {
        numerator = line[0] * point[0] + line[1] * point[1] + line[2];
        Denominator = pow(line[0], 2) + pow(line[1], 2);
        result = abs(numerator) / sqrt(Denominator);
        return result;
    }

    /*-------------------------------------------DISTANCE PLAN AND LINE-----------------------------------------------*/
    // Functions that calculates distance Line and Plane
    // First entry is the plane, seconds entry is the line
    public static double distancePlaneAndLine(double[] plane, double[] line) {
        numerator = plane[0] * line[0] + plane[1] * line[1] + plane[2] * line[2] + plane[3];
        Denominator = pow(plane[0], 2) + pow(plane[1], 2) + pow(plane[2], 2);
        result = abs(numerator) / sqrt(Denominator);
        return result;
    }

    /*------------------------------------------DISTANCE BETWEEN LINES------------------------------------------------*/
    //Function that calculates angle line canonical
    public static double distanceLines(double[] pointA, double[] vectorA, double[] pointB, double[] vectorB) {
        // PointA --> VectorA
        // PointB --> VectorB

        double[] cross = crossProduct(vectorA, vectorB);
        double d = cross[0] * (pointB[0]) + cross[1] * (pointB[1]) + cross[2] * (pointB[2]);
        double Numerator = (cross[0] * pointA[0]) + (cross[1] * pointA[1]) + (cross[2] * pointA[2]) - d;
        double Denominator = sqrt(pow(cross[0], 2) + pow(cross[1], 2) + pow(cross[2], 2));
        return abs(Numerator / Denominator);
    }

    /*------------------------------------------DISTANCE BETWEEN PLANES------------------------------------------------*/
    public static double distancePlane(double[] PlaneA, double[] PlaneB) {
        double Denominator, Numerator, Result, Aux;
        double[] PlaneAux = new double[4];

        // Se valores de Ax diferirem de Bx entra no if
        // Esse if fará a divisão do PlanoB pelo valor de Ax/Bx
        // Após isso, fará comparação se o PlanoAuxY != PlanoAY eles se interceptam
        // Após isso, fará comparação se o PlanoAuxZ != PlanoAZ eles se interceptam
        // Se não, ele fará a conta normalmente
        double a = pow(PlaneAux[0], 2) + pow(PlaneAux[1], 2) + pow(PlaneAux[2], 2);
        if (PlaneA[0] != PlaneB[0]) {
            Aux = PlaneA[0] / PlaneB[0];
            for (int i = 0; i < PlaneA.length; i++) {
                PlaneAux[i] = PlaneB[i] * Aux;
            }
            if (PlaneAux[1] != PlaneA[1]) {
                return -1; //Intersect
            } else if (PlaneAux[2] != PlaneA[2]) {
                return -1; //Intersect
            } else {
                Numerator = abs(PlaneAux[3] - PlaneA[3]);
                Denominator = sqrt(a);
                Result = Numerator / Denominator;
            }
        }
        // Se não, ele fará a conta normalmente
        else {
            Numerator = abs(PlaneAux[3] - PlaneA[3]);
            Denominator = sqrt(a);
            Result = Numerator / Denominator;
        }
        return Result;
    }

    // Método para calcular o produto escalar de dois vetores
    public static double produtoEscalar(double[] vec1, double[] vec2) {
        double resultado = 0.0;
        for (int i = 0; i < vec1.length; i++)
            resultado += vec1[i] * vec2[i];
        return resultado;
    }

    // Método para calcular a magnitude de um vetor
    public static double magnitude(double[] vec) {
        return Math.sqrt(produtoEscalar(vec, vec));
    }

    // Método para verificar se dois vetores são paralelos
    public static boolean saoParalelos(double[] vec1, double[] vec2) {
        double ratio = vec1[0] / vec2[0];
        for (int i = 1; i < vec1.length; i++) {
            if (vec1[i] / vec2[i] != ratio)
                return false;
        }
        return true;
    }

    // Método para verificar se dois vetores são perpendiculares
    public static boolean saoPerpendiculares(double[] vec1, double[] vec2) {
        return produtoEscalar(vec1, vec2) == 0;
    }

    // Método para projetar vec1 em vec2
    public static double[] projecao(double[] vec1, double[] vec2) {
        double produtoEscalar = produtoEscalar(vec1, vec2);
        double magnitudeAoQuadrado = magnitude(vec2) * magnitude(vec2);
        double[] resultado = new double[vec1.length];
        for (int i = 0; i < vec1.length; i++) {
            resultado[i] = (produtoEscalar / magnitudeAoQuadrado) * vec2[i];
            resultado[i] = Math.round(resultado[i] * 10) / 10.0;  // Arredondamento para 1 casa decimal
        }
        return resultado;
    }
}