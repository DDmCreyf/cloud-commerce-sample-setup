package com.mirakl.hybris.core.order.services;

import com.mirakl.client.mmp.domain.message.MiraklOrderMessages;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface MarketplaceConsignmentMessagesService {

  /**
   * Returns the messages for the given consignment
   *
   * @param consignmentCode the code of the consignment (= Mirakl Order Id)
   * @return a List of MiraklOrderMessages
   */
  MiraklOrderMessages getMessagesForConsignment(String consignmentCode);

  /**
   * Send the user message to Mirakl for the given consignment
   *
   * @param consignmentCode the code of the consignment (= Mirakl Order Id)
   * @param message         the message from the user
   */
  void postMessageForConsignment(String consignmentCode, MiraklCreateOrderMessage message);
}
