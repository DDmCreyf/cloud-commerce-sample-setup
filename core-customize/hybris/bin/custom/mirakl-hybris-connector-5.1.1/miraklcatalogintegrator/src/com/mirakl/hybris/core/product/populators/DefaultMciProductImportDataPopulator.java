package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.enums.MiraklAttributeRole.SHOP_SKU_ATTRIBUTE;
import static com.mirakl.hybris.core.enums.MiraklAttributeRole.VARIANT_GROUP_CODE_ATTRIBUTE;

import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopSkuModel;
import com.mirakl.hybris.core.product.services.MciProductService;
import com.mirakl.hybris.core.product.services.ShopSkuService;
import com.mirakl.hybris.core.product.strategies.UniqueIdentifierMatchingStrategy;
import com.mirakl.hybris.core.shop.services.ShopService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMciProductImportDataPopulator
    implements Populator<Pair<MiraklRawProductModel, ProductImportFileContextData>, ProductImportData> {

  protected ShopService shopService;
  protected MciProductService mciProductService;
  protected ShopSkuService shopSkuService;
  protected ModelService modelService;
  protected UniqueIdentifierMatchingStrategy uniqueIdentifierMatchingStrategy;

  @Override
  public void populate(Pair<MiraklRawProductModel, ProductImportFileContextData> source, ProductImportData target)
      throws ConversionException {
    MiraklRawProductModel rawProduct = source.getLeft();
    ProductImportFileContextData context = source.getRight();

    target.setModelsToSave(new HashSet<ItemModel>());
    ShopModel shop = shopService.getShopForId(context.getShopId());
    target.setShop(shop);
    target.setRawProduct(rawProduct);
    String sku = rawProduct.getValues().get(getShopSkuAttributeCode(context));
    target.setShopSku(sku);
    CatalogVersionModel catalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());
    ShopSkuModel shopSku = shopSkuService.getShopSkuForSku(sku, shop, catalogVersion);
    if (shopSku != null) {
      target.setProductResolvedBySku(shopSku.getProduct());
    }
    target.setProductsResolvedByUID(uniqueIdentifierMatchingStrategy.getMatches(target, context));
    if (context.getGlobalContext().getVariantAttributesPerType() != null) {
      String shopVariantGroupCode = rawProduct.getValues().get(getVariantGroupAttributeCode(context));
      target.setVariantGroupCode(shopVariantGroupCode);
      if (StringUtils.isNotBlank(shopVariantGroupCode)) {
        target.setProductResolvedByVariantGroup(
            mciProductService.getProductForShopVariantGroupCode(shop, shopVariantGroupCode, catalogVersion));
      }
    }
  }

  protected String getVariantGroupAttributeCode(ProductImportFileContextData context) {
    return context.getGlobalContext().getCoreAttributePerRole().get(VARIANT_GROUP_CODE_ATTRIBUTE);
  }

  protected String getShopSkuAttributeCode(ProductImportFileContextData context) {
    return context.getGlobalContext().getCoreAttributePerRole().get(SHOP_SKU_ATTRIBUTE);
  }

  @Required
  public void setShopService(ShopService shopService) {
    this.shopService = shopService;
  }

  @Required
  public void setMciProductService(MciProductService mciProductService) {
    this.mciProductService = mciProductService;
  }

  @Required
  public void setShopSkuService(ShopSkuService shopSkuService) {
    this.shopSkuService = shopSkuService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setUniqueIdentifierMatchingStrategy(UniqueIdentifierMatchingStrategy uniqueIdentifierMatchingStrategy) {
    this.uniqueIdentifierMatchingStrategy = uniqueIdentifierMatchingStrategy;
  }

}
