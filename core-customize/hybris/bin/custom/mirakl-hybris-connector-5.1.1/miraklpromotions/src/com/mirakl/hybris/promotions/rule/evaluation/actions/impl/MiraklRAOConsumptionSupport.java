package com.mirakl.hybris.promotions.rule.evaluation.actions.impl;

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryConsumedRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.DefaultRAOConsumptionSupport;

public class MiraklRAOConsumptionSupport extends DefaultRAOConsumptionSupport {

	@Override
	public OrderEntryConsumedRAO consumeOrderEntry(final OrderEntryRAO orderEntryRAO, final AbstractRuleActionRAO actionRAO) {
		if (Boolean.TRUE.equals(orderEntryRAO.getIsMarketplace())) {
			return null;
		}
		return superConsumeOrderEntry(orderEntryRAO, actionRAO);
	}

	protected OrderEntryConsumedRAO superConsumeOrderEntry(final OrderEntryRAO orderEntryRAO, final AbstractRuleActionRAO actionRAO) {
		return super.consumeOrderEntry(orderEntryRAO, actionRAO);
	}
}
