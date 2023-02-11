package com.thr.crunch;

import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

public class TestingCrunch {

  public static void main(String[] args) {

    EvaluationEnvironment env = new EvaluationEnvironment();
    env.setVariableNames(new String[]{"salary", "teoricdays","workeddays"});
    CompiledExpression exp = Crunch.compileExpression("salary / teoricdays * workeddays", env);

    double[] doubles = new double[]{3000.0,30.0,15.0};
    System.out.println(doubles);
    System.out.println(exp.evaluate(doubles));


  }
}
