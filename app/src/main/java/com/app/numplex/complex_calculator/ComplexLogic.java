package com.app.numplex.complex_calculator;

import static com.app.numplex.complex_calculator.ComplexCalculatorActivity.scientificFormat;

import org.apache.commons.math3.complex.Complex;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Stack;

public class ComplexLogic {
    private final static double pi = 3.141592653589793;
    private static int newPos = 0;

    public static NumplexComplex evaluate(String expression) {
        expression = expression.replaceAll("--", ""); //Fixes multiple negatives bug
        expression = expression.replaceAll("j", "i");
        // Replacing all special characters:
        expression = replaceSpecialCharacters(expression);

        /*------------------------------------------------------------------------*/
        //Checking syntax
        if(expression.contains("ii") || expression.contains("si") || expression.contains("x") || expression.contains("[") || expression.contains("º"))
            throw new RuntimeException();

        String[] errorList1 = {"C", "I", ",", "A", "P"};
        String[] errorList2 = {"C", "I", ",", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "A", "P"};
        for (String s : errorList1)
            if (expression.contains("i" + s))
                throw new RuntimeException();
        for (String s : errorList2)
            if (expression.contains("s" + s) || expression.contains(s + "A") || expression.contains(s + "P"))
                throw new RuntimeException();
        /*------------------------------------------------------------------------*/

        Stack<NumplexComplex> numplexComplexNumbers = new Stack<>(); // Stack for complex numbers
        Stack<Character> operators = new Stack<>(); // Stack for operators:

        String strComplex; // String to be parsed after

        char[] input = expression.toCharArray(); // Creating new array from String
        char value; // Value of input at certain position

        for (int i = 0; i < input.length; i++) {
            value = input[i];

            //Current token is a number. Reading complex number:
            if (isNumber(value) && !isPolar(input, i)) {
                strComplex = findComplex(input, i);
                numplexComplexNumbers.push(parseComplex(strComplex));
                i += (newPos - 1);
            }
            //Current token is Ans:
            else if(value == 'A'){
                numplexComplexNumbers.push(ComplexCalculatorActivity.getANS());
                i += 3;
            }
            //Current token is a polar number. Reading polar number:
            else if (value == '∠') {
                numplexComplexNumbers.push(parsePolar(input, i));
                i += newPos;
            }
            //Current token is a Capacitor or Inductor. Reading number:
            else if (value == 'C' || value == 'I') {
                numplexComplexNumbers.push(parseCapInd(input, i, value));
                i = newPos;
            }
            //Current token is an opening brace, push it to 'operators'
            else if (input[i] == '(')
                operators.push(input[i]);

                // Closing brace encountered, solve entire brace
            else if (input[i] == ')') {
                while (operators.peek() != '(')
                    numplexComplexNumbers.push(applyOp(operators.pop(), numplexComplexNumbers.pop(), numplexComplexNumbers.pop()));
                operators.pop();
            }

            // Current token is an operator
            else if (input[i] == '+' || input[i] == '‖' || input[i] == '﹣' || input[i] == '÷' || input[i] == '×') {

                /* While top of operators has same or greater precedence to current token, which is an operator.
                Apply operator on top of 'ops' to top two elements in values stack */
                while (!operators.empty() && hasPrecedence(input[i], operators.peek()))
                    numplexComplexNumbers.push(applyOp(operators.pop(), numplexComplexNumbers.pop(), numplexComplexNumbers.pop()));

                //Push current token to 'operators'.
                operators.push(input[i]);
            }
        }

        //Checking expression
        if (numplexComplexNumbers.size() != 1 && operators.size() == 0)
            throw new RuntimeException();

        //Entire expression has been parsed at this point, apply remaining operators to remaining complex
        while (!operators.empty())
            numplexComplexNumbers.push(applyOp(operators.pop(), numplexComplexNumbers.pop(), numplexComplexNumbers.pop()));

        //Top of 'complex' contains result, return it
        return numplexComplexNumbers.pop();
    }

    //Returns true if 'op2' has higher or same precedence as 'op1', otherwise returns false.
    public static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        return (op1 != '×' && op1 != '÷') || (op2 != '+' && op2 != '﹣' && op2 != '|');
    }

    //Calculating result:
    public static NumplexComplex applyOp(char op, NumplexComplex b, NumplexComplex a) {
        NumplexComplex aux = NumplexComplex.plus(
                NumplexComplex.divides(new NumplexComplex(1, 0), a),
                NumplexComplex.divides(new NumplexComplex(1, 0), b)
        ); //Auxiliary variable

        switch (op) {
            case '+':
                return NumplexComplex.plus(a, b);
            case '﹣':
                return NumplexComplex.minus(a, b);
            case '÷':
                if (b.getReal() == 0 && b.getImaginary() == 0)
                    throw new RuntimeException();
                return NumplexComplex.divides(a, b);
            case '×':
                return NumplexComplex.times(a, b);
            case '‖':
                if (a.getReal() == 0 && a.getImaginary() == 0)
                    return b;
                else if (b.getReal() == 0 && b.getImaginary() == 0)
                    return a;
                else if (aux.getReal() == 0 && aux.getImaginary() == 0)
                    return new NumplexComplex(0, 0);
                else
                    return NumplexComplex.divides(new NumplexComplex(1, 0), aux);
            default:
                throw new RuntimeException();
        }
    }

    // Method to check if x is a number
    private static boolean isNumber(char x) {
        return (x >= '0' && x <= '9') ||
                x == '.' ||
                x == '-' ||
                x == 'E' ||
                x == 'π' ||
                x == 'i';
    }

    // Method to replace all special characters
    private static String replaceSpecialCharacters(String expression) {
        expression = expression.replace("μ", "E-6");
        expression = expression.replace("m", "E-3");
        expression = expression.replace("n", "E-9");
        expression = expression.replace("p", "E-12");
        expression = expression.replace("k", "E3");
        expression = expression.replace("M", "E6");
        expression = expression.replace("G", "E9");
        expression = expression.replace(" ", "");

        return expression;
    }

    // Method to parse complex in a string:
    public static NumplexComplex parseComplex(String text) {
        StringBuilder expression = new StringBuilder();

        if(text.contains("+-"))
            text = text.replace("+-", "-");

        char[] input = text.toCharArray();
        double imaginaryPart = 0;
        double realPart = 0;
        char value;

        for (int i = 0; i < input.length; i++) {
            value = input[i];

            if(value == '-' && i != 0 && input[i - 1] != 'E') {
                realPart = Double.parseDouble(expression.toString());
                expression = new StringBuilder("-");
            }
            else if(value == '+'){
                realPart = Double.parseDouble(expression.toString());
                expression = new StringBuilder();
            }
            else if(value == 'i'){
                if(expression.toString().equals(""))
                    expression.append("1");
                imaginaryPart = Double.parseDouble(expression.toString());
                break;
            }
            else if (isNumber(value))
                expression.append(value);
        }

        return new NumplexComplex(realPart, imaginaryPart);
    }

    // Method to create new string from input with complex number:
    public static String findComplex(char[] input, int position) {
        StringBuilder expression = new StringBuilder();
        String outcome;
        boolean hasFirstPlus = false;
        boolean hasImag = false;
        char value;

        for (int i = position; i < input.length && !hasImag; i++) {
            value = input[i];

            if (isNumber(value) || value == '+' || value == 'i') {
                if (value == '+' && !hasFirstPlus)
                    hasFirstPlus = true;
                else if (value == '+')
                    break;
                else if (value == 'i') {

                    if(i + 1 < input.length && i != 0
                            && (isNumber(input[i-1]) && input[i-1] != '-')
                            && (isNumber(input[i+1]) && input[i+1] != '+'))
                        throw new RuntimeException();

                    if(i == 0){
                        i++;
                        while(i < input.length && isNumber(input[i]) && input[i] != '+')
                            expression.append(input[i++]);
                        i--;
                    }
                    else if((i + 1 < input.length)
                            && (isNumber(input[i+1]) && input[i+1] != '+') && (!isNumber(input[i-1]) || input[i-1] == '-')) {
                        i++;
                        while(i < input.length && isNumber(input[i]) && input[i] != '+')
                            expression.append(input[i++]);
                        i--;
                    }
                    hasImag = true;
                }
                else if(value == '-')
                    findNegError(input, i);
                expression.append(value);
            } else
                break;
        }
        expression = new StringBuilder(expression.toString().replace("-i", "-1i"));

        if (!hasImag) {
            if (expression.toString().contains("+")) {
                String[] str;
                String aux = expression.toString();
                str = aux.split("\\+");
                expression = new StringBuilder(str[0]);
            }

            newPos = expression.length();
            expression.append("+0i");
            outcome = expression.toString();
        } else if (!expression.toString().contains("+")) {
            String aux = expression.toString();
            aux = aux.replace("E-", "");

            if (aux.contains("-")) {
                String[] str;
                str = aux.split("-");
                if (str[0].equals(""))
                    outcome = "0" + expression;
                else
                    outcome = expression.toString();
            } else
                outcome = "0+" + expression;
            newPos = expression.length();
        } else {
            outcome = expression.toString();
            newPos = expression.length();
        }
        return outcome;
    }

    // Auxiliary method:
    public static void findNegError(char[] input, int position){
        char value;
        boolean hasImag = false;

        for(int i = position + 1; i < input.length; i++){
            value = input[i];
            if(value == 'i')
                hasImag = true;
            if(!isNumber(value))
                break;
        }

        if(position - 1 >= 0){
            if (isNumber(input[position - 1]) && input[position - 1] != 'E' && !hasImag)
                throw new RuntimeException();
        }
    }

    // Method to parse polar form:
    public static NumplexComplex parsePolar(char[] input, int position) {
        StringBuilder expression = new StringBuilder();

        // Angle:
        double angle;
        for (int i = position + 1; i < input.length; i++) {
            char value = input[i];
            if (isNumber(value)) {
                expression.append(value);
            } else {
                break;
            }
        }
        newPos = expression.length();

        // Angle has π, could be more than one:
        if (expression.indexOf("π") != -1) {
            int count = expression.length() - expression.toString().replace("π", "").length();
            expression = expression.deleteCharAt(expression.indexOf("π"));
            if (expression.length() == 0 || expression.toString().equals("-")) {
                expression.append("1");
            }
            angle = Double.parseDouble(expression.toString()) * Math.pow(Math.PI, count);
        } else {
            angle = Double.parseDouble(expression.toString());
        }

        //Modules:
        double modules;
        expression.setLength(0);
        for (int i = position - 1; i >= 0; i--) {
            char value = input[i];
            if (isNumber(value)) {
                expression.append(value);
            } else {
                break;
            }
        }
        modules = Double.parseDouble(expression.reverse().toString());

        //Calculating complex from polar form:
        double sinePart = Math.sin(angle);
        double cosPart = Math.cos(angle);
        double imag_aux = modules * sinePart;
        double real_aux = modules * cosPart;
        if (NumplexComplex.getPolarunit() != 1) {
            sinePart = Math.sin(angle / (180 / Math.PI));
            cosPart = Math.cos(angle / (180 / Math.PI));
            imag_aux = modules * sinePart;
            real_aux = modules * cosPart;
        }
        imag_aux = Double.parseDouble(String.format(Locale.ENGLISH,"%.12f", imag_aux));
        real_aux = Double.parseDouble(String.format(Locale.ENGLISH, "%.12f", real_aux));
        return new NumplexComplex(real_aux, imag_aux);
    }

    // Method to parse a capacitor:
    public static NumplexComplex parseCapInd(char[] input, int position, char type) {
        StringBuilder expression = new StringBuilder();
        int i = (type == 'I') ? -1 : 0; // Count of array input
        double frequency; // frequency
        double capacitance = 0.0; // Capacitance
        boolean hasComma = false; //Checks if string contains ','
        boolean hasClosingBrace = false; //Checks if string contains ')'

        // Capacitance or Inductance and Frequency:
        for (; i < input.length; i++) {
            char value = input[i + position + 7];
            if (isNumber(value)) {
                expression.append(value);
            } else if (value == ',') {
                hasComma = true;
                capacitance = Double.parseDouble(expression.toString());
                expression.setLength(0);
            } else if (value == ')') {
                hasClosingBrace = true;
                newPos = i+7;
                break;
            } else {
                throw new RuntimeException();
            }
        }

        // Frequency has π, could be more than one:
        if (expression.toString().contains("π")) {
            int count = expression.length() - expression.toString().replace("π", "").length();
            expression.setLength(0);
            expression.append(expression.toString().replace("π", ""));
            if (expression.length() == 0 || expression.toString().equals("-")) {
                expression.append("1");
            }
            frequency = Double.parseDouble(expression.toString()) * Math.pow(pi, count);
        } else {
            frequency = Double.parseDouble(expression.toString());
        }

        //Calculating complex:
        if (frequency <= 0.0 || capacitance <= 0.0 || !hasComma || !hasClosingBrace) {
            throw new RuntimeException();
        }

        if (ComplexCalculatorActivity.getFrequency()) {
            frequency *= 2 * pi;
        }

        double reactance = (type == 'C') ? (-1.0 / (capacitance * frequency)) : (capacitance * frequency);
        return new NumplexComplex(0.0, reactance);
    }

    // Method that return if number is in polar form:
    public static boolean isPolar(char[] input, int position) {
        char value;
        boolean outcome = false; // If true the number is in polar form
        for (int i = position; i < input.length; i++) {
            value = input[i];
            if (!isNumber(value)) {
                if (value == '∠')
                    outcome = true;
                break;
            }
        }
        return outcome;
    }

    // Method to calculate admittance
    public static String convertImpedanceToAdmittance(double real, double imag) {
        // Cria um novo objeto Complex a partir das partes real e imaginária
        Complex z = new Complex(real, -1.0*imag);

        z = z.conjugate().reciprocal();
        BigDecimal realBigDecimal = BigDecimal.valueOf(z.getReal());
        BigDecimal imagBigDecimal = BigDecimal.valueOf(z.getImaginary());

        String bigDecimal;
        String realString = realBigDecimal.toString();
        String imagString = imagBigDecimal.toString();

        if(realString.contains("E") || imagString.contains("E")|| realString.length() > 5 || imagString.length() > 5){
            bigDecimal = scientificFormat(String.format(Locale.ENGLISH, "%.4E", z.getReal()), String.format(Locale.ENGLISH, "%.4E", z.getImaginary()));
            bigDecimal = bigDecimal.replace("00x", "x");
            bigDecimal = bigDecimal.replace("0x", "x");
        }
        else {
            if (z.getImaginary() < 0) {
                bigDecimal = realString + " - " + imagString.replace("-", "") + "i";
                if (realBigDecimal.doubleValue() == 0)
                    bigDecimal = bigDecimal.replace("0.0 - ", "-");
                bigDecimal = bigDecimal.replace(" - 0.0i", "");
            } else {
                bigDecimal = realString + " + " + imagString + "i";
                if (realBigDecimal.doubleValue() == 0)
                    bigDecimal = bigDecimal.replace("0.0 + ", "");
                bigDecimal = bigDecimal.replace(" + 0.0i", "");
            }
            if (z.getImaginary() == 0 && bigDecimal.endsWith(".0"))
                bigDecimal = bigDecimal.replace(".0", " ");
            else
                bigDecimal = bigDecimal.replace(".0 ", " ");
            bigDecimal = bigDecimal.replace(".0i", "i");
        }

        // Calcula a admitância e retorna o resultado
        return bigDecimal;
    }
}