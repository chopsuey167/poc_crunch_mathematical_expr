package com.thr.crunch;

import entity.Formula;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

public class CrunchMain {

  public static void main(String[] args) {

    // Set initialized values
    Map<String, Double> initializedValues = new LinkedHashMap<>();
    initializedValues.put("salary", 12000.0);
    initializedValues.put("teoricdays", 30.0);
    initializedValues.put("workeddays", 5.0);
    initializedValues.put("salary_per_day", 0.0);
    initializedValues.put("salary_calculated", 0.0);
    initializedValues.put("percentage_essalud", 0.09);
    initializedValues.put("essalud", 0.0);
    initializedValues.put("min_salary_flag", 0.0);
    initializedValues.put("min_salary", 1025.0);
    initializedValues.put("bonus_low_salary", 0.0);
    initializedValues.put("bonus_high_salary", 0.0);

    // Set formula
    Map<String, Formula> formula = buildFormula();

    // Execute each formula and conditionals
    for (Map.Entry<String, Formula> formulaDetail : formula.entrySet()) {

      System.out.println("Key = " + formulaDetail.getKey() +
          ", Value = " + formulaDetail.getValue());

      if (!Objects.isNull(formulaDetail.getValue().getKeyJumper())
          && formulaDetail.getValue().getValueJumper() == initializedValues.get(
          formulaDetail.getValue().getKeyJumper())) {
        continue;
      }

      double result = 0;

      result = executeFormulaLogic(initializedValues, formulaDetail);

      initializedValues.put(formulaDetail.getValue().getOutputKey(), result);

      System.out.println(
          formulaDetail.getKey() + " - " + initializedValues.get(formulaDetail.getValue().getOutputKey()));
    }

    // Check final values of payroll concepts
    for (Map.Entry<String, Double> finalValues : initializedValues.entrySet()) {
      System.out.println("Key = " + finalValues.getKey() +
          ", Value = " + finalValues.getValue());
    }
  }

  private static Map<String, Formula> buildFormula() {
    Map<String, Formula> formula = new LinkedHashMap<>();
    formula.put("formula.salary_per_day",
        Formula.builder()
            .formula("salary / teoricdays")
            .inputs(List.of("salary", "teoricdays"))
            .outputKey("salary_per_day")
            .type("logic")
            .build());
    formula.put("formula.salary_calculated",
        Formula.builder()
            .formula("salary / teoricdays * workeddays")
            .inputs(List.of("salary", "teoricdays", "workeddays"))
            .outputKey("salary_calculated")
            .type("logic")
            .build());
    formula.put("formula.essalud",
        Formula.builder()
            .formula("salary_calculated * percentage_essalud")
            .inputs(List.of("salary_calculated", "percentage_essalud"))
            .outputKey("essalud")
            .type("logic")
            .build());
    formula.put("formula.essalud_changed",
        Formula.builder()
            .formula("0.065")
            .inputs(null)
            .outputKey("percentage_essalud")
            .type("logic")
            .build());
    formula.put("formula.min_salary_conditional",
        Formula.builder()
            .formula("salary < min_salary")
            .inputs(List.of("salary", "min_salary"))
            .outputKey("min_salary_flag")
            .type("conditional")
            .build());

    formula.put("formula.bonus_low_salary",
        Formula.builder()
            .formula("salary * 0.5")
            .inputs(List.of("salary"))
            .outputKey("bonus_low_salary")
            .type("logic")
            .keyJumper("min_salary_flag")
            .valueJumper(0.0)
            .build());

    formula.put("formula.bonus_high_salary",
        Formula.builder()
            .formula("salary * 0.2")
            .inputs(List.of("salary"))
            .outputKey("bonus_high_salary")
            .type("logic")
            .keyJumper("min_salary_flag")
            .valueJumper(1.0)
            .build());

    formula.put("formula.total_incomes",
        Formula.builder()
            .formula("salary_calculated + bonus_high_salary + bonus_low_salary")
            .inputs(List.of("salary_calculated", "bonus_high_salary", "bonus_low_salary"))
            .outputKey("total_incomes")
            .type("logic")
            .build());
    return formula;
  }

  private static double executeFormulaLogic(Map<String, Double> initializedValues,
      Entry<String, Formula> formulaDetail) {
    double result;
    CompiledExpression exp;
    if (Objects.isNull(formulaDetail.getValue().getInputs())) { // if in formula is seted a fixed value
      exp = Crunch.compileExpression(formulaDetail.getValue().getFormula());
      result = exp.evaluate();

    } else { // if you use variables in formula
      Map<String, Double> filtermap = filterMap(formulaDetail.getValue().getInputs(), initializedValues);

      String[] variableNames = filtermap.keySet().toArray(new String[filtermap.size()]);
      double[] objects = filtermap.values().stream().mapToDouble(d -> d).toArray();

      EvaluationEnvironment env = new EvaluationEnvironment();
      env.setVariableNames(variableNames);

      exp = Crunch.compileExpression(formulaDetail.getValue().getFormula(), env);
      result = exp.evaluate(objects);
    }
    return result;
  }


  private static Map<String, Double> filterMap(List<String> inputNames, Map<String, Double> init) {
    EvaluationEnvironment env = new EvaluationEnvironment();

    Map<String, Double> filterMap = init.entrySet().stream()
        .filter(entry -> inputNames.contains(entry.getKey()))
        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

    return filterMap;
  }

}
