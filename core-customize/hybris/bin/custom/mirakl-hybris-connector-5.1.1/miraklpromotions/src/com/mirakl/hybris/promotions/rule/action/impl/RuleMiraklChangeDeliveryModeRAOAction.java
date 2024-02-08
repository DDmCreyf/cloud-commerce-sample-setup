package com.mirakl.hybris.promotions.rule.action.impl;

import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.DeliveryModeRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleChangeDeliveryModeRAOAction;

public class RuleMiraklChangeDeliveryModeRAOAction extends RuleChangeDeliveryModeRAOAction {

	@Override
	public void changeDeliveryMode(final CartRAO cartRao, final DeliveryModeRAO mode, final RuleEngineResultRAO result, final RuleActionContext context) {
		mode.setCost(mode.getCost().add(cartRao.getMarketplaceDeliveryCost()));
		super.changeDeliveryMode(cartRao, mode, result, context);
	}
}
