package com.app.numplex.calculator;


import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "value")
public class Log2Function extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(
            Expression expression, Token functionToken, EvaluationValue... parameterValues) {

        EvaluationValue value = parameterValues[0];
        if(parameterValues.length != 1){
            throw new IllegalArgumentException("Not one parameter");
        }

        return new EvaluationValue(Math.log(value.getNumberValue().doubleValue()) / Math.log(2));
    }
}


