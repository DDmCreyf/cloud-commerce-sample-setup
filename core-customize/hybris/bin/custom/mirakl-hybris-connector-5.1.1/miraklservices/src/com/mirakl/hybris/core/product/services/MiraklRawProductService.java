package com.mirakl.hybris.core.product.services;

import java.util.List;

import com.mirakl.hybris.beans.ShopVariantGroupCode;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

public interface MiraklRawProductService {

  /**
   * Retrieves all the shop variant group codes listed within a single import (MCI)
   *
   * @param importId Id of the import
   * @return the list of {@link ShopVariantGroupCode}
   * @see com.mirakl.hybris.core.product.daos.MiraklRawProductDao#findShopVariantGroupCodesByImportId(String)
   */
  List<ShopVariantGroupCode> getShopVariantGroupCodesForImportId(String importId);

  /**
   * Retrieves all the variant group codes listed within a single import (MCM)
   *
   * @param importId Id of the import
   * @return a list of variant group codes
   */
  List<String> getMiraklVariantGroupCodesForImportId(String importId);

  /**
   * Retrieves all the raw products matching the given shop variant group code from the given import (MCI)
   *
   * @param importId Id of the import
   * @param variantGroupCode Shop variant group code
   * @return a list of {@link MiraklRawProductModel}
   * @see com.mirakl.hybris.core.product.daos.MiraklRawProductDao#findShopVariantGroupCodesByImportId(String)
   */
  List<MiraklRawProductModel> getRawProductsForImportIdAndVariantGroupCode(String importId, String variantGroupCode);

  /**
   * Retrieves all the raw products without variant group code from the given import (ie. the non variant raw products)
   *
   * @param importId the id of the import
   * @return the list of {@link MiraklRawProductModel}
   * @see com.mirakl.hybris.core.product.daos.MiraklRawProductDao#findRawProductsWithNoVariantGroupByImportId(String)
   */
  List<MiraklRawProductModel> getRawProductsWithNoVariantGroupForImportId(String importId);
}
