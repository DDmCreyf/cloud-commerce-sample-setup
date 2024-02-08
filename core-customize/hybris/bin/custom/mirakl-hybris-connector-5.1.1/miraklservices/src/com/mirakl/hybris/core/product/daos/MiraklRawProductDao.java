package com.mirakl.hybris.core.product.daos;

import java.util.List;

import com.mirakl.hybris.beans.ShopVariantGroupCode;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.platform.servicelayer.internal.dao.GenericDao;

public interface MiraklRawProductDao extends GenericDao<MiraklRawProductModel> {

  /**
   * Retrieves all the shop variant group codes listed within a single import (MCI)
   *
   * @param importId Id of the import
   * @return the list of {@link ShopVariantGroupCode}
   */
  List<ShopVariantGroupCode> findShopVariantGroupCodesByImportId(String importId);

  /**
   * Retrieves all the Mirakl variant group codes listed within a single import (MCM)
   *
   * @param importId Id of the import
   * @return the list of variant group codes
   */
  List<String> findMiraklVariantGroupCodesByImportId(String importId);

  /**
   * Retrieves all the raw products matching the given shop variant group code from the given import (MCI)
   *
   * @param importId Id of the import
   * @param variantGroupCode Shop variant group code
   * @return a list of {@link MiraklRawProductModel}
   */
  List<MiraklRawProductModel> findRawProductsByImportIdAndVariantGroupCode(String importId, String variantGroupCode);

  /**
   * Retrieves all the raw products without variant group code from the given import (ie. the non variant raw products)
   *
   * @param importId the id of the import
   * @return the list of {@link MiraklRawProductModel}
   */
  List<MiraklRawProductModel> findRawProductsWithNoVariantGroupByImportId(String importId);
}
