package com.app.numplex.calculator;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "value")
public class FacFunction extends AbstractFunction {

    @Override
    public EvaluationValue evaluate(
            Expression expression, Token functionToken, EvaluationValue... parameterValues) {

        if (parameterValues.length != 1) {
            throw new IllegalArgumentException("Invalid number of parameters. Expected 1.");
        }

        long number = parameterValues[0].getNumberValue().longValue();

        if (number < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers.");
        }

        long result = 1;

        for (long i = 2; i <= number; i++) {
            result *= i;
        }

        return new EvaluationValue(result);
    }
}
