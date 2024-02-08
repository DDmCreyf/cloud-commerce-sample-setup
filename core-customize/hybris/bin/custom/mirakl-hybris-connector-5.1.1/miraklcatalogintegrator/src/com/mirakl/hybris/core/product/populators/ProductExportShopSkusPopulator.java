package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.COLLECTION_ITEM_SEPARATOR;
import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.KEY_VALUE_SEPARATOR;
import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.SHOP_SKUS;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.mirakl.hybris.core.model.ShopSkuModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;

public class ProductExportShopSkusPopulator implements Populator<ProductModel, Map<String, String>> {

  @Override
  public void populate(ProductModel source, Map<String, String> target) {
    target.put(SHOP_SKUS.getCode(), getShopSkus(source));
  }

  protected String getShopSkus(ProductModel product) {
    Collection<ShopSkuModel> shopSkus = product.getShopSkus();
    if (isEmpty(shopSkus)) {
      return null;
    }

    return FluentIterable.from(shopSkus).transform(shopSkuToString()).join(Joiner.on(COLLECTION_ITEM_SEPARATOR));
  }

  protected Function<ShopSkuModel, String> shopSkuToString() {
    return new Function<ShopSkuModel, String>() {

      @Override
      public String apply(ShopSkuModel shopSku) {
        return Joiner.on(KEY_VALUE_SEPARATOR).join(shopSku.getShop().getId(), shopSku.getSku());
      }
    };
  }
}
