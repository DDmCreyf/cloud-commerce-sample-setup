package com.mirakl.hybris.facades.order;

import java.util.List;

import com.mirakl.hybris.beans.MessageData;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface MarketplaceConsignmentMessagesFacade {

  /**
   * Returns whether or not the user is able to write messages for the given consignment code (Strategy)
   *
   * @param consignmentCode The code of the consignment
   * @return true if the user can write messages, false otherwise
   */
  boolean canWriteMessages(String consignmentCode);

  /**
   * Returns the messages between the client and the seller for the given consignment
   *
   * @param consignmentCode The code of the consignment
   * @return a list of messages
   */
  List<MessageData> getMessagesForConsignment(String consignmentCode);

  /**
   * Posts a message from the user to the seller using Mirakl API
   * 
   * @param consignmentCode The code of the consignment
   * @param message The data of the message
   * @return true if the message can be posted, false otherwise
   */
  boolean postMessageForConsignment(String consignmentCode, MessageData message);
}
