package com.mirakl.hybris.core.product.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopVariantGroupModel;
import com.mirakl.hybris.core.product.daos.MciProductDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultMciProductDao extends DefaultGenericDao<ProductModel> implements MciProductDao {

  public DefaultMciProductDao() {
    super(ProductModel._TYPECODE);
  }

  protected static final String SHOP_VARIANT_GROUP_QUERY = "SELECT {svg:" + ShopVariantGroupModel.PK + "}"//
      + " FROM {" + ShopVariantGroupModel._TYPECODE + " AS svg"//
      + " JOIN " + ProductModel._TYPECODE + " AS p ON {p:" + ProductModel.PK + "}={svg:" + ShopVariantGroupModel.PRODUCT + "}}" //
      + " WHERE {svg:" + ShopVariantGroupModel.SHOP + "} = ?" + ShopVariantGroupModel.SHOP //
      + " AND {svg:" + ShopVariantGroupModel.CODE + "} = ?" + ShopVariantGroupModel.CODE//
      + " AND {p:" + ProductModel.CATALOGVERSION + "} = ?" + ProductModel.CATALOGVERSION;

  @Override
  public ProductModel findProductForShopVariantGroupCode(ShopModel shop, String variantGroupCode,
      CatalogVersionModel catalogVersion) {
    validateParameterNotNullStandardMessage("shop", shop);
    validateParameterNotNullStandardMessage("variantGroupCode", variantGroupCode);
    validateParameterNotNullStandardMessage("catalogVersion", catalogVersion);

    Map<String, Object> params = new HashMap<>();
    params.put(ShopVariantGroupModel.SHOP, shop);
    params.put(ShopVariantGroupModel.CODE, variantGroupCode);
    params.put(ProductModel.CATALOGVERSION, catalogVersion);

    SearchResult<ShopVariantGroupModel> searchResult = getFlexibleSearchService().search(SHOP_VARIANT_GROUP_QUERY, params);
    if (searchResult.getCount() > 1) {
      throw new AmbiguousIdentifierException(
          format("Found multiple root base products matching shop [%s] and variant group [%s]", shop.getId(), variantGroupCode));
    }

    if (searchResult.getCount() == 1) {
      return searchResult.getResult().get(0).getProduct();
    }

    return null;
  }


}
