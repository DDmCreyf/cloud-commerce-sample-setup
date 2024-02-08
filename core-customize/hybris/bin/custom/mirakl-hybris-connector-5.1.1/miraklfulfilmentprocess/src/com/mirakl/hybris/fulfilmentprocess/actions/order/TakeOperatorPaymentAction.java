/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with
 * hybris.
 *
 * 
 */
package com.mirakl.hybris.fulfilmentprocess.actions.order;

import com.mirakl.hybris.core.fulfilment.strategies.ProcessOperatorPaymentStrategy;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import org.springframework.beans.factory.annotation.Required;


/**
 * The TakeOperatorPayment step captures the operator order amount.
 */
public class TakeOperatorPaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel> {

  protected ProcessOperatorPaymentStrategy processOperatorPaymentStrategy;

  @Override
  public Transition executeAction(final OrderProcessModel process) {

    if (processOperatorPaymentStrategy.processPayment(process.getOrder())) {
      return Transition.OK;
    }

    return Transition.NOK;
  }

  @Required
  public void setProcessOperatorPaymentStrategy(ProcessOperatorPaymentStrategy processOperatorPaymentStrategy) {
    this.processOperatorPaymentStrategy = processOperatorPaymentStrategy;
  }
}
