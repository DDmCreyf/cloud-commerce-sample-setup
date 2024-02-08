package com.mirakl.hybris.core.product.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.product.daos.MciProductDao;
import com.mirakl.hybris.core.product.services.MciProductService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;

public class DefaultMciProductService implements MciProductService {

  protected MciProductDao mciProductDao;

  @Override
  public ProductModel getProductForShopVariantGroupCode(ShopModel shop, String variantGroupCode,
      CatalogVersionModel catalogVersion) {
    validateParameterNotNullStandardMessage("shop", shop);
    validateParameterNotNullStandardMessage("variantGroupCode", variantGroupCode);
    validateParameterNotNullStandardMessage("catalogVersion", catalogVersion);

    return mciProductDao.findProductForShopVariantGroupCode(shop, variantGroupCode, catalogVersion);
  }

  @Required
  public void setMciProductDao(MciProductDao mciProductDao) {
    this.mciProductDao = mciProductDao;
  }

}
