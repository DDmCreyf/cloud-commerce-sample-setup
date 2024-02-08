package com.mirakl.hybris.core.product.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ShopVariantGroupCode;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.daos.MiraklRawProductDao;
import com.mirakl.hybris.core.product.services.MiraklRawProductService;

public class DefaultMiraklRawProductService implements MiraklRawProductService {

  protected MiraklRawProductDao rawProductDao;

  @Override
  public List<ShopVariantGroupCode> getShopVariantGroupCodesForImportId(String importId) {
    validateParameterNotNullStandardMessage("importId", importId);

    return rawProductDao.findShopVariantGroupCodesByImportId(importId);
  }

  @Override
  public List<String> getMiraklVariantGroupCodesForImportId(String importId) {
    validateParameterNotNullStandardMessage("importId", importId);

    return rawProductDao.findMiraklVariantGroupCodesByImportId(importId);
  }

  @Override
  public List<MiraklRawProductModel> getRawProductsForImportIdAndVariantGroupCode(String importId, String variantGroupCode) {
    validateParameterNotNullStandardMessage("importId", importId);
    validateParameterNotNullStandardMessage("variantGroupCode", variantGroupCode);

    return rawProductDao.findRawProductsByImportIdAndVariantGroupCode(importId, variantGroupCode);
  }

  @Override
  public List<MiraklRawProductModel> getRawProductsWithNoVariantGroupForImportId(String importId) {
    validateParameterNotNullStandardMessage("importId", importId);

    return rawProductDao.findRawProductsWithNoVariantGroupByImportId(importId);
  }

  @Required
  public void setRawProductDao(MiraklRawProductDao rawProductDao) {
    this.rawProductDao = rawProductDao;
  }

}
