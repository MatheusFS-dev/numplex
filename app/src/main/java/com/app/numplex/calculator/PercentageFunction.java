package com.app.numplex.calculator;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "percentage")
@FunctionParameter(name = "expression")
@FunctionParameter(name = "operator")
public class PercentageFunction extends AbstractFunction {

    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... parameterValues) {

        double percentage = parameterValues[0].getNumberValue().doubleValue();
        double expressionValue = parameterValues[1].getNumberValue().doubleValue();

        int operatorValue = (int) parameterValues[2].getNumberValue().doubleValue();
        double result;
        switch (operatorValue) {
            case 0 -> result = expressionValue + (expressionValue * (percentage / 100));
            case 1 -> result = expressionValue - (expressionValue * (percentage / 100));
            case 2 -> result = expressionValue * (percentage / 100);
            case 3 -> {
                if (percentage == 0) {
                    throw new IllegalArgumentException("Cannot divide by zero percent");
                }
                result = expressionValue / (percentage / 100);
            }
            default -> throw new IllegalArgumentException("Invalid operation in the input string");
        }

        return new EvaluationValue(result);
    }
}
