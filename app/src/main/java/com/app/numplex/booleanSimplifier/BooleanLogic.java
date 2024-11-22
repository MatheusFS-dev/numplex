package com.app.numplex.booleanSimplifier;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;

import java.util.Locale;

public class BooleanLogic {

    public static String simplify(String input) {
        if(input.contains("Ans") && BooleanActivity.getANS() != null)
            input = input.replace("Ans", BooleanActivity.getANS());
        input = input.toUpperCase(Locale.ENGLISH);

        Expression<String> expr = convertToExpression(input);
        Expression<String> simplified = RuleSet.simplify(expr);

        return simplified.toString();
    }

    public static String transformNot(String input) {
        String formattedInput = input;
        int counter = 0;
        int maxIterations = 1000;

        while(formattedInput.contains("'")) {
            counter++;
            for (char letter = 'A'; letter <= 'Z'; letter++) {
                // Replace <letter>' with !<letter>
                formattedInput = formattedInput.replace(letter + "'", "!" + letter);
            }

            if (formattedInput.contains(")'")) {
                // Replace the expected format with the transformed string
                formattedInput = formattedInput.replaceAll("\\(([^)]+)\\)'", "!($1)");
            }
            if (counter == maxIterations)
                throw new RuntimeException();
        }

        return formattedInput;
    }

    public static Expression<String> convertToExpression(String input) {
        // Remove any whitespace from the input string
        String formattedInput = input.replace("\\s+", "");

        // A⊕B
        formattedInput = xorToOr(formattedInput);

        // AA -> A*A
        formattedInput = replaceLetterPairsAndParentheses(formattedInput);

        //'<letter> case and '<(>
        formattedInput = formattedInput.replaceAll("'([a-zA-Z])", "'·$1");
        formattedInput = formattedInput.replaceAll("'\\(", "'·(");

        // Replace any NOT operators with "!"
        formattedInput = transformNot(formattedInput);


        // Replace 0 and 1 with true and false
        formattedInput = formattedInput.replace("0", "false");
        formattedInput = formattedInput.replace("1", "true");

        // Replace any AND operators with "&"
        formattedInput = formattedInput.replace("·", "&");

        // Replace any OR operators with "|"
        formattedInput = formattedInput.replace("+", "|");

        // Surround the entire expression with parentheses
        formattedInput = "(" + formattedInput + ")";

        // Parse the expression string into a jbool Expression object
        return ExprParser.parse(formattedInput);
    }

    public static String convertToString(String input) {
        String formattedInput = input;

        // Remove the surrounding parentheses
        if (input.startsWith("(") && input.endsWith(")"))
            formattedInput = input.substring(1, input.length() - 1);

        // Replace true and false with 0 and 1
        formattedInput = formattedInput.replace("true", "1");
        formattedInput = formattedInput.replace("false", "0");

        // Replace any product operators with "·"
        formattedInput = formattedInput.replace("&", "·");

        // Replace any sum operators with "+"
        formattedInput = formattedInput.replace("|", "+");

        // Replace any complements with the corresponding letter followed by an
        // apostrophe
        for (char letter = 'A'; letter <= 'Z'; letter++)
            formattedInput = formattedInput.replace("!" + letter, letter + "'");

        if (formattedInput.contains("!("))
            // Replace the transformed string with the expected format
            formattedInput = formattedInput.replaceAll("!\\(([^)]+)\\)", "($1)'");

        formattedInput = formattedInput.replaceAll(" ", "");

        return formattedInput;
    }

    public static String replaceLetterPairsAndParentheses(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (i < input.length() - 1 && Character.isLetter(c)
                    && (Character.isLetter(input.charAt(i + 1)) || input.charAt(i + 1) == '(')) {
                result.append(c).append('·');
            } else if (i > 0 && Character.isLetter(c) && input.charAt(i - 1) == ')') {
                result.append('·').append(c);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String xorToOr(String input) {
        if (input.contains("⊕")) {
            String formattedInput = input;
            int xorNumber = countChar(formattedInput, '⊕');

            for (int i = 0; i < xorNumber; i++) {
                for (int j = 0; j < formattedInput.length(); j++) {
                    char c = formattedInput.charAt(j);

                    if (c == '⊕') {
                        String expression1 = "";
                        for (int k = j - 1; k >= 0; k--) {
                            char pc = formattedInput.charAt(k);
                            if (Character.isUpperCase(pc) || pc == '·') {
                                expression1 = pc + expression1;
                            } else if (pc == ')') {
                                // Append everything inside the parentheses
                                int l = k;
                                int openCount = 0;
                                while (l >= 0) {
                                    char ppc = formattedInput.charAt(l);
                                    if (ppc == '(') {
                                        openCount--;
                                        if (openCount == 0) {
                                            expression1 = ppc + expression1;
                                            break;
                                        }
                                    } else if (ppc == ')') {
                                        openCount++;
                                    }
                                    expression1 = ppc + expression1;
                                    l--;
                                }
                                // Break out of loop if we've closed the outermost parentheses
                                if (openCount == 0) {
                                    break;
                                }
                            } else if (pc != '(') {
                                // Stop building the expression if we encounter anything else
                                break;
                            }
                        }

                        String expression2 = "";
                        for (int k = j + 1; k < formattedInput.length(); k++) {
                            char pc = formattedInput.charAt(k);
                            if (Character.isUpperCase(pc)|| pc == '·') {
                                expression2 += pc;
                            } else if (pc == '(') {
                                // Append everything inside the parentheses
                                int l = k;
                                int openCount = 0;
                                while (l < formattedInput.length()) {
                                    char ppc = formattedInput.charAt(l);
                                    if (ppc == ')') {
                                        openCount--;
                                        if (openCount == 0) {
                                            expression2 += ppc;
                                            break;
                                        }
                                    } else if (ppc == '(') {
                                        openCount++;
                                    }
                                    expression2 += ppc;
                                    l++;
                                }
                                // Break out of loop if we've closed the outermost parentheses
                                if (openCount == 0) {
                                    break;
                                }
                            } else {
                                // Stop building the expression if we encounter anything else
                                break;
                            }
                        }

                        String replacement = "((" + expression1 + ")&!(" + expression2 + "))|(!(" + expression1 + ")&("
                                + expression2 + "))";

                        formattedInput = formattedInput.replace(expression1 + "⊕" + expression2, replacement);
                        break;
                    }
                }
            }

            return formattedInput;
        } else
            return input;
    }

    public static int countChar(String input, char target) {
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }
}