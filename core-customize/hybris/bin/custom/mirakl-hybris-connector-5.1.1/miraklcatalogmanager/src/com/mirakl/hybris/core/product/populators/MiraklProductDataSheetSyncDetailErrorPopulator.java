package com.mirakl.hybris.core.product.populators;

import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncDetail;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklProductDataSheetSyncDetailErrorPopulator
    implements Populator<MiraklProductDataSheetSyncDetail, MiraklProductDataSheetSyncDetail> {

  @Override
  public void populate(MiraklProductDataSheetSyncDetail source, MiraklProductDataSheetSyncDetail target)
      throws ConversionException {
    target.setProductSku(source.getProductSku());
    target.setMiraklProductId(source.getMiraklProductId());
    target.setStatus(source.getStatus());
    target.setAcceptance(source.getAcceptance());
    target.setIntegrationErrors(source.getIntegrationErrors());
    target.setSynchronizationErrors(source.getSynchronizationErrors());
  }

}
