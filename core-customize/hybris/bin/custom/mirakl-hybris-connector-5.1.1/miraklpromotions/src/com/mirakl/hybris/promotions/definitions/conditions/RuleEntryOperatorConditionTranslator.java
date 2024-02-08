package com.mirakl.hybris.promotions.definitions.conditions;

import static de.hybris.platform.ruledefinitions.conditions.builders.RuleIrAttributeConditionBuilder.newAttributeConditionFor;
import static de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator.EQUAL;

import de.hybris.platform.ruledefinitions.conditions.AbstractRuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;

public class RuleEntryOperatorConditionTranslator extends AbstractRuleConditionTranslator {

  public RuleIrCondition translate(RuleCompilerContext context, RuleConditionData condition,
      RuleConditionDefinitionData conditionDefinition) {
    final String orderEntryRaoVariable = context.generateVariable(OrderEntryRAO.class);
    return newAttributeConditionFor(orderEntryRaoVariable).withAttribute("isMarketplace").withOperator(EQUAL).withValue(false)
        .build();
  }

}
