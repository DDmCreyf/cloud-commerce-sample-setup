package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopVariantGroupModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

public class DefaultShopVariantGroupAttributeHandler extends AbstractCoreAttributeHandler<MiraklCoreAttributeModel> {

  @Override
  public void setValue(AttributeValueData attribute, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    final ShopModel shop = data.getShop();
    final String variantGroupCode = attribute.getValue();

    if (isNotBlank(variantGroupCode)) {
      verifyShopVariantGroupConsistency(data);
      ProductModel rootBaseProductToUpdate = data.getRootBaseProductToUpdate();
      Collection<ShopVariantGroupModel> shopVariantGroups = getShopVariantGroups(rootBaseProductToUpdate);

      Optional<ShopVariantGroupModel> existingShopVariantGroup = lookupShopVariantGroup(shopVariantGroups, shop);
      if (!existingShopVariantGroup.isPresent()) {
        ShopVariantGroupModel shopVariantGroup = modelService.create(ShopVariantGroupModel.class);
        updateShopVariantGroup(shopVariantGroup, shop, variantGroupCode, rootBaseProductToUpdate);
        shopVariantGroups.add(shopVariantGroup);
        rootBaseProductToUpdate.setShopVariantGroups(shopVariantGroups);
        markItemsToSave(data, shopVariantGroup, rootBaseProductToUpdate);
      } else {
        updateShopVariantGroup(existingShopVariantGroup.get(), shop, variantGroupCode, rootBaseProductToUpdate);
        markItemsToSave(data, existingShopVariantGroup.get(), rootBaseProductToUpdate);
      }
    }
  }

  protected Optional<ShopVariantGroupModel> lookupShopVariantGroup(Collection<ShopVariantGroupModel> shopVariantGroups,
      final ShopModel shop) {
    return FluentIterable.from(shopVariantGroups).firstMatch(new Predicate<ShopVariantGroupModel>() {

      @Override
      public boolean apply(ShopVariantGroupModel shopVariantGroup) {
        return shopVariantGroup.getShop().equals(shop);
      }
    });
  }

  protected Collection<ShopVariantGroupModel> getShopVariantGroups(ProductModel rootBaseProduct) {
    Collection<ShopVariantGroupModel> shopVariantGroups = new HashSet<>();
    if (isNotEmpty(rootBaseProduct.getShopVariantGroups())) {
      shopVariantGroups.addAll(rootBaseProduct.getShopVariantGroups());
    }
    return shopVariantGroups;
  }

  protected void updateShopVariantGroup(ShopVariantGroupModel shopVariantGroup, ShopModel shop, String variantGroupCode,
      ProductModel rootBaseProductToUpdate) {
    shopVariantGroup.setCode(variantGroupCode);
    shopVariantGroup.setShop(shop);
    shopVariantGroup.setProduct(rootBaseProductToUpdate);
  }

  protected void verifyShopVariantGroupConsistency(ProductImportData data) throws ProductImportException {
    if (!(data.getProductToUpdate() instanceof VariantProductModel)) {
      throw new ProductImportException(data.getRawProduct(), "A variant group code was provided on a non variant product.");
    }

    ProductModel rootBaseProductToUpdate = data.getRootBaseProductToUpdate();
    ProductModel productResolvedByVG = data.getProductResolvedByVariantGroup();
    if (productResolvedByVG != null && rootBaseProductToUpdate != null && !productResolvedByVG.equals(rootBaseProductToUpdate)) {
      throw new ProductImportException(data.getRawProduct(),
          format("Base product resolved by variant group [%s] is different from the root base product to update [%s]",
              productResolvedByVG.getCode(), rootBaseProductToUpdate.getCode()));
    }
  }

}
