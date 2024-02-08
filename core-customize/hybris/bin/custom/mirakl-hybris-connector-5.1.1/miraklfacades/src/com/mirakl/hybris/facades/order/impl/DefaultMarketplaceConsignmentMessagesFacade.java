package com.mirakl.hybris.facades.order.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.message.MiraklOrderMessage;
import com.mirakl.client.mmp.domain.message.MiraklOrderMessages;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.beans.MessageData;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.MarketplaceConsignmentMessagesService;
import com.mirakl.hybris.core.order.strategies.MarketplaceConsignmentMessagesStrategy;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.facades.order.MarketplaceConsignmentMessagesFacade;

import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultMarketplaceConsignmentMessagesFacade implements MarketplaceConsignmentMessagesFacade {

  protected MarketplaceConsignmentMessagesService marketplaceConsignmentMessagesService;
  protected MarketplaceConsignmentMessagesStrategy marketplaceConsignmentMessagesStrategy;
  protected MarketplaceConsignmentService marketplaceConsignmentService;
  protected Converter<MiraklOrderMessage, MessageData> orderMessageConverter;
  protected Converter<MessageData, MiraklCreateOrderMessage> miraklCreateOrderMessageConverter;
  protected Comparator<MessageData> messagesComparator;

  @Override
  public boolean canWriteMessages(String consignmentCode) {
    MarketplaceConsignmentModel consignment = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode);
    return marketplaceConsignmentMessagesStrategy.canWriteMessages(consignment);
  }

  @Override
  public List<MessageData> getMessagesForConsignment(String consignmentCode) {
    MiraklOrderMessages messages = marketplaceConsignmentMessagesService.getMessagesForConsignment(consignmentCode);
    List<MessageData> messagesData = orderMessageConverter.convertAll(messages.getMessages());
    Collections.sort(messagesData, messagesComparator);
    return messagesData;
  }

  @Override
  public boolean postMessageForConsignment(String consignmentCode, MessageData message) {
    if (!canWriteMessages(consignmentCode)) {
      return false;
    }
    marketplaceConsignmentMessagesService.postMessageForConsignment(consignmentCode,
        miraklCreateOrderMessageConverter.convert(message));
    return true;
  }

  @Required
  public void setMarketplaceConsignmentMessagesService(
      MarketplaceConsignmentMessagesService marketplaceConsignmentMessagesService) {
    this.marketplaceConsignmentMessagesService = marketplaceConsignmentMessagesService;
  }

  @Required
  public void setOrderMessageConverter(Converter<MiraklOrderMessage, MessageData> orderMessageConverter) {
    this.orderMessageConverter = orderMessageConverter;
  }

  @Required
  public void setMiraklCreateOrderMessageConverter(
      Converter<MessageData, MiraklCreateOrderMessage> miraklCreateOrderMessageConverter) {
    this.miraklCreateOrderMessageConverter = miraklCreateOrderMessageConverter;
  }

  @Required
  public void setMarketplaceConsignmentMessagesStrategy(
      MarketplaceConsignmentMessagesStrategy marketplaceConsignmentMessagesStrategy) {
    this.marketplaceConsignmentMessagesStrategy = marketplaceConsignmentMessagesStrategy;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setMessagesComparator(Comparator<MessageData> messagesComparator) {
    this.messagesComparator = messagesComparator;
  }
}
