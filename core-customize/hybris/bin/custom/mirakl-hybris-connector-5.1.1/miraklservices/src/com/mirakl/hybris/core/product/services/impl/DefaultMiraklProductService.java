package com.mirakl.hybris.core.product.services.impl;

import static com.google.common.collect.Sets.newHashSet;
import static com.mirakl.hybris.core.util.DataModelUtils.extractAttributeQualifiers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableMap;
import com.mirakl.hybris.core.product.daos.MiraklProductDao;
import com.mirakl.hybris.core.product.services.MiraklProductService;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultMiraklProductService implements MiraklProductService {

  protected BaseStoreService baseStoreService;
  protected CommerceStockService commerceStockService;
  protected CommercePriceService commercePriceService;
  protected TypeService typeService;
  protected MiraklProductDao miraklProductDao;

  @Override
  public boolean isSellableByOperator(ProductModel product) {
    return isPurchasable(product) && hasStock(product) && hasPrice(product);
  }

  @Override
  public Map<ComposedTypeModel, Set<AttributeDescriptorModel>> getAttributeDescriptorsPerProductType() {
    Map<ComposedTypeModel, Set<AttributeDescriptorModel>> result = new HashMap<>();
    for (ComposedTypeModel composedType : getAllProductTypes()) {
      Set<AttributeDescriptorModel> attributeDescriptors = typeService.getAttributeDescriptorsForType(composedType);
      result.put(composedType, attributeDescriptors);
    }

    return ImmutableMap.copyOf(result);
  }

  @Override
  public Map<ComposedTypeModel, Set<String>> getAttributeDescriptorQualifiersPerProductType() {
    Map<ComposedTypeModel, Set<String>> result = new HashMap<>();
    for (ComposedTypeModel composedType : getAllProductTypes()) {
      Set<AttributeDescriptorModel> attributeDescriptors = typeService.getAttributeDescriptorsForType(composedType);
      result.put(composedType, extractAttributeQualifiers(attributeDescriptors));
    }

    return ImmutableMap.copyOf(result);
  }

  protected Collection<ComposedTypeModel> getAllProductTypes() {
    ComposedTypeModel productComposedType = typeService.getComposedTypeForCode(ProductModel._TYPECODE);
    Collection<ComposedTypeModel> allProductTypes = newHashSet(productComposedType);
    allProductTypes.addAll(productComposedType.getAllSubTypes());
    return allProductTypes;
  }


  protected boolean isPurchasable(ProductModel product) {
    return product.getVariantType() == null && isApproved(product);
  }

  protected boolean isApproved(ProductModel product) {
    return ArticleApprovalStatus.APPROVED.equals(product.getApprovalStatus());
  }

  protected boolean hasStock(ProductModel product) {
    BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
    if (baseStore == null) {
      return false;
    }
    StockLevelStatus stockLevelStatus = commerceStockService.getStockLevelStatusForProductAndBaseStore(product, baseStore);

    return !StockLevelStatus.OUTOFSTOCK.equals(stockLevelStatus);
  }

  protected boolean hasPrice(ProductModel product) {
    PriceInformation productPrice = commercePriceService.getWebPriceForProduct(product);

    return productPrice != null;
  }

  @Required
  public void setBaseStoreService(BaseStoreService baseStoreService) {
    this.baseStoreService = baseStoreService;
  }

  @Required
  public void setCommerceStockService(CommerceStockService commerceStockService) {
    this.commerceStockService = commerceStockService;
  }

  @Required
  public void setCommercePriceService(CommercePriceService commercePriceService) {
    this.commercePriceService = commercePriceService;
  }

  @Required
  public void setTypeService(TypeService typeService) {
    this.typeService = typeService;
  }

  @Required
  public void setMiraklProductDao(MiraklProductDao miraklProductDao) {
    this.miraklProductDao = miraklProductDao;
  }

}
