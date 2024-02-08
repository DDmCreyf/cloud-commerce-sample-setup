package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_ALREADY_RECEIVED_MESSAGE;
import static java.lang.String.format;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportSuccessData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.services.McmProductService;
import com.mirakl.hybris.core.product.strategies.ProductReceptionCheckStrategy;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMcmProductReceptionCheckStrategy implements ProductReceptionCheckStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMcmProductReceptionCheckStrategy.class);

  protected McmProductService mcmProductService;
  protected ModelService modelService;
  protected L10NService l10nService;

  @Override
  public boolean isAlreadyReceived(MiraklRawProductModel rawProduct, ProductImportFileContextData context) {
    if (context.getGlobalContext().isForceProductUpdate()) {
      return false;
    }
    CatalogVersionModel catalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());
    return mcmProductService.getProductForChecksum(rawProduct.getChecksum(), catalogVersion) != null;
  }

  @Override
  public void handleAlreadyReceived(MiraklRawProductModel rawProduct, ProductImportFileContextData context) {
    try {
      ProductImportSuccessData productImportSuccessData = new ProductImportSuccessData();
      productImportSuccessData.setProductCode(rawProduct.getSku());
      productImportSuccessData.setLineValues(rawProduct.getValues());
      productImportSuccessData.setRowNumber(rawProduct.getRowNumber());
      productImportSuccessData.setAdditionalMessage(getAlreadyReceivedMessage(context));
      context.getImportResultQueue().put(productImportSuccessData);
    } catch (InterruptedException e) {
      LOG.warn(format("Unable to write to the success queue. Line value: [%s], Line number: [%s]", rawProduct.getValues(),
          rawProduct.getRowNumber()), e);
      Thread.currentThread().interrupt();
    }
  }

  protected String getAlreadyReceivedMessage(ProductImportFileContextData context) {
    return l10nService.getLocalizedString(PRODUCTS_IMPORT_ALREADY_RECEIVED_MESSAGE);
  }

  @Required
  public void setMcmProductService(McmProductService mcmProductService) {
    this.mcmProductService = mcmProductService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setL10nService(L10NService l10nService) {
    this.l10nService = l10nService;
  }
}
