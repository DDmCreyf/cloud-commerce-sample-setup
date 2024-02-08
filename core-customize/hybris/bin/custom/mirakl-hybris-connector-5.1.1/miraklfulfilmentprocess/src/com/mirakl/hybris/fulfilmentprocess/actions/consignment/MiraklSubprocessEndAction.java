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
package com.mirakl.hybris.fulfilmentprocess.actions.consignment;

import static com.mirakl.hybris.fulfilmentprocess.constants.MiraklfulfilmentprocessConstants.CONSIGNMENT_SUBPROCESS_END_EVENT_NAME;
import static java.lang.String.format;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MarketplaceConsignmentProcessModel;

import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;


public class MiraklSubprocessEndAction extends AbstractProceduralAction<MarketplaceConsignmentProcessModel> {
  private static final Logger LOG = Logger.getLogger(MiraklSubprocessEndAction.class);

  protected BusinessProcessService businessProcessService;

  @Override
  public void executeAction(final MarketplaceConsignmentProcessModel process) {
    LOG.info(format("Process: %s in step %s", process.getCode(), getClass()));

    process.setDone(true);
    save(process);
    LOG.info(format("Process: %s wrote DONE marker", process.getCode()));

    businessProcessService
        .triggerEvent(format("%s_%s", process.getParentProcess().getCode(), CONSIGNMENT_SUBPROCESS_END_EVENT_NAME));
    LOG.info(format("Process: %s fired event %s", process.getCode(), CONSIGNMENT_SUBPROCESS_END_EVENT_NAME));
  }

  @Required
  public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
    this.businessProcessService = businessProcessService;
  }
}
