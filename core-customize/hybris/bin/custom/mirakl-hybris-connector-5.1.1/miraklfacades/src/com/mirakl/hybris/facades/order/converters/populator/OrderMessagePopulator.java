package com.mirakl.hybris.facades.order.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.mirakl.client.mmp.domain.message.MiraklMessageDocument;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.common.MiraklUserType;
import com.mirakl.client.mmp.domain.message.MiraklOrderMessage;
import com.mirakl.hybris.beans.DocumentData;
import com.mirakl.hybris.beans.MessageData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

public class OrderMessagePopulator implements Populator<MiraklOrderMessage, MessageData> {

  protected Converter<MiraklMessageDocument, DocumentData> documentConverter;

  @Override
  public void populate(MiraklOrderMessage miraklOrderMessage, MessageData messageData) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklOrderMessage", miraklOrderMessage);
    validateParameterNotNullStandardMessage("messageData", messageData);

    messageData.setAuthor(miraklOrderMessage.getUserSender().getName());
    messageData.setSubject(miraklOrderMessage.getSubject());
    messageData.setBody(miraklOrderMessage.getBody());
    messageData.setDateCreated(miraklOrderMessage.getDateCreated());
    if (miraklOrderMessage.getUserSender().getType() == MiraklUserType.CUSTOMER) {
      messageData.setIsFromCustomer(true);
    }
    messageData.setDocuments(documentConverter.convertAll(miraklOrderMessage.getDocuments()));
  }

  @Required
  public void setDocumentConverter(Converter<MiraklMessageDocument, DocumentData> documentConverter) {
    this.documentConverter = documentConverter;
  }
}
