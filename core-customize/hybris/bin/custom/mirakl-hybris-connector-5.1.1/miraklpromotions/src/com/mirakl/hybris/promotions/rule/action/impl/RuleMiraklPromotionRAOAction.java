package com.mirakl.hybris.promotions.rule.action.impl;

import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklDiscountRAO;
import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklPromotionRAO;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleOrderFixedDiscountRAOAction;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class RuleMiraklPromotionRAOAction extends RuleOrderFixedDiscountRAOAction {

  @Override
  public boolean performActionInternal(final RuleActionContext context) {
    CartRAO cart = context.getValue(CartRAO.class);
    return performAction(context, cart.getAppliedMiraklPromotions());
  }

  protected boolean performAction(final RuleActionContext context, final List<MiraklPromotionRAO> miraklPromotions) {
    if (isEmpty(miraklPromotions)) {
      return false;
    }

    for (MiraklPromotionRAO miraklPromotionRAO : miraklPromotions) {
      if (!performAction(context, miraklPromotionRAO)) {
        return false;
      }
    }
    return true;
  }

  protected boolean performAction(RuleActionContext context, MiraklPromotionRAO miraklPromotionRao) {
    CartRAO cart = context.getValue(CartRAO.class);
    if (isEmpty(cart.getEntries())) {
      return false;
    }

    super.performAction(context, miraklPromotionRao.getDeducedAmount());
    addPromotionInfos(miraklPromotionRao, context);
    return true;
  }

  protected void addPromotionInfos(MiraklPromotionRAO miraklPromotionRao, RuleActionContext context) {
    MiraklDiscountRAO miraklDiscount = getLastAddedDiscount(context);
    miraklDiscount.setPromotionId(miraklPromotionRao.getPromotionId());
    miraklDiscount.setShopId(miraklPromotionRao.getShopId());
  }

  protected MiraklDiscountRAO getLastAddedDiscount(RuleActionContext context) {
    LinkedHashSet<AbstractRuleActionRAO> actions = context.getRuleEngineResultRao().getActions();
    AbstractRuleActionRAO[] actionsArray = actions.toArray(new AbstractRuleActionRAO[actions.size()]);
    AbstractRuleActionRAO lastAction = actionsArray[actions.size() - 1];
    if (!(lastAction instanceof MiraklDiscountRAO)) {
      throw new IllegalStateException("Last action is not an instance of MiraklDiscountRAO");
    }
    MiraklDiscountRAO miraklDiscount = (MiraklDiscountRAO) lastAction;
    return miraklDiscount;
  }

  protected void validateParameters(Map<String, Object> parameters) {
    // No Parameters
  }

}
