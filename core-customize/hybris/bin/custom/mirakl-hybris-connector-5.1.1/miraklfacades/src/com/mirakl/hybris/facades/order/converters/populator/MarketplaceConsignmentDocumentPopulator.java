package com.mirakl.hybris.facades.order.converters.populator;

import com.mirakl.client.mmp.domain.order.document.MiraklOrderDocument;
import com.mirakl.hybris.beans.DocumentData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class MarketplaceConsignmentDocumentPopulator implements Populator<MiraklOrderDocument, DocumentData> {

  @Override
  public void populate(MiraklOrderDocument source, DocumentData target) throws ConversionException {
    target.setCode(source.getId());
    target.setDateUploaded(source.getDateUploaded());
    target.setFileName(source.getFileName());
    target.setType(source.getTypeCode());
  }

}
