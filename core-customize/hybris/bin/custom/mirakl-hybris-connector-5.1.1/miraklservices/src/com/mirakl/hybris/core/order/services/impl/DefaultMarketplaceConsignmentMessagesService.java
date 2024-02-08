package com.mirakl.hybris.core.order.services.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.message.MiraklOrderMessages;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.order.message.MiraklCreateOrderMessageRequest;
import com.mirakl.client.mmp.front.request.order.message.MiraklGetOrderMessagesRequest;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.core.order.services.MarketplaceConsignmentMessagesService;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.ordersplitting.services.impl.DefaultMarketplaceConsignmentService;

import de.hybris.platform.servicelayer.user.UserService;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultMarketplaceConsignmentMessagesService implements MarketplaceConsignmentMessagesService {

  protected MiraklMarketplacePlatformFrontApi miraklApi;
  protected MarketplaceConsignmentService consignmentService;
  protected UserService userService;

  @Override
  public MiraklOrderMessages getMessagesForConsignment(String consignmentCode) {
    consignmentService.checkUserAccessRightsForConsignment(consignmentCode);
    MiraklGetOrderMessagesRequest request = new MiraklGetOrderMessagesRequest(consignmentCode);
    return miraklApi.getOrderMessages(request);
  }

  @Override
  public void postMessageForConsignment(String consignmentCode, MiraklCreateOrderMessage message) {
    consignmentService.checkUserAccessRightsForConsignment(consignmentCode);
    MiraklCreateOrderMessageRequest request = new MiraklCreateOrderMessageRequest(consignmentCode, message);
    miraklApi.createOrderMessage(request);
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

  @Required
  public void setConsignmentService(DefaultMarketplaceConsignmentService consignmentService) {
    this.consignmentService = consignmentService;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }
}
