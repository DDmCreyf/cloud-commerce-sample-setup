package com.mirakl.hybris.core.catalog.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.model.ShopSkuModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.product.services.ShopSkuService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultShopSkuAttributeHandler extends AbstractCoreAttributeHandler<MiraklCoreAttributeModel> {

  protected ShopSkuService shopSkuService;
  protected Converter<ProductImportData, ShopSkuModel> shopSkuConverter;

  @Override
  public void setValue(AttributeValueData attribute, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    ProductModel identifiedProduct = data.getIdentifiedProduct();
    ShopSkuModel shopSku;

    if (identifiedProduct == null) {
      shopSku = shopSkuConverter.convert(data);
      shopSkuService.addShopSkuToProduct(shopSku, data.getProductToUpdate());
      markItemsToSave(data, data.getProductToUpdate(), shopSku);
      return;
    }

    MiraklRawProductModel rawProduct = data.getRawProduct();
    ProductModel productResolvedByShopSku = data.getProductResolvedBySku();
    if (productResolvedByShopSku != null && !productResolvedByShopSku.equals(identifiedProduct)) {
      shopSkuService.removeShopSkuFromProduct(rawProduct.getSku(), data.getShop(), productResolvedByShopSku);
      markItemsToSave(data, productResolvedByShopSku);
    }

    shopSku = shopSkuService.getShopSku(data.getShop(), identifiedProduct);
    shopSku = shopSku == null ? shopSkuConverter.convert(data) : shopSkuConverter.convert(data, shopSku);

    shopSkuService.addShopSkuToProduct(shopSku, identifiedProduct);
    markItemsToSave(data, identifiedProduct, shopSku);
  }

  @Required
  public void setShopSkuService(ShopSkuService shopSkuService) {
    this.shopSkuService = shopSkuService;
  }

  @Required
  public void setShopSkuConverter(Converter<ProductImportData, ShopSkuModel> shopSkuConverter) {
    this.shopSkuConverter = shopSkuConverter;
  }

}
