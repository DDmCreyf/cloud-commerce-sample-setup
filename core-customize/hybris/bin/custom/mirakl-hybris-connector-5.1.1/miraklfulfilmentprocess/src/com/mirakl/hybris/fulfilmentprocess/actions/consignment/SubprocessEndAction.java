/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package com.mirakl.hybris.fulfilmentprocess.actions.consignment;

import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.fulfilmentprocess.constants.MiraklfulfilmentprocessConstants;


/**
 *
 */
public class SubprocessEndAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SubprocessEndAction.class);

	private static final String PROCESS_MSG = "Process: ";

	private BusinessProcessService businessProcessService;

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	@Override
	public void executeAction(final ConsignmentProcessModel process)
	{
		LOG.info(PROCESS_MSG + process.getCode() + " in step " + getClass());

		try
		{
			// simulate different ending times
			Thread.sleep((long) (Math.random() * 2000));
		}
		catch (final InterruptedException e)
		{
			// can't help it
		}

		process.setDone(true);

		save(process);
		LOG.info(PROCESS_MSG + process.getCode() + " wrote DONE marker");

		getBusinessProcessService().triggerEvent(
				process.getParentProcess().getCode() + "_"
						+ MiraklfulfilmentprocessConstants.CONSIGNMENT_SUBPROCESS_END_EVENT_NAME);
		LOG.info(PROCESS_MSG + process.getCode() + " fired event "
				+ MiraklfulfilmentprocessConstants.CONSIGNMENT_SUBPROCESS_END_EVENT_NAME);
	}
}
