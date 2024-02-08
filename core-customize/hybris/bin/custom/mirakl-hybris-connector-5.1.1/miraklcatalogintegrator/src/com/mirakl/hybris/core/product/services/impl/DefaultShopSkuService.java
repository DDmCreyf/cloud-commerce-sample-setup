package com.mirakl.hybris.core.product.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopSkuModel;
import com.mirakl.hybris.core.product.daos.ShopSkuDao;
import com.mirakl.hybris.core.product.services.ShopSkuService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class DefaultShopSkuService implements ShopSkuService {

  protected ShopSkuDao shopSkuDao;

  @Override
  public ShopSkuModel getShopSkuForChecksum(String checksum, ShopModel shop, CatalogVersionModel catalogVersion) {
    return shopSkuDao.findShopSkuByChecksum(checksum, shop, catalogVersion);
  }

  @Override
  public ShopSkuModel getShopSkuForSku(String sku, ShopModel shop, CatalogVersionModel catalogVersion) {
    return shopSkuDao.findShopSkuBySku(sku, shop, catalogVersion);
  }

  @Override
  public void removeShopSkuFromProduct(String sku, ShopModel shop, ProductModel product) {
    validateParameterNotNullStandardMessage("sku", sku);
    validateParameterNotNullStandardMessage("shop", shop);
    validateParameterNotNullStandardMessage("product", product);

    ShopSkuModel shopSku = getShopSku(shop, product);
    if (shopSku == null) {
      throw new UnknownIdentifierException(
          format("Cannot find ShopSku for shop [%s] and sku [%s] in product [%s]", shop.getId(), sku, product.getCode()));
    }

    Collection<ShopSkuModel> shopSkus = new ArrayList<>(product.getShopSkus());
    shopSkus.remove(shopSku);
    product.setShopSkus(shopSkus);
  }

  @Override
  public ShopSkuModel addShopSkuToProduct(ShopSkuModel shopSku, ProductModel product) {
    validateParameterNotNullStandardMessage("shopSku", shopSku);
    validateParameterNotNullStandardMessage("product", product);

    Set<ShopSkuModel> shopSkus = new HashSet<>();
    if (CollectionUtils.isNotEmpty(product.getShopSkus())) {
      shopSkus.addAll(product.getShopSkus());
    }
    shopSkus.add(shopSku);
    product.setShopSkus(shopSkus);

    return shopSku;
  }

  @Override
  public ShopSkuModel getShopSku(final ShopModel shop, final ProductModel product) {
    Collection<ShopSkuModel> shopSkus = product.getShopSkus();
    if (isNotEmpty(shopSkus)) {
      for (ShopSkuModel shopSku : shopSkus) {
        if (shop.equals(shopSku.getShop())) {
          return shopSku;
        }
      }
    }
    return null;
  }

  @Required
  public void setShopSkuDao(ShopSkuDao shopSkuDao) {
    this.shopSkuDao = shopSkuDao;
  }


}
