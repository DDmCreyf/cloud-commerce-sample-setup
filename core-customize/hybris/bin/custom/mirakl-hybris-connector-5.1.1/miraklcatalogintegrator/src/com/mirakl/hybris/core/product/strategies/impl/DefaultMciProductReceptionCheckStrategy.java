package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_ALREADY_RECEIVED_MESSAGE;
import static java.lang.String.format;

import com.mirakl.hybris.core.product.strategies.ProductReceptionCheckStrategy;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportSuccessData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.product.services.ShopSkuService;
import com.mirakl.hybris.core.shop.services.ShopService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMciProductReceptionCheckStrategy implements ProductReceptionCheckStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMciProductReceptionCheckStrategy.class);

  protected ModelService modelService;
  protected ShopService shopService;
  protected ShopSkuService shopSkuService;
  protected L10NService l10nService;

  @Override
  public boolean isAlreadyReceived(MiraklRawProductModel rawProduct, ProductImportFileContextData context) {
    if (context.getGlobalContext().isForceProductUpdate()) {
      return false;
    }
    CatalogVersionModel catalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());
    ShopModel shop = shopService.getShopForId(context.getShopId());
    return shopSkuService.getShopSkuForChecksum(rawProduct.getChecksum(), shop, catalogVersion) != null;
  }

  @Override
  public void handleAlreadyReceived(MiraklRawProductModel rawProduct, ProductImportFileContextData context) {
    try {
      ProductImportSuccessData successData = new ProductImportSuccessData();
      successData.setLineValues(rawProduct.getValues());
      successData.setRowNumber(rawProduct.getRowNumber());
      successData.setAdditionalMessage(getAlreadyReceivedMessage(context));
      context.getImportResultQueue().put(successData);
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
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setShopService(ShopService shopService) {
    this.shopService = shopService;
  }

  @Required
  public void setShopSkuService(ShopSkuService shopSkuService) {
    this.shopSkuService = shopSkuService;
  }

  @Required
  public void setL10nService(L10NService l10nService) {
    this.l10nService = l10nService;
  }
}
