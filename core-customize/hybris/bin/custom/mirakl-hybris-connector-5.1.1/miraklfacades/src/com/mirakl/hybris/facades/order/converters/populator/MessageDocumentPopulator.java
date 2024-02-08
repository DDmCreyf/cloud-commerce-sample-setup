package com.mirakl.hybris.facades.order.converters.populator;

import com.mirakl.client.mmp.domain.message.MiraklMessageDocument;
import com.mirakl.hybris.beans.DocumentData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class MessageDocumentPopulator implements Populator<MiraklMessageDocument, DocumentData> {

  @Override
  public void populate(MiraklMessageDocument source, DocumentData target) throws ConversionException {
    target.setCode(source.getId());
    target.setDateUploaded(source.getDateUploaded());
    target.setFileName(source.getFileName());
    target.setFileSize(source.getFileSize());
    target.setType(source.getType());
  }

}
