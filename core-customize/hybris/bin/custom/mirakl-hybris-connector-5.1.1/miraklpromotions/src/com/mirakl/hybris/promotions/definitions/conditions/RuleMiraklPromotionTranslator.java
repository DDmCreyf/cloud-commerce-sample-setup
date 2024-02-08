package com.mirakl.hybris.promotions.definitions.conditions;

import static de.hybris.platform.ruledefinitions.conditions.builders.RuleIrAttributeConditionBuilder.newAttributeConditionFor;
import static de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator.NOT_EQUAL;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;

public class RuleMiraklPromotionTranslator implements RuleConditionTranslator {

  protected static final String APPLIED_MIRAKL_PROMOTIONS_ATTRIBUTE = "appliedMiraklPromotions";

  @Override
  public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
      final RuleConditionDefinitionData conditionDefinition) throws RuleCompilerException {
    final String cartRaoVariable = context.generateVariable(CartRAO.class);
    return newAttributeConditionFor(cartRaoVariable).withAttribute(APPLIED_MIRAKL_PROMOTIONS_ATTRIBUTE).withOperator(NOT_EQUAL)
        .withValue(null).build();
  }

}
