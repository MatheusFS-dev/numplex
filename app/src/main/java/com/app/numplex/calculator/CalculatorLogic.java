package com.app.numplex.calculator;


import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorLogic {

    private static boolean rad = false;

    public static String evaluate(String str) throws EvaluationException, ParseException {

        if (str.contains("log(%)") || str.contains("ln(%)") ||
                str.contains("sin(%)") || str.contains("cos(%)") ||
                str.contains("tan(%)") || str.contains("asin(%)") ||
                str.contains("acos(%)") || str.contains("atan(%)")) {
            throw new RuntimeException("A expressão não pode conter % seguido imediatamente por um número após a função");
        }

        if(rad){
            str = str.replace("sin", "sinr");
            str = str.replace("cos", "cosr");
            str = str.replace("tan", "tanr");

            str = str.replace("asin", "asinr");
            str = str.replace("acos", "acosr");
            str = str.replace("atan", "atanr");
        }

        str = str.replace(" ", "");
        str = str.replace("Ans", CalculatorActivity.getANS());
        str = str.replace("log(", "log10(");
        str = str.replace("ln", "log");
        str = str.replace("π", "pi");
        str = str.replace("÷", "/");
        str = str.replace("×", "*");
        while (str.contains("!"))
            str = transformFactorial(str);
        while (str.contains("%"))
            str = transformPercentage(str);
        while(str.contains("√"))
            str = transformSqrt(str);
        while(str.contains("\u221B"))
            str = transformCbrt(str);

        ExpressionConfiguration configuration =
                ExpressionConfiguration.defaultConfiguration()
                        .withAdditionalFunctions(
                                Map.entry("cbrt", new CbrtFunction()),
                                Map.entry("log2", new Log2Function()),
                                Map.entry("Percentage", new PercentageFunction()),
                                Map.entry("Fac", new FacFunction()));

        Expression expression = new Expression(str, configuration);
        EvaluationValue result = expression.evaluate();


        return String.valueOf(result.getNumberValue().doubleValue());
    }

    public static String transformSqrt(String input) {
        if (input.contains("√%")) {
            throw new RuntimeException("A expressão não pode conter √%");
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '√') {
                sb.append("sqrt(");
                i++;
                if (input.charAt(i) == '(') {
                    int j = i+1;
                    int count = 1;
                    while (count > 0) {
                        j++;
                        if (input.charAt(j) == '(') count++;
                        else if (input.charAt(j) == ')') count--;
                    }
                    String insideSqrt = input.substring(i+1, j);
                    sb.append(insideSqrt);
                    i = j+1;
                } else {
                    while (i < input.length() && Character.isDigit(input.charAt(i))) {
                        sb.append(input.charAt(i));
                        i++;
                    }
                }
                sb.append(")");
            } else {
                sb.append(input.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }

    public static String transformCbrt(String input) {
        if (input.contains("\u221B%")) {
            throw new RuntimeException("A expressão não pode conter √%");
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '\u221B') {
                sb.append("cbrt(");
                i++;
                if (input.charAt(i) == '(') {
                    int j = i+1;
                    int count = 1;
                    while (count > 0) {
                        j++;
                        if (input.charAt(j) == '(') count++;
                        else if (input.charAt(j) == ')') count--;
                    }
                    String insideSqrt = input.substring(i+1, j);
                    sb.append(insideSqrt);
                    i = j+1;
                } else {
                    while (i < input.length() && Character.isDigit(input.charAt(i))) {
                        sb.append(input.charAt(i));
                        i++;
                    }
                }
                sb.append(")");
            } else {
                sb.append(input.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }

    public static String transformFactorial(String input) {
        if (input.matches(".*!%.*")) {
            throw new RuntimeException("A expressão não pode conter ! seguido imediatamente por % e um número");
        }
        if (input.matches(".*%!.*")) {
            throw new RuntimeException("A expressão não pode conter % seguido imediatamente por um número e então por !");
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '!') {
                int j = i - 1;
                while (j >= 0 && (Character.isDigit(input.charAt(j)) || input.charAt(j) == '+' || input.charAt(j) == '-')) {
                    j--;
                }
                if (j >= 0 && input.charAt(j) == ')') {
                    int openParenthesesCount = 1;
                    int k = j - 1;
                    while (k >= 0 && openParenthesesCount > 0) {
                        if (input.charAt(k) == ')') {
                            openParenthesesCount++;
                        } else if (input.charAt(k) == '(') {
                            openParenthesesCount--;
                        }
                        k--;
                    }
                    if (openParenthesesCount == 0) {
                        String expressionWithinParentheses = input.substring(k + 2, j);
                        sb.append(input, 0, k + 1).append("Fac(").append(expressionWithinParentheses).append(")");
                        input = sb + input.substring(i + 1);
                        sb.setLength(0);
                        i = k + 1;
                    } else {
                        sb.append(input, 0, i + 1);
                        input = sb + input.substring(i + 1);
                        sb.setLength(0);
                        i++;
                    }
                } else {
                    String factorialExpression = input.substring(j + 1, i);
                    sb.append(input, 0, j + 1).append("Fac(").append(factorialExpression).append(")");
                    input = sb + input.substring(i + 1);
                    sb.setLength(0);
                    i = j + 1;
                }
            }
            i++;
        }
        sb.append(input);
        return sb.toString();
    }

    public static String transformPercentage(String expression) {
        if (expression.matches(".*[+\\-*/]%.*")) {
            throw new RuntimeException("A expressão não pode conter um operador seguido imediatamente por %");
        }
        if(expression.equals("%")) {
            throw new RuntimeException("Erro");
        }
        if(expression.matches("%[0-9]+")) {
            throw new RuntimeException("A expressão não pode começar com % seguido imediatamente por um número");
        }
        if (expression.matches(".*%[0-9]+%.*")) {
            throw new RuntimeException("A expressão não pode conter % seguido imediatamente por um número e então por outro %");
        }


        Map<String, Integer> operatorMapping = Map.of(
                "+", 0,
                "-", 1,
                "*", 2,
                "/", 3
        );

        String percentagePattern = "(\\d+)(\\s*([+\\-*/])\\s*(\\d+)?)*((%)+)";
        Pattern pattern = Pattern.compile(percentagePattern);
        Matcher matcher = pattern.matcher(expression);
        StringBuilder result = new StringBuilder(expression);

        while (matcher.find()) {
            String firstNumber = matcher.group(1);
            String operator = matcher.group(3);
            String secondNumber = matcher.group(4);

            if (secondNumber == null || operator == null) {
                secondNumber = "001";
                operator = "*";
            }


            operator = Objects.requireNonNull(operatorMapping.get(operator)).toString();

            String transformed = "Percentage(" + secondNumber + ", " + firstNumber + ", " + operator + ")";
            result.replace(matcher.start(), matcher.end(), transformed);
            matcher = pattern.matcher(result.toString());
        }

        return result.toString();
    }


    public static int aux(char expr) {

        return switch (expr) {
            case '+' -> 0;
            case '-' -> 1;
            case '*' -> 2;
            case '/' -> 3;
            default -> throw new RuntimeException("Erro");
        };
    }


    public static void setRad(boolean rad) {
        CalculatorLogic.rad = rad;
    }
}
