package com.app.numplex.digital;

import static java.lang.Math.pow;

import java.math.BigDecimal;

public class ConversionLogic {

    //-----------------------------------------------Octal -> Decimal---------------------------------------------------
    public static String OctalFloatToDecimal(String string){
        boolean itsNegative = false;
        if (string.startsWith("-")){
            itsNegative = true;
            string = string.replaceFirst("-", "");
        }
        int soma = 0;
        float soma1 = 0, res1;

        if (checaString(string)) {
            string = alteraString(string);
        }

        String[] splitted = string.split("\\."); // separando
        String parteReal = splitted[0]; // 16
        String parteFrac = splitted[1]; // 43

        char[] parteRealArray = new StringBuilder(parteReal).reverse().toString().toCharArray(); // invertendo para multiplicar direto, convertendo para vetor char
        char[] parteFracArray = parteFrac.toCharArray(); // convertendo para vetor de char
        // até aqui de boa

        for(int i = 0; i < parteReal.length(); i++){
            soma += Integer.parseInt(String.valueOf(parteRealArray[i])) * pow(8, i);
        }

        for(int i = 0; i < parteFrac.length(); i++){
            soma1 += Float.parseFloat(String.valueOf(parteFracArray[i])) * pow(8, (-(i+1)));
        }

        res1 = soma + soma1;
        String output = String.valueOf(res1);
        if(output.contains("E"))
            output = new BigDecimal(res1).toPlainString();
        if(output.endsWith(".0"))
            output = output.replace(".0", "");
        if (itsNegative)
            output = "-" + output;

        return output;
    }
    //------------------------------------------------------------------------------------------------------------------

    //-----------------------------------------------Binario -> Decimal-------------------------------------------------
    public static String BinToDecimal(String string){
        boolean itsNegative = false;
        if (string.startsWith("-")){
            itsNegative = true;
            string = string.replaceFirst("-", "");
        }
        int soma = 0;
        float soma1 = 0, res1;

        if (checaString(string)) {
            string = alteraString(string);
        }

        String[] splitted = string.split("\\."); // separando
        String parteReal = splitted[0]; // 16
        String parteFrac = splitted[1]; // 43

        char[] parteRealArray = new StringBuilder(parteReal).reverse().toString().toCharArray(); // invertendo para multiplicar direto, convertendo para vetor char
        char[] parteFracArray = parteFrac.toCharArray(); // convertendo para vetor de char
        // até aqui de boa

        for(int i = 0; i < parteReal.length(); i++){
            soma += Integer.parseInt(String.valueOf(parteRealArray[i])) * pow(2, i);
        }

        for(int i = 0; i < parteFrac.length(); i++){
            soma1 += Float.parseFloat(String.valueOf(parteFracArray[i])) * pow(2, (-(i+1)));
        }

        res1 = soma + soma1;

        String output = String.valueOf(res1);
        if(output.endsWith(".0"))
            output = output.replace(".0", "");
        if(output.contains("E"))
            output = new BigDecimal(res1).toPlainString();
        if (itsNegative)
            output = "-" + output;

        return output;
    }
    //------------------------------------------------------------------------------------------------------------------

    public static boolean checaString(String string){ // função pra checar se tem dot
        char[] charArray = string.toCharArray();
        boolean check = false;

        for (char c : charArray) {
            if (c == '.') {
                check = true;
                break;
            }
        }
        return !check;
    }

    public static String alteraString(String string){
        string = string + ".0";
        return string;
    }
    //-----------------------------------------------Hex -> Decimal-----------------------------------------------------
    public static String HexadecimalFloatToDecimal(String string){ // substituir por só adicionar um . na string
        boolean itsNegative = false;
        if (string.startsWith("-")){
            itsNegative = true;
            string = string.replaceFirst("-", "");
        }
        int soma = 0;
        float soma1 = 0, res1;

        if (checaString(string)) {
            string = alteraString(string);
        }

        String[] splitted = string.split("\\."); // separando
        String parteReal = splitted[0];
        String parteFrac = splitted[1];

        char[] parteRealArray = new StringBuilder(parteReal).reverse().toString().toCharArray(); // invertendo para multiplicar direto, convertendo para vetor char
        char[] parteFracArray = parteFrac.toCharArray(); // convertendo para vetor de char
        // até aqui de boa

        for(int i = 0; i < parteReal.length(); i++){
            switch (parteRealArray[i]) {
                case 'A':
                    soma += 10 * pow(16, i);
                    break;
                case 'B':
                    soma += 11 * pow(16, i);
                    break;
                case 'C':
                    soma += 12 * pow(16, i);
                    break;
                case 'D':
                    soma += 13 * pow(16, i);
                    break;
                case 'E':
                    soma += 14 * pow(16, i);
                    break;
                case 'F':
                    soma += 15 * pow(16, i);
                    break;
                default:
                    soma += Integer.parseInt(String.valueOf(parteRealArray[i])) * pow(16, i);
                    break;
            }
        }

        for(int i = 0; i < parteFrac.length(); i++){
            switch (parteFracArray[i]) {
                case 'A':
                    soma1 += 10 * pow(16, (-(i + 1)));
                    break;
                case 'B':
                    soma1 += 11 * pow(16, (-(i + 1)));
                    break;
                case 'C':
                    soma1 += 12 * pow(16, (-(i + 1)));
                    break;
                case 'D':
                    soma1 += 13 * pow(16, (-(i + 1)));
                    break;
                case 'E':
                    soma1 += 14 * pow(16, (-(i + 1)));
                    break;
                case 'F':
                    soma1 += 15 * pow(16, (-(i + 1)));
                    break;
                default:
                    soma1 += Float.parseFloat(String.valueOf(parteFracArray[i])) * pow(16, (-(i + 1)));
                    break;
            }
        }

        res1 = soma + soma1;
        String output = String.valueOf(res1);
        if(output.contains("E"))
            output = new BigDecimal(res1).toPlainString();
        if(output.endsWith(".0"))
            output = output.replace(".0", "");
        if (itsNegative)
            output = "-" + output;

        return output;
    }
    //------------------------------------------------------------------------------------------------------------------

    //-----------------------------------------------Decimal -> Hex-----------------------------------------------------
    public static String DecimalFloatToHexadecimal(String numm) {
        boolean itsNegative = false;
        if (numm.startsWith("-")){
            itsNegative = true;
            numm = numm.replaceFirst("-", "");
        }
        double num = Double.parseDouble(numm);
        StringBuilder binaryBuilder = new StringBuilder();
        int numIntegral = (int) num;
        double numFractional = num - numIntegral;

        // binary conversion for the integral part
        if(numIntegral == 0){
            binaryBuilder.append("0");
        }
        else{
            while (numIntegral > 0) {
                switch (numIntegral % 16) {
                    case 10:
                        binaryBuilder.append('A');
                        break;
                    case 11:
                        binaryBuilder.append('B');
                        break;
                    case 12:
                        binaryBuilder.append('C');
                        break;
                    case 13:
                        binaryBuilder.append('D');
                        break;
                    case 14:
                        binaryBuilder.append('E');
                        break;
                    case 15:
                        binaryBuilder.append('F');
                        break;
                    default:
                        binaryBuilder.append(numIntegral % 16);
                        break;
                }
                numIntegral /= 16;
            }
            binaryBuilder.reverse(); // pegando do último até o primeiro, já está em binário
        }

        // binary conversion for the fractional part
        if (numFractional != 0) {
            binaryBuilder.append(".");

            while (numFractional != 0) {
                numFractional *= 16;
                switch ((int) numFractional) {
                    case 10:
                        binaryBuilder.append('A');
                        break;
                    case 11:
                        binaryBuilder.append('B');
                        break;
                    case 12:
                        binaryBuilder.append('C');
                        break;
                    case 13:
                        binaryBuilder.append('D');
                        break;
                    case 14:
                        binaryBuilder.append('E');
                        break;
                    case 15:
                        binaryBuilder.append('F');
                        break;
                    default:
                        binaryBuilder.append((int) numFractional);
                        break;
                }
                numFractional = numFractional - (int) numFractional;
            }
        }
        if (itsNegative)
            binaryBuilder = new StringBuilder("-" + binaryBuilder);

        return binaryBuilder.toString();
    }
    //------------------------------------------------------------------------------------------------------------------

    //-----------------------------------------------Decimal -> Octal---------------------------------------------------
    public static String DecimalFloatToOctal(String numm) {
        boolean itsNegative = false;
        if (numm.startsWith("-")){
            itsNegative = true;
            numm = numm.replaceFirst("-", "");
        }
        double num = Double.parseDouble(numm);
        StringBuilder binaryBuilder = new StringBuilder();
        int numIntegral = (int) num;
        double numFractional = num - numIntegral;

        // binary conversion for the integral part
        if(numIntegral == 0){
            binaryBuilder.append("0");
        }
        else{
            while (numIntegral > 0) {
                binaryBuilder.append(numIntegral % 8);
                numIntegral /= 8;
            }
            binaryBuilder.reverse(); // pegando do último até o primeiro, já está em binário
        }

        // binary conversion for the fractional part
        if (numFractional != 0) {
            binaryBuilder.append(".");

            while (numFractional != 0) {
                numFractional *= 8;
                binaryBuilder.append((int) numFractional);
                numFractional = numFractional - (int) numFractional;
            }
        }
        if (itsNegative)
            return "-" + binaryBuilder;
        else
            return binaryBuilder.toString();
    }
    //------------------------------------------------------------------------------------------------------------------

    //-----------------------------------------------Decimal -> Binario-------------------------------------------------
    public static String DecimalFloatToBinary(String numm) {
        boolean itsNegative = false;
        if (numm.startsWith("-")){
            itsNegative = true;
            numm = numm.replaceFirst("-", "");
        }
        double num = Double.parseDouble(numm);
        StringBuilder binaryBuilder = new StringBuilder();
        int numIntegral = (int) num;
        double numFractional = num - numIntegral;

        // binary conversion for the integral part
        if(numIntegral == 0){
            binaryBuilder.append("0");
        }
        else{
            while (numIntegral > 0) {
                binaryBuilder.append(numIntegral % 2);
                numIntegral /= 2;
            }
            binaryBuilder.reverse(); // pegando do último até o primeiro, já está em binário
        }

        // binary conversion for the fractional part
        if (numFractional != 0) {
            binaryBuilder.append(".");

            while (numFractional != 0) {
                numFractional *= 2;
                binaryBuilder.append((int) numFractional);
                numFractional = numFractional - (int) numFractional;
            }
        }
        if (itsNegative)
            binaryBuilder = new StringBuilder("-" + binaryBuilder);

        return binaryBuilder.toString();
    }
    /*----------------------------------------------------------------------------------------------------------------*/
    //Auxiliary methods:
    public static void isBinary(String value){
        char[] input = value.toCharArray();
        for (char c : input) {
            if (c != '1' && c != '0'&& c != '.' && c != '-')
                throw new RuntimeException();
        }
    }

    public static void isDecimal(String value){
        char[] dec = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-'};
        char[] input = value.toCharArray();
        boolean ok = false;
        for (char c : input) {
            for (char item : dec) {
                if (c == item) {
                    ok = true;
                    break;
                }
            }
            if (ok)
                ok = false;
            else
                throw new RuntimeException();
        }
    }

    public static void isOctal(String value){
        char[] octal = {'0', '1', '2', '3', '4', '5', '6', '7', '.', '-'};
        char[] input = value.toCharArray();
        boolean ok = false;
        for (char c : input) {
            for (char item : octal) {
                if (c == item) {
                    ok = true;
                    break;
                }
            }
            if (ok)
                ok = false;
            else
                throw new RuntimeException();
        }
    }

    public static void isHexa(String value){
        char[] hexa = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', '.', '-'};
        char[] input = value.toCharArray();
        boolean ok = false;
        for (char c : input) {
            for (char item : hexa) {
                if (c == item) {
                    ok = true;
                    break;
                }
            }
            if (ok)
                ok = false;
            else
                throw new RuntimeException();
        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/
}
