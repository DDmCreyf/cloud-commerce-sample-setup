package com.mirakl.hybris.promotions.definitions.conditions;

import java.math.BigDecimal;
import java.util.Map;

import de.hybris.platform.ruledefinitions.AmountOperator;
import de.hybris.platform.ruledefinitions.conditions.RuleCartTotalConditionTranslator;
import de.hybris.platform.ruledefinitions.conditions.builders.RuleIrAttributeConditionBuilder;
import de.hybris.platform.ruledefinitions.conditions.builders.RuleIrGroupConditionBuilder;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rao.CartRAO;

public class RuleCartOperatorTotalConditionTranslator extends RuleCartTotalConditionTranslator {

  @Override
  protected RuleIrGroupCondition getCartTotalConditions(RuleCompilerContext context, AmountOperator comparisonOperator,
      Map<String, BigDecimal> values) {
    RuleIrGroupCondition originalConditions = super.getCartTotalConditions(context, comparisonOperator, values);

    String cartRao = context.generateVariable(CartRAO.class);
    RuleIrGroupCondition addedConditions = RuleIrGroupConditionBuilder.newGroupConditionOf(RuleIrGroupOperator.OR).build();

    for (Map.Entry<String, BigDecimal> amount : values.entrySet()) {
      addedConditions.getChildren().add(getConditionForAmount(amount, comparisonOperator, cartRao));
    }

    RuleIrGroupCondition finalCartConditions = RuleIrGroupConditionBuilder.newGroupConditionOf(RuleIrGroupOperator.AND).build();
    finalCartConditions.getChildren().add(originalConditions);
    finalCartConditions.getChildren().add(addedConditions);
    return finalCartConditions;
  }

  private RuleIrGroupCondition getConditionForAmount(Map.Entry<String, BigDecimal> currencyAmount, AmountOperator amountOperator,
      String cartRao) {

    RuleIrGroupCondition totalConditionForCurrency =
        RuleIrGroupConditionBuilder.newGroupConditionOf(RuleIrGroupOperator.AND).build();

    totalConditionForCurrency.getChildren().add( //
        RuleIrAttributeConditionBuilder.newAttributeConditionFor(cartRao) //
            .withAttribute("currencyIsoCode") //
            .withOperator(RuleIrAttributeOperator.EQUAL) //
            .withValue(currencyAmount.getKey()) //
            .build() //
    );

    totalConditionForCurrency.getChildren().add( //
        RuleIrAttributeConditionBuilder.newAttributeConditionFor(cartRao) //
            .withAttribute("operatorTotal") //
            .withOperator(RuleIrAttributeOperator.valueOf(amountOperator.name())) //
            .withValue(currencyAmount.getValue()) //
            .build() //
    );

    return totalConditionForCurrency;
  }

}
