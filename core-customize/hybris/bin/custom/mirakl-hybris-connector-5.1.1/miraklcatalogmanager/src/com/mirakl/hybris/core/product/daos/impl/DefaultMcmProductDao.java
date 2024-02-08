package com.mirakl.hybris.core.product.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mirakl.hybris.core.product.daos.McmProductDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class DefaultMcmProductDao extends DefaultGenericDao<ProductModel> implements McmProductDao {

  public DefaultMcmProductDao() {
    super(ProductModel._TYPECODE);
  }

  @Override
  public ProductModel findProductForMiraklVariantGroupCode(String variantGroupCode, CatalogVersionModel catalogVersion) {
    validateParameterNotNullStandardMessage("variantGroupCode", variantGroupCode);
    validateParameterNotNullStandardMessage("catalogVersion", catalogVersion);

    Map<String, Object> params = new HashMap<>();
    params.put(ProductModel.MIRAKLVARIANTGROUPCODE, variantGroupCode);
    params.put(ProductModel.CATALOGVERSION, catalogVersion);

    List<ProductModel> products = find(params);

    if (products.size() > 1) {
      throw new AmbiguousIdentifierException(
          format("Found multiple root base products matching variant group [%s]", variantGroupCode));
    }

    if (products.size() == 1) {
      return products.get(0);
    }

    return null;
  }

  @Override
  public ProductModel findProductForChecksum(String checksum, CatalogVersionModel catalogVersion) {
    validateParameterNotNullStandardMessage("checksum", checksum);
    validateParameterNotNullStandardMessage("catalogVersion", catalogVersion);

    ImmutableMap<String, Object> params = ImmutableMap.of( //
        ProductModel.CHECKSUM, checksum, //
        ProductModel.CATALOGVERSION, catalogVersion);

    List<ProductModel> products = find(params);

    if (products.size() > 1) {
      throw new AmbiguousIdentifierException(
          String.format("Found [%s] products with checksum [%s] on catalog [name=%s, version=%s]", products.size(), checksum,
              catalogVersion.getCategorySystemName(), catalogVersion.getVersion()));
    }
    if (products.size() == 1) {
      return products.get(0);
    }

    return null;
  }

}
